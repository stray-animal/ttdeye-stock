package com.ttdeye.stock.domain.dto.poi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/***
 **@author 张永明
 **@date 2022/4/27 00:27
 ***/


public class SkuExportDto implements Serializable {

    /**
     * 商品代码
     */
    @Excel(name = "SPU代码",width = 25)
    private String spuCode;

    /**
     * 商品中文名称
     */
    @Excel(name = "商品中文名称",width = 20)
    private String titleCh;

    /**
     *  商品英文名称
     */
    @Excel(name = "商品英文名称" ,width = 25)
    private String titleEn;

    /**
     * 上架平台
     */
    @Excel(name = "上架平台",replace = {"Shopify_1", "其他_0"})
    private Integer eCommercePlatform;

    /**
     * SKU代码
     */
    @Excel(name = "SKU代码",width = 20)
    private String skuCode;

    /**
     * sku名称
     */
    @Excel(name = "sku标题",width = 25)
    private String skuName;

    /**
     * 采购单价
     */
    @Excel(name = "单价")
    private BigDecimal purchasePrice;


    /**
     * 库存-实时库存
     */
    @Excel(name = "库存")
    private Long stockCurrentNum;

    /**
     * 状态 ：1-在售，2-已下架，3-缺货下架
     */
    @Excel(name = "状态",replace = {"在售_1", "已下架_2","缺货下架_3"})
    private Integer state;

    public SkuExportDto(String spuCode, String titleCh, String titleEn, Integer eCommercePlatform, String skuCode, String skuName, BigDecimal purchasePrice, Long stockCurrentNum, Integer state) {
        this.spuCode = spuCode;
        this.titleCh = titleCh;
        this.titleEn = titleEn;
        this.eCommercePlatform = eCommercePlatform;
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.purchasePrice = purchasePrice;
        this.stockCurrentNum = stockCurrentNum;
        this.state = state;
    }

    public SkuExportDto() {
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public String getTitleCh() {
        return titleCh;
    }

    public void setTitleCh(String titleCh) {
        this.titleCh = titleCh;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public Integer geteCommercePlatform() {
        return eCommercePlatform;
    }

    public void seteCommercePlatform(Integer eCommercePlatform) {
        this.eCommercePlatform = eCommercePlatform;
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

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getStockCurrentNum() {
        return stockCurrentNum;
    }

    public void setStockCurrentNum(Long stockCurrentNum) {
        this.stockCurrentNum = stockCurrentNum;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SkuExportDto{" +
                "spuCode='" + spuCode + '\'' +
                ", titleCh='" + titleCh + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", eCommercePlatform=" + eCommercePlatform +
                ", skuCode='" + skuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", stockCurrentNum=" + stockCurrentNum +
                ", state=" + state +
                '}';
    }
}
