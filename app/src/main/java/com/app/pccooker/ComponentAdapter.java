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
        try {
            ComponentModel component = componentList.get(position);
            
            // Debug logging
            android.util.Log.d("ComponentAdapter", "Binding component at position " + position + ": " + 
                (component.getName() != null ? component.getName() : "NULL_NAME"));
            
            // Safely set text with null checks and proper formatting
            String name = component.getName() != null ? component.getName().trim() : "Unknown Component";
            String brand = component.getBrand() != null ? component.getBrand().trim() : "Unknown Brand";
            String category = component.getCategory() != null ? component.getCategory().trim() : "Unknown Category";
            String description = component.getDescription() != null ? component.getDescription().trim() : "No description available";
            
            // Format price with proper Indian currency formatting
            String price;
            try {
                double priceValue = component.getPrice();
                if (priceValue >= 100000) {
                    // For prices >= 1 lakh, show in lakhs
                    price = "₹" + String.format("%.1f", priceValue / 100000) + " Lakh";
                } else if (priceValue >= 1000) {
                    // For prices >= 1000, show with commas
                    price = "₹" + String.format("%,.0f", priceValue);
                } else {
                    // For smaller prices
                    price = "₹" + String.format("%.0f", priceValue);
                }
            } catch (Exception e) {
                price = "₹0";
            }
            
            // Format rating with proper star display
            String rating;
            try {
                double ratingValue = component.getRating();
                if (ratingValue > 0) {
                    rating = String.format("%.1f ★", ratingValue);
                } else {
                    rating = "No Rating";
                }
            } catch (Exception e) {
                rating = "No Rating";
            }
            
            // Set text with improved visibility
            holder.nameText.setText(name);
            holder.brandText.setText(brand.toUpperCase()); // Make brand uppercase for better visibility
            holder.categoryText.setText(category);
            holder.descriptionText.setText(description);
            holder.priceText.setText(price);
            holder.ratingText.setText(rating);
            
            // Debug logging for text values
            android.util.Log.d("ComponentAdapter", "Set text values - Name: " + name + 
                ", Brand: " + brand + ", Category: " + category + 
                ", Price: " + price + ", Rating: " + rating);

            // Enhanced image loading with ImageCacheManager
            try {
                String imageUrl = component.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageCacheManager.getInstance(holder.itemView.getContext())
                            .loadImage(imageUrl, holder.imageView, component.getCategory());
                } else {
                    // Use ImageCacheManager to get fallback image for category
                    ImageCacheManager.getInstance(holder.itemView.getContext())
                            .loadImage(null, holder.imageView, component.getCategory());
                }
            } catch (Exception e) {
                // If image loading fails, set placeholder
                holder.imageView.setImageResource(R.drawable.ic_placeholder);
            }

            // Set up button click listeners with robust error handling
            holder.selectButton.setOnClickListener(v -> {
                try {
                    if (listener != null && component != null) {
                        listener.onComponentClick(component);
                        Toast.makeText(holder.itemView.getContext(), 
                            component.getName() + " added to build", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Error adding component to build", Toast.LENGTH_SHORT).show();
                }
            });

            holder.saveButton.setOnClickListener(v -> {
                try {
                    if (component != null) {
                        Toast.makeText(holder.itemView.getContext(), 
                            "Saved for later: " + component.getName(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Error saving component", Toast.LENGTH_SHORT).show();
                }
            });

            // Set up item click listener with error handling
            holder.itemView.setOnClickListener(v -> {
                try {
                    if (listener != null && component != null) {
                        listener.onComponentClick(component);
                    }
                } catch (Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Error opening component details", Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            android.util.Log.e("ComponentAdapter", "Error binding component at position " + position, e);
            // Set default values to prevent crash with more visible text
            holder.nameText.setText("ERROR: Component " + position);
            holder.brandText.setText("ERROR");
            holder.categoryText.setText("ERROR");
            holder.descriptionText.setText("Error loading component details");
            holder.priceText.setText("₹0");
            holder.ratingText.setText("No Rating");
            holder.imageView.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return componentList.size();
    }

    static class ComponentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, brandText, categoryText, descriptionText, priceText, ratingText;
        ImageView imageView;
        Button selectButton, saveButton;

        public ComponentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            brandText = itemView.findViewById(R.id.brandText);
            categoryText = itemView.findViewById(R.id.categoryText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            priceText = itemView.findViewById(R.id.priceText);
            ratingText = itemView.findViewById(R.id.ratingText);
            imageView = itemView.findViewById(R.id.componentImage);
            selectButton = itemView.findViewById(R.id.selectButton);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }
}
