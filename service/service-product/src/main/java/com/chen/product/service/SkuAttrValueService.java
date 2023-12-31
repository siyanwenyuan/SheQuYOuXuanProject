package com.chen.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.product.SkuAttrValue;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    List<SkuAttrValue> SkuAttrValue(Long id);
}
