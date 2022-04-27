package com.ttdeye.stock.domain.dto.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.ttdeye.stock.domain.dto.poi.SkuOutOfStockDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/***
 **@author 张永明
 **@date 2022/4/27 14:07
 ***/
public class SkuOutOfStockReq extends SkuOutOfStockDto implements Serializable {


    /**
     * SKU ID
     */
    @Excel(name = "SKU id")
    @NotNull
    private String skuId;

    /**
     * 库存-实时库存
     */
    @Excel(name = "出库数量")
    @NotNull
    private Long stockNum;


    public SkuOutOfStockReq() {
    }

    public SkuOutOfStockReq(String skuId, Long stockNum) {
        this.skuId = skuId;
        this.stockNum = stockNum;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Long getStockNum() {
        return stockNum;
    }

    public void setStockNum(Long stockNum) {
        this.stockNum = stockNum;
    }
}
