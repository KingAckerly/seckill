package com.lsm.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsm.seckill.OrderEntity;
import com.lsm.seckill.config.RabbitMQConfig;
import com.lsm.seckill.dto.OrderDTO;
import com.lsm.seckill.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Component
public class RabbitMQConsumer extends ServiceImpl<OrderMapper, OrderEntity> {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 监听订单超时死信队列
     *
     * @param message
     */
    @Transactional
    @RabbitListener(queues = {RabbitMQConfig.PUT_ORDER_QUEUE})
    public void putMoneyPackageQueue(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        OrderDTO orderDTO = JSON.parseObject(message, OrderDTO.class);
        System.out.println("orderDTO:" + orderDTO);
        OrderEntity orderEntity = new OrderEntity();
        //秒杀订单
        orderEntity.setType(1);
        //待支付
        orderEntity.setStatus(0);
        //数量1
        orderEntity.setCount(1);
        orderEntity.setUserId(orderDTO.getUserId());
        orderEntity.setProductId(orderDTO.getProductId());
        try {
            int r = orderMapper.insert(orderEntity);
            if (r > 0) {
                //第二个参数为true表示签收多条消息,且只签收tag往前的消息,如果设为false表示不签收多条消息
                channel.basicAck(tag, true);
            } else {
                //第三个参数为true表示让消息重回队列,重回队列之后又会继续发消息,设为false表示丢弃该条消息
                channel.basicNack(tag, true, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            channel.basicNack(tag, true, true);
        }
    }
}
