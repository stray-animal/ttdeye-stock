package com.ttdeye.stock.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.common.utils.SnowflakeIdWorker;
import com.ttdeye.stock.domain.dto.poi.SkuImportDto;
import com.ttdeye.stock.entity.*;
import com.ttdeye.stock.mapper.*;
import com.ttdeye.stock.service.ITtdeyeFileLogService;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
            return ApiResponseT.failed("文件不能为空！");
        }

        for (int i = 0; i < skuImportDtoList.size(); i++) {
            Integer line = i+1;
            SkuImportDto skuImportDto = skuImportDtoList.get(i);

            if(StringUtils.isEmpty(skuImportDto.getSpuCode()) && StringUtils.isEmpty(skuImportDto.getSpuNo())){
                return ApiResponseT.failed("第"+line+"行,SKU代码或编号至少一项必填！");
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
                return ApiResponseT.failed("第"+line+"行,为查询到有效SPU,请查证！");
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


}
