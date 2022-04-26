package com.ttdeye.stock.controller;

import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * <p>
 * 产品信息表SKU 前端控制器
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/ttdeye-sku")
public class TtdeyeSkuController extends BaseController {



    @Autowired
    private ITtdeyeSkuService iTtdeyeSkuService;

    /**
     * 新增SKU
     * @param iTtdeyeSku
     * @return
     */
    @PostMapping(value = "add")
    public ApiResponseT saveUser(@RequestBody TtdeyeSku iTtdeyeSku){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSku.setCreateTime(new Date());
        iTtdeyeSku.setUpdateTime(new Date());
        iTtdeyeSku.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        iTtdeyeSku.setSourceType(1);
        iTtdeyeSkuService.save(iTtdeyeSku);
        return ApiResponseT.ok();
    }


    /**
     * 编辑SKU
     * @param iTtdeyeSku
     * @return
     */
    @PostMapping(value = "edit")
    public ApiResponseT editUser(@RequestBody TtdeyeSku iTtdeyeSku){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSku.setUpdateTime(new Date());
        iTtdeyeSku.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        iTtdeyeSkuService.updateById(iTtdeyeSku);
        return ApiResponseT.ok();
    }


    /**
     * 根据id查询SKU信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getById")
    public ApiResponseT<TtdeyeSku> getById(Long skuId){
        TtdeyeSku ttdeyeBatch = iTtdeyeSkuService.getById(skuId);
        return ApiResponseT.ok(ttdeyeBatch);
    }


    /**
     * 批量导入SKU
     */
    @PostMapping(value = "/skuImport")
    public ApiResponseT spuImport(@RequestParam("file") MultipartFile multipartFile) throws Exception {

        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        ApiResponseT apiResponseT =  iTtdeyeSkuService.spuImport(multipartFile,ttdeyeUser);
        return apiResponseT;
    }




}
