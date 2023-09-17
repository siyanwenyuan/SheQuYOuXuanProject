package com.chen.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.product.mapper.SkuImageMapper;
import com.chen.product.service.SkuImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.model.product.SkuImage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品图片 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {


    @Override
    public List<SkuImage> getImageById(Long id) {


        List<SkuImage> skuImages = baseMapper.selectList(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, id));

        return skuImages;

    }
}
