package com.chen.product.controller;


import com.chen.product.service.SkuPosterService;
import com.chen.search.common.result.Result;
import com.chen.search.model.product.SkuPoster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("get/poster")
public class SkuPosterController {


    @Autowired
    private SkuPosterService skuPosterService;
    @GetMapping("getPoster/{id}")
    public Result getPoster(@PathVariable Long id){
        List<SkuPoster>  skuPosters= skuPosterService.getPoster(id);
        return Result.ok(skuPosters);

    }
}
