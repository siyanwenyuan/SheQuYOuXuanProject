package com.chen.payment.controller;


import com.chen.payment.service.PayMentService;
import com.chen.payment.service.WeiXinService;
import com.chen.search.common.result.Result;
import com.chen.search.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/payment/wiexin")
public class WeiXinController {


    @Autowired
    private WeiXinService weiXinService;
    @Autowired
    private PayMentService payMentService;



    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable("orderNo") String orderNo) {
        Map<String, String> map = weiXinService.createJsapi(orderNo);


        return Result.ok(map);
    }

    @ApiOperation("支付状态接口")
    @GetMapping("/queryPayStatus/{orderNo}")

    public Result queryPayStatus(@PathVariable("orderNo") String orderNo) {
        //首先通过微信支付接口查询微信支付状态
        Map<String, String> resultMap = weiXinService.queryPayStatus(orderNo);
        //如果返回结果为空，则说明支付失败
        if (resultMap == null) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
        //如果不为空，说明成功，则将数据库中支付表和订单库存表的数据进行修改
        if("SUCCESS".equals(resultMap.get("trade_state")))
        {
           String out_trade_no= resultMap.get("out_trade_no");
            payMentService.paySuccessStatus(out_trade_no,resultMap);
            return Result.ok(null);
        }

        //如果在支付中，则进行等待
        return Result.build(null,ResultCodeEnum.PAYMENT_WAITTING);

    }
}
