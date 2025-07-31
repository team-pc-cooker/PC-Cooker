package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private LinearLayout emptyCartLayout;
    private LinearLayout totalLayout;
    private Button checkoutButton;
    private TextView totalPriceText;
    private CartItemAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize views
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);
        totalLayout = view.findViewById(R.id.totalLayout);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        totalPriceText = view.findViewById(R.id.totalPriceText);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set checkout button click listener
        checkoutButton.setOnClickListener(v -> proceedToCheckout());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCart();
    }

    private void refreshCart() {
        CartManager.getInstance(requireContext()).loadCartFromFirebase(new CartManager.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<CartItem> cartItems) {
                double total = CartManager.getInstance(requireContext()).getCartTotal();
                boolean isCartEmpty = cartItems.isEmpty();

                emptyCartLayout.setVisibility(isCartEmpty ? View.VISIBLE : View.GONE);
                cartRecyclerView.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
                totalLayout.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
                checkoutButton.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);

                if (!isCartEmpty) {
                    totalPriceText.setText("₹" + String.format("%.2f", total));
                    
                    cartAdapter = new CartItemAdapter(requireContext(), cartItems, new CartItemAdapter.OnCartActionListener() {
                        @Override
                        public void onRemoveClicked(CartItem cartItem) {
                            CartManager.getInstance(requireContext()).removeFromCart(cartItem.getId());
                            refreshCart();
                        }

                        @Override
                        public void onSaveForLaterClicked(CartItem cartItem) {
                            // Convert CartItem back to ComponentModel for saving
                            ComponentModel component = new ComponentModel();
                            component.setId(cartItem.getId());
                            component.setName(cartItem.getName());
                            component.setImageUrl(cartItem.getImageUrl());
                            component.setPrice(cartItem.getPrice());
                            component.setDescription(cartItem.getDescription());
                            component.setRating(cartItem.getRating());
                            
                            CartManager.getInstance(requireContext()).saveForLater(component);
                            Toast.makeText(getContext(), cartItem.getName() + " saved for later", Toast.LENGTH_SHORT).show();
                            refreshCart();
                        }

                        @Override
                        public void onQuantityChanged(CartItem cartItem, int newQuantity) {
                            CartManager.getInstance(requireContext()).updateQuantity(cartItem.getId(), newQuantity);
                            refreshCart();
                        }

                        @Override
                        public void onMoveToCartClicked(CartItem cartItem) {
                            // Not used here
                        }
                    }, false);
                    cartRecyclerView.setAdapter(cartAdapter);
                }
            }
        });
    }

    private void proceedToCheckout() {
        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login to proceed with checkout", Toast.LENGTH_LONG).show();
            // Navigate to login
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
            return;
        }

        // Check if cart has items
        List<CartItem> cartItems = CartManager.getInstance(requireContext()).getCartItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to address selection
        navigateToAddressSelection();
    }

    private void navigateToAddressSelection() {
        // Create and show address selection dialog or navigate to address page
        AddressSelectionFragment addressFragment = new AddressSelectionFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, addressFragment)
                .addToBackStack("cart_to_address")
                .commit();
    }
}
