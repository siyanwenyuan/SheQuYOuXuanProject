package com.chen.cart.client;


import com.chen.search.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.xml.transform.Result;
import java.util.List;

@FeignClient("service-cart")
public interface CartFeignClient {


    @GetMapping("inner/getCartCheckList")
    public List<CartInfo> getCartCheckList(@PathVariable("userId") Long userId);

}
