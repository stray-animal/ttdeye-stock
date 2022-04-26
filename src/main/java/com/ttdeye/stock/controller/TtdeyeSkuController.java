package com.ttdeye.stock.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.domain.dto.poi.SkuExportDto;
import com.ttdeye.stock.domain.dto.req.SkuExportReq;
import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeUser;
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



}
