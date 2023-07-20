package com.chen.search.common.exception;


import com.chen.search.common.result.ResultCodeEnum;
import lombok.Data;


/**
 * 自定义异常处理：
 *  1：编写一个异常类，继承runtimeexception
 *  2：定义属性
 *
 */
@Data
public class SsyxException extends RuntimeException{
    /**
     * 异常状态码
     */
    private Integer code;

    /**
     * 通过异常信息和状态码
     * 并将异常信息给父类
     * @param message
     * @param code
     */
    public SsyxException(String message,Integer code){
        super(message);
        this.code=code;
    }

    /**
     * 接受枚举类型对象
     * @param resultCodeEnum
     */

    public SsyxException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }


}
