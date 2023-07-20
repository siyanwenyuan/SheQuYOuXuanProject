package com.chen.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.product.AttrGroup;
import com.chen.search.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
public interface AttrGroupService extends IService<AttrGroup> {

    IPage<AttrGroup> selectList(Page<AttrGroup> pageList, AttrGroupQueryVo attrGroupQueryVo);

    List<AttrGroup> selectListAll();
}
