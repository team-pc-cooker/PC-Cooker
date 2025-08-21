package com.app.pccooker.models;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class OrderModel {
    @PropertyName("orderId")
    private String orderId;
    
    @PropertyName("userId")
    private String userId;
    
    @PropertyName("customerName")
    private String customerName;
    
    @PropertyName("orderDate")
    private long orderDate;
    
    @PropertyName("totalAmount")
    private double totalAmount;
    
    @PropertyName("status")
    private String status;
    
    @PropertyName("paymentStatus")
    private String paymentStatus;
    
    @PropertyName("paymentMethod")
    private String paymentMethod;
    
    @PropertyName("shippingAddress")
    private String shippingAddress;
    
    @PropertyName("phoneNumber")
    private String phoneNumber;
    
    @PropertyName("items")
    private List<CartItem> items;
    
    @PropertyName("trackingNumber")
    private String trackingNumber;
    
    @PropertyName("estimatedDelivery")
    private String estimatedDelivery;
    
    @PropertyName("courierName")
    private String courierName;
    
    @PropertyName("paymentId")
    private String paymentId;
    
    @PropertyName("subtotal")
    private double subtotal;
    
    @PropertyName("tax")
    private double tax;
    
    @PropertyName("shipping")
    private double shipping;
    
    @PropertyName("tokenAmount")
    private double tokenAmount;

    public OrderModel() {
        // Required empty constructor for Firestore
    }

    public OrderModel(String orderId, String userId, long orderDate, double totalAmount, 
                     String status, String paymentStatus, String paymentMethod, String shippingAddress, 
                     List<CartItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
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
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public double getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(double tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    // Helper method to get formatted order date
    public String getFormattedOrderDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm");
        return sdf.format(new java.util.Date(orderDate));
    }

    // Helper method to get status color
    public int getStatusColor() {
        switch (status.toLowerCase()) {
            case "pending":
                return android.graphics.Color.parseColor("#FF9800");
            case "confirmed":
                return android.graphics.Color.parseColor("#2196F3");
            case "shipped":
                return android.graphics.Color.parseColor("#9C27B0");
            case "delivered":
                return android.graphics.Color.parseColor("#4CAF50");
            case "cancelled":
                return android.graphics.Color.parseColor("#F44336");
            default:
                return android.graphics.Color.parseColor("#757575");
        }
    }
} 