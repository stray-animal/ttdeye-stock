package com.ttdeye.stock.domain.dto.poi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/***
 **@author 张永明
 **@date 2022/4/26 00:45
 ***/
public class SpuImportDto implements Serializable {

    /**
     * 商品代码
     */
    @Excel(name = "SPU代码")
    @NotNull
    private String spuCode;


    /**
     * 中文名称
     */
    @Excel(name = "中文名称")
    @NotNull
    private String titleCh;

    /**
     * 英文名称
     */
    @Excel(name = "英文名称")
    @NotNull
    private String titleEn;

    /**
     * 采购链接
     */
    @Excel(name = "采购链接")
    private String purchaseUrl;

    /**
     * 电商平台
     */
    @Excel(name = "电商平台",replace = {"Shopify_1", "其他_0"})
    private Integer eCommercePlatform;

    /**
     * 是否支持批次：1-支持，0-不支持
     */
    @Excel(name = "是否分批次商品",replace = {"是_1", "否_0"})
    @NotNull
    private Integer batchFlag;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

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

    public String getPurchaseUrl() {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }

    public Integer geteCommercePlatform() {
        return eCommercePlatform;
    }

    public void seteCommercePlatform(Integer eCommercePlatform) {
        this.eCommercePlatform = eCommercePlatform;
    }

    public Integer getBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(Integer batchFlag) {
        this.batchFlag = batchFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
