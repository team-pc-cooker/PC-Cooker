package com.app.pccooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.R;
import com.app.pccooker.models.ComponentModel;
import com.app.pccooker.ImageCacheManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

public class TrendingComponentAdapter extends RecyclerView.Adapter<TrendingComponentAdapter.TrendingViewHolder> {

    private Context context;
    private List<ComponentModel> components;
    private OnComponentClickListener clickListener;

    public interface OnComponentClickListener {
        void onComponentClick(ComponentModel component);
    }

    public TrendingComponentAdapter(Context context, List<ComponentModel> components, OnComponentClickListener clickListener) {
        this.context = context;
        this.components = components;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trending_component, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        ComponentModel component = components.get(position);
        holder.bind(component);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    class TrendingViewHolder extends RecyclerView.ViewHolder {
        private ImageView componentImage;
        private TextView nameText, brandText, categoryText, descriptionText;
        private TextView priceText, ratingText, reviewCountText;
        private TextView trendingScoreText, buildTypeTag;
        private Button addToBuildButton, saveButton;
        private ImageButton compareButton, shareButton, infoButton;
        private View trendingIndicator;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            
            componentImage = itemView.findViewById(R.id.componentImage);
            nameText = itemView.findViewById(R.id.nameText);
            brandText = itemView.findViewById(R.id.brandText);
            categoryText = itemView.findViewById(R.id.categoryText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            priceText = itemView.findViewById(R.id.priceText);
            ratingText = itemView.findViewById(R.id.ratingText);
            reviewCountText = itemView.findViewById(R.id.reviewCountText);
            trendingScoreText = itemView.findViewById(R.id.trendingScoreText);
            buildTypeTag = itemView.findViewById(R.id.buildTypeTag);
            addToBuildButton = itemView.findViewById(R.id.addToBuildButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            compareButton = itemView.findViewById(R.id.compareButton);
            shareButton = itemView.findViewById(R.id.shareButton);
            infoButton = itemView.findViewById(R.id.infoButton);
            trendingIndicator = itemView.findViewById(R.id.trendingIndicator);
        }

        public void bind(ComponentModel component) {
            // Basic info
            nameText.setText(component.getName());
            brandText.setText(component.getBrand());
            categoryText.setText(component.getCategory());
            descriptionText.setText(component.getDescription());
            
            // Price
            priceText.setText(String.format(Locale.getDefault(), "₹%.0f", component.getPrice()));
            
            // Rating and reviews
            ratingText.setText(String.format(Locale.getDefault(), "%.1f", component.getRating()));
            reviewCountText.setText(String.format(Locale.getDefault(), "(%d)", component.getRatingCount()));
            
            // Trending score
            double trendingScore = component.getTrendingScore();
            trendingScoreText.setText(String.format(Locale.getDefault(), "🔥 %.0f", trendingScore));
            
            // Show trending indicator for high scores
            trendingIndicator.setVisibility(trendingScore > 90 ? View.VISIBLE : View.GONE);
            
            // Build type tag
            List<String> buildTypes = component.getBuildTypes();
            if (buildTypes != null && !buildTypes.isEmpty()) {
                buildTypeTag.setText(buildTypes.get(0));
                buildTypeTag.setVisibility(View.VISIBLE);
            } else {
                buildTypeTag.setVisibility(View.GONE);
            }
            
            // Enhanced image loading with ImageCacheManager
            ImageCacheManager.getInstance(context)
                    .loadImage(component.getImageUrl(), componentImage, component.getCategory());
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onComponentClick(component);
                }
            });
            
            addToBuildButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onComponentClick(component);
                }
            });
            
            saveButton.setOnClickListener(v -> {
                // TODO: Implement save for later functionality
            });
            
            compareButton.setOnClickListener(v -> {
                // TODO: Implement compare functionality
            });
            
            shareButton.setOnClickListener(v -> {
                // TODO: Implement share functionality
            });
            
            infoButton.setOnClickListener(v -> {
                // TODO: Implement detailed info functionality
            });
        }
    }
}