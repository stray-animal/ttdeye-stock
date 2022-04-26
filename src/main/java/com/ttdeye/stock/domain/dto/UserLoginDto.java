package com.ttdeye.stock.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 **@author 张永明
 **@date 2022/4/25 17:08
 ***/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    /**
     * 登陆用户名
     */
    private String loginAccount;

    /**
     * 登陆密码
     */
    private String loginPassword;


}
