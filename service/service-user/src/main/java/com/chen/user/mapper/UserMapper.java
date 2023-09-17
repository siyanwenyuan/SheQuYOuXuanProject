package com.chen.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.search.model.user.User;
import org.redisson.api.annotation.REntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

}
