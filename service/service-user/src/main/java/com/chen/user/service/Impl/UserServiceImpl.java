package com.chen.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.model.user.Leader;
import com.chen.search.model.user.User;
import com.chen.search.model.user.UserDelivery;
import com.chen.search.vo.user.LeaderAddressVo;
import com.chen.search.vo.user.UserLoginVo;
import com.chen.user.mapper.LeaderMapper;
import com.chen.user.mapper.UserDeliveryMapper;
import com.chen.user.mapper.UserMapper;
import com.chen.user.service.UserService;
import org.apache.ibatis.annotations.Lang;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {



    @Autowired
    private UserDeliveryMapper userDeliveryMapper;
    @Autowired
    private LeaderMapper leaderMapper;
    @Override
    public User selectByOpenId(String openid) {

        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openid));

        return user;
    }

    @Override
    public LeaderAddressVo getLeaderAddressUserById(Long id) {
        //根据用户id查询默认的团长id值
        UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, id)
                .eq(UserDelivery::getIsDefault, 1));


        if(userDelivery==null){
            return null;

        }
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(leader.getUserId());
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;

    }

    @Override
    public UserLoginVo getUserLogin(Long id) {
        UserLoginVo userLoginVo = new UserLoginVo();
        User user = this.getById(id);
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setUserId(id);
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setOpenId(user.getOpenId());
        userLoginVo.setIsNew(user.getIsNew());
        UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, id));
        if(userDelivery!=null){
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
            userLoginVo.setWareId(userDelivery.getWareId());
        }else{
            userLoginVo.setLeaderId(1L);
            userLoginVo.setWareId(1l);
        }
        return userLoginVo;
    }


}
