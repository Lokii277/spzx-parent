package com.aki.spzx.common.config.exception;

import com.aki.spzx.model.vo.common.ResultCodeEnum;
import lombok.Data;


@Data
public class AkiException extends RuntimeException {

    private Integer code;

    private String message;

    private ResultCodeEnum resultCodeEnum;

    public AkiException(ResultCodeEnum resultCodeEnum) {
        this.resultCodeEnum = resultCodeEnum;
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }
}
