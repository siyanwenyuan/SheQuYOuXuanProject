package com.chen.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.acl.Admin;
import com.chen.search.vo.acl.AdminQueryVo;

public interface AdminService extends IService<Admin>  {
    IPage<Admin> selectPageUser(Page<Admin> adminPage, AdminQueryVo adminQueryVo);
}
