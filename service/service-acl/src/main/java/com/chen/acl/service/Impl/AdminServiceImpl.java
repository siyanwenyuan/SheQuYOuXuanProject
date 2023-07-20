package com.chen.acl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.acl.mapper.AdminMapper;
import com.chen.acl.service.AdminService;
import com.chen.search.model.acl.Admin;
import com.chen.search.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public IPage<Admin> selectPageUser(Page<Admin> adminPage, AdminQueryVo adminQueryVo) {
        String userName=adminQueryVo.getUsername();
        String name=adminQueryVo.getName();
        //创建一个mp构造器
        LambdaQueryWrapper<Admin> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(userName))
        {
            lambdaQueryWrapper.eq(Admin::getUsername,userName);
        }
        if(!StringUtils.isEmpty(name)){
            lambdaQueryWrapper.like(Admin::getName,name);
        }
        Page<Admin> adminParam = baseMapper.selectPage(adminPage, lambdaQueryWrapper);

        return adminParam;
    }
}
