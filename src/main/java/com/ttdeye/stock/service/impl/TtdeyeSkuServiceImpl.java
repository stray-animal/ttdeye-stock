package com.ttdeye.stock.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.domain.dto.poi.SkuImportDto;
import com.ttdeye.stock.entity.TtdeyeFileLog;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeSpu;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeFileLogMapper;
import com.ttdeye.stock.mapper.TtdeyeSkuMapper;
import com.ttdeye.stock.mapper.TtdeyeSpuMapper;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /**
     * 导入SKU
     * @param multipartFile
     * @param ttdeyeUser
     * @return
     */
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
        }



        for (SkuImportDto skuImportDto : skuImportDtoList) {



        }
        //导入SKU
        for (SkuImportDto skuImportDto : skuImportDtoList) {
            TtdeyeSku ttdeyeSku = new TtdeyeSku();
            BeanUtils.copyProperties(skuImportDto,ttdeyeSku);
            ttdeyeSku.setUpdateTime(new Date());
            ttdeyeSku.setCreateTime(new Date());
            ttdeyeSku.setSourceType(2);
            ttdeyeSku.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
            ttdeyeSku.setStockAllNum(skuImportDto.getStockNum());
            ttdeyeSku.setStockCurrentNum(skuImportDto.getStockNum());
            //保存SPU信息
            ttdeyeSkuMapper.insert(ttdeyeSku);
        }

        //判断如果是批次商品则更创建批次库存




        //SPU保存完成，保存上传文件记录
        String url = ossClientUtils.uploadImg2Oss(multipartFile);
        TtdeyeFileLog ttdeyeFileLog = new TtdeyeFileLog();
        ttdeyeFileLog.setFileUrl(url);
        ttdeyeFileLog.setFileType(2);
        ttdeyeFileLog.setCreateTime(new Date());
        ttdeyeFileLog.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeFileLogMapper.insert(ttdeyeFileLog);

        return ApiResponseT.ok();






    }



}
