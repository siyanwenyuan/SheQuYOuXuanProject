package com.chen.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.acl.Role;
import com.chen.search.vo.acl.RoleQueryVo;

import java.util.Map;

public interface RoleService extends IService<Role>   {
    //分页查询
    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);


    Map<String, Object> getRoleByAdminId(Long adminId);

    void saveAdminRoleId(Long adminId, Long[] roleId);

    Map<String, Object> getPermissionAllId(Long id);


    void savePermissionRoleId(Long permissionId, Long[] roleId);
}
