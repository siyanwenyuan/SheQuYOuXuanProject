package com.chen.home.controller;


import com.chen.home.service.HomeService;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Api(tags = "首页数据显示")
@RestController
@RequestMapping("api/home")
public class HomeApiController {

    @Autowired
    private HomeService homeService;

    @GetMapping("index")
    @ApiOperation("首页数数据显示")
    public Result index(HttpServletRequest request){

        Long userId=AuthContextHolder.getUserId();
        Map<String,Object> map=
                homeService.indexData(userId);
        return Result.ok(map);

    }


}
