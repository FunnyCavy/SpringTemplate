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

    /** MinIO 主机 */
    private String host;
    /** MinIO 端口 */
    private Integer port;
    /** 是否使用 HTTPS */
    private Boolean secure;
    /** MinIO 访问密钥 */
    private String accessKey;
    /** MinIO 密钥 */
    private String secretKey;
    /** 使用的存储桶名称 */
    private String bucketName;

    /**
     * MinIO 客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(host, port, secure)
                .credentials(accessKey, secretKey)
                .build();
    }

}
