package com.dxmy.template.common.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 客户端属性
 */
@Data
@ConfigurationProperties(prefix = "spring.data.redis", ignoreInvalidFields = true)
public class RedisClientProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private Integer database = 0;
    private Integer timeout = 5000;
    private RedissonProperties redisson = new RedissonProperties();

    /**
     * Redisson 属性
     */
    @Data
    public static class RedissonProperties {

        /** 连接池的大小 */
        private Integer poolSize = 64;
        /** 连接池的最小空闲连接数 */
        private Integer minIdleSize = 10;
        /** 连接的最大空闲时间 (单位: 毫秒) */
        private Integer idleTimeout = 10000;
        /** 连接重试次数 */
        private Integer retryAttempts = 3;
        /** 连接重试间隔 (单位: 毫秒) */
        private Integer retryInterval = 1000;
        /** 定期检查连接是否可用的间隔 (单位: 毫秒, 0 表示不进行定期检查) */
        private Integer pingInterval = 0;
        /** 是否保持长连接 */
        private Boolean keepAlive = true;

    }

}
