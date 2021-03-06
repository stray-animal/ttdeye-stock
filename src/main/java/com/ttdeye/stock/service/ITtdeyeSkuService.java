package com.ttdeye.stock.service;

import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.poi.SkuExportDto;
import com.ttdeye.stock.domain.dto.req.SkuExportReq;
import com.ttdeye.stock.domain.dto.req.SkuOutOfStockReq;
import com.ttdeye.stock.domain.dto.req.SkuWarehousingReq;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ttdeye.stock.entity.TtdeyeUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 产品信息表SKU 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeSkuService extends IService<TtdeyeSku> {

    ApiResponseT spuImport(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception;



    List<SkuExportDto> selectExportData(SkuExportReq skuExportReq);

    ApiResponseT skuWarehousing(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception;

    ApiResponseT skuOutOfStock(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception;

    void skuOperaWarehousing(SkuWarehousingReq skuWarehousingReq, TtdeyeUser ttdeyeUser);

    void skuOperaOutOfStock(SkuOutOfStockReq skuOutOfStockReq, TtdeyeUser ttdeyeUser);
}
