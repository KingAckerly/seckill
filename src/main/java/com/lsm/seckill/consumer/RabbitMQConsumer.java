package com.lsm.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsm.seckill.config.LuaScriptConfig;
import com.lsm.seckill.config.RabbitMQConfig;
import com.lsm.seckill.dto.OrderDTO;
import com.lsm.seckill.entity.OrderEntity;
import com.lsm.seckill.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

@Component
public class RabbitMQConsumer extends ServiceImpl<OrderMapper, OrderEntity> {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private LuaScriptConfig luaScriptConfig;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 监听下单队列
     *
     * @param message
     */
    @Transactional
    @RabbitListener(queues = {RabbitMQConfig.PUT_ORDER_QUEUE}, concurrency = "10")
    public void consumePutOrderQueue(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        OrderDTO orderDTO = JSON.parseObject(message, OrderDTO.class);
        System.out.println("orderDTO:" + orderDTO);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUuid(orderDTO.getUuid());
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

    /**
     * 监听订单超时死信队列
     *
     * @param message
     * @param channel
     * @param tag
     * @throws Exception
     */
    @Transactional
    @RabbitListener(queues = {RabbitMQConfig.ORDER_QUEUE_DLX}, concurrency = "10")
    public void consumeOrderQueueDlx(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            OrderDTO orderDTO = JSON.parseObject(message, OrderDTO.class);
            System.out.println("orderDTO:" + orderDTO);
            //超时时间到,先判断订单状态
            OrderEntity orderEntity;
            QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uuid", orderDTO.getUuid());
            orderEntity = orderMapper.selectOne(queryWrapper);
            //订单状态为待支付
            if (orderEntity.getStatus().equals(0)) {
                //修改订单状态为已取消
                orderEntity = new OrderEntity();
                orderEntity.setStatus(2);
                UpdateWrapper<OrderEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("uuid", orderDTO.getUuid());
                updateWrapper.eq("status", 0);
                int r = orderMapper.update(orderEntity, updateWrapper);
                if (r < 1) {
                    channel.basicNack(tag, true, true);
                    return;
                }
                cancelOrder(orderDTO.getUserId(), orderDTO.getProductId());
            }
            channel.basicAck(tag, true);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            channel.basicNack(tag, true, true);
        }
    }

    /**
     * 删除redis订单,回退库存
     *
     * @param userId
     * @param productId
     */
    private void cancelOrder(Integer userId, Integer productId) {
        List<String> keys = new ArrayList<>();
        keys.add(userId + "-" + productId + "-order_status");
        keys.add(productId + "-stock");
        redisTemplate.execute(luaScriptConfig.cancelOrder, keys, 0);
    }
}
