package com.chen.client.product;


import com.chen.search.model.product.Category;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.product.SkuInfoVo;
import com.chen.search.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-product")
public interface ProductFeignClient {



    @GetMapping("aoi/product/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList();
    @GetMapping("api/product/inner/findAllCategoryList")
    public List<Category> findAllCategoryList();
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategoryId(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    @PostMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuId);
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword);

    @GetMapping("/api/product/inner/findCategoryList")
    public List<Category> findCategortList(@RequestBody List<Long> collects);


    @GetMapping("/api/product/inner/getSkuId/{skuId}")

    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId);


    @ApiOperation(value = "验证和锁定库存")
    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList
            ,@PathVariable String orderNo);



}
