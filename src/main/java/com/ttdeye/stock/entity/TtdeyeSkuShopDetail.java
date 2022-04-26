package com.ttdeye.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * sku售卖商店明细
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Getter
@Setter
@TableName("ttdeye_sku_shop_detail")
public class TtdeyeSkuShopDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细id
     */
      @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    /**
     * 商店id
     */
    private Long shopId;

    /**
     * skuid
     */
    private Long skuId;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * spuId
     */
    private Long spuId;

    /**
     * spu编码
     */
    private String spuCode;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人编号
     */
    private String createLoginAccount;

    /**
     * 是否删除：1-已删除，0-未删除
     */
    private Integer deleteFlag;


}
