package com.app.pccooker;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.PCComponent;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView, savedRecyclerView;
    private TextView totalPriceText;
    private Button checkoutButton;
    private LinearLayout totalLayout, savedLayout, emptyCartLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        savedRecyclerView = view.findViewById(R.id.savedRecyclerView);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);
        totalLayout = view.findViewById(R.id.totalLayout);
        savedLayout = view.findViewById(R.id.savedLayout);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
            // You can launch a checkout fragment or activity here
        });

        refreshCart();
        return view;
    }

    private void refreshCart() {
        List<PCComponent> cartItems = CartManager.getInstance().getCartItems();
        List<PCComponent> savedItems = CartManager.getInstance().getSavedForLaterItems();
        int total = CartManager.getInstance().getCartTotal();

        boolean isCartEmpty = cartItems.isEmpty();

        emptyCartLayout.setVisibility(isCartEmpty ? View.VISIBLE : View.GONE);
        cartRecyclerView.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
        totalLayout.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
        checkoutButton.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);

        CartItemAdapter cartAdapter = new CartItemAdapter(requireContext(), cartItems, new CartItemAdapter.OnCartActionListener() {
            @Override
            public void onRemoveClicked(PCComponent component) {
                CartManager.getInstance().removeFromCart(component);
                refreshCart();
            }

            @Override
            public void onSaveForLaterClicked(PCComponent component) {
                CartManager.getInstance().moveToSaved(component);
                refreshCart();
            }

            @Override
            public void onQuantityChanged(PCComponent component, int newQuantity) {
                CartManager.getInstance().updateQuantity(component, newQuantity);
                refreshCart();
            }

            @Override
            public void onMoveToCartClicked(PCComponent component) {
                // Not used here
            }
        }, false);
        cartRecyclerView.setAdapter(cartAdapter);

        if (!savedItems.isEmpty()) {
            savedLayout.setVisibility(View.VISIBLE);
            savedRecyclerView.setVisibility(View.VISIBLE);

            CartItemAdapter savedAdapter = new CartItemAdapter(requireContext(), savedItems, new CartItemAdapter.OnCartActionListener() {
                @Override
                public void onRemoveClicked(PCComponent component) {
                    CartManager.getInstance().removeFromSaved(component);
                    refreshCart();
                }

                @Override
                public void onSaveForLaterClicked(PCComponent component) {}

                @Override
                public void onQuantityChanged(PCComponent component, int newQuantity) {}

                @Override
                public void onMoveToCartClicked(PCComponent component) {
                    CartManager.getInstance().moveToCart(component);
                    refreshCart();
                }
            }, true);
            savedRecyclerView.setAdapter(savedAdapter);
        } else {
            savedLayout.setVisibility(View.GONE);
            savedRecyclerView.setVisibility(View.GONE);
        }

        totalPriceText.setText("₹" + total);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge();
        }
    }
}
