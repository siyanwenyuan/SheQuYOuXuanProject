package com.chen.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.product.SkuInfoQueryVo;
import com.chen.search.vo.product.SkuInfoVo;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
public interface SkuInfoService extends IService<SkuInfo> {

    IPage<SkuInfo> selectListAll(Page<SkuInfo> infoPage, SkuInfoQueryVo skuInfoQueryVo);

    void saveOne(SkuInfoVo skuInfoVo);

    void updateIdStatus(Long id, Integer status);

    void checkStatus(Long id, Integer status);

    void isNewPerson(Long id, Integer status);
}
