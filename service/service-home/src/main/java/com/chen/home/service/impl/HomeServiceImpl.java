package com.chen.home.service.impl;

import com.chen.client.product.ProductFeignClient;
import com.chen.client.search.SkuFeignClient;
import com.chen.client.user.UserFeignClient;
import com.chen.home.service.HomeService;
import com.chen.search.model.product.Category;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.model.search.SkuEs;
import com.chen.search.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class HomeServiceImpl implements HomeService {


    @Resource
    private UserFeignClient userFeignCLient;

    @Autowired
    private ProductFeignClient productFeignClient;


    @Autowired
    private SkuFeignClient skuFeignClient;




    @Override
    public Map<String, Object> indexData(Long userId) {

        Map<String, Object> resultMap = new HashMap<>();
        //通过远程调用，通过id查询收货地址信息，
        LeaderAddressVo userAddressByUser = userFeignCLient.getUserAddressByUserId(userId);

        //将结果放到map集合中去
        resultMap.put("userAddressByUser", userAddressByUser);

        //查询商品所有的分类信息
        List<Category> allCategoryList = productFeignClient.findAllCategoryList();
        resultMap.put("allCategoryList",allCategoryList);

        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        resultMap.put("newPersonSkuInfoList",newPersonSkuInfoList);


        //通过远程调用，调用es中的数据,进行爆款商品的数据显示，其实使用的是es
        List<SkuEs> hotSkuList = skuFeignClient.findHotSkuList();
        resultMap.put("hotSkuList",hotSkuList);

            return resultMap;

    }
}
