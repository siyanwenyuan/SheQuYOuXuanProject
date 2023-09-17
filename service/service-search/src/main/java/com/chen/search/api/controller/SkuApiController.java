package com.chen.search.api.controller;


import com.chen.search.common.result.Result;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.model.search.SkuEs;
import com.chen.search.service.SkuService;
import com.chen.search.vo.search.SkuEsQueryVo;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController

@RequestMapping("/api/search/sku")
public class SkuApiController {


    @Autowired
    private SkuService skuService;

    @ApiOperation("获取爆款商品")
    @GetMapping("/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList(){
        return skuService.findHotSkuList();
    }


    @ApiOperation("查询商品分类信息")
    @GetMapping("{page}/{limit}")
    public Result listSku(@PathVariable Integer page,
                          @PathVariable Integer limit,
                          SkuEsQueryVo skuEsQueryVo){
        //此处进行分页查询
        Pageable pageModel= PageRequest.of(page-1,limit);
      Page<SkuEs> skuEsPage=  skuService.search(pageModel,skuEsQueryVo);
      return Result.ok(skuEsPage);
    }

    //更新商品热度
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable Long skuId){
        skuService.incrHotScore(skuId);
        return true;

    }

}
