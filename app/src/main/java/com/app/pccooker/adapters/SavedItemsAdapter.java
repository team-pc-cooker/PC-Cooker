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

import com.app.pccooker.ComponentModel;
import com.app.pccooker.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.SavedItemViewHolder> {

    private Context context;
    private List<ComponentModel> savedItems;
    private OnSavedItemActionListener listener;

    public interface OnSavedItemActionListener {
        void onAddToCartClicked(ComponentModel component);
        void onRemoveClicked(ComponentModel component);
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
        private ImageView componentImage;
        private TextView componentName;
        private TextView componentPrice;
        private TextView componentRating;
        private Button addToCartButton;
        private Button removeButton;

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
            componentPrice.setText("₹" + String.format("%.2f", component.getPrice()));
            componentRating.setText("★ " + String.format("%.1f", component.getRating()));

            // Load image using Glide
            if (component.getImageUrl() != null && !component.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(component.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(componentImage);
            } else {
                componentImage.setImageResource(R.drawable.placeholder_image);
            }

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