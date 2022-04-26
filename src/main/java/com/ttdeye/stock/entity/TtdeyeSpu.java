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
 * 商品信息表
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Getter
@Setter
@TableName("ttdeye_spu")
public class TtdeyeSpu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * spuId
     */
      @TableId(value = "spu_id", type = IdType.AUTO)
    private Long spuId;

    /**
     * 商品编码
     */
    private String spuCode;

    /**
     * 中文名称
     */
    private String titleCh;

    /**
     * 英文名称
     */
    private String titleEn;

    /**
     * 采购链接
     */
    private String purchaseUrl;

    /**
     * 1-普通货
     */
    private Integer spuAttributesType;

    /**
     * 电商平台：0-其他，1-shopfily
     */
    private Integer eCommercePlatform;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 更新人账号
     */
    private String updateLoginAccount;

    /**
     * 是否删除：1-删除，0-未删除
     */
    private Integer deleteFlag;

    /**
     * 来源：1-手动录入，2-文件导入
     */
    private Integer sourceType;

    /**
     * 是否支持批次：1-支持，0-不支持
     */
    private Integer batchFlag;

    /**
     * SPU编号
     */
    private String spuNo;


}
