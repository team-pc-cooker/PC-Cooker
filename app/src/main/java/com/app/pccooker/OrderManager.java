package com.app.pccooker;

import android.content.Context;
import android.widget.Toast;
import com.app.pccooker.ComponentModel;
import com.app.pccooker.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {
    private static OrderManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context context;

    private OrderManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public static OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context);
        }
        return instance;
    }

    public void createOrder(List<CartItem> cartItems, String deliveryAddress, 
                           double totalAmount, double paidAmount, String paymentId, 
                           OnOrderCreatedListener listener) {
        
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(context, "Please login to create order", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("orderDate", System.currentTimeMillis());
        orderData.put("totalAmount", totalAmount);
        orderData.put("paidAmount", paidAmount);
        orderData.put("remainingAmount", totalAmount - paidAmount);
        orderData.put("paymentId", paymentId);
        orderData.put("deliveryAddress", deliveryAddress);
        orderData.put("status", "Confirmed");
        orderData.put("orderNumber", generateOrderNumber());

        // Convert cart items to Firebase-compatible format
        List<Map<String, Object>> itemsData = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", cartItem.getId());
            itemData.put("name", cartItem.getName());
            itemData.put("price", cartItem.getPrice());
            itemData.put("quantity", cartItem.getQuantity());
            itemData.put("imageUrl", cartItem.getImageUrl());
            itemData.put("description", cartItem.getDescription());
            itemData.put("rating", cartItem.getRating());
            itemsData.add(itemData);
        }
        orderData.put("items", itemsData);

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener(documentReference -> {
                String orderId = documentReference.getId();
                
                // Clear cart after successful order
                CartManager.getInstance(context).clearCart();
                
                Toast.makeText(context, "Order created successfully! Order #" + orderData.get("orderNumber"), 
                             Toast.LENGTH_LONG).show();
                
                if (listener != null) {
                    listener.onOrderCreated(orderId, (String) orderData.get("orderNumber"));
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to create order: " + e.getMessage(), 
                             Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onOrderFailed(e.getMessage());
                }
            });
    }

    public void getUserOrders(OnOrdersLoadedListener listener) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            if (listener != null) listener.onOrdersLoaded(new ArrayList<>());
            return;
        }

        db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<OrderModel> orders = new ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    OrderModel order = doc.toObject(OrderModel.class);
                    if (order != null) {
                        order.setOrderId(doc.getId());
                        orders.add(order);
                    }
                }
                if (listener != null) listener.onOrdersLoaded(orders);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to load orders", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onOrdersLoaded(new ArrayList<>());
            });
    }

    public void updateOrderStatus(String orderId, String newStatus, OnOrderUpdatedListener listener) {
        db.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Order status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onOrderUpdated();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onOrderUpdateFailed(e.getMessage());
            });
    }

    private String generateOrderNumber() {
        return "PC" + System.currentTimeMillis();
    }

    public interface OnOrderCreatedListener {
        void onOrderCreated(String orderId, String orderNumber);
        void onOrderFailed(String error);
    }

    public interface OnOrdersLoadedListener {
        void onOrdersLoaded(List<OrderModel> orders);
    }

    public interface OnOrderUpdatedListener {
        void onOrderUpdated();
        void onOrderUpdateFailed(String error);
    }
} 