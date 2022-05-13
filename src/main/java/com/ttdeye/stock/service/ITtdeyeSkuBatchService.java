package com.ttdeye.stock.service;

import com.ttdeye.stock.domain.dto.TtdeyeSkuBatchDto;
import com.ttdeye.stock.entity.TtdeyeSkuBatch;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * sku库存批次表 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeSkuBatchService extends IService<TtdeyeSkuBatch> {

    /**
     * 根据批次ID查询库存明细
     * @param batchId
     * @return
     */
     List<TtdeyeSkuBatchDto> selectTtdeyeSkuBatchDtoByBatchId(Long batchId);


    /**
     * 根据SKU ID查询库存明细
     * @param skuId
     * @return
     */
    public List<TtdeyeSkuBatchDto> selectTtdeyeSkuBatchDtoBySkuId(Long skuId);

}
