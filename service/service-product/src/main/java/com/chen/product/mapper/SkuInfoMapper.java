package com.chen.product.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.search.model.product.SkuInfo;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void selectList(Page<SkuInfo> infoPage, LambdaQueryWrapper<SkuInfo> lambdaQueryWrapper);
}
