package com.dxmy.template.common.oss;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 管理器
 */
@Component
@SuppressWarnings("unused")
public class MinioManager {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 上传文件
     *
     * @param file 待上传文件
     * @return 对象名
     */
    public String uploadFile(MultipartFile file) throws Exception {
        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains("."))
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成随机文件名并拼接路径
            String filename = IdUtil.simpleUUID();
            String path = DateUtil.format(new Date(), "yyyy/MM/dd");
            String objectName = path + "/" + filename + fileExtension;

            // 获取文件输入流和内容类型
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();

            // 上传文件到 MinIO
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(contentType)
                    .build()
            );
            return objectName;
        } catch (MinioException e) {
            throw new Exception("[MinIO] 上传文件时出现 MinIO 异常: " + e.getMessage());
        } catch (IOException e) {
            throw new Exception("[MinIO] 上传文件时出现 I/O 异常: " + e.getMessage());
        }
    }

    /**
     * 生成预签名 URL
     *
     * @param objectName 对象名
     * @return 预签名 URL
     */
    public String getPreSignedUrl(String objectName) throws Exception {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS)
                    .build()
            );
        } catch (Exception e) {
            throw new Exception("[MinIO] 生成预签名 URL 时出现异常: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名
     */
    public void deleteFile(String objectName) throws Exception {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (MinioException e) {
            throw new Exception("[MinIO] 删除文件时出现 MinIO 异常: " + e.getMessage());
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param objectName 对象名
     */
    public boolean doesFileExist(String objectName) throws Exception {
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
     * @param pathPrefix 路径前缀
     */
    public Iterable<Result<Item>> listFiles(String pathPrefix) {
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
