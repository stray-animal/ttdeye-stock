package com.ttdeye.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.entity.TtdeyeShop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeShopService extends IService<TtdeyeShop> {

    Page<TtdeyeShop> selectUserPage(Page page, TtdeyeShop ttdeyeShop);
}
