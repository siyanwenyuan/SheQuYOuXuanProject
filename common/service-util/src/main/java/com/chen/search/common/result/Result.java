package com.chen.search.common.result;


import lombok.Data;


/**
 * 学号
 */

/**
 * 泛型类中：
 *  其中因为需要对data中的数据进行类型的确当，所以需要设置一个泛型类，但是由于数据类型的不确定，所以设置为T
 * @param <T>
 */
@Data
public class Result<T>{

    //状态码
    private Integer code;
    //信息
    private String message;
    //数据
    private T data;
    //构造方法私有化，防止可以直接new对象
    private Result(){

    }
    //提供一个可以设置数据的方法,设置为static 通过类直接访问
    public static<T> Result<T> build(T data,Integer code, String message){
        //设置数据
        Result<T> result=new Result<T>();
        // 判断传入的data是否为空，如果不为空，则进行设置，因为某些业务逻辑（例如删除。。。）data为空
        if(data!=null){
            result.setData(data);
        }
        //在设置其他数据，通过设置的枚举进行
        result.setCode(code);
        result.setMessage(message);
        //返回设置成功的结果
        return result;
    }

    public static<T> Result<T> build(T data,ResultCodeEnum resultCodeEnum){
        //设置数据
        Result<T> result=new Result<T>();
        // 判断传入的data是否为空，如果不为空，则进行设置，因为某些业务逻辑（例如删除。。。）data为空
        if(data!=null){
            result.setData(data);
        }
        //在设置其他数据，通过设置的枚举进行
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(result.getMessage());
        //返回设置成功的结果
        return result;
    }

    //成功的方法
    public static<T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }

    //失败的方法
    public static<T> Result<T> fail(T data){
        return build(data,ResultCodeEnum.FAIL);
    }

}
