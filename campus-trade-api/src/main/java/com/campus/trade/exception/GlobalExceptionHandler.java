package com.campus.trade.exception;

import com.campus.trade.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        log.error("服务器内部错误: ", e);
        return Result.error(500, "服务器出现未知错误，请联系管理员！");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleCustomException(CustomException e) {
        log.warn("业务逻辑错误: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }
}