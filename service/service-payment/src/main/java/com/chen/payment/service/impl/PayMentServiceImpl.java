package com.chen.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.OrderFeignClient;
import com.chen.activity.mq.constant.MqConst;
import com.chen.activity.mq.service.RabbitService;
import com.chen.payment.mapper.PayMentMapper;
import com.chen.payment.service.PayMentService;
import com.chen.search.common.exception.SsyxException;
import com.chen.search.common.result.ResultCodeEnum;
import com.chen.search.enums.PaymentStatus;
import com.chen.search.model.order.OrderInfo;
import com.chen.search.model.order.PaymentInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class PayMentServiceImpl extends ServiceImpl<PayMentMapper, PaymentInfo> implements PayMentService {


    @Qualifier("orderFeignClient")
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Qualifier("rabbitService")
    private RabbitService rabbitService;




    //查询支付状态
    @Override
    public PaymentInfo getPayMentStatus(String orderNo) {

        PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().
                eq(PaymentInfo::getOrderNo, orderNo));
        return paymentInfo;
    }
    //添加支付记录

    @Override
    public PaymentInfo savePayMent(String orderNo) {

        OrderInfo orderInfo = orderFeignClient.getorderInfo(orderNo);

        if (orderInfo == null) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "orderId:" + orderInfo.getUserId();
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        baseMapper.insert(paymentInfo);



        return paymentInfo;
    }

    @Override
    public void paySuccessStatus(String orderNo, Map<String, String> resultMap) {
        PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().
                eq(PaymentInfo::getOrderNo, orderNo));
        if(paymentInfo.getPaymentStatus()!=PaymentStatus.UNPAID){
            return;
        }
        //将更新后的数据进行重新设置
        paymentInfo.setPaymentStatus(PaymentStatus.PAID);
        //将此数据进行修改
        baseMapper.updateById(paymentInfo);

        //使用rabbitmq进行消息的发送，在接受端进行状态的更新，和修改库存
        rabbitService.sendMsg(MqConst.EXCHANGE_PAY_DIRECT,
                MqConst.ROUTING_PAY_SUCCESS,orderNo);

    }




}
