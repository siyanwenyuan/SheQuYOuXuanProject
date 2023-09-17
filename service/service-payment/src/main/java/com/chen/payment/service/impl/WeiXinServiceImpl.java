package com.chen.payment.service.impl;

import com.chen.payment.service.PayMentService;
import com.chen.payment.service.WeiXinService;
import com.chen.payment.utils.ConstantPropertiesUtils;
import com.chen.payment.utils.HttpClient;
import com.chen.search.model.order.PaymentInfo;
import com.chen.search.vo.user.UserLoginVo;
import com.github.wxpay.sdk.WXPayUtil;
import io.swagger.models.Xml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class WeiXinServiceImpl implements WeiXinService {


    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private PayMentService payMentService;

    @Override
    public Map<String, String> createJsapi(String orderNo) {
        PaymentInfo paymentInfo = payMentService.getPayMentStatus(orderNo);
        if (paymentInfo == null) {
            payMentService.savePayMent(orderNo);
        }


        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", paymentInfo.getSubject());
        paramMap.put("out_trade_no", paymentInfo.getOrderNo());
        int totalFee = paymentInfo.getTotalAmount().multiply(new BigDecimal(100)).intValue();
        paramMap.put("total_fee", String.valueOf(totalFee));
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", ConstantPropertiesUtils.NOTIFYURL);
        paramMap.put("trade_type", "JSAPI");

//得到openid
        UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get("user:login:" + paymentInfo.getUserId());
        //将其中的openid封装到参数集合中
        paramMap.put("openid", userLoginVo.getOpenId());
        //使用HttpClient调用微信支付接口

        //首先创建一个client
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        //向其中加入参数，这里的参数需要的是xml格式
        //首先利用微信中提供的工具类将参数转换成xml格式
        //
        try {
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,
                    //注意此处的一定要加入的参数商户id
                    ConstantPropertiesUtils.PARTNERKEY)

            );
            //支持https协议
            client.setHttps(true);
            //参数添加完成之后，提交post请求
            client.post();
            //得到返回结果
            String xmlResult = client.getContent();
            //得到的结果是xml 将其转换成map集合的形式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);


            //4、再次封装参数
            Map<String, String> parameterMap = new HashMap<>();
            String prepayId = String.valueOf(resultMap.get("prepay_id"));
            String packages = "prepay_id=" + prepayId;
            parameterMap.put("appId", ConstantPropertiesUtils.APPID);
            parameterMap.put("nonceStr", resultMap.get("nonce_str"));
            parameterMap.put("package", packages);
            parameterMap.put("signType", "MD5");
            parameterMap.put("timeStamp", String.valueOf(new Date().getTime()));
            String sign = WXPayUtil.generateSignature(parameterMap, ConstantPropertiesUtils.PARTNERKEY);

            //返回结果
            Map<String, String> result = new HashMap();
            result.put("timeStamp", parameterMap.get("timeStamp"));
            result.put("nonceStr", parameterMap.get("nonceStr"));
            result.put("signType", "MD5");
            result.put("paySign", sign);
            result.put("package", packages);
            return result;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {


        //1、封装参数
        Map paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        try {
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            String xml = client.getContent();
            Map<String, String> xmlMapResult = WXPayUtil.xmlToMap(xml);
            return xmlMapResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
