package com.dxmy.template.common.log.request;

import com.dxmy.template.common.response.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.RandomUtil;
import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Arrays;
import java.util.Map;
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
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRequestInfo(ProceedingJoinPoint point) throws Throwable {
        // 若未开启 Debug 日志等级则跳过
        if (!log.isDebugEnabled())
            return point.proceed();

        // 获取请求对象
        ServletRequestAttributes reqAttrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = reqAttrs.getRequest();

        // 获取请求信息
        String reqId = RandomUtil.randomNumbers(5);
        String reqMethod = req.getMethod();
        String reqURI = req.getRequestURI();
        String reqParams = getReqParams(req);
        String reqBody = getReqBody(req);
        String reqIp = req.getRemoteAddr();
        String reqBaseInfo = "ID: " + reqId + " - " + reqMethod + " " + reqURI;

        // 打印开始日志
        String logBegin = AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, AnsiBackground.CYAN, "[BGN] " + reqBaseInfo);
        log.debug("{} --> {} {} - IP: {}", logBegin, reqParams, reqBody, reqIp);

        // 执行请求并记录耗时
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long costTime = System.currentTimeMillis() - startTime;

        // 获取响应结果, 过长则截断
        Object resp = result instanceof R ? ((R<?>) result).getData() : result;
        if (resp.toString().length() > 1000)
            resp = resp.toString().substring(0, 1000) + "...";

        // 打印结束日志
        String logEnd = AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, AnsiBackground.MAGENTA, "[END] " + reqBaseInfo);
        log.debug("{} --> {} ms - Resp: {}", logEnd, costTime, resp);

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
