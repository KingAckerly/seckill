package com.lsm.seckill.dto;

public class OrderDTO {
    private Integer userId;
    private Integer productId;

    public Integer getUserId() {
        return userId;
    }

    public OrderDTO setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public OrderDTO setProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "userId=" + userId +
                ", productId=" + productId +
                '}';
    }
}
