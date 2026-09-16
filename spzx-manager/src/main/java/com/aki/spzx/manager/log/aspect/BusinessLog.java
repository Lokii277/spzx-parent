package com.aki.spzx.manager.log.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解类 用于业务日志AOP
// 这个注解只能放在方法上
@Target(ElementType.METHOD)
//程序运行的时候，这个注解依然存在，如果运行时注解已经不存在了，AOP 就读取不到。
@Retention(RetentionPolicy.RUNTIME)
public @interface  BusinessLog {
    /**
     * 业务模块
     */
    String module();

    /**
     * 操作名称
     */
    String operation();

    /**
     * 业务类型
     */
    String businessType() default "";

    /**
     * 业务ID
     */
    String businessId() default "";

}
