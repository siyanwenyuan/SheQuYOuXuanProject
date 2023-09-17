package com.chen.ssyx.activity.client;


import com.chen.search.model.activity.CouponInfo;
import com.chen.search.model.order.CartInfo;
import com.chen.search.vo.order.CartInfoVo;
import com.chen.search.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient("service-activity")
public interface ActivityFeignClient {



    @PostMapping("/api/activity/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);

    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String ,Object> findActivityAndCoupon(@PathVariable Long skuId, @PathVariable
    Long userId);


    @PostMapping("/api/activity/inner/findCartActivityAndCoupon/{userId}")

    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfoVo> cartInfoVos, @PathVariable Long userId);


    @GetMapping("/api/activity/inner/findCartActivityList")
  public   List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoParamList);



    @GetMapping("api/activity/inner/findRangeSkuIdList")
   public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);
}
