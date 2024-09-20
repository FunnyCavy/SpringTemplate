package com.dxmy.template.common.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Redis 客户端配置
 */
@Configuration
@EnableConfigurationProperties(RedisClientProperties.class)
public class RedisClientConfig {

    @Bean
    public RedissonClient redissonClient(RedisClientProperties properties) {
        Config config = new Config();

        config.setCodec(new JsonJacksonCodec())
              .useSingleServer()
              .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
              .setUsername(StringUtils.hasText(properties.getUsername()) ? properties.getUsername() : null)
              .setPassword(StringUtils.hasText(properties.getPassword()) ? properties.getPassword() : null)
              .setDatabase(properties.getDatabase())
              .setConnectTimeout(properties.getTimeout())
              .setConnectionPoolSize(properties.getRedisson().getPoolSize())
              .setConnectionMinimumIdleSize(properties.getRedisson().getMinIdleSize())
              .setIdleConnectionTimeout(properties.getRedisson().getIdleTimeout())
              .setRetryAttempts(properties.getRedisson().getRetryAttempts())
              .setRetryInterval(properties.getRedisson().getRetryInterval())
              .setPingConnectionInterval(properties.getRedisson().getPingInterval())
              .setKeepAlive(properties.getRedisson().getKeepAlive());

        return Redisson.create(config);
    }

}
