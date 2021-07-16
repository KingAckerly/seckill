package com.lsm.seckill.service.impl;

import com.lsm.seckill.service.ISeckillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SeckillServiceImpl implements ISeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript<Boolean> redisScript;

    @Override
    public String seckill(Integer userId, Integer productId) {
        String orderStatus = (String) redisTemplate.opsForValue().get(userId + "-" + productId + "-order_status");
        if (StringUtils.isEmpty(orderStatus)) {
            //首次点击秒杀
            //查询是否有库存
            Object obj = redisTemplate.execute(redisScript, Collections.singletonList(productId + "-stock"), null);
            if (null != obj) {
                boolean result = (Boolean) obj;
                System.out.println(result);
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
