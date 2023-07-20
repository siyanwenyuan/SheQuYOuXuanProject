package com.chen.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.product.service.AttrGroupService;
import com.chen.search.common.result.Result;
import com.chen.search.model.product.AttrGroup;
import com.chen.search.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性分组 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */

@Api(tags = "属性分组接口")
@RestController
@RequestMapping("/admin/product/attrGroup")
@CrossOrigin
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    //分页查询
    @ApiOperation("列表查询")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, AttrGroupQueryVo attrGroupQueryVo) {
        Page<AttrGroup> pageList = new Page<>(page, limit);
        IPage<AttrGroup> pageModel = attrGroupService.selectList(pageList, attrGroupQueryVo);
        return Result.ok(pageModel);
    }

    //通过id查询
    @ApiOperation("通过id查询")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        AttrGroup aa = attrGroupService.getById(id);
        return Result.ok(aa);
    }

    //保存
    @ApiOperation("保存")
    @PostMapping("save")
    public Result save(@RequestBody AttrGroup attrGroup){
        attrGroupService.save(attrGroup);
        return Result.ok(null);
    }
    //根据id修改
    @ApiOperation("根据id修改")
    @PutMapping("update")
    public Result update(@RequestBody AttrGroup attrGroup){
        attrGroupService.updateById(attrGroup);
        return Result.ok(null);
    }

    //根据id删除
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result deleteById(@PathVariable Long id){
        attrGroupService.removeById(id);
        return Result.ok(null);
    }

    //批量删除
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long>ids)
    {
        attrGroupService.removeByIds(ids);
        return Result.ok(null);
    }
    @ApiOperation("查询所有")
    @GetMapping("findAllList")
    public Result findAllList(){
      List<AttrGroup> attrGroups= attrGroupService.selectListAll();
      return Result.ok(attrGroups);
    }
}

