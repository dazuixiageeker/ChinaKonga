package com.changgou.framework.exception;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BaseExceptionHandler {

    /**
     * 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR,e.getMessage());
    }

}
