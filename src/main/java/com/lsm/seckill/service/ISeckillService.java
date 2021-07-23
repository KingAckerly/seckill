package com.lsm.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsm.seckill.dto.OrderDTO;
import com.lsm.seckill.entity.OrderEntity;

public interface ISeckillService extends IService<OrderEntity> {
    String seckill(Integer userId, OrderDTO orderDTO);

    void cancelOrder(Integer userId, OrderDTO orderDTO);

    void payCallBack(Integer userId, OrderDTO orderDTO);

    void fail();
}
