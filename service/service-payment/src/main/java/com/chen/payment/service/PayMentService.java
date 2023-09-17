package com.chen.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.order.PaymentInfo;

import java.util.Map;

public interface PayMentService extends IService<PaymentInfo>  {
    PaymentInfo getPayMentStatus(String orderNo);

    PaymentInfo savePayMent(String orderNo);

    void paySuccessStatus(String out_trade_no, Map<String, String> resultMap);
}
