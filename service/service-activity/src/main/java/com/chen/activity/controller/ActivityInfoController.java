package com.chen.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.activity.service.ActivityInfoService;
import com.chen.search.common.result.Result;
import com.chen.search.model.activity.ActivityInfo;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.activity.ActivityRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
@RestController
@RequestMapping("admin/activity/activityInfo")
@Api(tags = "活动列表")
public class ActivityInfoController {
    @Autowired
    private ActivityInfoService activityInfoService;


    //reference to Customer is ambiguous
    //
    @ApiOperation("活动列表查询")
    @GetMapping("{page}/{limit}")
    public Result getPageResult(@PathVariable Long page,@PathVariable Long limit)
    {
        Page<ActivityInfo> PageParam=new Page<>(page,limit);
        IPage<ActivityInfo> pageModel=activityInfoService.selectPageList(PageParam);
        return Result.ok(pageModel);

    }
    //保存
    @PostMapping("save")
    public Result save(@RequestBody ActivityInfo activityInfo){
          activityInfoService.save(activityInfo);
          return Result.ok(null);

    }

    //查询活动列表
    @ApiOperation("查询活动列表")
    @GetMapping("findActivityRuleList/{id}")
    public Result findRuleList(@PathVariable Long id){
       Map<String, Object> selectRuleListMap= activityInfoService.selectRule(id);
       return Result.ok(selectRuleListMap);
    }

    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo)
    {
        activityInfoService.saveActivityrule(activityRuleVo);
        return Result.ok(null);
    }
    //通过id查询
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id)
    {
        activityInfoService.getById(id);
        return Result.ok(null);
    }
    //根据id修改
    @PutMapping("update")
    public Result update(@RequestBody ActivityInfo activityInfo){
        activityInfoService.updateById(activityInfo);
        return Result.ok(null);

    }

    //根据id删除
    @DeleteMapping("remove/{id}")
    public Result deleteById(@PathVariable Long id)
    {
        activityInfoService.removeById(id);
        return Result.ok(null);

    }

    //
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable String keyword){
      List<SkuInfo> skuInfoList= activityInfoService.findSkuInfoByKeyword(keyword);
      return Result.ok(skuInfoList);
    }






}

