package com.chen.acl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.acl.mapper.PermissionMapper;
import com.chen.acl.service.PermissionService;
import com.chen.acl.utils.BuildPermissionList;
import com.chen.search.model.acl.Permission;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    //查询所有菜单
    @Override
    public List<Permission> allPermissionList() {
        //查询所有菜单
        List<Permission> permissionList = baseMapper.selectList(null);
        //转换数据格式,通过一个工具类
        List<Permission> lists= BuildPermissionList.buildPermissionHelper(permissionList);
        return lists;
    }

    //递归删除菜单
    @Override
    public void removeChildById(Long id) {
        //将所有的id封装到一个集合中
        List<Long> ids = new ArrayList<>();
        //通过上层的id查询到对应的子菜单id
        //通过递归的方式得到子菜单id
        this.getAllPermissionId(id, ids);
        //主菜单id也需要封装到集合中进行删除
        ids.add(id);
        //通过集合中的id进行批量删除
        baseMapper.deleteBatchIds(ids);

    }

    private void getAllPermissionId(Long id, List<Long> ids) {

        //构造条件
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);
        // 得到子菜单的id，进行封装到List集合中
        List<Permission> permissionIds = baseMapper.selectList(wrapper);
        //进行递归查询
        permissionIds.stream().forEach(item -> {
            ids.add(item.getId());
            //递归调用
            this.getAllPermissionId(item.getId(), ids);
        });
    }
}
