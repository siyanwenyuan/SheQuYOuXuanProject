package com.chen.activity.api.activityInfoController;


import com.chen.activity.service.ActivityInfoService;
import com.chen.activity.service.CouponInfoService;
import com.chen.search.model.activity.CouponInfo;
import com.chen.search.model.order.CartInfo;
import com.chen.search.vo.order.CartInfoVo;
import com.chen.search.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityApiController {

    @Autowired
    private ActivityInfoService activityInfoService;

    @Autowired
    private CouponInfoService couponInfoService;
    @PostMapping("inner/findActivity")
 public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList){
    Map<Long,List<String>> mapList= activityInfoService.findActivity(skuIdList);
    return mapList;

    }

    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String ,Object> findActivityAndCoupon(@PathVariable Long skuId,@PathVariable
                                                    Long userId){
      return   activityInfoService.findActivityAndCoupon(skuId,userId);
    }

    @PostMapping("inner/findCartActivityAndCoupon/{userId}")

    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfoVo> cartInfoVos, @PathVariable Long userId){

     return activityInfoService.findCartActivityAndCoupon(cartInfoVos,userId);

    }



    @GetMapping("inner/findCartActivityList")
    public   List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList){

        CartInfoVo cartInfoVoList=new CartInfoVo();
        cartInfoVoList.setCartInfoList(cartInfoList);
   return      activityInfoService.findCartActivityList((List<CartInfoVo>) cartInfoVoList);

    }

    @GetMapping("inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,
                                         @PathVariable Long couponId){
        return couponInfoService.findRangeSkuIdList(cartInfoList,couponId);


    }


}
