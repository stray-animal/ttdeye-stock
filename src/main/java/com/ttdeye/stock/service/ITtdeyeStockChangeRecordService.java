package com.ttdeye.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.domain.dto.TtdeyeStockChangeRecordDto;
import com.ttdeye.stock.domain.dto.req.TtdeyeStockChangeRecordReq;
import com.ttdeye.stock.entity.TtdeyeStockChangeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 库存变更记录 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeStockChangeRecordService extends IService<TtdeyeStockChangeRecord> {

    Page<TtdeyeStockChangeRecordDto> selectStockChangeRecord(TtdeyeStockChangeRecordReq ttdeyeStockChangeRecordReq, Page page);
}
