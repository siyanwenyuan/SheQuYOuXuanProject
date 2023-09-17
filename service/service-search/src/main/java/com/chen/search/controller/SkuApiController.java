package com.chen.search.controller;


import com.chen.search.common.result.Result;
import com.chen.search.service.SkuService;
import org.redisson.api.annotation.REntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/search/sku")

public class SkuApiController {

    @Autowired
    private SkuService skuService;


    //上架 （也就是通过id得到商品信息保存到es中）
    @GetMapping("inner/upperSkuInfo/{skuId}")
    public Result upper(@PathVariable Long skuId){

        skuService.upper(skuId);
        return Result.ok(null);
    }



    //下架（从es中删除商品信息）
    @GetMapping("inner/lowSkuSkuInfo/{skuId}")
    public Result low(@PathVariable Long skuId){
        skuService.low(skuId);
        return Result.ok(null);
    }
}
