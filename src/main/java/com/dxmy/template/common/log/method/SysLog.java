package com.dxmy.template.common.log.method;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 系统操作日志
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "系统操作日志")
@EqualsAndHashCode(callSuper = true)
public class SysLog extends Model<SysLog> {

    @Schema(name = "日志编号", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(name = "日志标题", example = "用户登录")
    private String title;

    @Schema(name = "日志类型 (0-正常, 9-异常)", example = "0")
    private Integer type;

    @Schema(name = "类名", example = "com.example.service.UserService")
    private String className;

    @Schema(name = "调用方法名称", example = "loginUser")
    private String methodName;

    @Schema(name = "调用方法参数", example = "[username, password]")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object[] methodArgs;

    @Schema(name = "调用方法返回值", example = "登录成功")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object methodReturn;

    @Schema(name = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(name = "请求 URI", example = "/login")
    private String requestUri;

    @Schema(name = "请求耗时", example = "1000")
    private Long costTime;

    @Schema(name = "异常信息", example = "NullPointerException")
    private String exception;

    @Schema(name = "操作者 ID", example = "1")
    private Long operatorId;

    @Schema(name = "操作完成时间", example = "2024-01-01 00:00:00")
    private LocalDateTime completionTime;

    /*
    SQL 建表语句:
    CREATE TABLE `sys_log` (
        `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志编号',
        `title` VARCHAR(255) NOT NULL COMMENT '日志标题',
        `type` TINYINT NOT NULL COMMENT '日志类型 (0-正常, 9-异常)',
        `class_name` VARCHAR(255) NOT NULL COMMENT '类名',
        `method_name` VARCHAR(255) NOT NULL COMMENT '调用方法名称',
        `method_args` JSON COMMENT '调用方法参数',
        `method_return` JSON COMMENT '调用方法返回值',
        `request_method` VARCHAR(255) NOT NULL COMMENT '请求方法',
        `request_uri` VARCHAR(255) NOT NULL COMMENT '请求 URI',
        `cost_time` BIGINT NOT NULL COMMENT '请求耗时',
        `exception` TEXT COMMENT '异常信息',
        `operator_id` BIGINT COMMENT '操作者 ID',
        `completion_time` DATETIME NOT NULL COMMENT '操作完成时间',
        INDEX `idx_operator_id` (`operator_id`),
        INDEX `idx_completion_time` (`completion_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志';
    */
}
