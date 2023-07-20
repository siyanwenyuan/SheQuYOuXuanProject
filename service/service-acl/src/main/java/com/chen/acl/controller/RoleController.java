package com.chen.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.acl.service.RoleService;
import com.chen.search.common.result.Result;
import com.chen.search.model.acl.Role;
import com.chen.search.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理模块
 */

@Api(tags = "角色管理")//在接口文档中可以显示中文
@RestController
@RequestMapping("/admin/acl/role")
@CrossOrigin//解决前后端跨域问题

public class RoleController {//角色列表（条件分页查询）

    //注入对象
    @Autowired
    private RoleService roleService;

    /**
     * 角色列表（按照条件分页查询）
     *
     * @param current     当前页
     * @param limit       每页记录数
     * @param roleQueryVo 查询条件
     * @return
     * @PathVariable 用于需要传入参数值的接口
     */
    @ApiOperation("分页查询")
    @GetMapping("{current}/{limit}") //通过路径传参，这是路径传参的格式
    public Result pageLimit(@PathVariable long current, @PathVariable Long limit, RoleQueryVo roleQueryVo) {
        //创建分页对象，传递当前页和每页记录数
        Page<Role> pageParam = new Page<Role>(current, limit);

        //调用service中的方法，执行条件查询，返回分页对象
        IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }


    //根据id查询角色

    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    //添加角色

    /**
     * RequestBody:此注解表示接受前端传入的对象是json格式字符串
     * @param role
     * @return
     */
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody Role role){
        boolean is_success = roleService.save(role);
        if(is_success){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }


    }


    //修改角色
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role){
        boolean is_update = roleService.updateById(role);
        if(is_update){
            return Result.ok(null);
        }else{
            return Result.fail(null);

        }

    }

    //根据id删除角色

    /**
     * 此处的删除角色在mp中只是逻辑删除（也就是实际上的数据没有被删除，只是查询不到结果，然后只是修改了一下状态值）
     * @param id
     * @return
     */
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result deleteById(@PathVariable Long id){
        boolean is_delete = roleService.removeById(id);
        if(is_delete){
            return Result.ok(null);
        }else{
            return Result.fail(null);

        }
    }

    /**
     * 此处的list集合 也需要用requestbody 因为这个支持的就是集合，对象的数据格式的转换
     * @param ids
     * @return
     *
     * json中的数组格式，对应后端中的List集合信息
     *
     */
    //批量删除角色

    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        boolean is_batchRemove = roleService.removeByIds(ids);
        if(is_batchRemove){
            return Result.ok(null);
        }else{
            return Result.fail(null);

        }
    }
}
