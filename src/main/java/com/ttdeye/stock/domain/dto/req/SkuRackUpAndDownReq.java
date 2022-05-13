package com.ttdeye.stock.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/***
 **@author 张永明
 **@date 2022/5/13 11:27
 ***/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuRackUpAndDownReq implements Serializable {


    /**
     * SKU ID
     */
    @NotNull
    private Long skuId;


    /**
     * 状态：1-在售，2-已下架，3-缺货下架
     */
    private Integer state;



}
