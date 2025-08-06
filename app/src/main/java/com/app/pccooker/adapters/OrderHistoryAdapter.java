package com.app.pccooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.R;
import com.app.pccooker.models.OrderModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public OrderHistoryAdapter(Context context, List<OrderModel> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderIdText;
        private TextView orderDateText;
        private TextView orderAmountText;
        private TextView orderStatusText;
        private TextView itemCountText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderAmountText = itemView.findViewById(R.id.orderAmountText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            itemCountText = itemView.findViewById(R.id.itemCountText);
        }

        public void bind(OrderModel order) {
            orderIdText.setText("Order #" + order.getOrderId().substring(0, 8).toUpperCase());
            
            if (order.getOrderDate() != null) {
                orderDateText.setText(dateFormat.format(order.getOrderDate()));
            } else {
                orderDateText.setText("Date not available");
            }
            
            orderAmountText.setText("₹" + String.format("%.2f", order.getTotalAmount()));
            
            // Set status with color
            String status = order.getStatus();
            orderStatusText.setText(status);
            
            // Set status color based on status
            switch (status.toLowerCase()) {
                case "success":
                case "completed":
                    orderStatusText.setTextColor(context.getResources().getColor(R.color.ai_success));
                    break;
                case "pending":
                    orderStatusText.setTextColor(context.getResources().getColor(R.color.ai_warning));
                    break;
                case "failed":
                case "cancelled":
                    orderStatusText.setTextColor(context.getResources().getColor(R.color.ai_error));
                    break;
                default:
                    orderStatusText.setTextColor(context.getResources().getColor(R.color.ai_secondary_text));
                    break;
            }
            
            // Set item count
            int itemCount = order.getItems() != null ? order.getItems().size() : 0;
            itemCountText.setText(itemCount + " item" + (itemCount != 1 ? "s" : ""));
        }
    }
} 