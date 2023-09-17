package com.chen.product.controller;


import com.chen.product.service.SkuImageService;
import com.chen.search.common.result.Result;
import com.chen.search.model.product.SkuImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("get/image")
public class ImageController {

    @Autowired
    private SkuImageService skuImageService;
    @GetMapping("{id}")
    public Result getImage(@PathVariable Long id){
       List<SkuImage> skuImages= skuImageService.getImageById(id);
       return Result.ok(skuImages);


    }
}
