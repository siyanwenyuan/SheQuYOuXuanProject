package com.chen.cart.controller;


import com.chen.cart.service.CartInfoService;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.common.result.Result;
import com.chen.search.model.order.CartInfo;
import com.chen.search.vo.order.CartInfoVo;
import com.chen.search.vo.order.OrderConfirmVo;
import com.chen.ssyx.activity.client.ActivityFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cart")
public class CartApiController {




    @Qualifier("activityFeignClient")
    private ActivityFeignClient activityFeignClient;


    @Autowired
    private CartInfoService cartInfoService;


    /**
     * 添加购物车
     * 其中使用的是redis中的数据结构：hash
     * 将购物车存放到redis中
     * @param skuId
     * @param skuNum
     * @return
     */


    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId, @PathVariable
    Integer skuNum){


        //得到用户id
        Long userId = AuthContextHolder.getUserId();
        //其中使用的redis的hash数据结构存储方式是：userId->skuId+skuNum
        cartInfoService.addToCart(userId,skuId,skuNum);
        return Result.ok(null);


    }

    /**
     * 删除购物车单个商品
     */

    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId){
        //此处根据skuId删除，得到userId，目的是删除该用户的购物车，而非其他用户
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.deleteCart(skuId,userId);
        return Result.ok(null);

    }

    /**
     * 清空购物车
     */
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart(){
        //也是根据userId删除
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.deleteAllCart(userId);
        return Result.ok(null);

    }

    /**
     * 批量删除购物车
     */
    @DeleteMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList){
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.batchDeleteCart(skuIdList,userId);
        return Result.ok(null);

    }
    /**
     * 购物车列表
     */
    @GetMapping("cartList")
    public Result cartList(){
        Long userId = AuthContextHolder.getUserId();
       List<CartInfo> cartInfoList= cartInfoService.cartList(userId);
       return Result.ok(cartInfoList);


    }


    @GetMapping("getCarList")
    public Result getCArtList(){
        Long userId = AuthContextHolder.getUserId();
        List<CartInfoVo> cartInfoVoList= cartInfoService.getCartList(userId);
                return Result.ok(cartInfoVoList);
    }

    /**
     * 获取优惠卷信息
     */

    @GetMapping("activityCartList")
    public Result activityCartList(@PathVariable Long skuId){
        Long userId = AuthContextHolder.getUserId();
        List<CartInfoVo> cartInfoList = cartInfoService.getCartList(userId);

       OrderConfirmVo cartInfos= activityFeignClient.findCartActivityAndCoupon(cartInfoList,userId);
       return Result.ok(cartInfos);



    }


    @GetMapping("inner/getCartCheckList")
    public List<CartInfo> getCartCheckList(@PathVariable("userId") Long userId){
        List<CartInfo> cartInfoList= cartInfoService.getCartCheckList(userId);


        return cartInfoList;

    }


}
