package com.app.pccooker;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.PCComponent;

import java.util.List;

public class CheckoutFragment extends Fragment {

    private RecyclerView summaryRecyclerView;
    private TextView summaryTotal;
    private Button payTokenButton;
    private Button payFullButton;
    private PaymentService paymentService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        // Initialize views
        summaryRecyclerView = view.findViewById(R.id.summaryRecyclerView);
        summaryTotal = view.findViewById(R.id.orderTotalText);
        payTokenButton = view.findViewById(R.id.payTokenButton);
        payFullButton = view.findViewById(R.id.payFullButton);

        summaryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize payment service
        paymentService = new PaymentService(requireContext(), new PaymentService.PaymentCallback() {
            @Override
            public void onPaymentSuccess(String paymentId) {
                // Handle successful payment
                Toast.makeText(getContext(), "Payment successful! Order placed.", Toast.LENGTH_LONG).show();
                // Navigate to order confirmation
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onPaymentError(String error) {
                Toast.makeText(getContext(), "Payment failed: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaymentCancelled() {
                Toast.makeText(getContext(), "Payment cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        setupPaymentButtons();
        loadSummary();

        return view;
    }

    private void loadSummary() {
        CartManager.getInstance(requireContext()).loadCartFromFirebase(new CartManager.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<ComponentModel> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double total = CartManager.getInstance(requireContext()).getCartTotal();
                summaryTotal.setText(String.format("Total: ₹%.0f", total));

                // read-only = true for checkout
                CartItemAdapter adapter = new CartItemAdapter(requireContext(), cartItems, null, true);
                summaryRecyclerView.setAdapter(adapter);
            }
        });
    }
    
    private void setupPaymentButtons() {
        CartManager.getInstance(requireContext()).loadCartFromFirebase(new CartManager.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<ComponentModel> cartItems) {
                double total = CartManager.getInstance(requireContext()).getCartTotal();
                double tokenAmount = total * 0.20; // 20% token amount
                
                payTokenButton.setText(String.format("Pay Token Amount (₹%.0f)", tokenAmount));
                payFullButton.setText(String.format("Pay Full Amount (₹%.0f)", total));
                
                payTokenButton.setOnClickListener(v -> {
                    paymentService.startTokenPayment(total);
                });
                
                payFullButton.setOnClickListener(v -> {
                    paymentService.startPayment(total, "INR", "Full payment for PC components");
                });
            }
        });
    }
}
