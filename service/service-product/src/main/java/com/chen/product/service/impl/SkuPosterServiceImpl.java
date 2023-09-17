package com.chen.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.product.mapper.SkuPosterMapper;
import com.chen.product.service.SkuPosterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.model.product.SkuPoster;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品海报表 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {

    @Override
    public List<SkuPoster> getPoster(Long id) {
  /*      LambdaQueryWrapper lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SkuPoster::getSkuId,id);*/

        LambdaQueryWrapper<SkuPoster> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SkuPoster::getSkuId,id);
        List<SkuPoster> skuPosters = baseMapper.selectList(lambdaQueryWrapper);



        /*List<SkuPoster> listSkuPoster=new ArrayList<>();
        SkuPoster skuPoster = baseMapper.selectById(id);
        listSkuPoster.add(skuPoster);*/
        return skuPosters;




    }
}
