package com.lsm.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsm.seckill.OrderEntity;
import com.lsm.seckill.config.RabbitMQConfig;
import com.lsm.seckill.dto.OrderDTO;
import com.lsm.seckill.mapper.OrderMapper;
import com.lsm.seckill.service.ISeckillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeckillServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements ISeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DefaultRedisScript<Boolean> redisScript;

    @Override
    public String seckill(Integer userId, Integer productId) {
        String userOrderKey = userId + "-" + productId + "-order_status";
        String orderStatus = (String) redisTemplate.opsForValue().get(userOrderKey);
        if (StringUtils.isEmpty(orderStatus)) {
            //首次点击秒杀
            //查询是否有库存
            List<String> keys = new ArrayList<>();
            keys.add(productId + "-stock");
            keys.add(userOrderKey);
            String value = "0";
            Object obj = redisTemplate.execute(redisScript, keys, value);
            if (null != obj) {
                boolean result = (Boolean) obj;
                System.out.println(result);
                if (!result) {
                    return "库存不足";
                }
                OrderDTO orderDTO = new OrderDTO().setUserId(userId).setProductId(productId);
                String message = JSON.toJSONString(orderDTO);
                //发mq消息创建数据库订单
                rabbitTemplate.convertAndSend(RabbitMQConfig.PUT_ORDER_QUEUE_EXCHANGE, RabbitMQConfig.PUT_ORDER_QUEUE_EXCHANGE_KEY, message);
                //发mq消息,超时未支付取消订单
                //rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_QUEUE_EXCHANGE, RabbitMQConfig.ORDER_QUEUE_EXCHANGE_KEY, message);
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

}
