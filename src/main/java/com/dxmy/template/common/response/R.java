package com.dxmy.template.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 返回结果
 */
@Data
@AllArgsConstructor
public class R<T> {

    private Integer code;
    private T data;
    private String message;

    /**
     * 生成成功返回结果
     *
     * @param data 响应数据
     * @param <T>  数据类型
     */
    public static <T> R<T> ok(T data) {
        Integer code = Code.SUCCESS.getCode();
        String message = Code.SUCCESS.getDesc();
        return new R<>(code, data, message);
    }

    /**
     * 生成失败返回结果
     *
     * @param errorCode 错误状态码
     * @param message   错误信息
     */
    public static <T> R<T> error(Code errorCode, String message) {
        Integer code = errorCode.getCode();
        return new R<>(code, null, message);
    }

}
