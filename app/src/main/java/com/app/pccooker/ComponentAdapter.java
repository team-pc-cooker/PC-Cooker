package com.app.pccooker;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.models.PCComponent;
import com.bumptech.glide.Glide;

import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder> {

    private final List<PCComponent> componentList;
    private final OnComponentActionListener listener;

    public interface OnComponentActionListener {
        void onSelectClicked(PCComponent component);
        void onSaveForLaterClicked(PCComponent component);
    }

    public ComponentAdapter(List<PCComponent> componentList, OnComponentActionListener listener) {
        this.componentList = componentList;
        this.listener = listener;
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
        PCComponent component = componentList.get(position);
        holder.nameText.setText(component.getName() != null ? component.getName() : "Unknown");

        holder.descriptionText.setText(component.getDescription());
        holder.priceText.setText("₹" + component.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(component.getImageUrl())
                .into(holder.imageView);

        holder.selectBtn.setOnClickListener(v -> {
            if (listener != null) listener.onSelectClicked(component);
        });

        holder.saveBtn.setOnClickListener(v -> {
            if (listener != null) listener.onSaveForLaterClicked(component);
        });
    }

    @Override
    public int getItemCount() {
        return componentList.size();
    }

    static class ComponentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, priceText;
        ImageView imageView;
        Button selectBtn, saveBtn;

        public ComponentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            priceText = itemView.findViewById(R.id.priceText);
            imageView = itemView.findViewById(R.id.componentImage);
            selectBtn = itemView.findViewById(R.id.selectButton);
            saveBtn = itemView.findViewById(R.id.saveButton);
        }
    }
}
