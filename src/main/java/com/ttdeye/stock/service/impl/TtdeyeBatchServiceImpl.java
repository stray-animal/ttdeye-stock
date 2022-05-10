package com.ttdeye.stock.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.entity.TtdeyeBatch;
import com.ttdeye.stock.mapper.TtdeyeBatchMapper;
import com.ttdeye.stock.service.ITtdeyeBatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 批次信息 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Service
public class TtdeyeBatchServiceImpl extends ServiceImpl<TtdeyeBatchMapper, TtdeyeBatch> implements ITtdeyeBatchService {


    @Autowired
    private TtdeyeBatchMapper ttdeyeBatchMapper;

    /**
     * 分页查询批次信息
     * @param page
     * @param ttdeyeBatch
     * @return
     */
    public Page<TtdeyeBatch> selectListPage(Page page, TtdeyeBatch ttdeyeBatch){
        Page<TtdeyeBatch> ttdeyeBatchPage =  ttdeyeBatchMapper.selectPage(page, Wrappers.<TtdeyeBatch>lambdaQuery()
                .eq(!StringUtils.isEmpty(ttdeyeBatch.getBatchNo()),TtdeyeBatch::getBatchNo,ttdeyeBatch.getBatchNo()));
        return ttdeyeBatchPage;
    }
}
