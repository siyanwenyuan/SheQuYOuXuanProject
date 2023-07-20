package com.chen.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.product.mapper.SkuInfoMapper;
import com.chen.product.service.SkuAttrValueService;
import com.chen.product.service.SkuImageService;
import com.chen.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.product.service.SkuPosterService;
import com.chen.search.model.product.SkuAttrValue;
import com.chen.search.model.product.SkuImage;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.model.product.SkuPoster;
import com.chen.search.vo.product.SkuInfoQueryVo;
import com.chen.search.vo.product.SkuInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private SkuImageService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Override
    public IPage<SkuInfo> selectListAll(Page<SkuInfo> infoPage, SkuInfoQueryVo skuInfoQueryVo) {
        Long categoryId = skuInfoQueryVo.getCategoryId();
        String skuType = skuInfoQueryVo.getSkuType();
        String keyword = skuInfoQueryVo.getKeyword();
        LambdaQueryWrapper<SkuInfo> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        if(!StringUtils.isEmpty(categoryId)){
            lambdaQueryWrapper.eq(SkuInfo::getCategoryId,categoryId);
        }
        if(!StringUtils.isEmpty(skuType)){
            lambdaQueryWrapper.eq(SkuInfo::getSkuType,skuType);
        }
        if(!StringUtils.isEmpty(keyword))
        {
            lambdaQueryWrapper.like(SkuInfo::getSkuName,keyword);
        }
        IPage<SkuInfo> skuInfoIPage=baseMapper.selectPage(infoPage,lambdaQueryWrapper);
        return skuInfoIPage;
    }

    @Override
    public void saveOne(SkuInfoVo skuInfoVo) {

        //保存基本信息,保存到skuinfo中去
        SkuInfo skuInfo=new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        baseMapper.insert(skuInfo);

        //保存海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if(!CollectionUtils.isEmpty(skuPosterList)){
            //不为空则添加到海报表中并且需要设置id
            for (SkuPoster skuPoster:skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            skuPosterService.saveBatch(skuPosterList);
        }
        //保存图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if(!CollectionUtils.isEmpty(skuImagesList)){
            for (SkuImage skuImage: skuImagesList
                 ) {
                skuImage.setSkuId(skuInfo.getId());

            }
            skuImagesService.saveBatch(skuImagesList);
        }
        //保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue: skuAttrValueList
                 ) {
                skuAttrValue.setSkuId(skuInfo.getId());

            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }

    }

    @Override
    public void updateIdStatus(Long id, Integer status) {
        if(status==1){
            SkuInfo skuInfo = baseMapper.selectById(id);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
        }else{
           SkuInfo skuInfo2= baseMapper.selectById(id);
           skuInfo2.setPublishStatus(status);
           baseMapper.updateById(skuInfo2);

        }

    }

    @Override
    public void checkStatus(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setCheckStatus(status);
        baseMapper.updateById(skuInfo);
    }

    @Override
    public void isNewPerson(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setIsNewPerson(status);
        baseMapper.updateById(skuInfo);
    }
}
