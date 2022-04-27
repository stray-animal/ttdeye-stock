package com.ttdeye.stock.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.GoodsInfoDto;
import com.ttdeye.stock.domain.dto.req.GoodsListReq;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeSpu;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import com.ttdeye.stock.service.ITtdeyeSpuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 商品信息（SPU）
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Slf4j
@RestController
@RequestMapping("/ttdeye-spu")
public class TtdeyeSpuController extends BaseController {


    @Autowired
    private ITtdeyeSpuService iTtdeyeSpuService;

    @Autowired
    private ITtdeyeSkuService iTtdeyeSkuService;



    /**
     * 新增SPU
     * @param iTtdeyeSpu
     * @return
     */
    @PostMapping(value = "add")
    public ApiResponseT saveUser(@RequestBody TtdeyeSpu iTtdeyeSpu){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSpu.setCreateTime(new Date());
        iTtdeyeSpu.setUpdateTime(new Date());
        iTtdeyeSpu.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        iTtdeyeSpu.setSourceType(1);
        iTtdeyeSpuService.save(iTtdeyeSpu);
        return ApiResponseT.ok();
    }


    /**
     * 编辑SPU
     * @param iTtdeyeSpu
     * @return
     */
    @PostMapping(value = "edit")
    public ApiResponseT editSpu(@RequestBody TtdeyeSpu iTtdeyeSpu){

        iTtdeyeSpuService.editSpu(iTtdeyeSpu);
        return ApiResponseT.ok();
    }



    /**
     * 分页查询SPU列表
     * @param goodsListReq
     * @param current 当前页数
     * @Param size 每页条数
     * @return
     */
    @PostMapping(value = "getList")
    public ApiResponseT<Page<GoodsInfoDto>> getList(@RequestBody GoodsListReq goodsListReq){
        Page page = getPage();
        Page<GoodsInfoDto> result = iTtdeyeSpuService.selectGoodsInfoDtoListPage(page, goodsListReq);
        return ApiResponseT.ok(result);
    }


    /**
     * 根据id查询SPU信息
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getById")
    public ApiResponseT<TtdeyeSpu> getById(Long spuId){
        TtdeyeSpu ttdeyeBatch = iTtdeyeSpuService.getById(spuId);
        return ApiResponseT.ok(ttdeyeBatch);
    }


    /**
     * 批量导入SPU
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/spuImport")
    public ApiResponseT spuImport(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        ApiResponseT apiResponseT =  iTtdeyeSpuService.spuImport(multipartFile,ttdeyeUser);
        return apiResponseT;
    }


    /**
     * 删除Spu
     * @param spuId
     * @return
     */
    @GetMapping(value = "/deleteSpu")
    public ApiResponseT deleteSpu(Long spuId){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        //如果存在生效的sku，则不允许删除spu
       Long skuCount =  iTtdeyeSkuService.count(Wrappers.<TtdeyeSku>lambdaQuery()
                .eq(TtdeyeSku::getDeleteFlag,0)
               .eq(TtdeyeSku::getSpuId,spuId));
        if(skuCount > 0){
            return ApiResponseT.failed("SPU下有未删除的SKU，不允许删除！");
        }
        TtdeyeSpu ttdeyeSpu = new TtdeyeSpu();
        ttdeyeSpu.setSpuId(spuId);
        ttdeyeSpu.setDeleteFlag(1);
        ttdeyeSpu.setUpdateTime(new Date());
        ttdeyeSpu.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        iTtdeyeSpuService.updateById(ttdeyeSpu);
        return ApiResponseT.ok();
    }





}
