package com.ttdeye.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.entity.TtdeyeBatch;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 批次信息 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeBatchService extends IService<TtdeyeBatch> {

    Page<TtdeyeBatch> selectListPage(Page page, TtdeyeBatch ttdeyeBatch);
}
