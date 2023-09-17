package com.chen.product.controller;


import com.chen.product.service.SkuAttrValueService;
import com.chen.search.common.result.Result;
import com.chen.search.model.product.SkuAttrValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("get/SkuAttrValue")
public class SkuAttrValueController {

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @GetMapping("find/SkuAttrValues/{id}")
    public Result SkuAttrValue(@PathVariable Long id){
       List<SkuAttrValue>  skuAttrValues= skuAttrValueService.SkuAttrValue(id);
       return Result.ok(skuAttrValues);

    }
}
