package com.ttdeye.stock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.entity.TtdeyeShop;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.service.ITtdeyeShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 *  商店管理 前端控制器
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/ttdeye-shop")
public class TtdeyeShopController extends BaseController {

    @Autowired
    private ITtdeyeShopService iTtdeyeShopService;

    /**
     * 新增店铺
     * @param ttdeyeShop
     * @return
     */
    @PostMapping(value = "add")
    public ApiResponseT saveUser(@RequestBody TtdeyeShop ttdeyeShop){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        ttdeyeShop.setCreateTime(new Date());
        iTtdeyeShopService.save(ttdeyeShop);
        return ApiResponseT.ok();
    }


    /**
     * 编辑店铺
     * @param ttdeyeShop
     * @return
     */
    @PostMapping(value = "edit")
    public ApiResponseT editUser(@RequestBody TtdeyeShop ttdeyeShop){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeShopService.updateById(ttdeyeShop);
        return ApiResponseT.ok();
    }



    /**
     * 分页查询店铺列表
     * @param ttdeyeShop
     * @param current 当前页数
     * @Param size 每页条数
     * @return
     */
    @PostMapping(value = "getList")
    public ApiResponseT<Page<TtdeyeShop>> getList(@RequestBody TtdeyeShop ttdeyeShop){
        Page page = getPage();
        Page<TtdeyeShop> result = iTtdeyeShopService.selectUserPage(page, ttdeyeShop);
        return ApiResponseT.ok(result);
    }


    /**
     * 根据id查询店铺信息
     * @param shopId
     * @return
     */
    @GetMapping(value = "/getById")
    public ApiResponseT<TtdeyeShop> getById(Long shopId){
        TtdeyeShop ttdeyeShop = iTtdeyeShopService.getById(shopId);
        return ApiResponseT.ok(ttdeyeShop);
    }






}
