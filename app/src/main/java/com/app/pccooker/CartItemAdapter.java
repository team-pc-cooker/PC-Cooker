package com.app.pccooker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.PCComponent;
import com.bumptech.glide.Glide;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private final Context context;
    private final List<PCComponent> componentList;
    private final OnCartActionListener listener;
    private final boolean isSavedList;

    public interface OnCartActionListener {
        void onRemoveClicked(PCComponent component);
        void onSaveForLaterClicked(PCComponent component);
        void onMoveToCartClicked(PCComponent component);
        void onQuantityChanged(PCComponent component, int newQuantity);
    }

    public CartItemAdapter(Context context, List<PCComponent> components,
                           OnCartActionListener listener, boolean isSavedList) {
        this.context = context;
        this.componentList = components;
        this.listener = listener;
        this.isSavedList = isSavedList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        PCComponent component = componentList.get(position);

        holder.nameText.setText(component.getName());
        holder.descriptionText.setText(component.getDescription());
        holder.priceText.setText("₹" + component.getPrice());
        holder.quantityText.setText(String.valueOf(component.getQuantity()));

        // Rating stars
        holder.ratingLayout.removeAllViews();
        int rating = component.getRating();
        for (int i = 0; i < rating; i++) {
            ImageView star = new ImageView(context);
            star.setImageResource(R.drawable.ic_star);
            star.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            holder.ratingLayout.addView(star);
        }

        Glide.with(context)
                .load(component.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.componentImage);

        // Action Buttons
        if (isSavedList) {
            holder.saveForLater.setVisibility(View.GONE);
            holder.moveToCart.setVisibility(View.VISIBLE);
            holder.incrementBtn.setVisibility(View.GONE);
            holder.decrementBtn.setVisibility(View.GONE);
            holder.quantityText.setVisibility(View.GONE);

            holder.moveToCart.setOnClickListener(v -> {
                if (listener != null) listener.onMoveToCartClicked(component);
            });

        } else {
            holder.saveForLater.setVisibility(View.VISIBLE);
            holder.moveToCart.setVisibility(View.GONE);
            holder.incrementBtn.setVisibility(View.VISIBLE);
            holder.decrementBtn.setVisibility(View.VISIBLE);
            holder.quantityText.setVisibility(View.VISIBLE);

            holder.incrementBtn.setOnClickListener(v -> {
                int qty = component.getQuantity();
                component.setQuantity(qty + 1);
                holder.quantityText.setText(String.valueOf(component.getQuantity()));
                if (listener != null) listener.onQuantityChanged(component, qty + 1);
            });

            holder.decrementBtn.setOnClickListener(v -> {
                int qty = component.getQuantity();
                if (qty > 1) {
                    component.setQuantity(qty - 1);
                    holder.quantityText.setText(String.valueOf(component.getQuantity()));
                    if (listener != null) listener.onQuantityChanged(component, qty - 1);
                }
            });

            holder.saveForLater.setOnClickListener(v -> {
                if (listener != null) listener.onSaveForLaterClicked(component);
            });
        }

        // Delete icon (works in both modes)
        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveClicked(component);
        });
    }

    @Override
    public int getItemCount() {
        return componentList.size();
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
