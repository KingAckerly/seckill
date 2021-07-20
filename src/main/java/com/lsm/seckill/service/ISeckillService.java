package com.lsm.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsm.seckill.OrderEntity;

public interface ISeckillService extends IService<OrderEntity> {
    String seckill(Integer userId, Integer productId);
}
