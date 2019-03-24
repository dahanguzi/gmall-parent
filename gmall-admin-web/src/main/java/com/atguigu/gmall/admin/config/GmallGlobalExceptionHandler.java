package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//@ControllerAdvice//处理系统全局异常
//@ResponseBody
@RestControllerAdvice
public class GmallGlobalExceptionHandler {

    //此方法处理数学运算异常
    @ExceptionHandler(ArithmeticException.class)
    public Object arithmeticExceptionHandler(Exception e){
        log.info("全局异常处理类感知到异常...");

        return new CommonResult().validateFailed(e.getMessage());
    }
}
