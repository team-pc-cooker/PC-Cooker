package com.app.pccooker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.pccooker.models.ComponentModel;

import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartViewHolder> {

    private final List<ComponentModel> cartItems;

    public CartListAdapter(List<ComponentModel> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_component, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ComponentModel component = cartItems.get(position);
        holder.nameText.setText(component.getName());
        holder.priceText.setText("₹" + component.getPrice());

        // Enhanced image loading with error handling
        String imageUrl = component.getImageUrl();
        android.util.Log.d("CartListAdapter", "Loading image for " + component.getName() + ": " + imageUrl);
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_placeholder);
            android.util.Log.w("CartListAdapter", "No image URL for component: " + component.getName());
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, priceText;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.componentImage);
            nameText = itemView.findViewById(R.id.componentName);
            priceText = itemView.findViewById(R.id.componentPrice);
        }
    }
}
