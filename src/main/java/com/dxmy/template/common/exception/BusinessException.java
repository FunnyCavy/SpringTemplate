package com.dxmy.template.common.exception;

import com.dxmy.template.common.response.Code;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Code errorCode;
    private final String message;

    public BusinessException(Code errorCode) {
        this(errorCode, errorCode.getDesc());
    }

    public BusinessException(Code errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

}
