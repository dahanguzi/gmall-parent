package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Slf4j
@Aspect
@Component
public class GmallValidatorAspect {

    @Around("execution(* com.atguigu.gmall.admin..controller.*.* (..))")
    public Object aroundNotice(ProceedingJoinPoint proceedingJoinPoint){

        log.info("校验切面切入进行工作...");
        Object[] args = proceedingJoinPoint.getArgs();

        Object proceed = null;
        try {
            for (Object arg:args) {
                if(arg instanceof BindingResult){
                    int count = ((BindingResult) arg).getErrorCount();
                    if(count > 0){
                        log.info("校验发生错误...直接给用户返回");
                        CommonResult commonResult = new CommonResult().validateFailed((BindingResult) arg);
                        return commonResult;
                    }

                }
            }
            proceed = proceedingJoinPoint.proceed(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return proceed;
    }
}
