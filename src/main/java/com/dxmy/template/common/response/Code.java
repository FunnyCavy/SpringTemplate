package com.dxmy.template.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码
 */
@Getter
@AllArgsConstructor
public enum Code {
    SUCCESS(0, "请求成功"),
    PARAMS_ERROR(40000, "请求参数非法"),
    REQUEST_ERROR(40001, "请求接收异常"),
    NOT_LOGIN_ERROR(40100, "登录状态异常"),
    AUTH_ERROR(40300, "用户权限异常"),
    SYSTEM_ERROR(50000, "系统未知异常");

    private final Integer code;
    private final String desc;
}
