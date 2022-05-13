package com.ttdeye.stock.domain.dto;

import com.ttdeye.stock.entity.TtdeyeStockChangeRecord;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/***
 **@author 张永明
 **@date 2022/5/13 19:44
 ***/
@Data
public class TtdeyeStockChangeRecordDto extends TtdeyeStockChangeRecord implements Serializable {


    /**
     * SKU代码
     */
    private String skuCode;

    /**
     * sku名称
     */
    private String skuName;


    /**
     * 生产日期
     */
    private Date productionDate;




}
