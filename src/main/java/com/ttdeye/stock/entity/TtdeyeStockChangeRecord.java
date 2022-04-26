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
 * 库存变更记录
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Getter
@Setter
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
    private String skuCode;

    /**
     * 批次id：无批次则为null
     */
    private Integer batchId;

    private String batchNo;

    /**
     * sku原库存
     */
    private Integer skuBeforeStock;

    /**
     * sku变更后库存
     */
    private Integer skuAfterStock;

    /**
     * sku批次原库存
     */
    private Integer skuBatchBeforeStock;

    /**
     * sku批次现库存
     */
    private Integer skuBatchAfterStock;

    /**
     * 出入库数量：发生量
     */
    private Integer occurStock;

    /**
     * 方向：1-入库，0-出库
     */
    private Integer direction;

    /**
     * 是否批次出库：1-是，0-否
     */
    private Integer batchFlag;

    /**
     * 来源：1-人工操作，2-批量导入，3-订单
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
    private String spuCode;

    /**
     * 导入文件的id
     */
    private Long fileId;

    /**
     * 订单-售卖店铺名称
     */
    private String shopName;


}
