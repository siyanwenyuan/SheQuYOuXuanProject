package com.chen.cart.service;

import com.chen.search.common.result.Result;
import com.chen.search.model.order.CartInfo;
import com.chen.search.vo.order.CartInfoVo;

import java.util.List;

public interface CartInfoService {
    void addToCart(Long userId, Long skuId, Integer skuNum);

    void deleteCart(Long skuId, Long userId);

    void deleteAllCart(Long userId);

    void batchDeleteCart(List<Long> skuIdList,Long userId);

    List<CartInfo> cartList(Long userId);

    List<CartInfoVo> getCartList(Long userId);

    List<CartInfo> getCartCheckList(Long userId);
}
