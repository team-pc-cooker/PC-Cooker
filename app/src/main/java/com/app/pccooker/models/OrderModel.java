package com.app.pccooker.models;

import java.util.Date;
import java.util.List;

public class OrderModel {
    private String orderId;
    private String userId;
    private Date orderDate;
    private double totalAmount;
    private String paymentId;
    private String status;
    private AddressModel deliveryAddress;
    private List<CartItem> items;

    public OrderModel() {
        // Required for Firebase
    }

    public OrderModel(String orderId, String userId, Date orderDate, double totalAmount, 
                     String paymentId, String status, AddressModel deliveryAddress, List<CartItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentId = paymentId;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AddressModel getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressModel deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
} 