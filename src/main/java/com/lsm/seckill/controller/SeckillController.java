package com.lsm.seckill.controller;

import com.lsm.seckill.service.ISeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/order")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;

    @RequestMapping(value = "/seckill", method = RequestMethod.POST)
    public String seckill(@RequestHeader(value = "userId") Integer userId, @RequestParam(value = "productId") Integer productId) {
        seckillService.seckill(userId, productId);
        return "成功";
    }
}
