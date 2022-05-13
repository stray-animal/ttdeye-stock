package com.ttdeye.stock.domain.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/***
 **@author 张永明
 **@date 2022/5/13 19:49
 ***/

@Data
public class TtdeyeStockChangeRecordReq implements Serializable {



    /**
     * SKU代码
     */
    private String skuCode;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * 批次号
     */
    private String batchNo;


    /**
     * 开始时间（>=）
     */
    private Date startTime;

    /**
     * 结束时间（<）
     */
    private Date endTime;




}
