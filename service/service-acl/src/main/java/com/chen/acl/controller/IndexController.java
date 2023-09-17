package com.chen.acl.controller;


import com.chen.search.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "登录接口")// 这个注解作用是：在接口文档中本来显示的有原本的英文接口，加上这个注解，可以设置成中文接口名
@RestController// 此注解的含义是： 在spring中进行注册，rest 是返回的json格式
@RequestMapping("/admin/acl/index")// 统一的前面的接口路径

public class IndexController {

    //login登录接口
    @ApiOperation("登录")//可以在接口文档中看到显示中文的接口名
    @PostMapping("login")
    public Result login(){
        //返回token
        Map<String ,String> map=new HashMap<>();
        map.put("token","token-admin");
        return Result.ok(map);
    }


    //info  获取登录信息
    @ApiOperation("获取信息")
    @GetMapping("info")
    public Result getInfo(){
        Map<String,String> map=new HashMap<>();
        map.put("name","admin");
        map.put("avator","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }


    //logout 退出
    @ApiOperation("退出")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok(null);
    }

}
