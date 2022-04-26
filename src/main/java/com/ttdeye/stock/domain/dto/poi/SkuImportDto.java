package com.ttdeye.stock.domain.dto.poi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 产品信息表SKU
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */

public class SkuImportDto implements Serializable {

    /**
     * 商品代码
     */
    @Excel(name = "SPU代码")
    @NotNull
    private String spuCode;

    /**
     * 商品编号
     */
    @Excel(name = "SPU编号")
    private String spuNo;

    /**
     * SKU代码
     */
    @Excel(name = "SKU代码")
    @NotNull
    private String skuCode;

    /**
     * sku名称
     */
    @Excel(name = "sku名称")
    private String skuName;


    /**
     * 库存-实时库存
     */
    @Excel(name = "初始库存数量")
    private Long stockNum;


    /**
     * 批次编号
     */
    @Excel(name = "初始批次号")
    private String batchNo;


    @Excel(name = "度数")
    private BigDecimal degree;

    @Excel(name = "采购单价")
    private BigDecimal purchasePriceDecimal;


    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;


    public String getSpuNo() {
        return spuNo;
    }

    public void setSpuNo(String spuNo) {
        this.spuNo = spuNo;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Long getStockNum() {
        return stockNum;
    }

    public void setStockNum(Long stockNum) {
        this.stockNum = stockNum;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public BigDecimal getDegree() {
        return degree;
    }

    public void setDegree(BigDecimal degree) {
        this.degree = degree;
    }

    public BigDecimal getPurchasePriceDecimal() {
        return purchasePriceDecimal;
    }

    public void setPurchasePriceDecimal(BigDecimal purchasePriceDecimal) {
        this.purchasePriceDecimal = purchasePriceDecimal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
