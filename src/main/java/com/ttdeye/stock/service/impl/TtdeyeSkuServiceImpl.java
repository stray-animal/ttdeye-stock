package com.ttdeye.stock.service.impl;
import java.io.IOException;
import java.math.BigDecimal;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttdeye.stock.common.domain.ApiResponseCode;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.exception.ApiException;
import com.ttdeye.stock.common.utils.*;
import com.ttdeye.stock.domain.dto.poi.SkuExportDto;
import com.ttdeye.stock.domain.dto.poi.SkuImportDto;
import com.ttdeye.stock.domain.dto.poi.SkuOutOfStockDto;
import com.ttdeye.stock.domain.dto.poi.SkuWarehousingDto;
import com.ttdeye.stock.domain.dto.req.SkuExportReq;
import com.ttdeye.stock.domain.dto.req.SkuOutOfStockReq;
import com.ttdeye.stock.domain.dto.req.SkuWarehousingReq;
import com.ttdeye.stock.entity.*;
import com.ttdeye.stock.mapper.*;
import com.ttdeye.stock.service.ITtdeyeFileLogService;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 产品信息表SKU 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */

@Slf4j
@Service
public class TtdeyeSkuServiceImpl extends ServiceImpl<TtdeyeSkuMapper, TtdeyeSku> implements ITtdeyeSkuService {



    @Autowired
    private TtdeyeFileLogMapper ttdeyeFileLogMapper;


    @Autowired
    private TtdeyeSkuMapper ttdeyeSkuMapper;



    @Autowired
    private OSSClientUtils ossClientUtils;


    @Autowired
    private TtdeyeSpuMapper ttdeyeSpuMapper;

    @Autowired
    private TtdeyeBatchMapper ttdeyeBatchMapper;

    @Autowired
    private TtdeyeSkuBatchMapper ttdeyeSkuBatchMapper;

    @Autowired
    private TtdeyeStockChangeRecordMapper ttdeyeStockChangeRecordMapper;


    @Autowired
    private ITtdeyeFileLogService iTtdeyeFileLogService;

    /**
     * 导入SKU
     * @param multipartFile
     * @param ttdeyeUser
     * @return
     */
    @Transactional
    public ApiResponseT spuImport(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception {

        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        List<SkuImportDto> skuImportDtoList = ExcelImportUtil.importExcel(multipartFile.getInputStream(), SkuImportDto.class,  params);
        log.info("skuImportDtoList----{}", JacksonUtil.toJsonString(skuImportDtoList));
        //文件校验
        if(CollectionUtils.isEmpty(skuImportDtoList)){
            return ApiResponseT.failed("不能导入空文件！");
        }

        for (int i = 0; i < skuImportDtoList.size(); i++) {
            Integer line = i + 1;
            SkuImportDto skuImportDto = skuImportDtoList.get(i);

            if (StringUtils.isEmpty(skuImportDto.getSpuCode()) && StringUtils.isEmpty(skuImportDto.getSpuNo())) {
                return ApiResponseT.failed("第" + line + "行,SPU代码或SPU编号至少一项必填！");
            }
            //SKU代码是否重复
            Long count = ttdeyeSkuMapper.selectCount(Wrappers.<TtdeyeSku>lambdaQuery()
                    .eq(TtdeyeSku::getDeleteFlag, 0)
                    .eq(TtdeyeSku::getSkuCode, skuImportDto.getSkuCode()));
            if (count > 0) {
                return ApiResponseT.failed("第" + line + "行,SKU代码" + skuImportDto.getSkuCode() + "已存在,请修改后重新上传！");
            }
            //校验批次商品未填写批次号提示
            //查询SPU信息
            TtdeyeSpu ttdeyeSpu = ttdeyeSpuMapper.selectOne(Wrappers.<TtdeyeSpu>lambdaQuery()
                    .eq(TtdeyeSpu::getDeleteFlag, 0)
                    .eq(skuImportDto.getSpuCode() != null, TtdeyeSpu::getSpuCode, skuImportDto.getSpuCode())
                    .eq(skuImportDto.getSpuNo() != null, TtdeyeSpu::getSpuNo, skuImportDto.getSpuNo()));
            if (ttdeyeSpu == null) {
                return ApiResponseT.failed("第" + line + "行,未查询到有效SPU,请查证！");
            }
            if (ttdeyeSpu.getBatchFlag() == 1 && skuImportDto.getStockNum() > 0) {
                if (StringUtils.isEmpty(skuImportDto.getBatchNo())) {
                    return ApiResponseT.failed("第" + line + "行,批次商品，必须填写批次号！");
                } else {
                    TtdeyeBatch ttdeyeBatch = ttdeyeBatchMapper.selectOne(
                            Wrappers.<TtdeyeBatch>lambdaQuery()
                                    .eq(TtdeyeBatch::getDeleteFlag, 0)
                                    .eq(TtdeyeBatch::getBatchNo, skuImportDto.getBatchNo())
                    );
                    if (ttdeyeBatch == null) {
                        return ApiResponseT.failed("第" + line + "行,批次商品，初始批次号不可用，请修改！");
                    }
                }

            }


            //SPU校验完成，保存上传文件记录
            String fileUrl = iTtdeyeFileLogService.saveFile(multipartFile, 2, ttdeyeUser.getLoginAccount());
            //创建SKU信息
            TtdeyeSku ttdeyeSku = this.saveTtdeyeSku(ttdeyeUser, skuImportDto, ttdeyeSpu);

            //如果库存数大于0，则进行初始入库
            if (skuImportDto.getStockNum() > 0) {
                //保存库存变更记录
                TtdeyeStockChangeRecord ttdeyeStockChangeRecord = new TtdeyeStockChangeRecord();
                ttdeyeStockChangeRecord.setSkuId(ttdeyeSku.getSkuId());
                ttdeyeStockChangeRecord.setSkuNo(ttdeyeSku.getSkuNo());
                ttdeyeStockChangeRecord.setSkuBeforeStock(0L);
                ttdeyeStockChangeRecord.setSkuAfterStock(ttdeyeSku.getStockCurrentNum());
                ttdeyeStockChangeRecord.setOccurStock(ttdeyeSku.getStockCurrentNum());
                ttdeyeStockChangeRecord.setDirection(1);
                ttdeyeStockChangeRecord.setSourceType(1);
                ttdeyeStockChangeRecord.setFileUrl(fileUrl);
                ttdeyeStockChangeRecord.setCreateTime(new Date());
                ttdeyeStockChangeRecord.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
                ttdeyeStockChangeRecord.setCreateNikeName(ttdeyeUser.getNickName());
                ttdeyeStockChangeRecord.setDeleteFlag(0);
                ttdeyeStockChangeRecord.setSpuId(ttdeyeSku.getSpuId());
                ttdeyeStockChangeRecord.setSpuNo(ttdeyeSku.getSpuNo());
                ttdeyeStockChangeRecord.setUnitPrice(ttdeyeSku.getPurchasePrice());

                //如果是批次商品
                if (ttdeyeSpu.getBatchFlag() == 1) {
                    TtdeyeBatch ttdeyeBatch = ttdeyeBatchMapper.selectOne(
                            Wrappers.<TtdeyeBatch>lambdaQuery()
                                    .eq(TtdeyeBatch::getDeleteFlag, 0)
                                    .eq(TtdeyeBatch::getBatchNo, skuImportDto.getBatchNo())
                    );

                    //保存批次库存信息
                    TtdeyeSkuBatch ttdeyeSkuBatch = this.savetdeyeSkuBatch(skuImportDto, ttdeyeSku, ttdeyeBatch);

                    //变更记录相应处理
                    ttdeyeStockChangeRecord.setBatchFlag(1);
                    ttdeyeStockChangeRecord.setBatchId(ttdeyeBatch.getBatchId());
                    ttdeyeStockChangeRecord.setBatchNo(ttdeyeBatch.getBatchNo());
                    ttdeyeStockChangeRecord.setSkuBatchNo(ttdeyeSkuBatch.getSkuBatchNo());
                    ttdeyeStockChangeRecord.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());
                    ttdeyeStockChangeRecord.setSkuBatchBeforeStock(0L);
                    ttdeyeStockChangeRecord.setSkuBatchAfterStock(ttdeyeSkuBatch.getStockCurrentNum());
                }
                //保存库存变更记录
                ttdeyeStockChangeRecordMapper.insert(ttdeyeStockChangeRecord);
            }
        }
        return ApiResponseT.ok();
    }


    /**
     * 根据上传内容保存批次库存信息
     * @param skuImportDto
     * @param ttdeyeSku
     * @param ttdeyeBatch
     * @return
     */
    @NotNull
    private TtdeyeSkuBatch savetdeyeSkuBatch(SkuImportDto skuImportDto, TtdeyeSku ttdeyeSku, TtdeyeBatch ttdeyeBatch) {
        TtdeyeSkuBatch ttdeyeSkuBatch = new TtdeyeSkuBatch();
        ttdeyeSkuBatch.setSkuId(ttdeyeSku.getSkuId());
        ttdeyeSkuBatch.setSkuNo(ttdeyeSku.getSkuNo());
        ttdeyeSkuBatch.setBatchId(ttdeyeBatch.getBatchId());
        ttdeyeSkuBatch.setBatchNo(skuImportDto.getBatchNo());
        ttdeyeSkuBatch.setStockCurrentNum(skuImportDto.getStockNum());
        ttdeyeSkuBatch.setStockAllNum(skuImportDto.getStockNum());
        ttdeyeSkuBatch.setStockOutNum(0L);
        ttdeyeSkuBatch.setCreateTime(new Date());
        ttdeyeSkuBatch.setUpdateTime(new Date());
        ttdeyeSkuBatch.setSkuBatchNo(SnowflakeIdWorker.generateIdStr());
        ttdeyeSkuBatchMapper.insert(ttdeyeSkuBatch);
        return ttdeyeSkuBatch;
    }

    /**
     * 根据上传的每一行保存sku信息
     * @param ttdeyeUser
     * @param skuImportDto
     * @param ttdeyeSpu
     * @return
     */
    @NotNull
    private TtdeyeSku saveTtdeyeSku(TtdeyeUser ttdeyeUser, SkuImportDto skuImportDto, TtdeyeSpu ttdeyeSpu) {
        TtdeyeSku ttdeyeSku = new TtdeyeSku();
        BeanUtils.copyProperties(skuImportDto,ttdeyeSku);
        ttdeyeSku.setSkuNo(SnowflakeIdWorker.generateIdStr());
        ttdeyeSku.setUpdateTime(new Date());
        ttdeyeSku.setCreateTime(new Date());
        ttdeyeSku.setSourceType(2);
        ttdeyeSku.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeSku.setStockAllNum(skuImportDto.getStockNum());
        ttdeyeSku.setStockCurrentNum(skuImportDto.getStockNum());
        ttdeyeSku.setSpuNo(ttdeyeSpu.getSpuNo());
        ttdeyeSku.setSpuId(ttdeyeSpu.getSpuId());
        //保存SPU信息
        ttdeyeSkuMapper.insert(ttdeyeSku);
        return ttdeyeSku;
    }


    /**
     * 查询导出数据
     * @param skuExportReq
     * @return
     */
    public List<SkuExportDto> selectExportData(SkuExportReq skuExportReq) {
        List<SkuExportDto> ttdeyeSkus = ttdeyeSkuMapper.selectExportData(skuExportReq);
        return ttdeyeSkus;
    }


    /**
     * sku手动入库
     * @param skuWarehousingReq
     * @param ttdeyeUser
     */
    public void skuOperaWarehousing(SkuWarehousingReq skuWarehousingReq, TtdeyeUser ttdeyeUser){

        TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectById(skuWarehousingReq.getSkuId());
        //查询SPU信息
        TtdeyeSpu ttdeyeSpu = ttdeyeSpuMapper.selectOne(Wrappers.<TtdeyeSpu>lambdaQuery()
                .eq(TtdeyeSpu::getDeleteFlag,0)
                .eq(TtdeyeSpu::getSpuId,ttdeyeSku.getSpuId())
        );

        if(ttdeyeSpu.getBatchFlag() == 1){
            if(StringUtils.isEmpty(skuWarehousingReq.getBatchNo())){
                throw new ApiException(ApiResponseCode.COMMON_FAILED_CODE,"批次商品，必须填写批次号！");
            }else{
                TtdeyeBatch ttdeyeBatch = ttdeyeBatchMapper.selectOne(
                        Wrappers.<TtdeyeBatch>lambdaQuery()
                                .eq(TtdeyeBatch::getDeleteFlag,0)
                                .eq(TtdeyeBatch::getBatchNo,skuWarehousingReq.getBatchNo())
                );
                if(ttdeyeBatch == null){
                    throw new ApiException(ApiResponseCode.COMMON_FAILED_CODE,"当前批次号不可用，请修改！");
                }
            }

        }

        this.skuWareHousing(ttdeyeUser,skuWarehousingReq,ttdeyeSku,ttdeyeSpu,null);
    }

    /**
     * 采购入库
     * @param multipartFile
     * @param ttdeyeUser
     * @return
     */
    public ApiResponseT skuWarehousing(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception {
        ImportParams params = new ImportParams();
        params.setHeadRows(1);

        List<SkuWarehousingDto> skuWarehousingDtos = ExcelImportUtil.importExcel(multipartFile.getInputStream(), SkuWarehousingDto.class,  params);
        if(CollectionUtils.isEmpty(skuWarehousingDtos)){
            return ApiResponseT.failed("不能导入空文件！");
        }

        //文件校验
        for (int i = 0; i < skuWarehousingDtos.size(); i++) {
            SkuWarehousingDto skuWarehousingDto = skuWarehousingDtos.get(i);
            Integer line = i+1;
            //SKU代码是否重复
            TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectOne(Wrappers.<TtdeyeSku>lambdaQuery()
                    .eq(TtdeyeSku::getDeleteFlag,0)
                    .eq(TtdeyeSku::getSkuCode,skuWarehousingDto.getSkuCode()));
            if(ttdeyeSku == null){
                return ApiResponseT.failed("第"+line+"行,SKU代码"+skuWarehousingDto.getSkuCode()+"的SKU不存在,请修改后重新上传！");
            }

            //校验批次商品未填写批次号提示
            //查询SPU信息
            TtdeyeSpu ttdeyeSpu = ttdeyeSpuMapper.selectOne(Wrappers.<TtdeyeSpu>lambdaQuery()
                    .eq(TtdeyeSpu::getDeleteFlag,0)
                    .eq(TtdeyeSpu::getSpuId,ttdeyeSku.getSpuId())
                    );
            if(ttdeyeSpu == null){
                return ApiResponseT.failed("第"+line+"行,未查询到有效SPU,请查证！");
            }

            if(ttdeyeSpu.getBatchFlag() == 1){
                if(StringUtils.isEmpty(skuWarehousingDto.getBatchNo())){
                    return ApiResponseT.failed("第"+line+"行,批次商品，必须填写批次号！");
                }else{
                    TtdeyeBatch ttdeyeBatch = ttdeyeBatchMapper.selectOne(
                            Wrappers.<TtdeyeBatch>lambdaQuery()
                                    .eq(TtdeyeBatch::getDeleteFlag,0)
                                    .eq(TtdeyeBatch::getBatchNo,skuWarehousingDto.getBatchNo())
                    );
                    if(ttdeyeBatch == null){
                        return ApiResponseT.failed("第"+line+"行,批次商品，初始批次号不可用，请修改！");
                    }
                }

            }

            if(skuWarehousingDto.getStockNum() <=0 ){
                return ApiResponseT.failed("第"+line+"行,入库数量必须大于0！");
            }

            //SPU校验完成，保存上传文件记录
            String fileUrl = iTtdeyeFileLogService.saveFile(multipartFile,3,ttdeyeUser.getLoginAccount());

            //校验通过处理入库逻辑
            this.skuWareHousing(ttdeyeUser, skuWarehousingDto, ttdeyeSku, ttdeyeSpu, fileUrl);

        }
        return ApiResponseT.ok();
    }

    /**
     * 入库逻辑
     * @param ttdeyeUser
     * @param skuWarehousingDto
     * @param ttdeyeSku
     * @param ttdeyeSpu
     * @param fileUrl
     */
    private void skuWareHousing(TtdeyeUser ttdeyeUser, SkuWarehousingDto skuWarehousingDto, TtdeyeSku ttdeyeSku, TtdeyeSpu ttdeyeSpu, String fileUrl) {
        //首先入库到SKU
        TtdeyeSku ttdeyeSkuNew = new TtdeyeSku();
        ttdeyeSkuNew.setStockCurrentNum(ttdeyeSku.getStockCurrentNum() + skuWarehousingDto.getStockNum());
        ttdeyeSkuNew.setStockAllNum(ttdeyeSku.getStockAllNum() + skuWarehousingDto.getStockNum());
        ttdeyeSkuNew.setUpdateTime(new Date());
        ttdeyeSkuNew.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeSkuNew.setSkuId(ttdeyeSku.getSkuId());
        ttdeyeSkuMapper.updateById(ttdeyeSkuNew);

        TtdeyeStockChangeRecord ttdeyeStockChangeRecord =  new TtdeyeStockChangeRecord();
        ttdeyeStockChangeRecord.setSkuId(ttdeyeSku.getSkuId());
        ttdeyeStockChangeRecord.setSkuNo(ttdeyeSku.getSkuNo());
        ttdeyeStockChangeRecord.setSkuBeforeStock(ttdeyeSku.getStockCurrentNum());
        ttdeyeStockChangeRecord.setSkuAfterStock(ttdeyeSku.getStockCurrentNum() + skuWarehousingDto.getStockNum());
        ttdeyeStockChangeRecord.setOccurStock(skuWarehousingDto.getStockNum());
        ttdeyeStockChangeRecord.setDirection(1);
        ttdeyeStockChangeRecord.setSourceType(2);
        ttdeyeStockChangeRecord.setFileUrl(fileUrl);
        ttdeyeStockChangeRecord.setCreateTime(new Date());
        ttdeyeStockChangeRecord.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeStockChangeRecord.setCreateNikeName(ttdeyeUser.getNickName());
        ttdeyeStockChangeRecord.setSpuId(ttdeyeSpu.getSpuId());
        ttdeyeStockChangeRecord.setSpuNo(ttdeyeSpu.getSpuNo());
        ttdeyeStockChangeRecord.setUnitPrice(skuWarehousingDto.getUnitPrice());


        //如果支持批次则按照批次入库
        if(ttdeyeSpu.getBatchFlag() == 1 ){
           //查询到批次库存信息
            TtdeyeSkuBatch ttdeyeSkuBatch =  ttdeyeSkuBatchMapper.selectOne(
                    Wrappers.<TtdeyeSkuBatch>lambdaQuery()
                            .eq(TtdeyeSkuBatch::getSkuBatchNo, skuWarehousingDto.getBatchNo())
                            .eq(TtdeyeSkuBatch::getSkuId, ttdeyeSku.getSkuId())
            );
            //查询批次信息
            TtdeyeBatch ttdeyeBatch = ttdeyeBatchMapper.selectOne(
                    Wrappers.<TtdeyeBatch>lambdaQuery()
                            .eq(TtdeyeBatch::getDeleteFlag,0)
                            .eq(TtdeyeBatch::getBatchNo, skuWarehousingDto.getBatchNo())
            );

            //如果没有当前SKU批次库存信息则生成
            if(ttdeyeSkuBatch == null){
                ttdeyeSkuBatch = new TtdeyeSkuBatch();
                ttdeyeSkuBatch.setSkuNo(ttdeyeSku.getSkuNo());
                ttdeyeSkuBatch.setBatchId(ttdeyeBatch.getBatchId());
                ttdeyeSkuBatch.setBatchNo(skuWarehousingDto.getBatchNo());
                ttdeyeSkuBatch.setStockCurrentNum(skuWarehousingDto.getStockNum());
                ttdeyeSkuBatch.setStockAllNum(skuWarehousingDto.getStockNum());
                ttdeyeSkuBatch.setCreateTime(new Date());
                ttdeyeSkuBatch.setUpdateTime(new Date());
                ttdeyeSkuBatch.setSkuBatchNo(SnowflakeIdWorker.generateIdStr());
                ttdeyeSkuBatchMapper.insert(ttdeyeSkuBatch);

                ttdeyeStockChangeRecord.setSkuBatchBeforeStock(0L);
                ttdeyeStockChangeRecord.setSkuBatchAfterStock(ttdeyeSkuBatch.getStockCurrentNum());
            }else{
                ttdeyeStockChangeRecord.setSkuBatchBeforeStock(ttdeyeSkuBatch.getStockCurrentNum());
                ttdeyeStockChangeRecord.setSkuBatchAfterStock(ttdeyeSkuBatch.getStockCurrentNum()+ skuWarehousingDto.getStockNum());

                TtdeyeSkuBatch ttdeyeSkuBatchNew = new TtdeyeSkuBatch();
                ttdeyeSkuBatchNew.setStockAllNum(ttdeyeSkuBatch.getStockAllNum() + skuWarehousingDto.getStockNum());
                ttdeyeSkuBatchNew.setStockCurrentNum(ttdeyeSkuBatch.getStockCurrentNum() + skuWarehousingDto.getStockNum());
                ttdeyeSkuBatchNew.setUpdateTime(new Date());
                ttdeyeSkuBatchNew.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());
                ttdeyeSkuBatchMapper.updateById(ttdeyeSkuBatch);
            }
            //保存批次入库记录
            ttdeyeStockChangeRecord.setBatchFlag(1);
            ttdeyeStockChangeRecord.setBatchId(ttdeyeBatch.getBatchId());
            ttdeyeStockChangeRecord.setBatchNo(ttdeyeBatch.getBatchNo());
            ttdeyeStockChangeRecord.setSkuBatchNo(ttdeyeSkuBatch.getSkuBatchNo());
            ttdeyeStockChangeRecord.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());

        }
        ttdeyeStockChangeRecordMapper.insert(ttdeyeStockChangeRecord);
    }



    public void skuOperaOutOfStock(SkuOutOfStockReq skuOutOfStockReq, TtdeyeUser ttdeyeUser){

        //SKU代码是否重复
        TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectById(skuOutOfStockReq.getSkuId());

        //查询SPU信息
        TtdeyeSpu ttdeyeSpu = ttdeyeSpuMapper.selectOne(Wrappers.<TtdeyeSpu>lambdaQuery()
                .eq(TtdeyeSpu::getDeleteFlag,0)
                .eq(TtdeyeSpu::getSpuId,ttdeyeSku.getSpuId())
        );


        this.skuOutOfStock(ttdeyeUser,skuOutOfStockReq,ttdeyeSku,null,ttdeyeSpu);
    }


    public ApiResponseT skuOutOfStock(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception {
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        List<SkuOutOfStockDto> skuOutOfStockDtos = ExcelImportUtil.importExcel(multipartFile.getInputStream(), SkuOutOfStockDto.class,  params);

        if(CollectionUtils.isEmpty(skuOutOfStockDtos)){
            return ApiResponseT.failed("不能导入空文件！");
        }
        //文件校验
        for (int i = 0; i < skuOutOfStockDtos.size(); i++) {
            Integer line = i+1;
            SkuOutOfStockDto skuOutOfStockDto = skuOutOfStockDtos.get(i);
            TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectOne(Wrappers.<TtdeyeSku>lambdaQuery()
                    .eq(TtdeyeSku::getDeleteFlag,0)
                    .eq(TtdeyeSku::getSkuCode,skuOutOfStockDto.getSkuCode()));

            if(ttdeyeSku == null){
                return ApiResponseT.failed("第"+line+"行,SKU代码"+skuOutOfStockDto.getSkuCode()+"的SKU不存在,请修改后重新上传！");
            }

            //SPU校验完成，保存上传文件记录
            String fileUrl = iTtdeyeFileLogService.saveFile(multipartFile,4,ttdeyeUser.getLoginAccount());

            //查询SPU信息
            TtdeyeSpu ttdeyeSpu = ttdeyeSpuMapper.selectOne(Wrappers.<TtdeyeSpu>lambdaQuery()
                    .eq(TtdeyeSpu::getDeleteFlag,0)
                    .eq(TtdeyeSpu::getSpuId,ttdeyeSku.getSpuId())
            );

            skuOutOfStock(ttdeyeUser, skuOutOfStockDto, ttdeyeSku, fileUrl, ttdeyeSpu);


        }

        return ApiResponseT.ok();

    }


    /**
     * 抽离的单比出库方法
     * @param ttdeyeUser
     * @param skuOutOfStockDto
     * @param ttdeyeSku
     * @param fileUrl
     * @param ttdeyeSpu
     */
    private void skuOutOfStock(TtdeyeUser ttdeyeUser, SkuOutOfStockDto skuOutOfStockDto, TtdeyeSku ttdeyeSku, String fileUrl, TtdeyeSpu ttdeyeSpu) {
        //更新SKU出库
        TtdeyeSku ttdeyeSkuNew = new TtdeyeSku();
        ttdeyeSkuNew.setStockCurrentNum(ttdeyeSku.getStockCurrentNum() - skuOutOfStockDto.getStockNum());
        ttdeyeSkuNew.setStockOutNum(ttdeyeSku.getStockOutNum() + skuOutOfStockDto.getStockNum());
        ttdeyeSkuNew.setUpdateTime(new Date());
        ttdeyeSkuNew.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeSkuNew.setSkuId(ttdeyeSku.getSkuId());
        ttdeyeSkuMapper.updateById(ttdeyeSkuNew);

        //判断是否批次商品
        if(ttdeyeSpu.getBatchFlag() == 0){
            //非批次商品直接走SKU出库
            TtdeyeStockChangeRecord ttdeyeStockChangeRecord =  new TtdeyeStockChangeRecord();
            ttdeyeStockChangeRecord.setSkuId(ttdeyeSku.getSkuId());
            ttdeyeStockChangeRecord.setSkuNo(ttdeyeSku.getSkuNo());
            ttdeyeStockChangeRecord.setSkuBeforeStock(ttdeyeSku.getStockCurrentNum());
            ttdeyeStockChangeRecord.setSkuAfterStock(ttdeyeSku.getStockCurrentNum() - skuOutOfStockDto.getStockNum());
            ttdeyeStockChangeRecord.setOccurStock(skuOutOfStockDto.getStockNum());
            ttdeyeStockChangeRecord.setDirection(0);
            ttdeyeStockChangeRecord.setSourceType(3);
            ttdeyeStockChangeRecord.setFileUrl(fileUrl);
            ttdeyeStockChangeRecord.setCreateTime(new Date());
            ttdeyeStockChangeRecord.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
            ttdeyeStockChangeRecord.setCreateNikeName(ttdeyeUser.getNickName());
            ttdeyeStockChangeRecord.setSpuId(ttdeyeSpu.getSpuId());
            ttdeyeStockChangeRecord.setSpuNo(ttdeyeSpu.getSpuNo());
            ttdeyeStockChangeRecordMapper.insert(ttdeyeStockChangeRecord);

        }else if(ttdeyeSpu.getBatchFlag() == 1){
            //进行批次出库
            //todo 查询sku批次库存列表,以批次号正序排序

            List<TtdeyeSkuBatch> ttdeyeSkuBatchListYe = ttdeyeSkuBatchMapper.selectList(
                Wrappers.<TtdeyeSkuBatch>lambdaQuery()
                        .eq(TtdeyeSkuBatch::getSkuId, ttdeyeSku.getSkuId())
                        .le(TtdeyeSkuBatch::getStockCurrentNum,0)
                        .orderByDesc(TtdeyeSkuBatch::getBatchNo)
            );

            if(CollectionUtils.isEmpty(ttdeyeSkuBatchListYe)){
                throw new ApiException(ApiResponseCode.COMMON_FAILED_CODE,"未查询到批次库存，出库失败！");
            }
            //批量查询批次信息


            List<Long> batchIdList = ttdeyeSkuBatchListYe.stream().map(TtdeyeSkuBatch::getBatchId).collect(Collectors.toList());

            List<TtdeyeBatch> batchListYe = ttdeyeBatchMapper.selectList(
                    Wrappers.<TtdeyeBatch>lambdaQuery()
                            .in(TtdeyeBatch :: getBatchId,batchIdList)
            );

            List<TtdeyeBatch> batchList = batchListYe.stream().sorted(

                    (e1,e2) -> DateLaterUtils.dateSorted(DateLaterUtils.getLastDate(e1.getProductionDate(),e1.getShelfLife()), DateLaterUtils.getLastDate(e2.getProductionDate(),e2.getShelfLife()))

            ).collect(Collectors.toList());


            List<Long> batchIdListNew  = batchList.stream().map(TtdeyeBatch::getBatchId).collect(Collectors.toList());


            List<TtdeyeSkuBatch> ttdeyeSkuBatchList = ttdeyeSkuBatchListYe.stream().sorted(

                    (e1, e2) -> {

                        if (batchIdListNew.indexOf(e1.getBatchId()) < batchIdListNew.indexOf(e2.getBatchId())) {
                            return -1;
                        } else if (batchIdListNew.indexOf(e1.getBatchId()) > batchIdListNew.indexOf(e2.getBatchId())) {
                            return 1;
                        } else {
                            return 0;
                        }

                    }


            ).collect(Collectors.toList());



            //出库剩余数量
             Long outSumBalance = skuOutOfStockDto.getStockNum();

            for (TtdeyeSkuBatch ttdeyeSkuBatch : ttdeyeSkuBatchList) {

                TtdeyeSkuBatch ttdeyeSkuBatchNew = new TtdeyeSkuBatch();
                if(ttdeyeSkuBatch.getStockCurrentNum() >= outSumBalance){
                    //如果大于等于出库数量，就把库出完
                    ttdeyeSkuBatchNew.setStockCurrentNum(ttdeyeSkuBatch.getStockCurrentNum() - outSumBalance);
                    ttdeyeSkuBatchNew.setStockOutNum(ttdeyeSkuBatch.getStockOutNum() + outSumBalance);
                    ttdeyeSkuBatchNew.setUpdateTime(new Date());
                    ttdeyeSkuBatchNew.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());
                    ttdeyeSkuBatchMapper.updateById(ttdeyeSkuBatchNew);
                    //保存日志
                    TtdeyeStockChangeRecord ttdeyeStockChangeRecord =  new TtdeyeStockChangeRecord();
                    ttdeyeStockChangeRecord.setSkuId(ttdeyeSku.getSkuId());
                    ttdeyeStockChangeRecord.setSkuNo(ttdeyeSku.getSkuNo());
                    ttdeyeStockChangeRecord.setSkuBeforeStock(ttdeyeSku.getStockCurrentNum());
                    ttdeyeStockChangeRecord.setSkuAfterStock(ttdeyeSku.getStockCurrentNum() - outSumBalance);
                    ttdeyeStockChangeRecord.setOccurStock(outSumBalance);
                    ttdeyeStockChangeRecord.setDirection(0);
                    ttdeyeStockChangeRecord.setSourceType(3);
                    ttdeyeStockChangeRecord.setFileUrl(fileUrl);
                    ttdeyeStockChangeRecord.setCreateTime(new Date());
                    ttdeyeStockChangeRecord.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
                    ttdeyeStockChangeRecord.setCreateNikeName(ttdeyeUser.getNickName());
                    ttdeyeStockChangeRecord.setSpuId(ttdeyeSpu.getSpuId());
                    ttdeyeStockChangeRecord.setSpuNo(ttdeyeSpu.getSpuNo());

                    //保存批次入库记录
                    ttdeyeStockChangeRecord.setBatchFlag(1);
                    ttdeyeStockChangeRecord.setBatchId(ttdeyeSkuBatch.getBatchId());
                    ttdeyeStockChangeRecord.setBatchNo(ttdeyeSkuBatch.getBatchNo());
                    ttdeyeStockChangeRecord.setSkuBatchNo(ttdeyeSkuBatch.getSkuBatchNo());
                    ttdeyeStockChangeRecord.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());
                    ttdeyeStockChangeRecord.setSkuBatchBeforeStock(ttdeyeSkuBatch.getStockCurrentNum());
                    ttdeyeStockChangeRecord.setSkuBatchAfterStock(ttdeyeSkuBatch.getStockCurrentNum() - outSumBalance);
                    ttdeyeStockChangeRecordMapper.insert(ttdeyeStockChangeRecord);
                    break;
                }else{
                    if(ttdeyeSkuBatchList.size() - 1 == ttdeyeSkuBatchList.indexOf(ttdeyeSkuBatch)){
                      //如果是最后一个则出负数
                        ttdeyeSkuBatchNew.setStockCurrentNum(ttdeyeSkuBatch.getStockCurrentNum() - outSumBalance);
                        ttdeyeSkuBatchNew.setStockOutNum(ttdeyeSkuBatch.getStockOutNum() + outSumBalance);

                    }else{
                        //如果小于出库数量，就把自己出完
                        ttdeyeSkuBatchNew.setStockCurrentNum(0L);
                        ttdeyeSkuBatchNew.setStockOutNum(ttdeyeSkuBatch.getStockOutNum() + ttdeyeSkuBatch.getStockCurrentNum());
                    }
                    //如果小于出库数量，就把自己出完

                    ttdeyeSkuBatchNew.setUpdateTime(new Date());
                    ttdeyeSkuBatchNew.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());
                    ttdeyeSkuBatchMapper.updateById(ttdeyeSkuBatchNew);

                    //保存日志
                    TtdeyeStockChangeRecord ttdeyeStockChangeRecord =  new TtdeyeStockChangeRecord();
                    ttdeyeStockChangeRecord.setSkuId(ttdeyeSku.getSkuId());
                    ttdeyeStockChangeRecord.setSkuNo(ttdeyeSku.getSkuNo());
                    ttdeyeStockChangeRecord.setSkuBeforeStock(ttdeyeSku.getStockCurrentNum());
                    ttdeyeStockChangeRecord.setSkuAfterStock(ttdeyeSku.getStockCurrentNum() - ttdeyeSkuBatch.getStockCurrentNum());
                    ttdeyeStockChangeRecord.setOccurStock(outSumBalance);
                    ttdeyeStockChangeRecord.setDirection(0);
                    ttdeyeStockChangeRecord.setSourceType(3);
                    ttdeyeStockChangeRecord.setFileUrl(fileUrl);
                    ttdeyeStockChangeRecord.setCreateTime(new Date());
                    ttdeyeStockChangeRecord.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
                    ttdeyeStockChangeRecord.setCreateNikeName(ttdeyeUser.getNickName());
                    ttdeyeStockChangeRecord.setSpuId(ttdeyeSpu.getSpuId());
                    ttdeyeStockChangeRecord.setSpuNo(ttdeyeSpu.getSpuNo());

                    //保存批次入库记录
                    ttdeyeStockChangeRecord.setBatchFlag(1);
                    ttdeyeStockChangeRecord.setBatchId(ttdeyeSkuBatch.getBatchId());
                    ttdeyeStockChangeRecord.setBatchNo(ttdeyeSkuBatch.getBatchNo());
                    ttdeyeStockChangeRecord.setSkuBatchNo(ttdeyeSkuBatch.getSkuBatchNo());
                    ttdeyeStockChangeRecord.setSkuBatchId(ttdeyeSkuBatch.getSkuBatchId());
                    ttdeyeStockChangeRecord.setSkuBatchBeforeStock(ttdeyeSkuBatch.getStockCurrentNum());
                    ttdeyeStockChangeRecord.setSkuBatchAfterStock(0L);
                    ttdeyeStockChangeRecordMapper.insert(ttdeyeStockChangeRecord);
                    outSumBalance = outSumBalance - ttdeyeSkuBatch.getStockCurrentNum();
                    //如果出库后剩余需出库金额正好小于等于0就结束
                    if(outSumBalance <= 0){
                        break;
                    }
                }
            }


        }
    }

}
