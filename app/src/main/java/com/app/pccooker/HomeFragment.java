package com.app.pccooker;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class HomeFragment extends Fragment {

    private EditText searchBar;
    private ViewPager2 viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchBar = view.findViewById(R.id.searchBar);
        viewPager = view.findViewById(R.id.imageSlider);

        setupSearchBar();
        setupImageSlider();
        setupButtons(view);

        return view;
    }

    private void setupSearchBar() {
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch(searchBar.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;

        Toast.makeText(getContext(), "No results found for: " + query, Toast.LENGTH_SHORT).show();
    }

    private void setupImageSlider() {
        int[] images = {
                R.drawable.pc_image1,
                R.drawable.pc_image2,
                R.drawable.pc_image3,
                R.drawable.pc_image4,
                R.drawable.pc_image5
        };

        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(adapter);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = adapter.getItemCount();
                int nextItem = (currentItem + 1) % totalItems;
                viewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void setupButtons(View view) {
        Button buildPcButton = view.findViewById(R.id.buildPcButton);
        Button serviceButton = view.findViewById(R.id.serviceButton);
        Button sellPcButton = view.findViewById(R.id.sellPcButton);

        buildPcButton.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new BuildPCFragment());
        });

        serviceButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Service screen coming soon", Toast.LENGTH_SHORT).show();
        });

        sellPcButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sell PC screen coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}
