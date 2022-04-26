package com.ttdeye.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Getter
@Setter
@TableName("ttdeye_user")
public class TtdeyeUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户主键id
     */
      @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 登陆账号
     */
    private String loginAccount;

    /**
     * 密码
     */
    private String loginPassword;

    /**
     * 状态：1-启用，2-停用
     */
    private Integer state;

    /**
     * 是否删除：1-删除，0-未删除
     */
    private Integer deleteFlag;

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
    private String updateUserAccount;

    /**
     * 是否Admin：1-是，0-否
     */
    private Integer adminFlag;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户token
     */
    @TableField(exist = false)
    private String token;

}
