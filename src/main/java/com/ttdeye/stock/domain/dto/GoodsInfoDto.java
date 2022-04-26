package com.ttdeye.stock.domain.dto;

import com.ttdeye.stock.entity.TtdeyeSku;
import com.ttdeye.stock.entity.TtdeyeSpu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/***
 **@author 张永明
 **@date 2022/4/26 00:08
 ***/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoDto extends TtdeyeSpu {


    /**
     * 商品sku列表
     */
    private  List<TtdeyeSku> skuList;


}
