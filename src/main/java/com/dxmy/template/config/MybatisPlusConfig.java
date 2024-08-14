package com.dxmy.template.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 创建分页拦截器
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor() {
            @Override
            protected void handlerOverflow(IPage<?> page) {
                // 当前页大于最大页时返回最后一页
                page.setCurrent(page.getPages());
            }
        };
        // 添加分页拦截器
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }

}
