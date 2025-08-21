package com.app.pccooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.ComponentModel;
import com.app.pccooker.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.SavedItemViewHolder> {

    private final Context context;
    private final List<ComponentModel> savedItems;
    private final OnSavedItemActionListener listener;

    public interface OnSavedItemActionListener {
        void onAddToCartClicked(ComponentModel component);
        void onRemoveClicked(ComponentModel component);
        void onItemClicked(ComponentModel component);
    }

    public SavedItemsAdapter(Context context, List<ComponentModel> savedItems, OnSavedItemActionListener listener) {
        this.context = context;
        this.savedItems = savedItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_component, parent, false);
        return new SavedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedItemViewHolder holder, int position) {
        ComponentModel component = savedItems.get(position);
        holder.bind(component);
    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    class SavedItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView componentImage;
        private final TextView componentName;

        private final TextView componentPrice;
        private final TextView componentRating;
        private final Button addToCartButton;
        private final Button removeButton;

        public SavedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            componentImage = itemView.findViewById(R.id.componentImage);
            componentName = itemView.findViewById(R.id.componentName);

            componentPrice = itemView.findViewById(R.id.componentPrice);
            componentRating = itemView.findViewById(R.id.componentRating);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        public void bind(ComponentModel component) {
            componentName.setText(component.getName());

            componentPrice.setText("₹" + component.getPrice());
            
            if (component.getRating() > 0) {
                componentRating.setText(String.format("%.1f", component.getRating()));
                componentRating.setVisibility(View.VISIBLE);
            } else {
                componentRating.setVisibility(View.GONE);
            }

            // Load image
            if (component.getImageUrl() != null && !component.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(component.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(componentImage);
            } else {
                componentImage.setImageResource(R.drawable.ic_placeholder);
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClicked(component);
                }
            });

            addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClicked(component);
                }
            });

            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClicked(component);
                }
            });
        }
    }
} 