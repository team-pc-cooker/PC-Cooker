package com.app.pccooker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final Context context;
    private final List<AddressModel> addressList;
    private final OnAddressActionListener listener;
    private int selectedPosition = -1;

    public interface OnAddressActionListener {
        void onAddressSelected(AddressModel address);
        void onAddressEdit(AddressModel address);
        void onAddressDelete(AddressModel address);
    }

    public AddressAdapter(Context context, List<AddressModel> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);
        holder.bind(address, position);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout addressCard;
        private final TextView nameText, mobileText, addressText, labelText;
        private final Button editButton, deleteButton;
        private final ImageView selectedIcon;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            addressCard = itemView.findViewById(R.id.addressCard);
            nameText = itemView.findViewById(R.id.nameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            addressText = itemView.findViewById(R.id.addressText);
            labelText = itemView.findViewById(R.id.labelText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            selectedIcon = itemView.findViewById(R.id.selectedIcon);
        }

        public void bind(AddressModel address, int position) {
            nameText.setText(address.getName());
            mobileText.setText(address.getMobile());
            addressText.setText(address.getAddress() + "\n" + 
                              address.getCity() + ", " + address.getState() + " - " + 
                              address.getPincode());
            labelText.setText(address.getLabel());

            // Set selection state
            if (position == selectedPosition) {
                selectedIcon.setVisibility(View.VISIBLE);
                addressCard.setBackgroundResource(R.drawable.selected_address_bg);
            } else {
                selectedIcon.setVisibility(View.GONE);
                addressCard.setBackgroundResource(R.drawable.address_card_bg);
            }

            // Set click listeners
            addressCard.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                listener.onAddressSelected(address);
            });

            editButton.setOnClickListener(v -> listener.onAddressEdit(address));
            deleteButton.setOnClickListener(v -> listener.onAddressDelete(address));
        }
    }
}