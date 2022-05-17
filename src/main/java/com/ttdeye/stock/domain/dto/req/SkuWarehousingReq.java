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
    @NotNull
    private Long skuId;

    public SkuWarehousingReq(String skuCode, Long stockNum, BigDecimal unitPrice, String batchNo, Long skuId) {
        super(skuCode, stockNum, unitPrice, batchNo);
        this.skuId = skuId;
    }

    public SkuWarehousingReq(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "SkuWarehousingReq{" +
                "skuId=" + skuId +
                '}';
    }
}
