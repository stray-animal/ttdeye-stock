package com.ttdeye.stock.domain.dto;

import com.ttdeye.stock.entity.TtdeyeSkuBatch;
import lombok.Data;

import java.io.Serializable;

/***
 **@author 张永明
 **@date 2022/5/13 19:25
 ***/
@Data
public class TtdeyeSkuBatchDto extends TtdeyeSkuBatch implements Serializable {

    /**
     * SKU代码
     */
    private String skuCode;

    /**
     * sku名称
     */
    private String skuName;

}
