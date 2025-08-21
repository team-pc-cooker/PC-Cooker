package com.app.pccooker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.pccooker.models.CartItem;
import com.app.pccooker.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Professional Order Management System
 * Handles order creation, display, and notifications
 */
public class OrderManager {

    private static final String TAG = "OrderManager";
    private static OrderManager instance;
    
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    // Notification services
    private EmailService emailService;
    private WhatsAppService whatsAppService;
    
    public interface OrderCallback {
        void onOrderCreated(String orderId);
        void onOrderCreationFailed(String error);
        void onNotificationSent();
        void onNotificationFailed(String error);
    }

    private OrderManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        
        // Initialize notification services
        this.emailService = new EmailService(context);
        this.whatsAppService = new WhatsAppService(context);
    }

    public static synchronized OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context);
        }
        return instance;
    }

    /**
     * Create a professional order with all details
     */
    public void createOrder(List<CartItem> cartItems, double totalAmount, String shippingAddress, 
                           String paymentId, String customerName, String customerPhone, OrderCallback callback) {
        
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onOrderCreationFailed("User not logged in");
            return;
        }

        try {
            // Generate professional order ID
            String orderId = generateOrderId();
            
            // Create comprehensive order
            OrderModel order = new OrderModel();
            order.setOrderId(orderId);
            order.setUserId(user.getUid());
            order.setCustomerName(customerName);
            order.setPhoneNumber(customerPhone);
            order.setItems(cartItems);
            order.setTotalAmount(totalAmount);
            order.setShippingAddress(shippingAddress);
            order.setOrderDate(System.currentTimeMillis());
            order.setStatus("CONFIRMED");
            order.setPaymentStatus("COMPLETED");
            order.setPaymentId(paymentId);
            
            // Calculate additional details
            double subtotal = calculateSubtotal(cartItems);
            double shipping = subtotal > 1000 ? 0 : 100;
            double tokenAmount = subtotal * 0.20;
            
            order.setSubtotal(subtotal);
            order.setTax(0.0); // No GST charged
            order.setShipping(shipping);
            order.setTokenAmount(tokenAmount);
            
            // Save to Firestore
            db.collection("orders").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order created successfully: " + orderId);
                    
                    // Send notifications
                    sendOrderNotifications(order, user, callback);
                    
                    callback.onOrderCreated(orderId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create order: " + e.getMessage());
                    callback.onOrderCreationFailed("Failed to create order: " + e.getMessage());
                });
                
        } catch (Exception e) {
            Log.e(TAG, "Error creating order: " + e.getMessage());
            callback.onOrderCreationFailed("Error creating order: " + e.getMessage());
        }
    }

    /**
     * Send professional notifications based on user's contact method
     */
    private void sendOrderNotifications(OrderModel order, FirebaseUser user, OrderCallback callback) {
        
        try {
            Log.d(TAG, "Starting to send notifications for order: " + order.getOrderId());
            
            // Check user's contact method and send appropriate notifications
            String userEmail = user.getEmail();
            String userPhone = user.getPhoneNumber();
            
            if (userEmail != null && !userEmail.isEmpty()) {
                // User has email - send email invoice
                Log.d(TAG, "User has email: " + userEmail + " - Sending email invoice");
                sendEmailInvoice(order, userEmail, callback);
            } else if (userPhone != null && !userPhone.isEmpty()) {
                // User only has phone - send WhatsApp notification
                Log.d(TAG, "User has phone: " + userPhone + " - Sending WhatsApp notification");
                sendWhatsAppNotification(order, user, callback);
            } else {
                // Try to get contact info from Firestore user profile
                getUserContactFromFirestore(order, user, callback);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error sending notifications: " + e.getMessage(), e);
            // Continue anyway - order was created successfully
            callback.onNotificationFailed("Error sending notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get user contact info from Firestore profile
     */
    private void getUserContactFromFirestore(OrderModel order, FirebaseUser user, OrderCallback callback) {
        db.collection("users").document(user.getUid()).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userEmail = documentSnapshot.getString("email");
                    String userPhone = documentSnapshot.getString("phoneNumber");
                    
                    if (userEmail != null && !userEmail.isEmpty()) {
                        Log.d(TAG, "Found email in Firestore: " + userEmail);
                        sendEmailInvoice(order, userEmail, callback);
                    } else if (userPhone != null && !userPhone.isEmpty()) {
                        Log.d(TAG, "Found phone in Firestore: " + userPhone);
                        sendWhatsAppNotification(order, user, callback);
                    } else {
                        Log.w(TAG, "No contact info found in Firestore");
                        callback.onNotificationFailed("No contact information found");
                    }
                } else {
                    Log.w(TAG, "User document not found in Firestore");
                    callback.onNotificationFailed("User profile not found");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get user profile from Firestore: " + e.getMessage());
                callback.onNotificationFailed("Failed to get user profile");
            });
    }
    
    /**
     * Send email invoice
     */
    private void sendEmailInvoice(OrderModel order, String userEmail, OrderCallback callback) {
        emailService.sendOrderInvoice(order, userEmail, new EmailService.EmailCallback() {
            @Override
            public void onEmailSent() {
                Log.d(TAG, "Email invoice sent successfully to: " + userEmail);
                callback.onNotificationSent();
            }

            @Override
            public void onEmailFailed(String error) {
                Log.e(TAG, "Failed to send email to " + userEmail + ": " + error);
                callback.onNotificationFailed("Email failed: " + error);
            }
        });
    }

    /**
     * Send WhatsApp notification
     */
    private void sendWhatsAppNotification(OrderModel order, FirebaseUser user, OrderCallback callback) {
        try {
            // Get user's phone number from Firebase or use a default
            String phoneNumber = user.getPhoneNumber();
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                // Try to get from user profile in Firestore
                db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("phoneNumber")) {
                            String userPhone = documentSnapshot.getString("phoneNumber");
                            if (userPhone != null && !userPhone.isEmpty()) {
                                sendWhatsAppToNumber(order, userPhone, callback);
                            } else {
                                sendWhatsAppToNumber(order, "+917989592441", callback); // Default for testing
                            }
                        } else {
                            sendWhatsAppToNumber(order, "+917989592441", callback); // Default for testing
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Failed to get user phone number, using default");
                        sendWhatsAppToNumber(order, "+917989592441", callback); // Default for testing
                    });
            } else {
                sendWhatsAppToNumber(order, phoneNumber, callback);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in WhatsApp notification: " + e.getMessage(), e);
            callback.onNotificationFailed("WhatsApp error: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to send WhatsApp to specific number
     */
    private void sendWhatsAppToNumber(OrderModel order, String phoneNumber, OrderCallback callback) {
        whatsAppService.sendOrderNotification(order, phoneNumber, new WhatsAppService.WhatsAppCallback() {
            @Override
            public void onWhatsAppSent() {
                Log.d(TAG, "WhatsApp notification sent successfully to: " + phoneNumber);
                callback.onNotificationSent();
            }

            @Override
            public void onWhatsAppFailed(String error) {
                Log.e(TAG, "Failed to send WhatsApp to " + phoneNumber + ": " + error);
                callback.onNotificationFailed("WhatsApp failed: " + error);
            }
        });
    }

    /**
     * Generate professional order ID
     */
    private String generateOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = sdf.format(new Date());
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PC" + date + random;
    }

    /**
     * Calculate subtotal from cart items
     */
    private double calculateSubtotal(List<CartItem> cartItems) {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    /**
     * Get user's orders for display
     */
    public void getUserOrders(OrderCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onOrderCreationFailed("User not logged in");
            return;
        }

        db.collection("orders")
            .whereEqualTo("userId", user.getUid())
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                // This will be handled by the calling activity
                Log.d(TAG, "User orders retrieved successfully");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get user orders: " + e.getMessage());
            });
    }
} 