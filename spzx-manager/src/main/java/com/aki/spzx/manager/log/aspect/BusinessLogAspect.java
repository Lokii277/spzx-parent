package com.aki.spzx.manager.log.aspect;

import com.aki.spzx.manager.log.service.BusinessLogService;
import com.aki.spzx.model.entity.system.SysBusinessLog;
import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.utils.AuthContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class BusinessLogAspect {
    @Autowired
    private BusinessLogService businessLogService;
    // SpEL 解析器（可以复用）
    private final ExpressionParser parser = new SpelExpressionParser();

    // 拦截带有 @BusinessLog 注解的方法
    @Around("@annotation(businessLog)")
    public Object around(ProceedingJoinPoint joinPoint,BusinessLog businessLog) throws Throwable{
        System.out.println("===== AOP执行  =====");
        // 开始时间 用于计算方法执行时间
        long startTime = System.currentTimeMillis();
        // 获取方法签名
        MethodSignature signature =
                (MethodSignature) joinPoint.getSignature();
        // 从方法签名中获取方法参数名
        String[] parameterNames = signature.getParameterNames();
        // 获取方法参数值
        Object[] args = joinPoint.getArgs();
        // 组装方法参数名和参数值
        Map<String, Object> variables = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            variables.put(parameterNames[i], args[i]);
        }
        Object result = null;
        String errorMsg = null;
        Integer status = 1;      // 1成功 0失败
        try {
            // 先执行方法，避免某些Insert方法拿不到ID
            result = joinPoint.proceed();
        }catch (Exception e) {
            System.out.println("方法执行异常：" + e.getMessage());
            errorMsg = e.getMessage();
            status = 0;
            throw e;  // 继续抛出，不影响原有异常逻辑
        }
        // 计算方法执行时间
        long costTime = System.currentTimeMillis() - startTime;
        // 创建 SpEL 上下文对象
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 把参数放进去
        variables.forEach(context::setVariable);

        // 解析业务ID
        String businessId = null;
        if (StringUtils.hasText(businessLog.businessId())) {
            try {
                Object value = parser.parseExpression(businessLog.businessId()).getValue(context);
                if (value != null) {
                    businessId = value.toString();
                }
            } catch (Exception e) {
                System.out.println("解析 业务Id 失败：" + businessLog.businessId());
                e.printStackTrace();
            }
        }
        SysUser currentUser = AuthContextUtil.get();
        SysBusinessLog sysBusinessLog = new SysBusinessLog();
        if (currentUser != null) {
            sysBusinessLog.setOperUserId(currentUser.getId());
            sysBusinessLog.setOperUserName(currentUser.getUserName());
        }
        sysBusinessLog.setBusinessId(businessId);
        sysBusinessLog.setModule(businessLog.module());
        sysBusinessLog.setOperation(businessLog.operation());
        sysBusinessLog.setBusinessType(businessLog.businessType());
        sysBusinessLog.setStatus(status);
        sysBusinessLog.setErrorMsg(errorMsg);
        sysBusinessLog.setCostTime(costTime);
        sysBusinessLog.setOperTime(new Date());
        businessLogService.saveLog(sysBusinessLog);
        return result;
    }
}
