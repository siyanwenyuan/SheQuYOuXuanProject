package com.chen.search.receiver;


import com.chen.search.service.SkuService;
import com.chen.activity.mq.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SkuReceiver {

    @Autowired
    private SkuService skuService;

    /**
     * 上架商品
     * @param skuId
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_GOODS_UPPER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT),
            key = {MqConst.ROUTING_GOODS_UPPER}))

    public void upper(Long skuId, Message message, Channel channel) throws IOException {

        if (skuId != null) {
            skuService.upper(skuId);
        }
        //手动确认模式
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);


    }

    /**
     * 下架商品
     * @param skuId
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_GOODS_LOWER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT),
            key = {MqConst.ROUTING_GOODS_LOWER}))
    public void lower(Long skuId, Message message, Channel channel) throws IOException {
                        if(skuId!=null){
                            skuService.low(skuId);
                        }
                        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
