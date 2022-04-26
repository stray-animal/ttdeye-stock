package com.ttdeye.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.GoodsInfoDto;
import com.ttdeye.stock.domain.dto.req.GoodsListReq;
import com.ttdeye.stock.entity.TtdeyeSpu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ttdeye.stock.entity.TtdeyeUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 商品信息表 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeSpuService extends IService<TtdeyeSpu> {

    ApiResponseT spuImport(MultipartFile multipartFile, TtdeyeUser ttdeyeUser) throws Exception;

    /**
     * 查询商品列表
     * @param page
     * @param goodsListReq
     * @return
     */
    Page<GoodsInfoDto> selectGoodsInfoDtoListPage(Page page, GoodsListReq goodsListReq);
}
