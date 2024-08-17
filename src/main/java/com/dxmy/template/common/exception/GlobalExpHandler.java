package com.dxmy.template.common.exception;

import com.dxmy.template.common.response.Code;
import com.dxmy.template.common.response.R;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.sql.SQLException;

/**
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExpHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> businessExceptionHandler(BusinessException e) {
        return R.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> bindExceptionHandler(BindException e) {
        Code errorCode = Code.PARAMS_ERROR;
        String msg = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() : errorCode.getDesc();
        log.warn("[参数校验异常] {}", msg, e);
        return R.error(errorCode, msg);
    }

    /**
     * 参数约束异常处理
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Code errorCode = Code.PARAMS_ERROR;
        String msg = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("[参数约束异常] {}", msg, e);
        return R.error(errorCode, msg);
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        Code errorCode = Code.PARAMS_ERROR;
        String msg = e.getMessage();
        log.warn("[非法参数异常] {}", msg, e);
        return R.error(errorCode, msg);
    }

    /**
     * 请求接收异常处理
     */
    @ExceptionHandler({
            MissingRequestValueException.class,
            HttpMessageConversionException.class,
            HttpRequestMethodNotSupportedException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> commonHttpExceptionsHandler(Exception e) {
        log.warn("[请求接收异常] {}", e.getMessage(), e);
        Code errorCode = Code.REQUEST_ERROR;
        return R.error(errorCode, errorCode.getDesc());
    }

    /**
     * 文件上传异常处理
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> multipartExceptionHandler(MultipartException e) {
        log.warn("[文件上传异常] {}", e.getMessage(), e);
        Code errorCode = Code.FILE_PROCESSING_ERROR;
        return R.error(errorCode, errorCode.getDesc());
    }

    /**
     * 空指针异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> nullPointerExceptionHandler(NullPointerException e) {
        log.error("[空指针异常] {}", e.getMessage(), e);
        Code errorCode = Code.SYSTEM_ERROR;
        return R.error(errorCode, errorCode.getDesc());
    }

    /**
     * 持久化异常处理
     */
    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> persistenceExceptionHandler(PersistenceException e) {
        log.error("[持久化异常] {}", e.getMessage(), e);
        Code errorCode = Code.SYSTEM_ERROR;
        return R.error(errorCode, errorCode.getDesc());
    }

    /**
     * SQL 异常处理
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> sqlExceptionHandler(SQLException e) {
        log.error("[SQL 异常] {}", e.getMessage(), e);
        Code errorCode = Code.SYSTEM_ERROR;
        return R.error(errorCode, errorCode.getDesc());
    }

    /**
     * 未知异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exceptionHandler(Exception e) {
        log.error("[未知异常] {}", e.getMessage(), e);
        Code errorCode = Code.SYSTEM_ERROR;
        return R.error(errorCode, errorCode.getDesc());
    }

}
