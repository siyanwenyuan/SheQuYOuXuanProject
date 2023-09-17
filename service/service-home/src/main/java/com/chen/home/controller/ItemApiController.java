package com.chen.home.controller;


import com.chen.home.service.ItemService;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.common.result.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api("商品详情接口")
@RestController
@RequestMapping("api/home")
public class ItemApiController {


    @Autowired
    private ItemService itemService;

    @RequestMapping("item/{id}")
    public Result index(@PathVariable Long  id)

    {
        Long userId = AuthContextHolder.getUserId();
       Map<String,Object> map= itemService.item(id,userId);
       return Result.ok(map);


    }




}
