package com.lsm.seckill.producer;

import com.alibaba.fastjson.JSON;
import com.lsm.seckill.dto.OrderDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitMQProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    /**
     * 发送方确认
     * 消息正确投递到交换机时,ack为true,当消息投递到一个不存在的交换机或者投递到交换机失败时,ack为false
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("执行confirm");
        System.out.println("correlationData:" + correlationData);
        System.out.println("correlationData.returnedMessage:" + correlationData.getReturnedMessage());
        OrderDTO orderDTO = JSON.parseObject(correlationData.getReturnedMessage().getBody(), OrderDTO.class);
        System.out.println("orderDTO:" + orderDTO);
        System.out.println("ack:" + ack);
        if (!ack) {
            //将发送到交换机失败的消息记录下来,以便后续补偿
        }
        System.out.println("cause:" + cause);
    }

    /**
     * 失败通知
     * 消息投递到不存在的队列或者投递到队列失败时执行此通知
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("执行returnedMessage");
        System.out.println("message:" + message);
        System.out.println("replyCode:" + replyCode);
        System.out.println("replyText:" + replyText);
        System.out.println("exchange:" + exchange);
        System.out.println("routingKey:" + routingKey);
        OrderDTO orderDTO = JSON.parseObject(message.getBody(), OrderDTO.class);
        System.out.println("orderDTO:" + orderDTO);
        //将发送到交换机失败的消息记录下来,以便后续补偿
    }

    /**
     * 开启发送方确认和失败通知
     */
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }
}
