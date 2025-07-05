package com.app.pccooker;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyOrdersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate layout (create this layout file if not already present)
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        // Optional placeholder text
        TextView placeholder = view.findViewById(R.id.orderPlaceholder);
        placeholder.setText("Your orders will appear here.");

        return view;
    }
}
