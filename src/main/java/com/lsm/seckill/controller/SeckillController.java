package com.lsm.seckill.controller;

import com.lsm.seckill.dto.OrderDTO;
import com.lsm.seckill.service.ISeckillService;
import com.lsm.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/order")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private IUserService userService;

    /**
     * 秒杀
     *
     * @param userId
     * @param orderDTO
     * @return
     */
    @RequestMapping(value = "/seckill", method = RequestMethod.POST)
    //public String seckill(@RequestHeader(value = "userId") Integer userId, @RequestBody OrderDTO orderDTO) {
    public String seckill(@RequestBody OrderDTO orderDTO) {
        //return seckillService.seckill(userId, orderDTO);
        return seckillService.seckill(orderDTO.getUserId(), orderDTO);
    }

    /**
     * 取消订单
     *
     * @param userId
     * @param orderDTO
     * @return
     */
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public String cancelOrder(@RequestHeader(value = "userId") Integer userId, @RequestBody OrderDTO orderDTO) {
        seckillService.cancelOrder(userId, orderDTO);
        return "取消订单成功";
    }

    /**
     * 支付成功后回调
     *
     * @param userId
     * @param orderDTO
     * @return
     */
    @RequestMapping(value = "/payCallBack", method = RequestMethod.POST)
    public String payCallBack(@RequestHeader(value = "userId") Integer userId, @RequestBody OrderDTO orderDTO) {
        seckillService.payCallBack(userId, orderDTO);
        return "支付回调处理成功";
    }

    /**
     * 批量导入Users
     *
     * @return
     */
    @RequestMapping(value = "/batchImportUsers", method = RequestMethod.GET)
    public String batchImportUsers() throws Exception {
        userService.batchImportUsers();
        return "批量导入Users成功";
    }

    /**
     * 测试失败
     *
     * @return
     */
    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String test() {
        seckillService.fail();
        return "fail";
    }
}
