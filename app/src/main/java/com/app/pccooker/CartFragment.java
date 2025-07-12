package com.app.pccooker;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;

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
            // Navigate to checkout fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CheckoutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        refreshCart();
        return view;
    }

    private void refreshCart() {
        CartManager.getInstance(requireContext()).loadCartFromFirebase(new CartManager.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<ComponentModel> cartItems) {
                double total = CartManager.getInstance(requireContext()).getCartTotal();
                boolean isCartEmpty = cartItems.isEmpty();

                emptyCartLayout.setVisibility(isCartEmpty ? View.VISIBLE : View.GONE);
                cartRecyclerView.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
                totalLayout.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
                checkoutButton.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);

                if (!isCartEmpty) {
                    CartItemAdapter cartAdapter = new CartItemAdapter(requireContext(), cartItems, new CartItemAdapter.OnCartActionListener() {
                        @Override
                        public void onRemoveClicked(ComponentModel component) {
                            CartManager.getInstance(requireContext()).removeFromCart(component.getId());
                            refreshCart();
                        }

                        @Override
                        public void onSaveForLaterClicked(ComponentModel component) {
                            CartManager.getInstance(requireContext()).saveForLater(component);
                            Toast.makeText(getContext(), component.getName() + " saved for later", Toast.LENGTH_SHORT).show();
                            refreshCart();
                        }

                        @Override
                        public void onQuantityChanged(ComponentModel component, int newQuantity) {
                            CartManager.getInstance(requireContext()).updateQuantity(component.getId(), newQuantity);
                            refreshCart();
                        }

                        @Override
                        public void onMoveToCartClicked(ComponentModel component) {
                            // Not used here
                        }
                    }, false);
                    cartRecyclerView.setAdapter(cartAdapter);
                }

                totalPriceText.setText("₹" + String.format("%.0f", total));
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge();
                }
            }
        });
    }
}
