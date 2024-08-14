package com.dxmy.template.common.oss;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO 管理器
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MinioManager {

    private final MinioClient minioClient;

    /**
     * 上传文件
     *
     * @param bucketName 桶名
     * @param file       待上传文件
     * @return 文件访问 URL
     */
    public String uploadFile(String bucketName, MultipartFile file) throws Exception {
        try {
            String filename = file.getOriginalFilename() + UUID.randomUUID();
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build()
            );
            return filename;
        } catch (MinioException e) {
            throw new Exception("[MinIO] 上传文件时出现 MinIO 异常: " + e.getMessage());
        } catch (IOException e) {
            throw new Exception("[MinIO] 上传文件时出现 I/O 异常: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 桶名
     * @param objectName 对象
     */
    public void deleteFile(String bucketName, String objectName) throws Exception {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (MinioException e) {
            throw new Exception("[MinIO] 删除文件时出现 MinIO 异常: " + e.getMessage());
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     */
    public boolean doesFileExist(String bucketName, String objectName) throws Exception {
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return stat != null;
        } catch (ErrorResponseException e) {
            // 若为 404 对应的错误码则说明文件不存在
            if (e.errorResponse().code().equals("NoSuchKey"))
                return false;
            throw new Exception("[MinIO] 判断文件是否存在时出现异常: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("[MinIO] 判断文件是否存在时出现异常: " + e.getMessage());
        }
    }

    /**
     * 根据路径前缀列出桶内文件
     *
     * @param bucketName 桶名
     * @param pathPrefix 路径前缀
     */
    public Iterable<Result<Item>> listFiles(String bucketName, String pathPrefix) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucketName)
                .prefix(pathPrefix)
                .recursive(true)
                .build()
        );
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 桶名
     */
    public void createBucket(String bucketName) throws Exception {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found)
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (MinioException e) {
            throw new Exception("[MinIO] 创建存储桶时出现异常: " + e.getMessage());
        }
    }

}
