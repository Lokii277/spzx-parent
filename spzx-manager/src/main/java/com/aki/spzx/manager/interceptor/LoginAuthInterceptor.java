package com.aki.spzx.manager.interceptor;

import cn.hutool.core.util.StrUtil;
import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.model.vo.common.Result;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import com.aki.spzx.utils.AuthContextUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class LoginAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOGIN_TOKEN_KEY_PREFIX = "user_login:";

    //在所有请求开始之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求方式
        String method = request.getMethod();
        //判断请求类型，如果请求方式是options是预检请求则放行
        if ("OPTIONS".equals(method)) {
            return true;
        }
        //从请求头获取token
        String token = request.getHeader("token");
        //判断token
        if (StrUtil.isEmpty(token)) {
            responseUnLogin(response);
            return false;
        }
        //如果token不为空，则拿token查询redis
        String userInfo = redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY_PREFIX + token);
        //判断查询结果
        if (StrUtil.isEmpty(userInfo)) {
            responseUnLogin(response);
            return false;
        }
        //查询到用户信息，把信息放到ThreadLocal中
        SysUser sysUser = JSON.parseObject(userInfo, SysUser.class);
        AuthContextUtil.set(sysUser);
        //把redis用户信息数据更新过期时间
        redisTemplate.expire(LOGIN_TOKEN_KEY_PREFIX + token,30, TimeUnit.MINUTES);
        //放行
        return true;
    }

    //在所有方法完成之后执行  删除ThreadLocal中的数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        AuthContextUtil.remove();
    }

    //用户未登录
    private void responseUnLogin(HttpServletResponse response) throws Exception {
        Result<Object> result = Result.build(null, ResultCodeEnum.LOGIN_AUTH);
        //向请求中写入用户未登录
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSON(result));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
