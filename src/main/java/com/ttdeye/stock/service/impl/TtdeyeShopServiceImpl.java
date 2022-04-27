package com.ttdeye.stock.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttdeye.stock.entity.TtdeyeShop;
import com.ttdeye.stock.mapper.TtdeyeShopMapper;
import com.ttdeye.stock.service.ITtdeyeShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Service
public class TtdeyeShopServiceImpl extends ServiceImpl<TtdeyeShopMapper, TtdeyeShop> implements ITtdeyeShopService {

    @Autowired
    private TtdeyeShopMapper ttdeyeShopMapper;
    public Page<TtdeyeShop> selectUserPage(Page page, TtdeyeShop ttdeyeShop){

        Page<TtdeyeShop> ttdeyeShopPage =   ttdeyeShopMapper.selectPage(page, Wrappers.<TtdeyeShop>lambdaQuery()
                .like(ttdeyeShop.getShopName() != null,TtdeyeShop::getShopName,ttdeyeShop.getShopName())
                        .like(ttdeyeShop.getPlatform() != null,TtdeyeShop::getPlatform,ttdeyeShop.getPlatform())
                );

        return ttdeyeShopPage;
    }
}
