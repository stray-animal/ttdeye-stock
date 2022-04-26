package com.ttdeye.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeUserService extends IService<TtdeyeUser> {

    Page<TtdeyeUser> selectUserPage(Page<TtdeyeUser> page, TtdeyeUser ttdeyeUser);
}
