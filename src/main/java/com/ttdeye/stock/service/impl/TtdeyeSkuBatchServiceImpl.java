package com.ttdeye.stock.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ttdeye.stock.domain.dto.TtdeyeSkuBatchDto;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeSkuBatch;
import com.ttdeye.stock.mapper.TtdeyeSkuBatchMapper;
import com.ttdeye.stock.mapper.TtdeyeSkuMapper;
import com.ttdeye.stock.service.ITtdeyeSkuBatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * sku库存批次表 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Service
public class TtdeyeSkuBatchServiceImpl extends ServiceImpl<TtdeyeSkuBatchMapper, TtdeyeSkuBatch> implements ITtdeyeSkuBatchService {


    @Autowired
    private TtdeyeSkuBatchMapper ttdeyeSkuBatchMapper;

    @Autowired
    private TtdeyeSkuMapper ttdeyeSkuMapper;

    /**
     * 根据批次ID查询库存明细
     * @param batchId
     * @return
     */
    public List<TtdeyeSkuBatchDto> selectTtdeyeSkuBatchDtoByBatchId(Long batchId){



        List<TtdeyeSkuBatch> ttdeyeSkuBatchList = ttdeyeSkuBatchMapper.selectList(Wrappers.<TtdeyeSkuBatch>lambdaQuery()
                .eq(TtdeyeSkuBatch::getBatchId,batchId)
                .orderByDesc(TtdeyeSkuBatch::getCreateTime)
        );

        List<TtdeyeSkuBatchDto> ttdeyeSkuBatchDtoList = Lists.newArrayList();
        for (TtdeyeSkuBatch ttdeyeSkuBatch : ttdeyeSkuBatchList) {
            TtdeyeSkuBatchDto ttdeyeSkuBatchDto = new TtdeyeSkuBatchDto();
            BeanUtils.copyProperties(ttdeyeSkuBatch,ttdeyeSkuBatchDto);
            TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectOne(
                    Wrappers.<TtdeyeSku>lambdaQuery().eq(TtdeyeSku::getSkuId,ttdeyeSkuBatch.getSkuId())
            );
            ttdeyeSkuBatchDto.setSkuCode(ttdeyeSku.getSkuCode());
            ttdeyeSkuBatchDto.setSkuName(ttdeyeSku.getSkuName());
            ttdeyeSkuBatchDtoList.add(ttdeyeSkuBatchDto);
        }
        return ttdeyeSkuBatchDtoList;
    }


    /**
     * 根据SKU ID查询库存明细
     * @param skuId
     * @return
     */
    public List<TtdeyeSkuBatchDto> selectTtdeyeSkuBatchDtoBySkuId(Long skuId){

        List<TtdeyeSkuBatch> ttdeyeSkuBatchList = ttdeyeSkuBatchMapper.selectList(Wrappers.<TtdeyeSkuBatch>lambdaQuery()
                .eq(TtdeyeSkuBatch::getSkuId,skuId)
                .orderByDesc(TtdeyeSkuBatch::getCreateTime)
        );

        List<TtdeyeSkuBatchDto> ttdeyeSkuBatchDtoList = Lists.newArrayList();
        for (TtdeyeSkuBatch ttdeyeSkuBatch : ttdeyeSkuBatchList) {
            TtdeyeSkuBatchDto ttdeyeSkuBatchDto = new TtdeyeSkuBatchDto();
            BeanUtils.copyProperties(ttdeyeSkuBatch,ttdeyeSkuBatchDto);
            TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectOne(
                    Wrappers.<TtdeyeSku>lambdaQuery().eq(TtdeyeSku::getSkuId,ttdeyeSkuBatch.getSkuId())
            );
            ttdeyeSkuBatchDto.setSkuCode(ttdeyeSku.getSkuCode());
            ttdeyeSkuBatchDto.setSkuName(ttdeyeSku.getSkuName());
            ttdeyeSkuBatchDtoList.add(ttdeyeSkuBatchDto);
        }
        return ttdeyeSkuBatchDtoList;
    }





}
