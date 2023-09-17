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
import com.chen.search.common.config.RedissonConfig;
import com.chen.search.common.constant.RedisConst;
import com.chen.search.common.exception.SsyxException;
import com.chen.search.common.result.ResultCodeEnum;
import com.chen.search.model.product.SkuAttrValue;
import com.chen.search.model.product.SkuImage;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.model.product.SkuPoster;
import com.chen.search.vo.product.SkuInfoQueryVo;
import com.chen.search.vo.product.SkuInfoVo;
import com.chen.activity.mq.constant.MqConst;
import com.chen.activity.mq.service.RabbitService;
import com.chen.search.vo.product.SkuStockLockVo;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

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
    private RedissonClient redissonClient;


    @Autowired
    private SkuImageService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private RabbitService rabbitService;

   /* @Autowired
    private RedissonConfig redissonClient;*/


    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public IPage<SkuInfo> selectListAll(Page<SkuInfo> infoPage, SkuInfoQueryVo skuInfoQueryVo) {
        Long categoryId = skuInfoQueryVo.getCategoryId();
        String skuType = skuInfoQueryVo.getSkuType();
        String keyword = skuInfoQueryVo.getKeyword();
        LambdaQueryWrapper<SkuInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(categoryId)) {
            lambdaQueryWrapper.eq(SkuInfo::getCategoryId, categoryId);
        }
        if (!StringUtils.isEmpty(skuType)) {
            lambdaQueryWrapper.eq(SkuInfo::getSkuType, skuType);
        }
        if (!StringUtils.isEmpty(keyword)) {
            lambdaQueryWrapper.like(SkuInfo::getSkuName, keyword);
        }
        IPage<SkuInfo> skuInfoIPage = baseMapper.selectPage(infoPage, lambdaQueryWrapper);
        return skuInfoIPage;
    }

    @Override
    public void saveOne(SkuInfoVo skuInfoVo) {

        //保存基本信息,保存到skuinfo中去
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.insert(skuInfo);

        //保存海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            //不为空则添加到海报表中并且需要设置id
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            skuPosterService.saveBatch(skuPosterList);
        }
        //保存图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            for (SkuImage skuImage : skuImagesList
            ) {
                skuImage.setSkuId(skuInfo.getId());

            }
            skuImagesService.saveBatch(skuImagesList);
        }
        //保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList
            ) {
                skuAttrValue.setSkuId(skuInfo.getId());

            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }

    }

    @Override
    public void updateIdStatus(Long id, Integer status) {
        if (status == 1) {
            SkuInfo skuInfo = baseMapper.selectById(id);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            //整合mq,发送消息到mq中去
            rabbitService.sendMsg(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_UPPER,
                    id);


        } else {
            SkuInfo skuInfo2 = baseMapper.selectById(id);
            skuInfo2.setPublishStatus(status);
            baseMapper.updateById(skuInfo2);

            //下架，发送到交换机中

            rabbitService.sendMsg(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_LOWER,
                    id);

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

    @Override
    public List<SkuInfo> findSkuInFoListById(List<Long> skuId) {
        List<SkuInfo> skuInfoList = baseMapper.selectBatchIds(skuId);
        return skuInfoList;

    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(SkuInfo::getSkuName, keyword);
        List<SkuInfo> skuInfoList = baseMapper.selectList(lambdaQueryWrapper);
        return skuInfoList;

    }

    //获取新人专享商品
    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {

        /**
         * 首页数据显示三条，可以通过一个分页查询进行显示，也可以通过一个集合的遍历进行显示
         */
        //得到数据显示三条，可以进行分页查询
        Page<SkuInfo> page = new Page<>(1, 3);

        //首先通过条件进行查询
        LambdaQueryWrapper<SkuInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SkuInfo::getIsNewPerson, 1);
        lambdaQueryWrapper.eq(SkuInfo::getCheckStatus, 1);
        lambdaQueryWrapper.orderByDesc(SkuInfo::getStock);//根据库存降序排序
        //进行分页查询
        IPage<SkuInfo> skuInfoPage = baseMapper.selectPage(page, lambdaQueryWrapper);

        //封装最后的结果集

        List<SkuInfo> infoList = skuInfoPage.getRecords();
        return infoList;
    }

    @Override
    public SkuInfoVo getSkuInfo(Long skuId) {

        //首先new 一个对应数据的对象
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        //通过skuId查询对应的基本信息
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        //通过skuId查询对应的图片
        List<SkuImage> images = skuImagesService.getImageById(skuId);
        //通过id查询对应的海报
        List<SkuPoster> poster = skuPosterService.getPoster(skuId);
        //通过id查询对应的商品属性
        List<SkuAttrValue> skuAttrValues = skuAttrValueService.SkuAttrValue(skuId);

        //将数据封装到skuInfoVo中去
        //因为对应skuinfo的数据其中没有set可以直接设置，需要此时的使用工具类中的直接复制即可
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        skuInfoVo.setSkuPosterList(poster);
        skuInfoVo.setSkuImagesList(images);

        skuInfoVo.setSkuAttrValueList(skuAttrValues);


        return skuInfoVo;
    }

    @Override
    public Boolean chekAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {

        //首先对集合进行判断，如果为空，直接返回一个异常
        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        //如果不为空，则得到其中的每个对象，通过stream流的形式
        //对其中的每个对象进行锁定和验证
        skuStockLockVoList.stream().forEach(skuStockLockVo -> {
            this.chekLock(skuStockLockVo);
        });
        //如果其中的一个锁定失败，则整个的锁定都需要解除，
        //其中通过isLock进行判断，详细看这个字段的解析
        //其中使用anyMatch表示任意一个匹配失败，则整个匹配失败
        boolean flag = skuStockLockVoList.stream().anyMatch(skuStockLockVo ->
                !skuStockLockVo.getIsLock());
        //对其中的商品进行解锁，解锁也即是修改数据库中的字段
        if (flag) {
            skuStockLockVoList.stream().filter(SkuStockLockVo::getIsLock).
                    forEach(skuStockLockVo -> {
                        baseMapper.unLockStock(skuStockLockVo.getSkuId(),
                                skuStockLockVo.getSkuNum());
                    });


            //返回失败的状态
            return false;

        }
        //如果都锁定成功，需要缓存进redis
        redisTemplate.opsForValue().set(RedisConst.SROCK_INFO + orderNo, skuStockLockVoList);


        return true;
    }

    @Override
    public void miusStock(String orderNo) {
        //减少库存
     List<SkuStockLockVo> skuStockLockVoList= (List<SkuStockLockVo>) redisTemplate.opsForValue().get(RedisConst.SROCK_INFO+orderNo);
     if(StringUtils.isEmpty(skuStockLockVoList))
     {
         return;
     }
     //遍历每个集合对象，对每个对象中的库存减少
      skuStockLockVoList.forEach(skuStockLockVo -> {
          baseMapper.miusStockDelete(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
      });

     //删除redis中的数据
        redisTemplate.delete(RedisConst.SROCK_INFO+orderNo);

    }

    private void chekLock(SkuStockLockVo skuStockLockVo) {

        //此处进行加锁操作
        //获取公平锁： 在队列中排的时间越长，月快得到
        Lock rLock = redissonClient.getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());


        //加锁
        rLock.lock();
        try {
            //从数据库中进行验证
            SkuInfo skuInfo = baseMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (skuInfo == null)
            //如果查询结果为为空说明失败需要全部解锁
            {
                skuStockLockVo.setIsLock(false);
                return;


            }
            //如果不为空，则说明存在，需要锁定库存，
            //也就是对数据库中的商品库存状态的更新操作
            Integer rows = baseMapper.lockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (rows == 1) {
                skuStockLockVo.setIsLock(false);

            }

        } finally {
            //关闭锁资源
            rLock.unlock();
        }
    }
}
