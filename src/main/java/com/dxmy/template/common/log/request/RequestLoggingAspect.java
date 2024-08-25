package com.dxmy.template.common.log.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 请求日志切面
 */
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    /**
     * 记录请求信息日志
     */
    @Around("execution(* *..*Controller.*(..))")
    public Object logRequestInfo(ProceedingJoinPoint point) throws Throwable {
        // 若未开启 Debug 日志等级则跳过
        if (!log.isDebugEnabled())
            return point.proceed();

        // 获取请求对象
        ServletRequestAttributes reqAttrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = reqAttrs.getRequest();

        // 获取请求信息
        String reqId = UUID.randomUUID().toString();
        String reqMethod = req.getMethod();
        String reqURI = req.getRequestURI();
        String reqParams = getReqParams(req);
        String reqBody = getReqBody(req);
        String reqIp = req.getRemoteAddr();

        // 打印开始日志
        String beginLog = AnsiOutput.toString(
                AnsiColor.BRIGHT_WHITE, AnsiStyle.BOLD, AnsiBackground.CYAN,
                "[BGN] ID: " + reqId + " - " + reqMethod + " " + reqURI
        );
        log.debug("{} --> {} {} - IP: {}", beginLog, reqParams, reqBody, reqIp);

        Object result;
        try {
            // 执行请求并记录耗时
            long startTime = System.currentTimeMillis();
            result = point.proceed();
            long costTimeMillis = System.currentTimeMillis() - startTime;

            // 打印结束日志
            String endLog = AnsiOutput.toString(
                    AnsiColor.BRIGHT_WHITE, AnsiStyle.BOLD, AnsiBackground.MAGENTA,
                    "[END] ID: " + reqId + " - " + reqMethod + " " + reqURI
            );
            log.debug("{} --> Cost: {} ms", endLog, costTimeMillis);
        } catch (Exception e) {
            // 打印错误日志
            String errorLog = AnsiOutput.toString(
                    AnsiColor.BRIGHT_WHITE, AnsiStyle.BOLD, AnsiBackground.RED,
                    "[ERR] ID: " + reqId + " - " + reqMethod + " " + reqURI
            );
            log.debug("{} --> Error: {}", errorLog, e.getMessage());
            throw e;
        }

        return result;
    }

    /**
     * 获取请求参数
     */
    private String getReqParams(HttpServletRequest req) {
        Map<String, String[]> paramMap = req.getParameterMap();
        if (paramMap.isEmpty())
            return "<NoParams>";
        return paramMap.entrySet().stream().map(entry -> {
            String key = entry.getKey();
            String[] value = entry.getValue();
            String formattedValue = (value.length == 1) ? value[0] : Arrays.toString(value);
            return key + "=" + formattedValue;
        }).collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * 获取请求体
     */
    private String getReqBody(HttpServletRequest req) {
        String reqBody = null;
        if (req instanceof ContentCachingRequestWrapper requestWrapper)
            reqBody = requestWrapper.getContentAsString();
        return StrUtil.isBlank(reqBody) ? "<NoBody>" : reqBody;
    }

}
