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
 * 通知记录
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Getter
@Setter
@TableName("ttdeye_notify_record")
public class TtdeyeNotifyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知记录id
     */
      @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 通知类型：1-订单通知接收，2-库存变动回调通知
     */
    private Integer notifyType;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 处理结果：1-成功，0-失败
     */
    private Integer result;

    /**
     * 通知内容JSON
     */
    private String notifyDesc;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
