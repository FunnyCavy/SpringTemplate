package com.dxmy.template.common.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 实体抽象基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntity<T extends Model<?>> extends Model<T> {

    @Schema(name = "主键")
    private Long id;

    @Schema(name = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "更新时间")
    private LocalDateTime updateTime;

    @JsonIgnore
    @Schema(name = "逻辑删除")
    private Integer isDeleted;

}
