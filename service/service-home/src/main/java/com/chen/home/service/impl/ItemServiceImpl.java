package com.chen.home.service.impl;

import com.chen.client.product.ProductFeignClient;
import com.chen.client.search.SkuFeignClient;
import com.chen.home.service.ItemService;
import com.chen.search.vo.product.SkuInfoVo;
import com.chen.ssyx.activity.client.ActivityFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service

public class ItemServiceImpl implements ItemService {


    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Autowired
    private SkuFeignClient skuFeignClient;



    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;



    @Override
    public Map<String, Object> item(Long id, Long userId) {

        Map<String, Object> map = new HashMap<>();

        /**
         * sku基本信息
         */
        //第一个执行对象，其中的supplyacync需要返回对象
        CompletableFuture<SkuInfoVo> completableFuture = CompletableFuture.supplyAsync(() -> {
            //将数据封装在此类中
            //通过远程调用查询对应的商品信息
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(id);
            //将数据封装在map中
            map.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;

        },threadPoolExecutor);


        /**
         * 优惠卷信息
         */

        //第二个执行对象,其中的runasync不需要返回对象
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            //直接将数据封装在map集合中
            //通过远程调用得到优惠卷信息的数据
            Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(id,userId);

            map.putAll(activityMap);

        },threadPoolExecutor);

        /**
         * 商品热度
         */

        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            //通过远程调用实现商品热度的更新

            skuFeignClient.incrHotScore(id);
            //incompatible types: Truck cannot be converted to MotoVehicle

        },threadPoolExecutor);


        /**
         * 以上三个任务的要完成之后，才可以进行下面的操作，也就是完成任务组合
         */

        //此处完成任务组合
        CompletableFuture.allOf(completableFuture, voidCompletableFuture, hotCompletableFuture).join();

        return map;


    }
}






