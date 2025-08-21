package com.app.pccooker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.ComponentModel;

import java.util.List;

public class AdminComponentAdapter extends RecyclerView.Adapter<AdminComponentAdapter.AdminViewHolder> {

    private final Context context;
    private final List<ComponentModel> components;
    private final OnComponentActionListener listener;

    public interface OnComponentActionListener {
        void onEditPrice(ComponentModel component);
        void onToggleStock(ComponentModel component);
        void onDeleteComponent(ComponentModel component);
    }

    public AdminComponentAdapter(Context context, List<ComponentModel> components, OnComponentActionListener listener) {
        this.context = context;
        this.components = components;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_component, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        ComponentModel component = components.get(position);

        holder.nameText.setText(component.getName());
        holder.categoryText.setText(component.getCategory());
        holder.priceText.setText("₹" + String.format("%.0f", component.getPrice()));
        holder.stockText.setText(component.isInStock() ? "In Stock" : "Out of Stock");
        holder.stockText.setTextColor(component.isInStock() ? 
            context.getResources().getColor(android.R.color.holo_green_dark) : 
            context.getResources().getColor(android.R.color.holo_red_dark));

        holder.editPriceButton.setOnClickListener(v -> {
            if (listener != null) listener.onEditPrice(component);
        });

        holder.toggleStockButton.setOnClickListener(v -> {
            if (listener != null) listener.onToggleStock(component);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteComponent(component);
        });
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, categoryText, priceText, stockText;
        Button editPriceButton, toggleStockButton, deleteButton;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.componentNameText);
            categoryText = itemView.findViewById(R.id.componentCategoryText);
            priceText = itemView.findViewById(R.id.componentPriceText);
            stockText = itemView.findViewById(R.id.componentStockText);
            editPriceButton = itemView.findViewById(R.id.editPriceButton);
            toggleStockButton = itemView.findViewById(R.id.toggleStockButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 