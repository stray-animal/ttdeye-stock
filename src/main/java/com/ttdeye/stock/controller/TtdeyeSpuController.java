package com.ttdeye.stock.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.domain.dto.GoodsInfoDto;
import com.ttdeye.stock.domain.dto.poi.SpuImportDto;
import com.ttdeye.stock.domain.dto.req.GoodsListReq;
import com.ttdeye.stock.entity.TtdeyeSpu;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.service.ITtdeyeSpuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ttdeye.stock.common.base.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品信息表 前端控制器
 * </p>
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
    private OSSClientUtils ossClientUtils;

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
        iTtdeyeSpu.setUpdateLoginName(ttdeyeUser.getLoginAccount());
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
    public ApiResponseT editUser(@RequestBody TtdeyeSpu iTtdeyeSpu){
        TtdeyeUser ttdeyeUser = getTtdeyeUser();
        iTtdeyeSpu.setUpdateTime(new Date());
        iTtdeyeSpu.setUpdateLoginName(ttdeyeUser.getLoginAccount());
        iTtdeyeSpuService.updateById(iTtdeyeSpu);
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
//        Page<GoodsInfoDto> result = iTtdeyeSpuService.selectListPage(page, goodsListReq);
        return ApiResponseT.ok(null);
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
     */
    @PostMapping(value = "/spuImport")
    public ApiResponseT spuImport(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        InputStream inputStream = multipartFile.getInputStream();

        String url = ossClientUtils.uploadImg2Oss(multipartFile);
        log.info("excel OSS url-{}",url);
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        List<SpuImportDto> spuImportDtoList = ExcelImportUtil.importExcel(inputStream, SpuImportDto.class,  params);

        log.info("spuImportDtoList----{}", JacksonUtil.toJsonString(spuImportDtoList));
        return null;
    }



}
