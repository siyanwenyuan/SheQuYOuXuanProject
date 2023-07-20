package com.chen.acl.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.acl.mapper.PermissionRoleMapper;
import com.chen.acl.service.PermissionRoleService;
import com.chen.search.model.acl.RolePermission;
import org.springframework.stereotype.Service;

@Service
public class PermissionRoleServiceImpl extends ServiceImpl<PermissionRoleMapper, RolePermission> implements PermissionRoleService {
}
