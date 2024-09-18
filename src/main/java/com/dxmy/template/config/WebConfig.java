package com.dxmy.template.config;

import com.dxmy.template.common.auth.AuthInterceptor;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web 配置
 */
@Slf4j
@Configuration
@ConfigurationProperties("app.auth")
public class WebConfig implements WebMvcConfigurer {

    /** API 文档相关路径 */
    private static final List<String> apiDocPaths = List.of(
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/webjars/**"
    );

    /** 无需鉴权的接口路径 */
    @Getter
    @Setter
    private List<String> skipAuthPaths;

    /** 鉴权拦截器 */
    @Resource
    private AuthInterceptor authInterceptor;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(apiDocPaths)
                .excludePathPatterns(skipAuthPaths);
        log.info("[鉴权拦截器] 配置成功, 排除路径: {}", String.join(", ", skipAuthPaths));
    }

}
