package com.ttdeye.stock.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.domain.dto.GoodsInfoDto;
import com.ttdeye.stock.domain.dto.TtdeyeStockChangeRecordDto;
import com.ttdeye.stock.domain.dto.req.TtdeyeStockChangeRecordReq;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeStockChangeRecord;
import com.ttdeye.stock.mapper.TtdeyeSkuMapper;
import com.ttdeye.stock.mapper.TtdeyeStockChangeRecordMapper;
import com.ttdeye.stock.service.ITtdeyeStockChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 库存变更记录 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Service
public class TtdeyeStockChangeRecordServiceImpl extends ServiceImpl<TtdeyeStockChangeRecordMapper, TtdeyeStockChangeRecord> implements ITtdeyeStockChangeRecordService {


    @Autowired
    private TtdeyeSkuMapper ttdeyeSkuMapper;

    @Autowired
    private TtdeyeStockChangeRecordMapper ttdeyeStockChangeRecordMapper;

    /**
     * 查询库存变动日志
     * @param ttdeyeStockChangeRecordReq
     * @param page
     * @return
     */
    @Override
    public Page<TtdeyeStockChangeRecordDto> selectStockChangeRecord(TtdeyeStockChangeRecordReq ttdeyeStockChangeRecordReq, Page page) {

        List<Long> skuIds = Lists.newArrayList();
        if(!StringUtils.isEmpty(ttdeyeStockChangeRecordReq.getSkuCode()) || !StringUtils.isEmpty(ttdeyeStockChangeRecordReq.getSkuName())){
            //查询SKU信息

            List<TtdeyeSku> ttdeyeSkus = ttdeyeSkuMapper.selectList(Wrappers.<TtdeyeSku>lambdaQuery()
                    .eq(!StringUtils.isEmpty(ttdeyeStockChangeRecordReq.getSkuCode()), TtdeyeSku::getSkuCode,ttdeyeStockChangeRecordReq.getSkuCode())
                    .like(!StringUtils.isEmpty(ttdeyeStockChangeRecordReq.getSkuName()),TtdeyeSku::getSkuName,ttdeyeStockChangeRecordReq.getSkuName())
            );

            skuIds = ttdeyeSkus.stream().map(TtdeyeSku::getSkuId).collect(Collectors.toList());
        }

        Page<TtdeyeStockChangeRecord>  ttdeyeStockChangeRecordPage = ttdeyeStockChangeRecordMapper.selectPage(page,
                Wrappers.<TtdeyeStockChangeRecord>lambdaQuery()
                        .eq(!StringUtils.isEmpty(ttdeyeStockChangeRecordReq.getBatchNo()), TtdeyeStockChangeRecord::getBatchNo,ttdeyeStockChangeRecordReq.getBatchNo())
                        .in(!CollectionUtils.isEmpty(skuIds),TtdeyeStockChangeRecord :: getSkuId,skuIds)
                        .ge(ttdeyeStockChangeRecordReq.getStartTime() != null ,TtdeyeStockChangeRecord::getCreateTime,ttdeyeStockChangeRecordReq.getStartTime())
                        .lt(ttdeyeStockChangeRecordReq.getEndTime() != null,TtdeyeStockChangeRecord :: getCreateTime,ttdeyeStockChangeRecordReq.getEndTime())
                );


        Page<TtdeyeStockChangeRecordDto> ttdeyeStockChangeRecordDtoPage = new Page<>();
        BeanUtils.copyProperties(ttdeyeStockChangeRecordPage,ttdeyeStockChangeRecordDtoPage);

        List<TtdeyeStockChangeRecord> ttdeyeStockChangeRecordList = ttdeyeStockChangeRecordPage.getRecords();

        List<TtdeyeStockChangeRecordDto> ttdeyeStockChangeRecordDtoList = Lists.newArrayList();
        for (TtdeyeStockChangeRecord ttdeyeStockChangeRecord : ttdeyeStockChangeRecordList) {
            TtdeyeStockChangeRecordDto ttdeyeStockChangeRecordDto = new TtdeyeStockChangeRecordDto();
            BeanUtils.copyProperties(ttdeyeStockChangeRecord,ttdeyeStockChangeRecordDto);

            TtdeyeSku ttdeyeSku = ttdeyeSkuMapper.selectOne(
                    Wrappers.<TtdeyeSku>lambdaQuery().eq(TtdeyeSku::getSkuId,ttdeyeStockChangeRecord.getSkuId())
            );
            ttdeyeStockChangeRecordDto.setSkuCode(ttdeyeSku.getSkuCode());
            ttdeyeStockChangeRecordDto.setSkuName(ttdeyeSku.getSkuName());

            ttdeyeStockChangeRecordDtoList.add(ttdeyeStockChangeRecordDto);
        }
        ttdeyeStockChangeRecordDtoPage.setRecords(ttdeyeStockChangeRecordDtoList);
        return ttdeyeStockChangeRecordDtoPage;
    }

}
