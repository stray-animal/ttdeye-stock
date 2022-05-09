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
 * 库存变更记录
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Data
@TableName("ttdeye_stock_change_record")
public class TtdeyeStockChangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 库存变更记录id
     */
      @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * sku编码
     */
    private String skuNo;

    /**
     * 批次id：无批次则为null
     */
    private Long batchId;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * sku原库存
     */
    private Long skuBeforeStock;

    /**
     * sku变更后库存
     */
    private Long skuAfterStock;

    /**
     * sku批次原库存
     */
    private Long skuBatchBeforeStock;

    /**
     * sku批次现库存
     */
    private Long skuBatchAfterStock;

    /**
     * 出入库数量：发生量
     */
    private Long occurStock;

    /**
     * 方向：1-入库，0-出库
     */
    private Integer direction;

    /**
     * 是否批次出库：1-是，0-否
     */
    private Integer batchFlag;

    /**
     * 来源：1-批量导入SKU，2-采购入库导入，3-批量出库，4，手动入库，5，手动出库
     */
    private Integer sourceType;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单商品明细：json
     */
    private String orderDesc;

    /**
     * 如果是因为批量导入，则将文件直接存储到记录中
     */
    private String fileUrl;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人登陆账号
     */
    private String createLoginAccount;

    /**
     * 创建人姓名
     */
    private String createNikeName;

    /**
     * 是否删除：1-已删除，0-未删除
     */
    private Integer deleteFlag;

    /**
     * 商品id
     */
    private Long spuId;

    /**
     * 商品编码
     */
    private String spuNo;

    /**
     * 订单-售卖店铺名称
     */
    private String shopName;

    /**
     * sku批次库存编号
     */
    private String skuBatchNo;

    /**
     * sku库存主键
     */
    private Long skuBatchId;

    /**
     * 入库单价
     */
    private BigDecimal unitPrice;

}
