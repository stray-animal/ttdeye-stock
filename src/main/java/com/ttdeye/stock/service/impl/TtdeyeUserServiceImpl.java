package com.ttdeye.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeUserMapper;
import com.ttdeye.stock.service.ITtdeyeUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Slf4j
@Service
public class TtdeyeUserServiceImpl extends ServiceImpl<TtdeyeUserMapper, TtdeyeUser> implements ITtdeyeUserService {


    @Autowired
    private TtdeyeUserMapper ttdeyeUserMapper;

    public Page<TtdeyeUser> selectUserPage(Page<TtdeyeUser> page, TtdeyeUser ttdeyeUser) {
//        page.addOrder(OrderItem.desc("create_time"));
        Page<TtdeyeUser> ttdeyeUserPage =  ttdeyeUserMapper.selectPage(page, Wrappers.<TtdeyeUser>lambdaQuery()
                        .eq(TtdeyeUser::getAdminFlag,0)
                .eq(ttdeyeUser.getPhone() != null, TtdeyeUser::getPhone, ttdeyeUser.getPhone())
                .like(ttdeyeUser.getNickName() != null, TtdeyeUser::getNickName, ttdeyeUser.getNickName())
                .orderByDesc(TtdeyeUser::getCreateTime));

        log.info("总条数 -------------> {}", ttdeyeUserPage.getTotal());
        log.info("当前页数 -------------> {}", ttdeyeUserPage.getCurrent());
        log.info("当前每页显示数 -------------> {}", ttdeyeUserPage.getSize());

        return ttdeyeUserPage;
    }

}
