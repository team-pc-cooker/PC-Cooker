package com.app.pccooker;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.adapters.CartItemAdapter;
import com.app.pccooker.models.ComponentModel;
import com.app.pccooker.models.CartItem;

import java.util.List;

public class CheckoutFragment extends Fragment {

    private RecyclerView summaryRecyclerView;
    private TextView summaryTotal;
    private Button payTokenButton;
    private Button payFullButton;
    // Payment is now handled by PaymentActivity

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

        // Payment is now handled by PaymentActivity

        setupPaymentButtons();
        loadSummary();

        return view;
    }

    private void loadSummary() {
        CartManager.getInstance(requireContext()).loadCartFromFirebase(new CartManager.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double total = CartManager.getInstance(requireContext()).getCartTotal();
                summaryTotal.setText(String.format("Total: ₹%.0f", total));

                // Create read-only adapter for checkout display
                CartItemAdapter adapter = new CartItemAdapter(requireContext(), cartItems, new CartItemAdapter.OnCartItemActionListener() {
                    @Override
                    public void onQuantityChanged(CartItem item, int newQuantity) {
                        // Read-only in checkout - do nothing
                    }

                    @Override
                    public void onRemoveClicked(CartItem item) {
                        // Read-only in checkout - do nothing
                    }

                    @Override
                    public void onSaveForLaterClicked(CartItem item) {
                        // Read-only in checkout - do nothing
                    }
                });
                summaryRecyclerView.setAdapter(adapter);
            }
        });
    }
    
    private void setupPaymentButtons() {
        CartManager.getInstance(requireContext()).loadCartFromFirebase(new CartManager.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<CartItem> cartItems) {
                double total = CartManager.getInstance(requireContext()).getCartTotal();
                double tokenAmount = total * 0.20; // 20% token amount
                
                payTokenButton.setText(String.format("Pay Token Amount (₹%.0f)", tokenAmount));
                payFullButton.setText(String.format("Pay Full Amount (₹%.0f)", total));
                
                payTokenButton.setOnClickListener(v -> {
                    // Navigate to PaymentActivity for token payment
                    Intent intent = new Intent(requireContext(), PaymentActivity.class);
                    startActivity(intent);
                });
                
                payFullButton.setOnClickListener(v -> {
                    // Navigate to PaymentActivity for full payment
                    Intent intent = new Intent(requireContext(), PaymentActivity.class);
                    startActivity(intent);
                });
            }
        });
    }
}
