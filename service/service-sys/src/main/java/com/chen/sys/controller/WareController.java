package com.chen.sys.controller;


import com.chen.search.common.result.Result;
import com.chen.search.model.sys.Ware;
import com.chen.sys.service.WareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 仓库表 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
@RestController
@RequestMapping("/admin/sys/ware")
@Api(tags = "查询所有仓库")
public class WareController {

    @Autowired
    private WareService wareService;

    //查询所有仓库
    @ApiOperation("查询所有仓库")
    @GetMapping("findAllList")
    public Result findAllList(){

      List<Ware> wares= wareService.selectAll();
       return  Result.ok(wares);
    }

}

