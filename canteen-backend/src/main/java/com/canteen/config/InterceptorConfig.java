package com.canteen.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/resetPass",
                        "/dish/list",
                        "/dish/info/**",
                        "/dish/hot",
                        "/foodCategory/list",
                        "/restaurant/list",
                        "/news/**",
                        "/banner/list",
                        "/coupon/list",
                        "/review/list/**",
                        "/uploads/**",
                        "/error"
                );
    }
}
