package com.ttdeye.stock.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.TtdeyeSkuBatchDto;
import com.ttdeye.stock.domain.dto.poi.SkuExportDto;
import com.ttdeye.stock.domain.dto.req.SkuExportReq;
import com.ttdeye.stock.domain.dto.req.SkuOutOfStockReq;
import com.ttdeye.stock.domain.dto.req.SkuRackUpAndDownReq;
import com.ttdeye.stock.domain.dto.req.SkuWarehousingReq;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeSkuBatch;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeSkuBatchMapper;
import com.ttdeye.stock.service.ITtdeyeSkuBatchService;
import com.ttdeye.stock.service.ITtdeyeSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 产品信息(SKU)
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Slf4j
@RestController
@RequestMapping("/ttdeye-sku")
public class TtdeyeSkuController extends BaseController {


    @Autowired
    private ITtdeyeSkuService iTtdeyeSkuService;



    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @Autowired
    private ITtdeyeSkuBatchService iTtdeyeSkuBatchService;


    /**
     * 新增SKU
     * @param iTtdeyeSku
     * @return
     */
    @PostMapping(value = "add")
    public ApiResponseT<Long> saveSku(@RequestBody TtdeyeSku iTtdeyeSku){

        Long count =  iTtdeyeSkuService.count(Wrappers.<TtdeyeSku>lambdaQuery()
                .eq(TtdeyeSku::getDeleteFlag,0)
                .eq(TtdeyeSku::getSkuCode,iTtdeyeSku.getSkuCode()));
        //SKU代码是否重复
        if(count > 0){
            return ApiResponseT.failed("SKU代码库中已经存在，请改正后重新提交！");
        }

        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSku.setCreateTime(new Date());
        iTtdeyeSku.setUpdateTime(new Date());
        iTtdeyeSku.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        iTtdeyeSku.setSourceType(1);
        iTtdeyeSku.setState(1);
        iTtdeyeSkuService.save(iTtdeyeSku);
        return ApiResponseT.ok(iTtdeyeSku.getSkuId());
    }


    /**
     * 编辑SKU
     * @param iTtdeyeSku
     * @return
     */
    @PostMapping(value = "edit")
    public ApiResponseT editSku(@RequestBody TtdeyeSku iTtdeyeSku){

        Long count =  iTtdeyeSkuService.count(Wrappers.<TtdeyeSku>lambdaQuery()
                .eq(TtdeyeSku::getDeleteFlag,0)
                .eq(TtdeyeSku::getSkuCode,iTtdeyeSku.getSkuCode())
                .ne(TtdeyeSku::getSkuId,iTtdeyeSku.getSkuId())
            );
        //SKU代码是否重复
        if(count > 0){
            return ApiResponseT.failed("SKU代码库中已经存在，请改正后重新提交！");
        }

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
     * 删除 SKU
     * @param skuId
     * @return
     */
    @GetMapping(value = "deleteSku")
    public ApiResponseT deleteSku(Long skuId){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        TtdeyeSku ttdeyeSku = new TtdeyeSku();
        ttdeyeSku.setSkuId(skuId);
        ttdeyeSku.setUpdateTime(new Date());
        ttdeyeSku.setUpdateLoginAccount(ttdeyeUser.getLoginAccount());
        ttdeyeSku.setDeleteFlag(1);
        iTtdeyeSkuService.updateById(ttdeyeSku);
        return ApiResponseT.ok();
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



    /**
     * 手动入库
     * @return
     */
    @RequestMapping(value = "skuOperaWarehousing")
    public ApiResponseT skuOperaWarehousing(@RequestBody SkuWarehousingReq skuWarehousingReq){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSkuService.skuOperaWarehousing(skuWarehousingReq,ttdeyeUser);
        return ApiResponseT.ok();
    }



    /**
     * 导入模版进行采购入库
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/skuWarehousing")
    public ApiResponseT skuWarehousing(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        ApiResponseT apiResponseT = iTtdeyeSkuService.skuWarehousing(multipartFile,ttdeyeUser);
        return apiResponseT;
    }


    /**
     * 手动出库
     * @return
     */
    @PostMapping(value = "skuOperaOutOfStock")
    public ApiResponseT skuOperaOutOfStock(@RequestBody SkuOutOfStockReq skuOutOfStockReq){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSkuService.skuOperaOutOfStock(skuOutOfStockReq,ttdeyeUser);
        return ApiResponseT.ok();
    }




    /**
     * 导入模版批量出库
     * @param multipartFile
     * @return
     */
    @PostMapping(value = "skuOutOfStock")
    public ApiResponseT skuOutOfStock(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        ApiResponseT apiResponseT = iTtdeyeSkuService.skuOutOfStock(multipartFile,ttdeyeUser);
        return apiResponseT;
    }





    /**
     * SKU导出
     * @param skuExportReq
     */
    @PostMapping(value = "/exportSku")
    public void exportSku(SkuExportReq skuExportReq) throws IOException {
        List<SkuExportDto> skuExportDtoList = iTtdeyeSkuService.selectExportData(skuExportReq);

        if(CollectionUtils.isEmpty(skuExportDtoList)){
            return;
        }
        String fileName = "SKU_"+System.currentTimeMillis();
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), SkuExportDto.class, skuExportDtoList);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("error:{}",e);
            throw new IOException(e.getMessage());
        }
    }


    /**
     * 操作SKU上下架
     * @param skuRackUpAndDownReq
     * @return
     */
    @PostMapping(value = "skuRackUpAndDown")
    public ApiResponseT skuRackUpAndDown(@RequestBody SkuRackUpAndDownReq skuRackUpAndDownReq){
        TtdeyeSku ttdeyeSku = new TtdeyeSku();
        ttdeyeSku.setSkuId(skuRackUpAndDownReq.getSkuId());
        ttdeyeSku.setState(skuRackUpAndDownReq.getState());
        iTtdeyeSkuService.updateById(ttdeyeSku);
        return ApiResponseT.ok();
    }


    /**
     * 查询SKU库存明细
     * @param skuId
     * @return
     */
    @GetMapping(value = "skuStockDetail")
    public ApiResponseT<List<TtdeyeSkuBatchDto>> skuStockDetail(Long skuId){
        return ApiResponseT.ok(iTtdeyeSkuBatchService.selectTtdeyeSkuBatchDtoBySkuId(skuId));
    }


}
