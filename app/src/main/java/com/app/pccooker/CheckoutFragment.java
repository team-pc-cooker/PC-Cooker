package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        // Initialize views
        summaryRecyclerView = view.findViewById(R.id.summaryRecyclerView);
        summaryTotal = view.findViewById(R.id.orderTotalText);

        summaryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSummary();

        return view;
    }

    private void loadSummary() {
        List<PCComponent> cartItems = CartManager.getInstance().getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        int total = CartManager.getInstance().getCartTotal();
        summaryTotal.setText(String.format("Total: ₹%d", total));

        // read-only = true for checkout
        CartItemAdapter adapter = new CartItemAdapter(requireContext(), cartItems, null, true);
        summaryRecyclerView.setAdapter(adapter);
    }
}
