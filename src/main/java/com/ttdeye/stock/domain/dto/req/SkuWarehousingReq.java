package com.ttdeye.stock.domain.dto.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.ttdeye.stock.domain.dto.poi.SkuWarehousingDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/***
 **@author 张永明
 **@date 2022/4/27 10:33
 ***/
public class SkuWarehousingReq extends SkuWarehousingDto implements Serializable {


    /**
     * SKUid
     */
    @Excel(name = "SKU id")
    @NotNull
    private Long skuId;




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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public SkuWarehousingReq(Long skuId, Long stockNum, BigDecimal unitPrice, String batchNo) {
        this.skuId = skuId;
        this.stockNum = stockNum;
        this.unitPrice = unitPrice;
        this.batchNo = batchNo;
    }

    public SkuWarehousingReq() {
    }

    @Override
    public String toString() {
        return "SkuWarehousingReq{" +
                "skuId=" + skuId +
                ", stockNum=" + stockNum +
                ", unitPrice=" + unitPrice +
                ", batchNo='" + batchNo + '\'' +
                '}';
    }
}
