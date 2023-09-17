package com.chen.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.activity.service.CouponInfoService;
import com.chen.search.common.result.Result;
import com.chen.search.model.activity.CouponInfo;
import com.chen.search.vo.activity.CouponRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.License;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {


    @Autowired
    private CouponInfoService couponInfoService;
    //优惠卷分页查询接口
    @ApiOperation("优惠卷分页查询接口")
    @GetMapping("{page}/{limit}")
    public Result page(@PathVariable Long page,@PathVariable Long limit)
    {
        Page<CouponInfo> couponInfoPage=new Page<>(page,limit);
       IPage<CouponInfo> pageModel= couponInfoService.selectPageLimit(couponInfoPage);
       return Result.ok(pageModel);
    }
    //根据id查询
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id)
    {
        CouponInfo couponInfo = couponInfoService.getById(id);
        return Result.ok(couponInfo);
    }
    //保存
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo){
        couponInfoService.save(couponInfo);
        return Result.ok(null);


    }

    //修改

    @PutMapping("update")
    public Result update(CouponInfo couponInfo){
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }
    //根据id删除
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        couponInfoService.removeById(id);
        return Result.ok(null);

    }
    //批量删除shequ-acl
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> Ids)
    {
        couponInfoService.removeByIds(Ids);
        return Result.ok(null);
    }
    //查询规则接口
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id)
    {
       Map<String,Object> map= couponInfoService.selectList(id);
       return Result.ok(map);
    }

    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo)
    {
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok(null);

    }



}

