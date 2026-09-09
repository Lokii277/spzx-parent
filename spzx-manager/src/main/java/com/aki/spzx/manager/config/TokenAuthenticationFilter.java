package com.aki.spzx.manager.config;

import cn.hutool.core.util.StrUtil;
import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.utils.AuthContextUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Token认证过滤器
 * 继承 OncePerRequestFilter：保证每个请求只执行一次过滤逻辑
 * 作用：从请求头中获取 token，验证其有效性，并把用户信息放入 Spring Security 上下文和自定义上下文中
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis 中存储登录用户信息的 key 前缀
    private static final String LOGIN_TOKEN_KEY_PREFIX = "user_login:";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 处理跨域预检请求（OPTIONS）
        // 浏览器在发送跨域请求前会先发 OPTIONS 请求，直接放行即可
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 从请求头中获取 token
        String token = request.getHeader("token");

        // 3. 如果 token 不为空，尝试从 Redis 中获取用户信息
        if (StrUtil.isNotEmpty(token)) {
            // Redis key = "user_login:" + token
            String userInfo = redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY_PREFIX + token);

            // 4. 如果 Redis 中存在该用户信息，说明 token 有效
            if (StrUtil.isNotEmpty(userInfo)) {
                // 把 JSON 字符串反序列化成 SysUser 对象
                SysUser sysUser = JSON.parseObject(userInfo, SysUser.class);

                // 5. 把用户信息放入自定义线程上下文（方便业务代码直接获取当前登录用户）
                AuthContextUtil.set(sysUser);

                // 6. 刷新 token 过期时间（滑动过期：每次请求成功后重新设置 30 分钟有效期）
                redisTemplate.expire(LOGIN_TOKEN_KEY_PREFIX + token, 30, TimeUnit.MINUTES);

                // 7. 创建 Spring Security 的认证对象
                // 第三个参数是权限列表，这里暂时给空列表（后续可根据用户角色填充）
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(sysUser, null, new ArrayList<>());

                // 8. 把认证对象放入 SecurityContextHolder（Spring Security 的核心上下文）
                // 后续 Spring Security 会认为当前请求已经认证成功
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 注意：如果 token 无效（Redis 中没有），这里不做任何处理，
            // 后续会由 Spring Security 的其他机制（如异常处理或权限校验）来拦截未认证请求
        }

        // 9. 无论是否认证成功，都继续执行后续过滤器链
        // 认证失败的请求会在后续的权限校验环节被拦截
        filterChain.doFilter(request, response);
    }
}