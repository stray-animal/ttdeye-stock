package com.ttdeye.stock.mapper;

import com.ttdeye.stock.domain.dto.poi.SkuExportDto;
import com.ttdeye.stock.domain.dto.req.SkuExportReq;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 产品信息表SKU Mapper 接口
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Mapper
public interface TtdeyeSkuMapper extends BaseMapper<TtdeyeSku> {

    List<SkuExportDto> selectExportData(@Param("skuExportReq") SkuExportReq skuExportReq);
}
