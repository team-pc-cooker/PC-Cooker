package com.app.pccooker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OtherPartsAdapter extends RecyclerView.Adapter<OtherPartsAdapter.ViewHolder> {

    private List<OtherPartsActivity.OtherPart> partsList;
    private OnPartClickListener listener;

    public interface OnPartClickListener {
        void onPartClick(OtherPartsActivity.OtherPart part);
    }

    public OtherPartsAdapter(List<OtherPartsActivity.OtherPart> partsList, OnPartClickListener listener) {
        this.partsList = partsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_other_part, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OtherPartsActivity.OtherPart part = partsList.get(position);
        
        holder.iconImage.setImageResource(part.getIconResId());
        holder.nameText.setText(part.getName());
        holder.descriptionText.setText(part.getDescription());
        holder.priceText.setText(part.getPrice());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPartClick(part);
            }
        });
    }

    @Override
    public int getItemCount() {
        return partsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nameText;
        TextView descriptionText;
        TextView priceText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImage);
            nameText = itemView.findViewById(R.id.nameText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            priceText = itemView.findViewById(R.id.priceText);
        }
    }
} 