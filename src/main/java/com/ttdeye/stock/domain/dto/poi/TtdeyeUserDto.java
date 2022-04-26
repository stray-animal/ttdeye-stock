package com.ttdeye.stock.domain.dto.poi;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ttdeye.stock.entity.TtdeyeUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 **@author 张永明
 **@date 2022/4/26 22:09
 ***/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TtdeyeUserDto extends TtdeyeUser {

    /**
     * 用户token
     */
    @TableField(exist = false)
    private String token;



}
