package com.chen.product.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.search.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

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


    //验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);

    //锁定库存
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //解锁
    void unLockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    void miusStockDelete(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
