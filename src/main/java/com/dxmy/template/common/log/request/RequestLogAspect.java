package com.dxmy.template.common.log.request;

import com.dxmy.template.common.response.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.RandomUtil;
import org.slf4j.MDC;
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
import java.util.stream.IntStream;

/**
 * 全局请求日志切面
 */
@Slf4j
@Aspect
@Component
public class RequestLogAspect {

    /** TraceID 键名 */
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 记录请求日志
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRequestInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        // 生成 TraceID 放入日志, 以便进行链路追踪
        MDC.put(TRACE_ID_KEY, RandomUtil.randomNumbers(5));

        // 未开启 Debug 日志等级时, 不进行日志输出
        if (!log.isDebugEnabled()) {
            Object result = joinPoint.proceed();
            MDC.clear();
            return result;
        }

        // 获取请求对象
        ServletRequestAttributes requestAttr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttr.getRequest();

        // 获取请求信息
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String requestParams = getRequestParams(request);
        String requestBody = getRequestBody(request);
        String requestIP = request.getRemoteAddr();

        // 输出请求日志
        String requestLogPrefix = AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, AnsiBackground.CYAN,
                "[请求] " + requestMethod + " " + requestURI);
        log.debug("{} <== {} {} - IP: {}", requestLogPrefix, requestParams, requestBody, requestIP);

        // 获取调用信息
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String methodParams = getMethodParams(joinPoint);

        // 输出调用日志
        log.debug("[调用] {}.{}{}", className, methodName, methodParams);

        // 处理请求并记录耗时
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long costTime = System.currentTimeMillis() - startTime;

        // 获取响应结果, 过长则截断
        Object res = result instanceof R ? ((R<?>) result).getData() : result;
        if (res.toString().length() > 1000)
            res = res.toString().substring(0, 1000) + "...";

        // 输出响应日志
        String responseLogPrefix = AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, AnsiBackground.MAGENTA,
                "[响应] " + requestMethod + " " + requestURI);
        log.debug("{} ==> {} ms - {}", responseLogPrefix, costTime, res);

        MDC.clear();
        return result;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty())
            return "<NoParams>";

        return parameterMap.entrySet().stream().map(entry -> {
            String key = entry.getKey();
            String[] value = entry.getValue();
            return key + "=" + (value.length == 1 ? value[0] : Arrays.toString(value));
        }).collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * 获取请求体
     */
    private String getRequestBody(HttpServletRequest request) {
        String requestBody = null;
        if (request instanceof ContentCachingRequestWrapper requestWrapper)
            requestBody = requestWrapper.getContentAsString();
        return StrUtil.isNotBlank(requestBody) ? requestBody : "<NoBody>";
    }

    /**
     * 获取方法参数
     */
    private String getMethodParams(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        if (ArrayUtil.isEmpty(paramValues))
            return "<NoParams>";

        return IntStream.range(0, paramNames.length).mapToObj(i -> {
            String paramName = paramNames[i];
            Object paramValue = paramValues[i];
            return paramName + "=" + (paramValue != null ? paramValue.toString() : "null");
        }).collect(Collectors.joining(", ", "(", ")"));
    }

}
