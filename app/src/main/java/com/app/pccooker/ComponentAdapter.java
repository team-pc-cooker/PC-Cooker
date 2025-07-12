package com.app.pccooker;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.ComponentModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder> {

    private final List<ComponentModel> componentList;
    private final OnComponentClickListener listener;

    public interface OnComponentClickListener {
        void onComponentClick(ComponentModel component);
    }

    public interface OnComponentActionListener {
        void onSelectClicked(ComponentModel component);
        void onSaveForLaterClicked(ComponentModel component);
    }

    public ComponentAdapter(Context context, List<ComponentModel> componentList, OnComponentClickListener listener) {
        this.componentList = componentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComponentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_component_modern, parent, false);
        return new ComponentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComponentViewHolder holder, int position) {
        ComponentModel component = componentList.get(position);
        
        holder.nameText.setText(component.getName());
        holder.brandText.setText(component.getBrand());
        holder.priceText.setText("₹" + String.format("%.0f", component.getPrice()));
        holder.ratingText.setText(String.format("%.1f", component.getRating()) + " ★");
        holder.ratingCountText.setText("(" + component.getRatingCount() + ")");
        
        // Load image with placeholder
        if (component.getImageUrl() != null && !component.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(component.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_placeholder);
        }

        // Set stock status
        if (component.isInStock()) {
            holder.stockText.setText("In Stock");
            holder.stockText.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.stockText.setText("Out of Stock");
            holder.stockText.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onComponentClick(component);
        });
    }

    @Override
    public int getItemCount() {
        return componentList.size();
    }

    static class ComponentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, brandText, priceText, ratingText, ratingCountText, stockText;
        ImageView imageView;

        public ComponentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            brandText = itemView.findViewById(R.id.brandText);
            priceText = itemView.findViewById(R.id.priceText);
            ratingText = itemView.findViewById(R.id.ratingText);
            ratingCountText = itemView.findViewById(R.id.ratingCountText);
            stockText = itemView.findViewById(R.id.stockText);
            imageView = itemView.findViewById(R.id.componentImage);
        }
    }
}
