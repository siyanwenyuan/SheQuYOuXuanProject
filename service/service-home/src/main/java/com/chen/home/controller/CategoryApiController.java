package com.chen.home.controller;


import com.chen.client.product.ProductFeignClient;
import com.chen.search.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "商品分类信息显示")
@RestController
@RequestMapping("api/home")
public class CategoryApiController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @ApiOperation("商品分类信息显示")
    @RequestMapping("category")
    public Result cateGoryList(){
        return Result.ok(productFeignClient.findAllCategoryList());

    }
}
