package com.ttdeye.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * sku库存批次表
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Data
@TableName("ttdeye_sku_batch")
public class TtdeyeSkuBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * sku库存批次表主键
     */
      @TableId(value = "sku_batch_id", type = IdType.AUTO)
    private Long skuBatchId;

    /**
     * skuid
     */
    private Long skuId;

    /**
     * sku编号
     */
    private String skuNo;

    /**
     * 批次id
     */
    private Long batchId;

    /**
     * 批次编号
     */
    private String batchNo;

    /**
     * 批次实时库存
     */
    private Long stockCurrentNum;

    /**
     * 批次总入库数量
     */
    private Long stockAllNum;

    /**
     * 批次总出库数量
     */
    private Long stockOutNum;

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
     * sku批次库存编号
     */
    private String skuBatchNo;


}
