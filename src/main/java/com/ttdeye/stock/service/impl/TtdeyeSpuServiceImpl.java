package com.ttdeye.stock.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.common.utils.SnowflakeIdWorker;
import com.ttdeye.stock.domain.dto.poi.SpuImportDto;
import com.ttdeye.stock.entity.TtdeyeFileLog;
import com.ttdeye.stock.entity.TtdeyeSpu;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeFileLogMapper;
import com.ttdeye.stock.mapper.TtdeyeSpuMapper;
import com.ttdeye.stock.service.ITtdeyeSpuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Slf4j
@Service
public class TtdeyeSpuServiceImpl extends ServiceImpl<TtdeyeSpuMapper, TtdeyeSpu> implements ITtdeyeSpuService {

    @Autowired
    private TtdeyeFileLogMapper ttdeyeFileLogMapper;

    @Autowired
    private TtdeyeSpuMapper ttdeyeSpuMapper;

    @Autowired
    private OSSClientUtils ossClientUtils;


    /**
     * 导入excel创建SPU
     * @param multipartFile
     * @param ttdeyeUser
     * @return
     * @throws Exception
     */
    @Transactional
    public ApiResponseT spuImport(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception {
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        List<SpuImportDto> spuImportDtoList = ExcelImportUtil.importExcel(multipartFile.getInputStream(), SpuImportDto.class,  params);

        log.info("spuImportDtoList----{}", JacksonUtil.toJsonString(spuImportDtoList));
        //文件校验
        if(CollectionUtils.isEmpty(spuImportDtoList)){
            return ApiResponseT.failed("文件不能为空！");
        }

        for (int i = 0; i < spuImportDtoList.size(); i++) {
            Integer line = i+1;
            SpuImportDto spuImportDto = spuImportDtoList.get(i);
            if(spuImportDto.getBatchFlag() == null){
                return ApiResponseT.failed("是否分批次商品存在空值！");
            }
            //商品代码是否重复
            Long count = ttdeyeSpuMapper.selectCount(Wrappers.<TtdeyeSpu>lambdaQuery()
                    .eq(TtdeyeSpu::getDeleteFlag,0)
                    .eq(TtdeyeSpu::getSpuCode,spuImportDto.getSpuCode()));
            if(count > 0){
                return ApiResponseT.failed("第"+line+"行,SKU代码商品代码"+spuImportDto.getSpuCode()+"已存在,请修改后重新上传！");
            }
            TtdeyeSpu ttdeyeSpu = new TtdeyeSpu();
            BeanUtils.copyProperties(spuImportDto,ttdeyeSpu);
            ttdeyeSpu.setUpdateTime(new Date());
            ttdeyeSpu.setCreateTime(new Date());
            ttdeyeSpu.setSourceType(2);
            ttdeyeSpu.setSpuAttributesType(1);
            ttdeyeSpu.setSpuNo(SnowflakeIdWorker.generateIdStr());
            ttdeyeSpu.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
            //保存SPU信息
            ttdeyeSpuMapper.insert(ttdeyeSpu);
        }

        //SPU保存完成，保存上传文件记录
        String url = ossClientUtils.uploadImg2Oss(multipartFile);
        TtdeyeFileLog ttdeyeFileLog = new TtdeyeFileLog();
        ttdeyeFileLog.setFileUrl(url);
        ttdeyeFileLog.setFileType(1);
        ttdeyeFileLog.setCreateTime(new Date());
        ttdeyeFileLog.setCreateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeFileLogMapper.insert(ttdeyeFileLog);

        return ApiResponseT.ok();
    }
}
