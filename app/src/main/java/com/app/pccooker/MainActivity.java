package com.app.pccooker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private EditText searchBar;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        setupSearchBar();
        setupImageSlider();
        setupBottomNavigation();
        setupButtons();
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Already on home
                return true;
            } else if (itemId == R.id.navigation_cart) {
                // Handle cart navigation
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Handle profile navigation
                return true;
            } else if (itemId == R.id.navigation_help) {
                // Handle help navigation
                return true;
            }
            return false;
        });
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
        if (query.isEmpty()) {
            return;
        }
        
        // TODO: Replace this with actual database search when components are added
        Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
    }

    private void setupImageSlider() {
        ViewPager2 viewPager = findViewById(R.id.imageSlider);
        
        // Add your images to the drawable folder and reference them here
        int[] images = new int[]{
            R.drawable.pc_image1,
            R.drawable.pc_image2,
            R.drawable.pc_image3,
            R.drawable.pc_image4,
            R.drawable.pc_image5
        };

        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(adapter);

        // Auto scroll (optional)
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = adapter.getItemCount();
                int nextItem = (currentItem + 1) % totalItems;
                viewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // Change image every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void setupButtons() {
        Button buildPcButton = findViewById(R.id.buildPcButton);
        if (buildPcButton == null) {
            Toast.makeText(this, "Button not found!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        buildPcButton.setOnClickListener(v -> {
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();
            try {
                Intent intent = new Intent(MainActivity.this, BuildPCActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }
}