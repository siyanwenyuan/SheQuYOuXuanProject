package com.chen.acl.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.acl.service.AdminService;
import com.chen.acl.service.RoleService;
import com.chen.search.common.result.Result;
import com.chen.search.common.utils.MD5Utils;
import com.chen.search.model.acl.Admin;
import com.chen.search.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.lang.Object;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/admin/acl/user")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    //查询所有角色列表，根用户id查询用户分配的角色列表
    @ApiOperation("查询角色列表")
    @GetMapping("toAssign/{adminId}")
    public Result toAssign(@PathVariable Long adminId){
        //这里使用map集合对结果结果进行封装，因为这里查询到的包含两部分的数据
        // 所有角色列表  用户分配的角色列表 因此使用map key--value的结构更合适
       // Map<String, Object> map=roleService.getRoleByAdminId(adminId);
        Map<String,Object> map=roleService.getRoleByAdminId(adminId);
        return Result.ok(map);
    }


    /**
     * 为用户分配角色
     * 其中一个用户可能对应多个角色，所以roleId使用数组
     * @param adminId
     * @param roleId
     * @return
     */
    @ApiOperation("为用户分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long adminId,@RequestParam Long[] roleId){

        roleService.saveAdminRoleId(adminId,roleId);
        return Result.ok(null);


    }




    //列表查询，分页查询
    @ApiOperation("列表查询")
    @GetMapping("{current}/{limit}")
    public Result list(@PathVariable Long current, @PathVariable Long limit,
                       AdminQueryVo adminQueryVo){
        Page<Admin> adminPage=new Page<>(current,limit);
        IPage<Admin> pageModel=adminService.selectPageUser(adminPage,adminQueryVo);
        return Result.ok(pageModel);

    }

    //id查询用户
    @ApiOperation("查询单个用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }


    //添加用户
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody Admin admin){
        //添加用户的时候需要对密码进行加密
        //获取用户密码
        String password = admin.getPassword();
        //对密码进行md5加密
        String encrypt = MD5Utils.encrypt(password);
        //将加密后的密码存储到admin对象中
        admin.setPassword(encrypt);
        //调用方法存储整个对象
        boolean is_save = adminService.save(admin);
        if(is_save){
            return Result.ok(null);

        }else{
            return Result.fail(null);
        }
    }


    //修改用户
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin){
        boolean is_update = adminService.updateById(admin);
        if(is_update){
            return Result.ok(null);

        }else{
            return Result.fail(null);
        }
    }

    //根据id删除用户
    @ApiOperation("删除用户")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Long id){
        boolean is_delete = adminService.removeById(id);
        if(is_delete){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }

    //批量删除用户
    @ApiOperation("批量删除用户")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        boolean is_batchRemove = adminService.removeByIds(ids);
        if (is_batchRemove) {
          return  Result.ok(null);
        } else {
           return  Result.fail(null);
        }
    }




}
