package com.aki.spzx.common.config.exception;

import com.aki.spzx.model.vo.common.Result;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException() {
        return Result.build(null, ResultCodeEnum.SYSTEM_ERROR);
    }

    //自定义异常处理
    @ExceptionHandler(AkiException.class)
    @ResponseBody
    public Result handleException(AkiException e) {
        return Result.build(null, e.getResultCodeEnum());
    }
}
