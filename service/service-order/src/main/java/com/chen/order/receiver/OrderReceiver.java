package com.chen.order.receiver;


import com.chen.activity.mq.constant.MqConst;
import com.chen.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 接受端，接受rabbitmq中发送过来的消息
 */
@Component
public class OrderReceiver {

    @Autowired
    private OrderInfoService orderInfoService;


    /**
     * 方法中进行消息的监听
     * @param orderNo
     * @param message
     * @param channel
     */

  /*  @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER_PAY, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_PAY_DIRECT),
            key = {MqConst.ROUTING_PAY_SUCCESS})
    )
    )*/

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER_PAY, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_PAY_DIRECT),
            key = {MqConst.ROUTING_PAY_SUCCESS}
    ))
    public void orderPay(String orderNo,
                         Message message,
                         Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)) {
            orderInfoService.updatePayStatus(orderNo);
        }
        //此处进行消息的手动确认接收
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                false);

    }

}
