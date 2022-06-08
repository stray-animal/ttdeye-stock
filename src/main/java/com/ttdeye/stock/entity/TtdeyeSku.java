package com.ttdeye.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 产品信息表SKU
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Data
@TableName("ttdeye_sku")
public class TtdeyeSku implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * skuId，主键id
     */
      @TableId(value = "sku_id", type = IdType.AUTO)
    private Long skuId;

    /**
     * SKU代码
     */
    private String skuCode;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * 库存-实时库存
     */
    private Long stockCurrentNum;

    /**
     * 总计入库数量
     */
    private Long stockAllNum;

    /**
     * 总计出库数量
     */
    private Long stockOutNum;

    /**
     * 采购单价
     */
    private BigDecimal purchasePrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 度数
     */
    private String degree;

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
     * 1-手动添加；2-文件导入
     */
    private Integer sourceType;

    /**
     * 状态：1-在售，2-已下架，3-缺货下架
     */
    private Integer state;

    /**
     * spuId
     */
    private Long spuId;

    /**
     * spu编码
     */
    private String spuNo;

    /**
     * 是否删除：1-删除，0-未删除
     */
    private Integer deleteFlag;

    /**
     * SKU编号
     */
    private String skuNo;


}
