package com.chen.search.common.auth;

import com.chen.search.common.constant.RedisConst;
import com.chen.search.common.utils.JwtHelper;
import com.chen.search.vo.user.UserLoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class UserLoginInterceptor implements HandlerInterceptor {


    private RedisTemplate redisTemplate;

    public UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        this.getUserLoginVo(request);
        return true;
    }

    private void getUserLoginVo(HttpServletRequest request) {
        //首先得到token
        String token = request.getHeader("token");
        if (!token.isEmpty()) {
            //从token中得到用户id
            Long userId = JwtHelper.getUserId(token);
            //根据id从redis中得到用户信息
            UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);

            //将从redis中得到的用户信息存入到auhtcontextholder
            AuthContextHolder.setUserId(userLoginVo.getUserId());
            AuthContextHolder.setWareId(userLoginVo.getWareId());
            AuthContextHolder.setUserLoginVo(userLoginVo);

        }

    }


}
