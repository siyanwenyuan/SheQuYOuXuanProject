package com.chen.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.product.mapper.CategoryMapper;
import com.chen.product.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.model.product.Category;
import com.chen.search.vo.product.CategoryQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 商品三级分类 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Override
    public IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryQueryVo categoryQueryVo) {
        String name = categoryQueryVo.getName();
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            lambdaQueryWrapper.like(Category::getName,name);
        }
       IPage<Category> categoryIPage= baseMapper.selectPage(pageParam,lambdaQueryWrapper);
        return categoryIPage;
    }

    @Override
    public Object findAllList() {
        List<Category> categories = baseMapper.selectList(null);
        return categories;
    }
}
