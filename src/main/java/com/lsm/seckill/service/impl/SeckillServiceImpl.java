package com.lsm.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsm.seckill.config.LuaScriptConfig;
import com.lsm.seckill.config.RabbitMQConfig;
import com.lsm.seckill.dto.OrderDTO;
import com.lsm.seckill.entity.OrderEntity;
import com.lsm.seckill.mapper.OrderMapper;
import com.lsm.seckill.service.ISeckillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SeckillServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements ISeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private LuaScriptConfig luaScriptConfig;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public String seckill(Integer userId, OrderDTO orderDTO) {
        String userOrderKey = userId + "-" + orderDTO.getProductId() + "-order_status";
        String orderStatus = (String) redisTemplate.opsForValue().get(userOrderKey);
        if (StringUtils.isEmpty(orderStatus)) {
            //首次点击秒杀
            //查询是否有库存
            List<String> keys = new ArrayList<>();
            keys.add(orderDTO.getProductId() + "-stock");
            keys.add(userOrderKey);
            String value = "0";
            Object obj = redisTemplate.execute(luaScriptConfig.putOrder, keys, value);
            if (null != obj) {
                boolean result = (Boolean) obj;
                System.out.println(result);
                if (!result) {
                    return "库存不足";
                }
                OrderDTO order = new OrderDTO().setUuid(UUID.randomUUID().toString().replaceAll("-", "")).setUserId(userId).setProductId(orderDTO.getProductId());
                String message = JSON.toJSONString(order);
                //发mq消息创建数据库订单
                rabbitTemplate.convertAndSend(RabbitMQConfig.PUT_ORDER_QUEUE_EXCHANGE, RabbitMQConfig.PUT_ORDER_QUEUE_EXCHANGE_KEY, message);
                //发mq消息,超时未支付取消订单
                rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_QUEUE_EXCHANGE, RabbitMQConfig.ORDER_QUEUE_EXCHANGE_KEY, message);
                return "秒杀成功";
            }
        }
        //待支付
        if (orderStatus.equals("0")) {
            return "已有订单,请您前去支付";
        }
        //已支付
        if (orderStatus.equals("1")) {
            return "您已经参与过此次秒杀";
        }
        return null;
    }

    @Override
    public void cancelOrder(Integer userId, OrderDTO orderDTO) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(2);
        UpdateWrapper<OrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uuid", orderDTO.getUuid());
        updateWrapper.eq("status", 0);
        int r = orderMapper.update(orderEntity, updateWrapper);
        if (r > 0) {
            List<String> keys = new ArrayList<>();
            keys.add(userId + "-" + orderDTO.getProductId() + "-order_status");
            keys.add(orderDTO.getProductId() + "-stock");
            redisTemplate.execute(luaScriptConfig.cancelOrder, keys, 0);
        }
    }

    @Override
    public void payCallBack(Integer userId, OrderDTO orderDTO) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(1);
        UpdateWrapper<OrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uuid", orderDTO.getUuid());
        updateWrapper.eq("status", 0);
        int r = orderMapper.update(orderEntity, updateWrapper);
        if (r > 0) {
            redisTemplate.opsForValue().set(userId + "-" + orderDTO.getProductId() + "-order_status", "1");
        }
    }
}
