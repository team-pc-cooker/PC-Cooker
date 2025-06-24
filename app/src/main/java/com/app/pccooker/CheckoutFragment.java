package com.app.pccooker;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.app.pccooker.models.PCComponent;

import java.util.List;

public class CheckoutFragment extends Fragment {

    private RecyclerView summaryRecyclerView;
    private TextView summaryTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        summaryRecyclerView = view.findViewById(R.id.summaryRecyclerView);
        summaryTotal = view.findViewById(R.id.orderTotalText);


        summaryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSummary();

        return view;
    }

    private void loadSummary() {
        List<PCComponent> cartItems = CartManager.getInstance().getCartItems();
        int total = CartManager.getInstance().getCartTotal();

        summaryTotal.setText("Total: ₹" + total);

        CartItemAdapter adapter = new CartItemAdapter(requireContext(), cartItems, null, true); // read-only mode

        summaryRecyclerView.setAdapter(adapter);
    }
}
