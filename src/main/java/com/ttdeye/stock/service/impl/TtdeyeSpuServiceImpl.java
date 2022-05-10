package com.ttdeye.stock.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.domain.ApiResponseCode;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.exception.ApiException;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.common.utils.SnowflakeIdWorker;
import com.ttdeye.stock.domain.dto.GoodsInfoDto;
import com.ttdeye.stock.domain.dto.poi.SpuImportDto;
import com.ttdeye.stock.domain.dto.req.GoodsListReq;
import com.ttdeye.stock.entity.TtdeyeFileLog;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeSpu;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeFileLogMapper;
import com.ttdeye.stock.mapper.TtdeyeSkuMapper;
import com.ttdeye.stock.mapper.TtdeyeSpuMapper;
import com.ttdeye.stock.service.ITtdeyeFileLogService;
import com.ttdeye.stock.service.ITtdeyeSpuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
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

    @Autowired
    private ITtdeyeFileLogService iTtdeyeFileLogService;

    @Autowired
    private TtdeyeSkuMapper ttdeyeSkuMapper;

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

        //上传文件并保存记录
        iTtdeyeFileLogService.saveFile(multipartFile,1,ttdeyeUser.getLoginAccount());

        return ApiResponseT.ok();
    }



    /**
     * 查询商品列表
     * @param page
     * @param goodsListReq
     * @return
     */
    public Page<GoodsInfoDto> selectGoodsInfoDtoListPage(Page page, GoodsListReq goodsListReq){
        Page<TtdeyeSpu> ttdeyeSpuPage = ttdeyeSpuMapper.selectPage(page,Wrappers.<TtdeyeSpu>lambdaQuery()
                .like(!StringUtils.isEmpty(goodsListReq.getSpuCode() ),TtdeyeSpu::getSpuCode,goodsListReq.getSpuCode())
                .eq(goodsListReq.getECommercePlatform() != null,TtdeyeSpu::getECommercePlatform,goodsListReq.getECommercePlatform())
                .ge(goodsListReq.getStartTime() != null,TtdeyeSpu::getCreateTime,goodsListReq.getStartTime())
                .lt(goodsListReq.getEndTime() !=null,TtdeyeSpu::getCreateTime,goodsListReq.getEndTime())
                .eq(TtdeyeSpu::getDeleteFlag,0)
                .orderByDesc(TtdeyeSpu::getCreateTime)
        );
        Page<GoodsInfoDto> goodsInfoDtoPage = new Page<>();
        BeanUtils.copyProperties(ttdeyeSpuPage,goodsInfoDtoPage);
        List<TtdeyeSpu> ttdeyeSpus = ttdeyeSpuPage.getRecords();
        if(CollectionUtils.isEmpty(ttdeyeSpus)){
            return goodsInfoDtoPage;
        }

        List<GoodsInfoDto> goodsInfoDtos = Lists.newArrayList();
        for (TtdeyeSpu ttdeyeSpu : ttdeyeSpus) {
            List<TtdeyeSku> ttdeyeSkus = ttdeyeSkuMapper.selectList(Wrappers.<TtdeyeSku>lambdaQuery().eq(TtdeyeSku::getDeleteFlag,0)
                            .eq(TtdeyeSku::getSpuId,ttdeyeSpu.getSpuId())
                        );
            GoodsInfoDto goodsInfoDto = new GoodsInfoDto();
            BeanUtils.copyProperties(ttdeyeSpu,goodsInfoDto);
            goodsInfoDto.setSkuList(ttdeyeSkus);
            goodsInfoDtos.add(goodsInfoDto);
        }
        goodsInfoDtoPage.setRecords(goodsInfoDtos);
        return goodsInfoDtoPage;
    }


    /**
     * 修改SPU
     * @param iTtdeyeSpu
     * @return
     */
    public Integer editSpu(TtdeyeSpu iTtdeyeSpu){

        //查询旧的SPU信息
        TtdeyeSpu ttdeyeSpuOld = ttdeyeSpuMapper.selectById(iTtdeyeSpu.getSpuId());
        if(ttdeyeSpuOld.getBatchFlag() != iTtdeyeSpu.getBatchFlag()){
            //查询是否存在SKU
          Long count = ttdeyeSkuMapper.selectCount(Wrappers.<TtdeyeSku>lambdaQuery().eq(TtdeyeSku::getSpuId,iTtdeyeSpu.getSpuId()));
          if(count > 0){
              throw  new ApiException(ApiResponseCode.COMMON_FAILED_CODE,"SPU下已经存在SKU，不允许修改批次支持类型！");
          }
        }
        return ttdeyeSpuMapper.updateById(iTtdeyeSpu);

    }


}
