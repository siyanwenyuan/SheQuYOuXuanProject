package com.chen.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.acl.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    List<Permission> allPermissionList();

    //递归删除
    void removeChildById(Long id);
}
