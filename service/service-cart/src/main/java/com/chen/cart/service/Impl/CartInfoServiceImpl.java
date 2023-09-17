package com.chen.cart.service.Impl;

import com.chen.cart.service.CartInfoService;
import com.chen.client.product.ProductFeignClient;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.common.constant.RedisConst;
import com.chen.search.common.exception.SsyxException;
import com.chen.search.common.result.Result;
import com.chen.search.common.result.ResultCodeEnum;
import com.chen.search.enums.SkuType;
import com.chen.search.model.order.CartInfo;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.order.CartInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartInfoServiceImpl implements CartInfoService {


    @Qualifier("productFeignClient")
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;


    private String getKey(Long userId) {
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;

    }

    @Override
    public void addToCart(Long userId, Long skuId, Integer skuNum) {

        //通过userId从redis中得到对应的skuId+skuNum

        String key = this.getKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(key);


        //得到了对应的skuId后进行判断

        CartInfo cartInfo = null;
        if (boundHashOperations.hasKey(userId.toString())) {
            //如果skuId以及存在，则不再进行添加

            //如果已经存在，则从中得到数据，在源数据的基础上进行数量增加
            cartInfo = boundHashOperations.get(skuId.toString());
            int current = cartInfo.getSkuNum() + skuNum;
            if (current < 0) {
                return;

            }
            cartInfo.setSkuNum(current);
            cartInfo.setCurrentBuyNum(current);

            Integer perLimit = cartInfo.getPerLimit();
            if (current > perLimit) {
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }

            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());


        } else {

            //如果skuId不存在,则进行添加

            skuNum = 1;
            cartInfo = new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            //此处进行一个数据非空的判断，目的是为了防止出现空指针异常
            if (skuInfo == null) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }


            //此处进行对象数据的封装
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());

            //最后更新redis缓存
            boundHashOperations.put(skuId.toString(), cartInfo);


            this.setCartKeyExpire(key);


        }


        //设置有效时间

    }

    @Override
    public void deleteCart(Long skuId, Long userId) {

        //首先从redis中获取到对应的数据
        BoundHashOperations<String,String,CartInfo> hashOperation = redisTemplate.boundHashOps(this.getKey(userId));
        //判断其中是否包含skuId对应的值
        if(hashOperation.hasKey(skuId.toString())){
            //删除对应的值
            hashOperation.delete(skuId.toString());


        }
    }

    @Override
    public void deleteAllCart(Long userId) {
        /**
         * 首先通过userId得到skuId+skuNum这个整体对象
         */
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(this.getKey(userId));

        //首先将redis中得到的数据变成集合
        List<CartInfo> cartInfoList = hashOperations.values();
        //遍历集合，通过skuId进行删除购物车中的每个数据
        for (CartInfo cartInfo : cartInfoList) {
            hashOperations.delete(cartInfo.getUserId().toString());

        }


    }


    //批量删除
    @Override
    public void batchDeleteCart(List<Long> skuIdList,Long userId) {

        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(this.getKey(userId));
        //首先遍历这个集合，通过forEach循环，使用lambda表达式
        skuIdList.forEach(skuId->{
            hashOperations.delete(skuId.toString());

        });
    }

    @Override
    public List<CartInfo> cartList(Long userId) {
        List<CartInfo> cartInfoList=new ArrayList<>();
        if(StringUtils.isEmpty(userId)){
            return cartInfoList;
        }

        //得到skuId+skuNum部分的数据对象
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(this.getKey(userId));

        //得到对应的skuId+skuNum部分的对象，将这个对象的列表得到，返回一个集合
         cartInfoList = hashOperations.values();
         //根据时间进行降序排序
         if(!StringUtils.isEmpty(cartInfoList)){
             cartInfoList.sort(new Comparator<CartInfo>() {
                 @Override
                 public int compare(CartInfo o1, CartInfo o2) {
                     return o1.getCreateTime().compareTo(o2.getCreateTime());

                 }
             });
         }
         return cartInfoList;


    }

    @Override
    public List<CartInfoVo> getCartList(Long userId) {

        List<CartInfoVo> cartInfoVoList=new ArrayList<>();
        BoundHashOperations<String,String,CartInfoVo> hashOperations = redisTemplate.boundHashOps(this.getKey(userId));
         cartInfoVoList = hashOperations.values();
         if(!StringUtils.isEmpty(cartInfoVoList)){
             cartInfoVoList.sort(new Comparator<CartInfoVo>() {
                 @Override
                 public int compare(CartInfoVo o1, CartInfoVo o2) {
                     return 0;
                 }
             });
         }

         return cartInfoVoList;



    }

    @Override
    public List<CartInfo> getCartCheckList(Long userId) {
        //首先从redis中获取到对应的相关信息
        BoundHashOperations boundHash = redisTemplate.boundHashOps(this.getKey(userId));
        List<CartInfo> cartList = boundHash.values();

        //从中得到ischeked为1的并且进行抓换通过stream
        List<CartInfo> cartInfos = cartList.stream().filter(cartInfo -> {
            return cartInfo.getIsChecked().intValue() == 1;
        }).collect(Collectors.toList());

        return cartInfos;
    }

    private void setCartKeyExpire(String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);

    }
}
