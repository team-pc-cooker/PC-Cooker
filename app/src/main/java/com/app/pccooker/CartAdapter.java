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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<ComponentModel> cartItems;

    public CartAdapter(List<ComponentModel> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_component, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ComponentModel component = cartItems.get(position);
        holder.nameText.setText(component.getName());
        holder.descriptionText.setText(component.getDescription());
        holder.priceText.setText("₹" + component.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(component.getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, descriptionText, priceText;

        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.componentImage);
            nameText = itemView.findViewById(R.id.componentName);
            descriptionText = itemView.findViewById(R.id.componentDescriptionText);
            priceText = itemView.findViewById(R.id.componentPrice);
        }
    }
}
