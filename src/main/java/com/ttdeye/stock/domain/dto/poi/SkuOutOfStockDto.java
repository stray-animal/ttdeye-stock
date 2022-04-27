package com.ttdeye.stock.domain.dto.poi;

import cn.afterturn.easypoi.excel.annotation.Excel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/***
 **@author 张永明
 **@date 2022/4/27 14:07
 ***/
public class SkuOutOfStockDto implements Serializable {


    /**
     * SKU代码
     */
    @Excel(name = "SKU代码")
    @NotNull
    private String skuCode;

    /**
     * 库存-实时库存
     */
    @Excel(name = "出库数量")
    @NotNull
    private Long stockNum;


    public SkuOutOfStockDto() {
    }

    public SkuOutOfStockDto(String skuCode, Long stockNum) {
        this.skuCode = skuCode;
        this.stockNum = stockNum;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Long getStockNum() {
        return stockNum;
    }

    public void setStockNum(Long stockNum) {
        this.stockNum = stockNum;
    }


    @Override
    public String toString() {
        return "SkuOutOfStockDto{" +
                "skuCode='" + skuCode + '\'' +
                ", stockNum=" + stockNum +
                '}';
    }
}
