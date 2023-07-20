package com.chen.acl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.acl.mapper.RoleMapper;
import com.chen.acl.service.AdminRoleService;
import com.chen.acl.service.PermissionRoleService;
import com.chen.acl.service.RoleService;
import com.chen.search.model.acl.AdminRole;
import com.chen.search.model.acl.Role;
import com.chen.search.model.acl.RolePermission;
import com.chen.search.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.lang.Object;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private PermissionRoleService permissionRoleService;

    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo) {
        //获取条件值
        String roleName = roleQueryVo.getRoleName();

        //封装mp查询条件，创建条件对象
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断条件值是否为空，如果不为空，则封装查询条件
        if (!StringUtils.isEmpty(roleName)) {
            lambdaQueryWrapper.like(Role::getRoleName, roleName);
        }
        //调用方法，实现分页查询
        Page<Role> rolePage = baseMapper.selectPage(pageParam, lambdaQueryWrapper);
        //返回查询对象
        return rolePage;
    }

    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        //查询所有角色列表
        List<Role> roles = baseMapper.selectList(null);

        //通过传入的用户id查询到对应的用户角色对象
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        wrapper.eq(AdminRole::getAdminId, adminId);
        //将查询道德对象封装成List集合
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);
        //通过stream流得到每个用户角色对象的id ，存入List集合中
        List<Long> collectIds = adminRoleList.stream().map(item -> item.getRoleId()).collect(Collectors.toList());
        //创建一个新的集合
        List<Role> assignList = new ArrayList<>();
        //判断已经分配的用户角色id在角色id中是否存在
        for (Role role : roles) {
            if (collectIds.contains(role.getId())) {
                assignList.add(role);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("allRoleList", roles);
        map.put("assignRoles", assignList);

        return map;

    }


    @Override
    public Map<String, Object> getPermissionAllId(Long id) {
        //先查询所有菜单
        List<Role> roles = baseMapper.selectList(null);
        //根据传入的id查询已经分配过的菜单
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getPermissionId, id);
        List<RolePermission> rolePermissions = permissionRoleService.list(wrapper);
        List<Long> collects = rolePermissions.stream().map(item -> item.getRoleId()).collect(Collectors.toList());
        List<Role> assignList = new ArrayList<>();
        for (Role role : roles) {
            if (collects.contains(role.getId())) {
                assignList.add(role);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("roles", roles);
        map.put("assignList", assignList);
        return map;

    }


    @Override
    public void saveAdminRoleId(Long adminId, Long[] roleId) {
        //如果已经分配角色，需要删除
        //通过用户id删除admin_role表中对应的数据
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);//设置条件
        adminRoleService.remove(wrapper);
        //重新进行分配，遍历每个角色id 将用户id+角色id 存储到admin_role表中

        /**
         * 使用mp中的批量保存实现代码的优化
         */
        List<AdminRole> list = new ArrayList<>();
        for (Long id : roleId
        ) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(id);
            //将遍历后的数据先保存到集合中
            list.add(adminRole);
        }
        //最后进行批量保存
        adminRoleService.saveBatch(list);


    }

    @Override
    public void savePermissionRoleId(Long permissionId, Long[] roleId) {

        //首先进行删除
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getPermissionId, permissionId);
        permissionRoleService.remove(wrapper);
        //在进行批量保存
        List<RolePermission> lists = new ArrayList<>();
        for (Long id : roleId) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(permissionId);
            rolePermission.setRoleId(id);
        }
        //最后调用mp中的方法进行保存
        permissionRoleService.saveBatch(lists);

    }


}
