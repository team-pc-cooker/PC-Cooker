package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrderConfirmationFragment extends Fragment {

    private TextView orderIdText, thankYouText, orderDetailsText;
    private Button continueShoppingButton, viewOrdersButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirmation, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        orderIdText = view.findViewById(R.id.orderIdText);
        thankYouText = view.findViewById(R.id.thankYouText);
        orderDetailsText = view.findViewById(R.id.orderDetailsText);
        continueShoppingButton = view.findViewById(R.id.continueShoppingButton);
        viewOrdersButton = view.findViewById(R.id.viewOrdersButton);
    }

    private void setupClickListeners() {
        continueShoppingButton.setOnClickListener(v -> {
            // Navigate to home
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        });

        viewOrdersButton.setOnClickListener(v -> {
            // Navigate to orders
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyOrdersFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
} 