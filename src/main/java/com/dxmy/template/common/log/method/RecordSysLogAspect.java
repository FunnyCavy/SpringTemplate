package com.dxmy.template.common.log.method;

import com.dxmy.template.common.auth.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 记录操作日志切面
 */
@Aspect
@Component
public class RecordSysLogAspect {

    /** 日志类型标识 */
    private static final int LOG_TYPE_NORMAL = 0;
    private static final int LOG_TYPE_EXCEPTION = 1;

    @Resource
    private HttpServletRequest request;

    @Around("@annotation(recordSysLog)")
    public Object recordSysLog(ProceedingJoinPoint joinPoint, RecordSysLog recordSysLog) throws Throwable {
        // 调用方法相关
        String logTitle = recordSysLog.value();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        // 执行方法相关
        Object result = null;
        String exceptionMessage = null;
        int logType = LOG_TYPE_NORMAL;
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            exceptionMessage = throwable.getMessage();
            logType = LOG_TYPE_EXCEPTION;
            throw throwable;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;

            // 构建并持久化系统操作日志
            SysLog.builder()
                  .title(logTitle)
                  .type(logType)
                  .className(className)
                  .methodName(methodName)
                  .methodArgs(methodArgs)
                  .methodReturn(result)
                  .requestMethod(request.getMethod())
                  .requestUri(request.getRequestURI())
                  .costTime(costTime)
                  .exception(exceptionMessage)
                  .operatorId(UserContext.getCurrentUserId())
                  .completionTime(LocalDateTime.now())
                  .build()
                  .insert();
        }

        return result;
    }

}
