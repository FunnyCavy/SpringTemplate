package com.dxmy.template.common.oss;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /** MinIO 端点 */
    private String endpoint;
    /** MinIO 访问密钥 */
    private String accessKey;
    /** MinIO 密钥 */
    private String secretKey;
    /** MinIO 存储桶名称 */
    private String bucketName;

    /**
     * MinIO 客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}
