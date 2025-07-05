package com.app.pccooker.models;


public class OrderModel {
    private String orderId;
    private String status;

    public OrderModel(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
}
