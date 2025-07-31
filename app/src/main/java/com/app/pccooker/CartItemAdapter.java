package com.app.pccooker;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.CartItem;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> cartItemList;
    private final OnCartActionListener listener;
    private final boolean isSavedList;

    public interface OnCartActionListener {
        void onRemoveClicked(CartItem cartItem);
        void onSaveForLaterClicked(CartItem cartItem);
        void onMoveToCartClicked(CartItem cartItem);
        void onQuantityChanged(CartItem cartItem, int newQuantity);
    }

    public CartItemAdapter(Context context, List<CartItem> cartItems,
                           OnCartActionListener listener, boolean isSavedList) {
        this.context = context;
        this.cartItemList = cartItems;
        this.listener = listener;
        this.isSavedList = isSavedList;
    }

    @NotNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        try {
            CartItem cartItem = cartItemList.get(position);
            if (cartItem == null) return;

            // Set text with null checks
            holder.nameText.setText(cartItem.getName() != null ? cartItem.getName() : "Unknown Component");
            holder.descriptionText.setText(cartItem.getDescription() != null ? cartItem.getDescription() : "No description available");
            holder.priceText.setText("₹" + String.format("%.0f", cartItem.getPrice()));
            
            // Get quantity from cart item (default to 1 if not set)
            int quantity = cartItem.getQuantity() > 0 ? cartItem.getQuantity() : 1;
            holder.quantityText.setText(String.valueOf(quantity));

            // Rating stars
            holder.ratingLayout.removeAllViews();
            int rating = (int) Math.max(0, Math.min(5, cartItem.getRating())); // Clamp between 0-5
            for (int i = 0; i < rating; i++) {
                ImageView star = new ImageView(context);
                star.setImageResource(R.drawable.ic_star);
                star.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
                holder.ratingLayout.addView(star);
            }

            // Load image with error handling
            if (cartItem.getImageUrl() != null && !cartItem.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(cartItem.getImageUrl())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(holder.componentImage);
            } else {
                holder.componentImage.setImageResource(R.drawable.ic_placeholder);
            }

            // Action Buttons
            if (isSavedList) {
                holder.saveForLater.setVisibility(View.GONE);
                holder.moveToCart.setVisibility(View.VISIBLE);
                holder.incrementBtn.setVisibility(View.GONE);
                holder.decrementBtn.setVisibility(View.GONE);
                holder.quantityText.setVisibility(View.GONE);

                holder.moveToCart.setOnClickListener(v -> {
                    if (listener != null) listener.onMoveToCartClicked(cartItem);
                });

            } else {
                holder.saveForLater.setVisibility(View.VISIBLE);
                holder.moveToCart.setVisibility(View.GONE);
                holder.incrementBtn.setVisibility(View.VISIBLE);
                holder.decrementBtn.setVisibility(View.VISIBLE);
                holder.quantityText.setVisibility(View.VISIBLE);

                holder.incrementBtn.setOnClickListener(v -> {
                    try {
                        int currentQuantity = Integer.parseInt(holder.quantityText.getText().toString());
                        int newQuantity = currentQuantity + 1;
                        holder.quantityText.setText(String.valueOf(newQuantity));
                        if (listener != null) listener.onQuantityChanged(cartItem, newQuantity);
                    } catch (NumberFormatException e) {
                        // Handle invalid number format
                        holder.quantityText.setText("1");
                        if (listener != null) listener.onQuantityChanged(cartItem, 1);
                    }
                });

                holder.decrementBtn.setOnClickListener(v -> {
                    try {
                        int currentQuantity = Integer.parseInt(holder.quantityText.getText().toString());
                        if (currentQuantity > 1) {
                            int newQuantity = currentQuantity - 1;
                            holder.quantityText.setText(String.valueOf(newQuantity));
                            if (listener != null) listener.onQuantityChanged(cartItem, newQuantity);
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid number format
                        holder.quantityText.setText("1");
                        if (listener != null) listener.onQuantityChanged(cartItem, 1);
                    }
                });

                holder.saveForLater.setOnClickListener(v -> {
                    if (listener != null) listener.onSaveForLaterClicked(cartItem);
                });
            }

            // Delete icon (works in both modes)
            holder.deleteBtn.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveClicked(cartItem);
            });
            
        } catch (Exception e) {
            // Handle any unexpected errors gracefully
            android.util.Log.e("CartItemAdapter", "Error binding view holder at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, priceText, quantityText;
        ImageView componentImage, incrementBtn, decrementBtn, deleteBtn;
        Button saveForLater, moveToCart;
        LinearLayout ratingLayout;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.componentNameText);
            descriptionText = itemView.findViewById(R.id.componentDescriptionText);
            priceText = itemView.findViewById(R.id.componentPriceText);
            quantityText = itemView.findViewById(R.id.quantityText);
            incrementBtn = itemView.findViewById(R.id.incrementButton);
            decrementBtn = itemView.findViewById(R.id.decrementButton);
            saveForLater = itemView.findViewById(R.id.saveForLaterBtn);
            moveToCart = itemView.findViewById(R.id.moveToCartBtn);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
            componentImage = itemView.findViewById(R.id.componentImage);
            ratingLayout = itemView.findViewById(R.id.ratingLayout);
        }
    }
}
