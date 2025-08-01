package com.app.pccooker;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();

        // Firebase data is now populated via scripts

        if (savedInstanceState == null) {
            // Check if we should show cart (coming from AI summary)
            if (getIntent().getBooleanExtra("show_cart", false)) {
                loadFragment(new CartFragment());
                bottomNav.setSelectedItemId(R.id.navigation_cart);
            } else {
                loadFragment(new HomeFragment());
            }
        }
    }
    public void setCartBadge(int count) {
        if (bottomNav != null) {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.navigation_cart);
            if (count > 0) {
                badge.setVisible(true);
                badge.setNumber(count);
            } else {
                badge.clearNumber();
                badge.setVisible(false);
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.navigation_cart) {
                selectedFragment = new CartFragment();
            } else if (id == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        updateCartBadge();
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void switchToTab(int menuItemId) {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(menuItemId);
        }
    }

    public void updateCartBadge() {
        if (bottomNav != null) {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.navigation_cart);
            int count = CartManager.getInstance(this).getCartItemCount();
            badge.setVisible(count > 0);
            badge.setNumber(count);
        }
    }

    public void showOrderConfirmation(String orderNumber) {
        // Navigate to order confirmation or show success message
        // For now, just show a toast and navigate to profile
        android.widget.Toast.makeText(this, "Order #" + orderNumber + " placed successfully!", 
                                     android.widget.Toast.LENGTH_LONG).show();
        
        // Navigate to profile to show order history
        switchToTab(R.id.navigation_profile);
    }

}
