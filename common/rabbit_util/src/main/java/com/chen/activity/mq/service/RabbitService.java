package com.chen.ssyx.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     *
     * @param exchange  交换机
     * @param routingKey  路由key
     * @param message 消息
     * @return
     */
    //发送消息的方法
    public boolean sendMsg(String exchange,String routingKey,Object message)
    {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;

    }

}
