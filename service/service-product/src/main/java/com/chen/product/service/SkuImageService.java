package com.chen.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.product.SkuImage;
import jdk.internal.dynalink.linker.LinkerServices;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
public interface SkuImageService extends IService<SkuImage> {


    List<SkuImage>  getImageById(Long id);
}
