package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class BuildPCFragment extends Fragment {
    private RecyclerView categoryRecyclerView;
    private final List<String> categories = Arrays.asList(
            "PROCESSOR", "GRAPHIC CARD", "RAM", "MOTHERBOARD", "STORAGE", "POWER SUPPLY", "CABINET", "COOLING", "MONITOR", "KEYBOARD", "MOUSE"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_build_pc, container, false);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            ComponentSelectionFragment fragment = ComponentSelectionFragment.newInstance(category, 0);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        categoryRecyclerView.setAdapter(adapter);
        return view;
    }
}
