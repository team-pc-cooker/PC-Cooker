package com.app.pccooker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.PCComponent;
import com.bumptech.glide.Glide;
import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder> {
    private List<PCComponent> components;

    public ComponentAdapter(List<PCComponent> components) {
        this.components = components;
    }

    public void updateComponents(List<PCComponent> newComponents) {
        this.components = newComponents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComponentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_component, parent, false);
        return new ComponentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComponentViewHolder holder, int position) {
        PCComponent component = components.get(position);
        holder.nameText.setText(component.getName());
        holder.descriptionText.setText(component.getDescription());
        holder.priceText.setText("₹" + component.getPrice());
        
        Glide.with(holder.itemView.getContext())
                .load(component.getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    static class ComponentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, descriptionText, priceText;

        ComponentViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.componentImage);
            nameText = itemView.findViewById(R.id.componentName);
            descriptionText = itemView.findViewById(R.id.componentDescription);
            priceText = itemView.findViewById(R.id.componentPrice);
        }
    }
}