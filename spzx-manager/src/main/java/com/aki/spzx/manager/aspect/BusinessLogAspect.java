package com.aki.spzx.manager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class BusinessLogAspect {

    // SpEL 解析器（可以复用）
    private final ExpressionParser parser = new SpelExpressionParser();

    // 拦截带有 @BusinessLog 注解的方法
    @Around("@annotation(businessLog)")
    public Object around(ProceedingJoinPoint joinPoint,BusinessLog businessLog) throws Throwable{
        System.out.println("===== AOP执行  =====");
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
        // 创建 SpEL 上下文对象
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 把参数放进去
        variables.forEach(context::setVariable);

        // 7. 解析业务ID
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
        // 4. 打印看看效果（后面我们再改成真正落库）
        System.out.println("模块：" + businessLog.module());
        System.out.println("操作：" + businessLog.operation());
        System.out.println("业务类型：" + businessLog.businessType());
        System.out.println("业务ID：" + businessId);
        return joinPoint.proceed();
    }
}
