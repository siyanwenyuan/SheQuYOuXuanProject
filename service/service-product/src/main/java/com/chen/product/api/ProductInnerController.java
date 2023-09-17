package com.chen.product.api;


import com.chen.product.service.CategoryService;
import com.chen.product.service.SkuInfoService;
import com.chen.search.model.product.Category;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.product.SkuInfoVo;
import com.chen.search.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductInnerController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("inner/findAllCategoryList")
    public List<Category> findAllCategoryList() {
        List<Category> categoryList = categoryService.list();

        return categoryList;

    }


    //获取新人专享商品
    @GetMapping("inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList(){
        List<SkuInfo> skuInfoList=skuInfoService.findNewPersonSkuInfoList();
        return skuInfoList;

    }

    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategoryId(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return category;
    }

    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return skuInfo;
    }

    @PostMapping("inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuId) {
        List<SkuInfo> skuInfoList = skuInfoService.findSkuInFoListById(skuId);
        return skuInfoList;

    }

    @GetMapping("inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        List<SkuInfo> skuInfoList = skuInfoService.findSkuInfoByKeyword(keyword);
        return skuInfoList;

    }

    @GetMapping("inner/findCategoryList")
    public List<Category> findCategortList(@RequestBody List<Long> collects) {
        return categoryService.listByIds(collects);

    }

    //根据skuId获取sku信息
    @GetMapping("inner/getSkuId/{skuId}")

    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId){

       return  skuInfoService.getSkuInfo(skuId);
    }


    //验证和锁定库存
    @ApiOperation(value = "验证和锁定库存")
    @PostMapping("inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList
    ,@PathVariable String orderNo){
        return skuInfoService.chekAndLock(skuStockLockVoList,orderNo);

    }

}
