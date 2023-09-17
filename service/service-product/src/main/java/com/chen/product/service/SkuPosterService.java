package com.chen.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.product.SkuPoster;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> getPoster(Long id);
}
