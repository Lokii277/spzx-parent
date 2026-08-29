package com.aki.spzx.manager.config;


import com.aki.spzx.manager.interceptor.LoginAuthInterceptor;
import com.aki.spzx.manager.properties.UserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//解决跨域问题的配置类
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginAuthInterceptor loginAuthInterceptor;

    @Autowired
    private UserProperties userProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001", "http://127.0.0.1:3001")  //允许跨域访问
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);  //允许携带凭证
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginAuthInterceptor)
                .addPathPatterns("/**")  //不需要拦截的请求
                .excludePathPatterns(userProperties.getNoAuthUrls()); //需要拦截的请求 全部请求
    }
}
