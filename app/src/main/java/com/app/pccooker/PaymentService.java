package com.app.pccooker;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import java.util.Map;
import java.util.List;
import com.app.pccooker.models.CartItem;

public class PaymentService implements PaymentResultListener {
    
    private static final String RAZORPAY_KEY_ID = "rzp_test_YOUR_KEY_ID"; // Replace with your test key
    private final Context context;
    private final PaymentCallback callback;
    
    public interface PaymentCallback {
        void onPaymentSuccess(String paymentId);
        void onPaymentError(String error);
        void onPaymentCancelled();
    }
    
    public PaymentService(Context context, PaymentCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    public void startPayment(double amount, String currency, String description) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY_ID);
        
        try {
            JSONObject options = new JSONObject();
            options.put("name", "PC Cooker");
            options.put("description", description);
            options.put("currency", currency);
            options.put("amount", (int)(amount * 100)); // Amount in paise
            options.put("prefill.email", "customer@example.com");
            options.put("prefill.contact", "9999999999");
            options.put("theme.color", "#4CAF50");
            
            // Enable multiple payment methods
            JSONObject config = new JSONObject();
            config.put("display", new JSONObject()
                .put("blocks", new JSONObject()
                    .put("banks", new JSONObject().put("name", "Pay using UPI").put("instrument", new JSONObject().put("method", "card").put("issuers", new JSONObject().put("HDFC", "HDFC").put("ICICI", "ICICI"))))
                    .put("wallets", new JSONObject().put("name", "Pay using Wallets").put("instrument", new JSONObject().put("method", "wallet").put("wallets", new JSONObject().put("paytm", "Paytm").put("amazonpay", "Amazon Pay"))))
                    .put("cards", new JSONObject().put("name", "Pay using Cards").put("instrument", new JSONObject().put("method", "card").put("issuers", new JSONObject().put("HDFC", "HDFC").put("ICICI", "ICICI"))))
                    .put("netbanking", new JSONObject().put("name", "Pay using Net Banking").put("instrument", new JSONObject().put("method", "netbanking").put("banks", new JSONObject().put("HDFC", "HDFC").put("ICICI", "ICICI"))))
                    .put("upi", new JSONObject().put("name", "Pay using UPI").put("instrument", new JSONObject().put("method", "upi"))))
            );
            
            options.put("config", config);
            
            checkout.open((Activity) context, options);
            
        } catch (Exception e) {
            Toast.makeText(context, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    public void startTokenPayment(double totalAmount) {
        // Calculate token amount (20% of total)
        double tokenAmount = totalAmount * 0.20;
        startPayment(tokenAmount, "INR", "Token Payment - 20% of total amount");
    }
    
    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        Toast.makeText(context, "Payment successful! ID: " + razorpayPaymentId, Toast.LENGTH_LONG).show();
        
        // Create order in Firebase
        List<CartItem> cartItems = CartManager.getInstance(context).getCartItems();
        if (!cartItems.isEmpty()) {
            double totalAmount = CartManager.getInstance(context).getCartTotal();
            double paidAmount = totalAmount; // For full payment
            
            OrderManager.getInstance(context).createOrder(
                cartItems,
                "Delivery Address", // TODO: Get from user's selected address
                totalAmount,
                paidAmount,
                razorpayPaymentId,
                new OrderManager.OnOrderCreatedListener() {
                    @Override
                    public void onOrderCreated(String orderId, String orderNumber) {
                        // Navigate to order confirmation
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).showOrderConfirmation(orderNumber);
                        }
                    }

                    @Override
                    public void onOrderFailed(String error) {
                        Toast.makeText(context, "Order creation failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }
        
        if (callback != null) {
            callback.onPaymentSuccess(razorpayPaymentId);
        }
    }
    
    @Override
    public void onPaymentError(int errorCode, String response) {
        String errorMessage = "Payment failed";
        switch (errorCode) {
            case Checkout.NETWORK_ERROR:
                errorMessage = "Network error";
                break;
            case Checkout.INVALID_OPTIONS:
                errorMessage = "Invalid payment options";
                break;
            case Checkout.TLS_ERROR:
                errorMessage = "TLS error";
                break;
            default:
                if (errorCode == 2) {
                    errorMessage = "Payment cancelled";
                } else {
                    errorMessage = "Payment failed: " + response;
                }
        }
        
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        if (callback != null) {
            callback.onPaymentError(errorMessage);
        }
    }
    
    public void onPaymentCancelled() {
        Toast.makeText(context, "Payment cancelled", Toast.LENGTH_SHORT).show();
        if (callback != null) {
            callback.onPaymentCancelled();
        }
    }
} 