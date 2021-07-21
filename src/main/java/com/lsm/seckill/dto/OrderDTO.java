package com.lsm.seckill.dto;

public class OrderDTO {
    private String uuid;
    private Integer userId;
    private Integer productId;

    public String getUuid() {
        return uuid;
    }

    public OrderDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

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
                "uuid='" + uuid + '\'' +
                ", userId=" + userId +
                ", productId=" + productId +
                '}';
    }
}
