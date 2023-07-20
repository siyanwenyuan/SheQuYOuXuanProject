package com.chen.search.common.exception;


import com.chen.search.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *返回的异常处理是定义的Result类型
 * 也就是，即使出现了异常，但是返回结果也要是Result
 *
 */
//aop  面向切面  表示在此功能基础之上，在对原代码的不修改，增强功能
@ControllerAdvice
public class GlobalExceptionHandler {

    //此注解是异常处理器，表示当出现该异常的时候，便开始执行这个异常处理方法
    @ExceptionHandler(Exception.class)
    @ResponseBody// 此注解表明：使方法中返回的数据是json数据格式
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail(null);
    }

    /**
     * 由于exception是系统异常
     * 但是由于可能不会够用，则需要自定义异常
     * 自定义异常在最后的接口中需要进行手动抛出，但是系统异常则是由系统自动抛出
     * @param e
     * @return
     */
    //自定义异常
    @ExceptionHandler(SsyxException.class)
    @ResponseBody
    public Result error(SsyxException e){
        e.printStackTrace();
        return Result.build(null,e.getCode(),e.getMessage());

    }

}
