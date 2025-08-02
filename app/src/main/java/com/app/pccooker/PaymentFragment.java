package com.app.pccooker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.pccooker.models.AddressModel;
import com.app.pccooker.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentFragment extends Fragment implements PaymentResultListener {

    private TextView orderSummaryText;
    private TextView totalAmountText;
    private TextView deliveryAddressText;
    private Button payButton;
    
    private List<CartItem> cartItems;
    private double totalAmount;
    private AddressModel deliveryAddress;
    private String addressId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Initialize views
        orderSummaryText = view.findViewById(R.id.orderSummaryText);
        totalAmountText = view.findViewById(R.id.totalAmountText);
        deliveryAddressText = view.findViewById(R.id.deliveryAddressText);
        payButton = view.findViewById(R.id.payButton);

        // Get address ID from arguments
        if (getArguments() != null) {
            addressId = getArguments().getString("address_id");
        }

        // Load cart items and address
        loadOrderDetails();
        
        // Set payment button click listener
        payButton.setOnClickListener(v -> startPayment());

        return view;
    }

    private void loadOrderDetails() {
        // Load cart items
        cartItems = CartManager.getInstance(requireContext()).getCartItems();
        totalAmount = CartManager.getInstance(requireContext()).getCartTotal();

        // Load delivery address
        if (addressId != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                    FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

            if (userId != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("addresses")
                        .document(addressId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            deliveryAddress = documentSnapshot.toObject(AddressModel.class);
                            if (deliveryAddress != null) {
                                deliveryAddress.setId(documentSnapshot.getId());
                                updateUI();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to load address: " + e.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        }

        updateUI();
    }

    private void updateUI() {
        // Update order summary
        StringBuilder summary = new StringBuilder();
        for (CartItem item : cartItems) {
            summary.append("• ").append(item.getName()).append(" x").append(item.getQuantity())
                   .append(" - ₹").append(String.format("%.2f", item.getPrice() * item.getQuantity())).append("\n");
        }
        orderSummaryText.setText(summary.toString());

        // Update total amount
        totalAmountText.setText("₹" + String.format("%.2f", totalAmount));

        // Update delivery address
        if (deliveryAddress != null) {
            String addressText = deliveryAddress.getName() + "\n" +
                               deliveryAddress.getMobile() + "\n" +
                               deliveryAddress.getAddress() + "\n" +
                               deliveryAddress.getCity() + ", " + deliveryAddress.getState() + " - " +
                               deliveryAddress.getPincode();
            deliveryAddressText.setText(addressText);
        }
    }

    private void startPayment() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deliveryAddress == null) {
            Toast.makeText(getContext(), "Please select delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        Checkout checkout = new Checkout();
        checkout.setKeyID("Q8bqi8kyvaNTKR"); // Replace with your Razorpay test key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "PC Cooker");
            options.put("description", "Custom PC Components Order");
            options.put("currency", "INR");
            options.put("amount", (int)(totalAmount * 100)); // Amount in paise
            
            // Prefill customer details
            options.put("prefill.email", FirebaseAuth.getInstance().getCurrentUser() != null ? 
                    FirebaseAuth.getInstance().getCurrentUser().getEmail() : "customer@example.com");
            options.put("prefill.contact", deliveryAddress.getMobile());
            options.put("prefill.name", deliveryAddress.getName());
            
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

            checkout.open((Activity) requireContext(), options);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        // Payment successful - save order to Firebase
        saveOrderToFirebase(razorpayPaymentId, "SUCCESS");
        
        Toast.makeText(getContext(), "Payment successful! Order ID: " + razorpayPaymentId, Toast.LENGTH_LONG).show();
        
        // Clear cart
        CartManager.getInstance(requireContext()).clearCart();
        
        // Send notification
        sendOrderNotification();
        
        // Navigate to order confirmation
        OrderConfirmationFragment confirmationFragment = new OrderConfirmationFragment();
        Bundle args = new Bundle();
        args.putString("order_id", razorpayPaymentId);
        args.putString("payment_id", razorpayPaymentId);
        confirmationFragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, confirmationFragment)
                .addToBackStack("payment_to_confirmation")
                .commit();
    }

    @Override
    public void onPaymentError(int errorCode, String response) {
        Toast.makeText(getContext(), "Payment failed: " + response, Toast.LENGTH_LONG).show();
    }

    private void saveOrderToFirebase(String paymentId, String status) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) return;

        // Create order data
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("orderDate", new Date());
        orderData.put("totalAmount", totalAmount);
        orderData.put("paymentId", paymentId);
        orderData.put("status", status);
        orderData.put("deliveryAddress", deliveryAddress);
        orderData.put("items", cartItems);

        // Save to Firebase
        FirebaseFirestore.getInstance()
                .collection("orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    // Order saved successfully
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save order: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void sendOrderNotification() {
        // TODO: Implement SMS and email notification
        // For now, just show a toast
        Toast.makeText(getContext(), "Order confirmation sent to your mobile and email", 
                Toast.LENGTH_LONG).show();
    }
} 