package com.lsm.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    /**
     * 订单超时时间-120秒
     */
    private static final long OVER_TIME = 1000 * 120;
    /**
     * 下单队列交换机
     */
    public static final String PUT_ORDER_QUEUE_EXCHANGE = "put.order.queue.exchange";
    /**
     * 下单队列
     */
    public static final String PUT_ORDER_QUEUE = "put.order.queue";
    /**
     * 下单交换机到下单队列的路由key
     */
    public static final String PUT_ORDER_QUEUE_EXCHANGE_KEY = "put.order.put";
    /**
     * 下单交换机到下单队列的路由topic
     */
    public String PUT_ORDER_QUEUE_EXCHANGE_TOPIC = "put.order.#";
    /**
     * 订单队列交换机
     */
    public static final String ORDER_QUEUE_EXCHANGE = "order.queue.exchange";
    /**
     * 订单队列(正常下单队列)
     */
    public static final String ORDER_QUEUE = "order.queue";
    /**
     * 订单交换机到订单队列的路由key
     */
    public static final String ORDER_QUEUE_EXCHANGE_KEY = "order.put";
    /**
     * 订单交换机到订单队列的路由topic
     */
    public String ORDER_QUEUE_EXCHANGE_TOPIC = "order.#";
    /**
     * 订单超时死信交换机
     */
    public static final String ORDER_QUEUE_EXCHANGE_DLX = "order.queue.exchange.dlx";
    /**
     * 订单超时死信队列
     */
    public static final String ORDER_QUEUE_DLX = "order.queue.dlx";
    /**
     * 订单超时死信交换机到订单超时死信队列的路由key
     */
    public static final String ORDER_QUEUE_EXCHANGE_DLX_KEY = "dlx.order.overtime";
    /**
     * 订单超时死信交换机到订单超时死信队列的路由topic
     */
    public String ORDER_QUEUE_EXCHANGE_DLX_TOPIC = "dlx.order.#";

    /**
     * 下单交换机
     *
     * @return
     */
    @Bean("putOrderQueueExchange")
    public Exchange putOrderQueueExchange() {
        return ExchangeBuilder.topicExchange(PUT_ORDER_QUEUE_EXCHANGE).durable(true).build();
    }

    /**
     * 下单队列
     *
     * @return
     */
    @Bean("putOrderQueue")
    public Queue putOrderQueue() {
        return QueueBuilder.durable(PUT_ORDER_QUEUE).build();
    }

    /**
     * 下单队列绑定到下单交换机
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingPutOrderQueueExchange(@Qualifier("putOrderQueue") Queue queue, @Qualifier("putOrderQueueExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(PUT_ORDER_QUEUE_EXCHANGE_TOPIC).noargs();
    }

    /**
     * 订单队列交换机
     *
     * @return
     */
    @Bean("orderQueueExchange")
    public Exchange orderQueueExchange() {
        return ExchangeBuilder.topicExchange(ORDER_QUEUE_EXCHANGE).durable(true).build();
    }

    /**
     * 订单队列
     *
     * @return
     */
    @Bean("orderQueue")
    public Queue orderQueue() {
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", ORDER_QUEUE_EXCHANGE_DLX);
        //声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", ORDER_QUEUE_EXCHANGE_DLX_KEY);
        //声明当前队列的TTL
        args.put("x-message-ttl", OVER_TIME);
        return QueueBuilder.durable(ORDER_QUEUE).withArguments(args).build();
    }

    /**
     * 订单队列绑定到订单队列交换机
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingOrderQueueExchange(@Qualifier("orderQueue") Queue queue, @Qualifier("orderQueueExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_QUEUE_EXCHANGE_TOPIC).noargs();
    }

    /**
     * 订单超时死信交换机
     *
     * @return
     */
    @Bean("orderQueueExchangeDlx")
    public Exchange orderQueueExchangeDlx() {
        return ExchangeBuilder.topicExchange(ORDER_QUEUE_EXCHANGE_DLX).durable(true).build();
    }

    /**
     * 订单超时死信队列
     *
     * @return
     */
    @Bean("orderQueueDlx")
    public Queue orderQueueDlx() {
        return QueueBuilder.durable(ORDER_QUEUE_DLX).build();
    }

    /**
     * 订单超时死信队列绑定到订单超时死信交换机
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingOrderQueueExchangeDlx(@Qualifier("orderQueueDlx") Queue queue, @Qualifier("orderQueueExchangeDlx") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_QUEUE_EXCHANGE_DLX_TOPIC).noargs();
    }
}
