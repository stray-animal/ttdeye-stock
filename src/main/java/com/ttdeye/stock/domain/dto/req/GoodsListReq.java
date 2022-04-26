package com.ttdeye.stock.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/***
 **@author 张永明
 **@date 2022/4/26 00:12
 ***/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsListReq implements Serializable {

    /**
     * 商品代码
     */
    private String spuCode;

    /**
     * 售卖平台：0-其他，1-shopfily
     */
    private Integer eCommercePlatform;

    /**
     * 创建时间开始（>=）
     */
    private Date startTime;

    /**
     * 创建时间结束 (<)
     */
    private Date endTime;
}
