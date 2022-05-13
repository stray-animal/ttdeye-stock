package com.ttdeye.stock.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.TtdeyeSkuBatchDto;
import com.ttdeye.stock.entity.TtdeyeBatch;
import com.ttdeye.stock.entity.TtdeyeSkuBatch;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeSkuBatchMapper;
import com.ttdeye.stock.service.ITtdeyeBatchService;
import com.ttdeye.stock.service.ITtdeyeSkuBatchService;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 批次信息
 *
 * @author 张永明
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/ttdeye-batch")
public class TtdeyeBatchController extends BaseController {

    @Autowired
    private ITtdeyeBatchService iTtdeyeBatchService;


    @Autowired
    private ITtdeyeSkuBatchService iTtdeyeSkuBatchService;


    /**
     * 新增批次
     * @param iTtdeyeBatch
     * @return
     */
    @PostMapping(value = "add")
    public ApiResponseT saveUser(@RequestBody TtdeyeBatch iTtdeyeBatch){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeBatch.setCreateTime(new Date());
        iTtdeyeBatchService.save(iTtdeyeBatch);
        return ApiResponseT.ok();
    }


    /**
     * 编辑批次
     * @param iTtdeyeBatch
     * @return
     */
    @PostMapping(value = "edit")
    public ApiResponseT editUser(@RequestBody TtdeyeBatch iTtdeyeBatch){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeBatchService.updateById(iTtdeyeBatch);
        return ApiResponseT.ok();
    }



    /**
     * 分页查询批次列表
     * @param ttdeyeBatch
     * @param current 当前页数
     * @Param size 每页条数
     * @return
     */
    @PostMapping(value = "getList")
    public ApiResponseT<Page<TtdeyeBatch>> getList(@RequestBody TtdeyeBatch ttdeyeBatch){
        Page page = getPage();
        Page<TtdeyeBatch> result = iTtdeyeBatchService.selectListPage(page, ttdeyeBatch);
        return ApiResponseT.ok(result);
    }


    /**
     * 根据id查询批次信息
     * @param batchId
     * @return
     */
    @GetMapping(value = "/getById")
    public ApiResponseT<TtdeyeBatch> getById(Long batchId){
        TtdeyeBatch ttdeyeBatch = iTtdeyeBatchService.getById(batchId);
        return ApiResponseT.ok(ttdeyeBatch);
    }



    /**
     * 查询批次库存明细
     * @param batchId
     * @return
     */
    @GetMapping(value = "batchStockDetail")
    public ApiResponseT<List<TtdeyeSkuBatchDto>> skuStockDetail(Long batchId){
        return ApiResponseT.ok(iTtdeyeSkuBatchService.selectTtdeyeSkuBatchDtoByBatchId(batchId));
    }




}
