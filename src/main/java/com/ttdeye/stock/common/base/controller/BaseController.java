package com.ttdeye.stock.common.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttdeye.stock.common.domain.ApiResponseCode;
import com.ttdeye.stock.common.exception.ApiException;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.RedisTemplateUtil;
import com.ttdeye.stock.entity.TtdeyeUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class BaseController {

    @Autowired
    private RedisTemplateUtil redisTemplateUtils;


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    public TtdeyeUser getTtdeyeUser(){
        try {
            String xAuthToken = request.getHeader("X-Auth-Token");
            Object userObject = redisTemplateUtils.get(xAuthToken);
            String userAsString = JacksonUtil.toJsonString(userObject);
            log.info("当前登陆用户信息---：{}",userAsString);
            TtdeyeUser ttdeyeUser = JacksonUtil.readValue(userAsString, TtdeyeUser.class);
            if(ttdeyeUser == null || StringUtils.isEmpty(ttdeyeUser.getLoginAccount())){
                //表示登陆失效
                throw new ApiException(ApiResponseCode.LOGIN_EXPIRED.code, ApiResponseCode.LOGIN_EXPIRED.message);
            }
            return ttdeyeUser;
        }catch (Exception e) {
            log.error("获取用户发生异常：{}",e.getMessage());
            throw new ApiException(ApiResponseCode.COMMON_FAILED_CODE.code, e.getMessage());
        }
    }


    public Page<T> getPage(){
        String currentStr = request.getParameter("current");
        String sizeStr = request.getParameter("size");
        Long current = 1L;
        Long size = 20L;
        if(!StringUtils.isEmpty(currentStr)){
            current =  Long.valueOf(currentStr);
        }
        if(!StringUtils.isEmpty(sizeStr)){
            size = Long.valueOf(sizeStr);
        }
        return new Page<T>(current,size);
    }


}
