package com.dxmy.template.common.log.method;

import java.lang.annotation.*;

/**
 * 记录操作日志注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordSysLog {

    /**
     * 日志标题
     */
    String value() default "";

}
