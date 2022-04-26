package com.ttdeye.stock.common.config;

import com.ttdeye.stock.common.domain.ApiResponseCode;
import com.ttdeye.stock.common.domain.GlobalBusinessConstant;
import com.ttdeye.stock.common.exception.ApiException;
import com.ttdeye.stock.common.utils.JacksonUtil;
import com.ttdeye.stock.common.utils.RedisTemplateUtil;
import com.ttdeye.stock.entity.TtdeyeUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Comment: 登陆拦截器
 * @Author: Zhangyongming
 * @Date:
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    @Autowired
    private RedisTemplateUtil redisTemplateUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从header获取X-Auth-Token的值
        String xAuthToken = checkAndGetXAuthTokenValue(request);
        try {
            //根据token获取参数
            //WePetUserDto wePetUserDto = (WePetUserDto) jedisUtils.get(xAuthToken);
            /**
             * 从redis取出json序列化的对象，然后转换成wepPetUserDto对象
             * */
            Object userObject = redisTemplateUtils.get(xAuthToken);
            String userAsString  = JacksonUtil.toJsonString(userObject);
            TtdeyeUser ttdeyeUser = JacksonUtil.readValue(userAsString,TtdeyeUser.class);
            if(ttdeyeUser == null || StringUtils.isEmpty(ttdeyeUser.getLoginAccount())){
                //表示登陆失效
                throw new ApiException(ApiResponseCode.LOGIN_EXPIRED.code, ApiResponseCode.LOGIN_EXPIRED.message);
            }
            //重置缓存的失效时间
            redisTemplateUtils.set(xAuthToken, ttdeyeUser, GlobalBusinessConstant.EXPIRE_TIMES.WEEKS_ONE);
        } catch (Exception e) {
            log.error("权限过滤器发生异常：{}",e.getMessage());
            throw new ApiException(ApiResponseCode.COMMON_FAILED_CODE.code, e.getMessage());
        }
        return true;
    }

    private String checkAndGetXAuthTokenValue(HttpServletRequest request) {

        String xAuthToken = request.getHeader("X-Auth-Token");
        log.info("=====>X-Auth-Token:{}", xAuthToken);
        if (StringUtils.isEmpty(xAuthToken)) {
            //表示没登录
            throw new ApiException(ApiResponseCode.LOGIN_EXPIRED.code, ApiResponseCode.LOGIN_EXPIRED.message);
        }
        return xAuthToken;
    }

}
