package com.ttdeye.stock.service.impl;
import java.math.BigDecimal;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.utils.BigDecimalUtils;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.common.utils.SnowflakeIdWorker;
import com.ttdeye.stock.domain.dto.poi.SkuExportDto;
import com.ttdeye.stock.domain.dto.poi.SkuImportDto;
import com.ttdeye.stock.domain.dto.poi.SkuWarehousingDto;
import com.ttdeye.stock.domain.dto.req.SkuExportReq;
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

import java.util.Date;
import java.util.List;

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
            Integer line = i+1;
            SkuImportDto skuImportDto = skuImportDtoList.get(i);

            if(StringUtils.isEmpty(skuImportDto.getSpuCode()) && StringUtils.isEmpty(skuImportDto.getSpuNo())){
                return ApiResponseT.failed("第"+line+"行,SPU代码或编号至少一项必填！");
            }
            //SKU代码是否重复
            Long count = ttdeyeSkuMapper.selectCount(Wrappers.<TtdeyeSku>lambdaQuery()
                    .eq(TtdeyeSku::getDeleteFlag,0)
                    .eq(TtdeyeSku::getSkuCode,skuImportDto.getSkuCode()));
            if(count > 0){
                return ApiResponseT.failed("第"+line+"行,SKU代码"+skuImportDto.getSkuCode()+"已存在,请修改后重新上传！");
            }
            //校验批次商品未填写批次号提示
            //查询SPU信息
            TtdeyeSpu ttdeyeSpu = ttdeyeSpuMapper.selectOne(Wrappers.<TtdeyeSpu>lambdaQuery()
                    .eq(TtdeyeSpu::getDeleteFlag,0)
                    .eq(skuImportDto.getSpuCode() != null,TtdeyeSpu::getSpuCode,skuImportDto.getSpuCode())
                    .eq(skuImportDto.getSpuNo() != null,TtdeyeSpu::getSpuNo,skuImportDto.getSpuNo()));
            if(ttdeyeSpu == null){
                return ApiResponseT.failed("第"+line+"行,未查询到有效SPU,请查证！");
            }
            if(ttdeyeSpu.getBatchFlag() == 1 && StringUtils.isEmpty(skuImportDto.getBatchNo())){
                return ApiResponseT.failed("第"+line+"行,批次商品，必须填写批次号！");
            }

            //SPU校验完成，保存上传文件记录
            String fileUrl = iTtdeyeFileLogService.saveFile(multipartFile,2,ttdeyeUser.getLoginAccount());

            //创建SKU信息
            TtdeyeSku ttdeyeSku = this.saveTtdeyeSku(ttdeyeUser, skuImportDto, ttdeyeSpu);

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
            if(ttdeyeSpu.getBatchFlag() == 1){
                TtdeyeBatch ttdeyeBatch = ttdeyeBatchMapper.selectOne(
                        Wrappers.<TtdeyeBatch>lambdaQuery()
                                .eq(TtdeyeBatch::getDeleteFlag,0)
                                .eq(TtdeyeBatch::getBatchNo,skuImportDto.getBatchNo())
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
            if(ttdeyeSpu.getBatchFlag() == 1 && StringUtils.isEmpty(skuWarehousingDto.getBatchNo())){
                return ApiResponseT.failed("第"+line+"行,批次商品，入库必须填写批次号！");
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
        ttdeyeSkuNew.setStockAllNum(ttdeyeSkuNew.getStockAllNum() + skuWarehousingDto.getStockNum());
        ttdeyeSkuNew.setPurchasePrice(skuWarehousingDto.getUnitPrice());
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

}
