package com.ttdeye.stock.domain.dto.poi;

import cn.afterturn.easypoi.excel.annotation.Excel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/***
 **@author 张永明
 **@date 2022/4/27 10:33
 ***/
public class SkuWarehousingDto implements Serializable {


    /**
     * SKU代码
     */
    @Excel(name = "SKU代码")
    @NotNull
    private String skuCode;




    /**
     * 库存-实时库存
     */
    @Excel(name = "入库数量")
    @NotNull
    private Long stockNum;


    /**
     * 单价
     */
    @Excel(name = "单价")
    private BigDecimal unitPrice;


    /**
     * 批次号
     */
    @Excel(name = "批次号")
    private String batchNo;


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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public SkuWarehousingDto(String skuCode, Long stockNum, BigDecimal unitPrice, String batchNo) {
        this.skuCode = skuCode;
        this.stockNum = stockNum;
        this.unitPrice = unitPrice;
        this.batchNo = batchNo;
    }


    public SkuWarehousingDto() {
    }


    @Override
    public String toString() {
        return "SkuWarehousingDto{" +
                "skuCode='" + skuCode + '\'' +
                ", stockNum=" + stockNum +
                ", unitPrice=" + unitPrice +
                ", batchNo='" + batchNo + '\'' +
                '}';
    }
}
