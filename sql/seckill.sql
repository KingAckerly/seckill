CREATE TABLE `t_user`
(
    `id`     int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`   varchar(20) NOT NULL COMMENT '姓名',
    `mobile` varchar(20) NOT NULL COMMENT '手机号',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';

INSERT INTO t_user(name, mobile)
VALUES ('admin', '17727921123');

CREATE TABLE `t_product`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`  varchar(20)    NOT NULL COMMENT '商品名称',
    `price` decimal(12, 2) NOT NULL COMMENT '商品价格',
    `stock` int(5) NOT NULL COMMENT '库存数量',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='商品表';

INSERT INTO t_product(name, price, stock)
VALUES ('iphone30', 1, 100);

CREATE TABLE `t_order`
(
    `uuid`       varchar(36) NOT NULL COMMENT '主键ID',
    `type`       int(2) NOT NULL COMMENT '订单类型(0:普通订单,1:秒杀订单)',
    `status`     int(2) NOT NULL COMMENT '订单状态(0:待支付,1:已支付,2:已取消)',
    `count`      int(2) NOT NULL COMMENT '订单数量',
    `user_id`    int(11) NOT NULL COMMENT '用户ID',
    `product_id` int(11) NOT NULL COMMENT '商品ID',
    PRIMARY KEY (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1741 DEFAULT CHARSET=utf8 COMMENT='订单表';
