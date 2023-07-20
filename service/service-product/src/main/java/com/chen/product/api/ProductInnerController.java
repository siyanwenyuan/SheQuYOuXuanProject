package com.chen.product.api;


import com.chen.product.service.CategoryService;
import com.chen.product.service.SkuInfoService;
import com.chen.search.model.product.Category;
import com.chen.search.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
public class ProductInnerController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategoryId(@PathVariable Long categoryId){
        Category category = categoryService.getById(categoryId);
        return category;
    }

    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return skuInfo;
    }
}
