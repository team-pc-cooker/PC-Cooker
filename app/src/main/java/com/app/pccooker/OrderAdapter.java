package com.app.pccooker;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.OrderModel;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<OrderModel> orderList;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderModel order);
    }

    public OrderAdapter(Context context, List<OrderModel> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        
        holder.orderNumber.setText("Order #" + order.getOrderNumber());
        holder.orderDate.setText(order.getFormattedDate());
        holder.orderAmount.setText(order.getFormattedAmount());
        holder.orderStatus.setText(order.getStatus());
        holder.componentCount.setText(order.getComponentCount() + " components");
        
        // Set status color
        holder.orderStatus.setTextColor(android.graphics.Color.parseColor(order.getStatusColor()));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderDate, orderAmount, orderStatus, componentCount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            componentCount = itemView.findViewById(R.id.componentCount);
        }
    }
}
