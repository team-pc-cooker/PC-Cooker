package com.app.pccooker;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(AddressModel address);
    }

    private final Context context;
    private final List<AddressModel> addressList;
    private final OnDeleteClickListener deleteClickListener;

    public AddressAdapter(Context context, List<AddressModel> addressList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.addressList = addressList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);
        holder.detailsText.setText(address.getAddress());
        holder.mobileText.setText("📞 " + address.getMobile());
        holder.labelText.setText(address.getLabel());

        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(address);
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditAddressActivity.class);
            intent.putExtra("addressData", address);  // address is Parcelable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView detailsText, mobileText, labelText;
        ImageButton deleteBtn, editBtn;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            detailsText = itemView.findViewById(R.id.detailsText);
            mobileText = itemView.findViewById(R.id.mobileText);
            labelText = itemView.findViewById(R.id.labelText);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }
}
