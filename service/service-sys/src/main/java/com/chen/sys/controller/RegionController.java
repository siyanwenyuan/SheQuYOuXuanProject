package com.chen.sys.controller;


import com.chen.search.common.result.Result;
import com.chen.search.model.sys.Region;
import com.chen.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
@RestController
@RequestMapping("/admin/sys/region")
@Api(tags = "查询区域信息")
@CrossOrigin
public class RegionController {

    @Autowired
    private RegionService regionService;

    //根据区域关键字查询区域信息
    @ApiOperation("通过关键字查询区域信息")
    @GetMapping("findRegionByKeyword/{keyword}")
    public Result findByKeyWord(@PathVariable("keyword") String keyword){
      List<Region> regions= (List<Region>) regionService.findByWord(keyword);
       return Result.ok(regions);
    }

}

