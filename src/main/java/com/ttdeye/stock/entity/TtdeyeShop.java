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
 * 商店管理
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Getter
@Setter
@TableName("ttdeye_shop")
public class TtdeyeShop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商店id
     */
      @TableId(value = "shop_id", type = IdType.AUTO)
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 回调地址
     */
    private String shopCallbackUrl;

    /**
     * 平台名称
     */
    private String platform;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 是否删除：1-是，0-否
     */
    private Integer deleteFlag;


}
