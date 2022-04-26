package com.ttdeye.stock.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/***
 **@author 张永明
 **@date 2022/4/27 00:21
 ***/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuExportReq implements Serializable {


    /**
     * SKU状态：状态：1-在售，2-已下架，3-缺货下架
     */
    private Integer state;

    /**
     * 库存区间开始（>=）
     */
    private Long startSum;

    /**
     * 库存区间结束（<）
     */
    private Long endSum;


}
