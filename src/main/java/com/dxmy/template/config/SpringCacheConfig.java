package com.dxmy.template.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

/**
 * Spring Cache 配置
 */
@Data
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "app.cache")
@EnableConfigurationProperties(CacheProperties.class)
public class SpringCacheConfig {

    /** 自定义各缓存过期时间 */
    private Map<String, Duration> timeToLive;

    /**
     * 配置 Redis 缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(CacheProperties cacheProperties, RedisConnectionFactory connectionFactory) {
        // 获取默认配置, 并配置 JSON 序列化器
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 使用配置文件中的配置覆盖默认配置
        CacheProperties.Redis redisCacheProperties = cacheProperties.getRedis();
        if (redisCacheProperties.getTimeToLive() != null)
            config = config.entryTtl(redisCacheProperties.getTimeToLive());
        if (!redisCacheProperties.isCacheNullValues())
            config = config.disableCachingNullValues();
        if (redisCacheProperties.isUseKeyPrefix() && StringUtils.hasText(redisCacheProperties.getKeyPrefix()))
            config = config.computePrefixWith(CacheKeyPrefix.prefixed(redisCacheProperties.getKeyPrefix()));

        // 为不同缓存配置不同的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        if (!CollectionUtils.isEmpty(timeToLive))
            for (String cacheName : timeToLive.keySet())
                cacheConfigs.put(cacheName, config.entryTtl(timeToLive.get(cacheName)));

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

}
