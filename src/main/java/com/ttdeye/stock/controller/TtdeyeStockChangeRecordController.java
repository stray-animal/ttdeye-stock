package com.ttdeye.stock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.TtdeyeStockChangeRecordDto;
import com.ttdeye.stock.domain.dto.req.TtdeyeStockChangeRecordReq;
import com.ttdeye.stock.service.ITtdeyeStockChangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ttdeye.stock.common.base.controller.BaseController;

import java.util.List;

/**
 *
 * 库存变更记录
 *
 * @author 张永明
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/ttdeye-stock-change-record")
public class TtdeyeStockChangeRecordController extends BaseController {

    @Autowired
    private ITtdeyeStockChangeRecordService iTtdeyeStockChangeRecordService;


    /**
     * 查询库存变动日志
     * @param ttdeyeStockChangeRecordReq
     * @return
     */
    @PostMapping(value = "selectStockChangeRecord")
    public ApiResponseT<Page<TtdeyeStockChangeRecordDto>> selectStockChangeRecord(@RequestBody TtdeyeStockChangeRecordReq ttdeyeStockChangeRecordReq){
        Page page = getPage();

        Page<TtdeyeStockChangeRecordDto>  ttdeyeStockChangeRecordDtoPage = iTtdeyeStockChangeRecordService.selectStockChangeRecord(ttdeyeStockChangeRecordReq,page);

        return ApiResponseT.ok(ttdeyeStockChangeRecordDtoPage);
    }








}
