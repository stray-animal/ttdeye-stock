package com.ttdeye.stock.service;

import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ttdeye.stock.entity.TtdeyeUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
