package com.dxmy.template.config;

import com.dxmy.template.common.auth.AuthInterceptor;
import com.dxmy.template.common.converter.CustomObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.array.ArrayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web 配置
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /** 鉴权拦截器 */
    private final AuthInterceptor authInterceptor;

    /** API 文档相关路径 */
    private final String[] apiDocPaths = {
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/webjars/**",
    };

    /** 无需鉴权的接口路径 */
    @Value("${app.auth.skip-auth-paths}")
    private String[] skipAuthPaths;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(ArrayUtil.addAll(apiDocPaths, skipAuthPaths));
    }

    /**
     * 配置消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jsonMessageConverter) {
                jsonMessageConverter.setObjectMapper(new CustomObjectMapper());
                break;
            }
        }
    }

}
