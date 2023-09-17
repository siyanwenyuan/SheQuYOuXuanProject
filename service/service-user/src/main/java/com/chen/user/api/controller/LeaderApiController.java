package com.chen.user.api.controller;


import com.chen.search.vo.user.LeaderAddressVo;
import com.chen.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/leader")
public class LeaderApiController {

    @Autowired
    private UserService userService;


    @GetMapping("/inner/getUserAddressByUserId/{userId}")

    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId){
        LeaderAddressVo leaderAddressUser= userService.getLeaderAddressUserById(userId);
        return leaderAddressUser;
    }
}
