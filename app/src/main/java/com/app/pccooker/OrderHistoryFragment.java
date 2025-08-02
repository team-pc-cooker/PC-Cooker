package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.pccooker.OrderModel;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView orderRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyStateText;
    private OrderAdapter adapter;
    private List<OrderModel> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        setupRecyclerView();
        setupSwipeRefresh();
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(requireContext(), orders, order -> {
            // Handle order click - show order details
            showOrderDetails(order);
        });
        orderRecyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadOrders();
        });
    }

    private void loadOrders() {
        OrderManager.getInstance(requireContext()).getUserOrders(new OrderManager.OnOrdersLoadedListener() {
            @Override
            public void onOrdersLoaded(List<OrderModel> orderList) {
                orders.clear();
                orders.addAll(orderList);
                
                if (orders.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    orderRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateText.setVisibility(View.GONE);
                    orderRecyclerView.setVisibility(View.VISIBLE);
                }
                
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showOrderDetails(OrderModel order) {
        // TODO: Navigate to order details fragment
        Toast.makeText(getContext(), "Order details for " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders(); // Refresh orders when returning to this fragment
    }
} 