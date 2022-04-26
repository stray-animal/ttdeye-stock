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
 * 文件记录
 * </p>
 *
 * @author 张永明
 * @since 2022-04-26
 */
@Getter
@Setter
@TableName("ttdeye_file_log")
public class TtdeyeFileLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 导入文件地址
     */
    private String fileUrl;

    /**
     * 文件类别：1-商品（spu）导入，2-产品（sku）导入，3-批量入库，4-批量出库
     */
    private Integer fileType;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 操作人账号
     */
    private String createLoginAccount;


}
