package com.chen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.user.User;
import com.chen.search.vo.user.LeaderAddressVo;
import com.chen.search.vo.user.UserLoginVo;

public interface UserService extends IService<User>  {
    User selectByOpenId(String openid);

    LeaderAddressVo getLeaderAddressUserById(Long id);

    UserLoginVo getUserLogin(Long id);
}
