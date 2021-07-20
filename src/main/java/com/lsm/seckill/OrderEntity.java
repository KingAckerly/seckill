package com.lsm.seckill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("t_order")
public class OrderEntity implements Serializable {
    private static final long serialVersionUID = -1231421576734185008L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer type;
    private Integer status;
    private Integer count;
    private Integer userId;
    private Integer productId;

    public Integer getId() {
        return id;
    }

    public OrderEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public OrderEntity setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public OrderEntity setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public OrderEntity setCount(Integer count) {
        this.count = count;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public OrderEntity setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public OrderEntity setProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", type=" + type +
                ", status=" + status +
                ", count=" + count +
                ", userId=" + userId +
                ", productId=" + productId +
                '}';
    }
}
