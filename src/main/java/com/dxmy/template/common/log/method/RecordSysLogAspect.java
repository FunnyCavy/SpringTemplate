package com.dxmy.template.common.log.method;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 记录操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RecordSysLogAspect {

    /** 日志类型标识 */
    private static final Integer NORMAL_LOG_TYPE = 0;
    private static final Integer EXCEPTION_LOG_TYPE = 9;

    @SneakyThrows
    @Around("@annotation(recordSysLog)")
    public Object recordSysLog(ProceedingJoinPoint joinPoint, RecordSysLog recordSysLog) {
        // 调用方法相关
        String logTitle = recordSysLog.value();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        // 请求相关
        ServletRequestAttributes reqAttrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = reqAttrs.getRequest();
        String reqMethod = req.getMethod();
        String reqURI = req.getRequestURI();

        // 执行方法相关
        Object result = null;
        Integer logType = NORMAL_LOG_TYPE;
        String exceptionMessage = null;
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logType = EXCEPTION_LOG_TYPE;
            exceptionMessage = throwable.getMessage();
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
                  .requestMethod(reqMethod)
                  .requestUri(reqURI)
                  .costTime(costTime)
                  .exception(exceptionMessage)
                  .operatorId(req.getRemoteUser())  // TODO: 获取操作者 ID
                  .completionTime(LocalDateTime.now())
                  .build()
                  .insert();
        }

        return result;
    }

}
