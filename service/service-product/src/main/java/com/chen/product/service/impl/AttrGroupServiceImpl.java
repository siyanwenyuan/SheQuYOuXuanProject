package com.chen.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.product.mapper.AttrGroupMapper;
import com.chen.product.service.AttrGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.model.product.AttrGroup;
import com.chen.search.vo.product.AttrGroupQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 属性分组 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Override
    public IPage<AttrGroup> selectList(Page<AttrGroup> pageList, AttrGroupQueryVo attrGroupQueryVo) {
        String name = attrGroupQueryVo.getName();
        LambdaQueryWrapper<AttrGroup> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(name))
        {
            lambdaQueryWrapper.like(AttrGroup::getName,name);
        }
        Page<AttrGroup> attrGroupPage = baseMapper.selectPage(pageList, lambdaQueryWrapper);
        return attrGroupPage;
    }

    @Override
    public List<AttrGroup> selectListAll() {
        List<AttrGroup> attrGroups = baseMapper.selectList(null);
        return attrGroups;

    }
}
