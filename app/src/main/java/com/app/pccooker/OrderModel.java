package com.app.pccooker;

import com.google.firebase.firestore.PropertyName;
import java.util.List;
import java.util.Map;

public class OrderModel {
    @PropertyName("orderId")
    private String orderId;
    
    @PropertyName("userId")
    private String userId;
    
    @PropertyName("orderNumber")
    private String orderNumber;
    
    @PropertyName("orderDate")
    private long orderDate;
    
    @PropertyName("totalAmount")
    private double totalAmount;
    
    @PropertyName("paidAmount")
    private double paidAmount;
    
    @PropertyName("remainingAmount")
    private double remainingAmount;
    
    @PropertyName("paymentId")
    private String paymentId;
    
    @PropertyName("deliveryAddress")
    private String deliveryAddress;
    
    @PropertyName("status")
    private String status;
    
    @PropertyName("components")
    private List<Map<String, Object>> components;

    public OrderModel() {
        // Required for Firebase
    }

    public OrderModel(String orderId, String userId, String orderNumber, long orderDate,
                     double totalAmount, double paidAmount, double remainingAmount,
                     String paymentId, String deliveryAddress, String status,
                     List<Map<String, Object>> components) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.paymentId = paymentId;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.components = components;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getOrderNumber() { return orderNumber; }
    public long getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public double getPaidAmount() { return paidAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public String getPaymentId() { return paymentId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getStatus() { return status; }
    public List<Map<String, Object>> getComponents() { return components; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setStatus(String status) { this.status = status; }
    public void setComponents(List<Map<String, Object>> components) { this.components = components; }

    // Helper methods
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm");
        return sdf.format(new java.util.Date(orderDate));
    }

    public String getFormattedAmount() {
        return "₹" + String.format("%.0f", totalAmount);
    }

    public String getFormattedPaidAmount() {
        return "₹" + String.format("%.0f", paidAmount);
    }

    public String getFormattedRemainingAmount() {
        return "₹" + String.format("%.0f", remainingAmount);
    }

    public int getComponentCount() {
        return components != null ? components.size() : 0;
    }

    public String getStatusColor() {
        switch (status.toLowerCase()) {
            case "confirmed":
                return "#4CAF50";
            case "processing":
                return "#2196F3";
            case "shipped":
                return "#FF9800";
            case "delivered":
                return "#4CAF50";
            case "cancelled":
                return "#F44336";
            default:
                return "#666666";
        }
    }
} 