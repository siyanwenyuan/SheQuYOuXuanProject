package com.chen.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.chen.search.common.constant.RedisConst;
import com.chen.search.common.exception.SsyxException;
import com.chen.search.common.result.Result;
import com.chen.search.common.result.ResultCodeEnum;
import com.chen.search.common.utils.JwtHelper;
import com.chen.search.enums.UserType;
import com.chen.search.model.user.User;
import com.chen.search.vo.user.LeaderAddressVo;
import com.chen.search.vo.user.UserLoginVo;
import com.chen.user.service.UserService;
import com.chen.user.utils.ConstantPropertiesUtil;
import com.chen.user.utils.HttpClientUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api//user/weixin")
public class WeixinApiController {


    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/wxLogin/{code}")
    @ApiOperation("微信登录接口")
    public Result WexinLogin(@PathVariable String code) {
        //首先得到code
        //拿到微信小程序id和密钥求请求微信接口服务
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        //使用get请求
        //此处拼接地址和参数
        StringBuffer url = new StringBuffer().append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");
        String TokenUrl = String.format(url.toString()
                , wxOpenAppId
                , wxOpenAppSecret
                , code);
        //使用httpclient发送请求

        String result = null;
        try {
            result = HttpClientUtils.get(TokenUrl);
        } catch (Exception e) {
            throw new SsyxException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //根据微信接口返回的值是open_id和 session_key
        JSONObject jsonObject = JSONObject.parseObject(result);
        String session_key = jsonObject.getString("session_key");
        String openid = jsonObject.getString("openid");


        //判断是否是第一次登录，通过open_id查询user表
        User user = userService.selectByOpenId(openid);
        if (user == null) {
            //如果是null说明是第一次登录，则需要将此数据加入到数据库中
            user = new User();

            user.setOpenId(openid);
            user.setNickName(openid);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);

            userService.save(user);

        }

        LeaderAddressVo addressVo = userService.getLeaderAddressUserById(user.getId());


        String token = JwtHelper.createToken(user.getId(), user.getNickName());

        UserLoginVo userLoginVo = userService.getUserLogin(user.getId());

        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId()
                , userLoginVo
                , RedisConst.USERKEY_TIMEOUT
                , TimeUnit.DAYS);

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", token);
        map.put("addressVo", addressVo);


        return Result.ok(map);

    }
}
