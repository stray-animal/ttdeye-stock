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
 * 批次信息
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Getter
@Setter
@TableName("ttdeye_batch")
public class TtdeyeBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 批次id
     */
      @TableId(value = "batch_id", type = IdType.AUTO)
    private Long batchId;

    /**
     * 批次编号
     */
    private String batchNo;

    /**
     * 生产日期
     */
    private Date productionDate;

    /**
     * 保质期-天
     */
    private Integer shelfLife;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 是否删除：1-已删除，0-未删除
     */
    private Integer deleteFlag;


}
