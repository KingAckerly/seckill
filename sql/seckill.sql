CREATE TABLE `t_user`
(
    `id`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`   VARCHAR(20) NOT NULL COMMENT '姓名',
    `mobile` VARCHAR(20) NOT NULL COMMENT '手机号',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户表';

INSERT INTO t_user(name, mobile)
VALUES ('admin', '17727921123');

CREATE TABLE `t_product`
(
    `id`    INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`  VARCHAR(20)    NOT NULL COMMENT '商品名称',
    `price` DECIMAL(12, 2) NOT NULL COMMENT '商品价格',
    `stock` INT(5) NOT NULL COMMENT '库存数量',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='商品表';

INSERT INTO t_product(name, price, stock)
VALUES ('iphone30', 1, 100);

CREATE TABLE `t_order`
(
    `uuid`       VARCHAR(36) NOT NULL COMMENT '主键ID',
    `type`       INT(2) NOT NULL COMMENT '订单类型(0:普通订单,1:秒杀订单)',
    `status`     INT(2) NOT NULL COMMENT '订单状态(0:待支付,1:已支付,2:已取消)',
    `count`      INT(2) NOT NULL COMMENT '订单数量',
    `user_id`    INT(11) NOT NULL COMMENT '用户ID',
    `product_id` INT(11) NOT NULL COMMENT '商品ID',
    PRIMARY KEY (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='订单表';
