package com.chen.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.product.service.SkuInfoService;
import com.chen.search.common.result.Result;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.product.SkuInfoQueryVo;
import com.chen.search.vo.product.SkuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * sku信息 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@RestController
@RequestMapping("/admin/product/skuInfo")
@Api(tags = "sku信息接口")
@CrossOrigin
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @ApiOperation("列表查询")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, SkuInfoQueryVo skuInfoQueryVo)
    {
        Page<SkuInfo> infoPage=new Page<>(page,limit);
      IPage<SkuInfo> skuModel= skuInfoService.selectListAll(infoPage,skuInfoQueryVo);
      return Result.ok(skuModel);
    }

    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        SkuInfo skuinfo = skuInfoService.getById(id);
        return Result.ok(skuinfo);
    }

    @ApiOperation("保存")
    @PostMapping("save")
    public Result save(@RequestBody SkuInfoVo skuInfoVo){
        skuInfoService.saveOne(skuInfoVo);
        return Result.ok(null);
    }

    @ApiOperation("修改")
    @PutMapping("update")
    public Result update(@RequestBody SkuInfoVo skuInfoVo){
        skuInfoService.updateById(skuInfoVo);
        return Result.ok(null);
    }

    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result deleteById(@PathVariable Long id){
        skuInfoService.removeById(id);
        return Result.ok(null);
    }
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result deleteIds(@RequestBody List<Long>ids)
    {
        skuInfoService.removeByIds(ids);
        return Result.ok(null);
    }

    @ApiOperation("商品上架")
    @GetMapping("publish/{id}/{status}")
    public Result publish(@PathVariable Long id,@PathVariable Integer status)
    {
       skuInfoService.updateIdStatus(id,status);
       return Result.ok(null);
    }

    @ApiOperation("商品审核状态")
    @GetMapping("check/{id}/{status}")
    public Result check(@PathVariable Long id, @PathVariable Integer status)
    {
        skuInfoService.checkStatus(id,status);
        return Result.ok(null);
    }

    @ApiOperation("新人专享")
    @GetMapping("isNewPerson/{id}/{status}")
    public Result isNewPerson(@PathVariable Long id,@PathVariable Integer status)
    {
       skuInfoService.isNewPerson(id,status);
        return Result.ok(null);

    }





}

