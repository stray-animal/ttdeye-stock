package com.ttdeye.stock.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



/**
 * @Comment: 权限拦截器
 * @Author: Zhangyongming
 * @Date: 20191017 18:26$
 */
@Configuration
public class ApiSecurityConfig implements WebMvcConfigurer {

    @Bean
    LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    /**
     * 拦截请求
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //注册登录拦截器
        registry.addInterceptor(loginInterceptor())
                //添加需要拦截的路径
                .addPathPatterns("/**")
                //添加不需要拦截的路径
                .excludePathPatterns("/test/**")
                .excludePathPatterns("/ttdeye-user/login")
                .excludePathPatterns("/user/get-login/v1")
        ;
    }


}
