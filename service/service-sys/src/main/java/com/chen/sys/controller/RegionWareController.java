package com.chen.sys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.search.common.result.Result;
import com.chen.search.model.sys.RegionWare;
import com.chen.search.vo.sys.RegionWareQueryVo;
import com.chen.sys.service.RegionWareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 城市仓库关联表 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
@Api(tags = "开通区域接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
public class RegionWareController {

    @Autowired
    private RegionWareService regionWareService;
        //分页查询
    @ApiOperation("列表查询")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, RegionWareQueryVo regionWareQueryVo){
        Page<RegionWare> pageParam=new Page<>(page,limit);
        IPage<RegionWare> pageModel=regionWareService.selectPageRegionWare(pageParam,regionWareQueryVo);
        return Result.ok(pageModel);
    }

    //添加仓库区域
    @ApiOperation("添加仓库区域")
    @PostMapping("save")
    public Result save(@RequestBody RegionWare regionWare){
        regionWareService.saveRegionWare(regionWare);
        return Result.ok(null);
    }

    //删除开通区域
    @ApiOperation("根据id删除开通区域")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id)
    {
        regionWareService.removeById(id);
        return Result.ok(null);
    }

    //取消开通区域
    @ApiOperation("取消开通区域")
    @PostMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status)
    {
        regionWareService.updateStatus(id,status);
        return Result.ok(null);

    }
}

