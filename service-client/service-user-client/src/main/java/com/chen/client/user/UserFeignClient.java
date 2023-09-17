package com.chen.client.user;


import com.chen.search.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")
public interface UserFeignClient {

    @GetMapping("api/user/leader/inner/getUserAddressByUserId/{userId}")

    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId);

}
