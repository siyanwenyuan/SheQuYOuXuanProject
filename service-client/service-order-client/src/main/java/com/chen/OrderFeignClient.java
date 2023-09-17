package com.chen;


import com.chen.search.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-order")
public interface OrderFeignClient {



    @GetMapping("/api/order/getorderInfo/{orderNo}")
    public OrderInfo getorderInfo(@PathVariable String orderNo);
}
