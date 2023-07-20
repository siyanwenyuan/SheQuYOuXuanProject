package com.chen.acl.controller;


import com.chen.acl.service.PermissionService;
import com.chen.acl.service.RoleService;
import com.chen.search.common.result.Result;
import com.chen.search.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;

    //查询所有菜单和查询角色以及分配过的菜单
    @ApiOperation("查询所有角色列表和已经分配过的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long id){
      Map<String,Object> map=roleService.getPermissionAllId(id);
      return Result.ok(map);

    }

    //为菜单分配角色
    @ApiOperation("分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long[] roleId,@RequestParam Long permissionId){
        roleService.savePermissionRoleId(permissionId,roleId);
        return Result.ok(null);


    }

    //查询所有菜单（按照树形结构（层级关系）显示）
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list(){
       List<Permission> list=permissionService.allPermissionList();
       return Result.ok(list);

    }
    //添加菜单
    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission){
        boolean is_save = permissionService.save(permission);
        if(is_save){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }


    //修改菜单
    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result update(@RequestBody Permission permission){
        boolean is_update = permissionService.updateById(permission);
        if(is_update){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }

    //删除菜单,因为其中存在树形结构，则需要进行递归删除
    @ApiOperation("删除菜单")
    @DeleteMapping("/remove/{id}")
    public Result delete(@PathVariable Long id){
        permissionService.removeChildById(id);
        return Result.ok(null);

    }

}
