package com.app.pccooker;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.PaymentResultListener;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();

        // Set up cart update listener
        CartManager.getInstance(this).setCartUpdateListener(new CartManager.CartUpdateListener() {
            @Override
            public void onCartUpdated() {
                updateCartBadge();
            }
        });

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
            } else if (id == R.id.navigation_help) {
                // Load the HelpFragment when help is clicked
                selectedFragment = new HelpFragment();
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

    // Payment Result Listener methods
    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        // Find the current PaymentFragment and delegate the callback
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof PaymentFragment) {
            ((PaymentFragment) currentFragment).onPaymentSuccess(razorpayPaymentId);
        }
    }

    @Override
    public void onPaymentError(int errorCode, String response) {
        // Find the current PaymentFragment and delegate the callback
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof PaymentFragment) {
            ((PaymentFragment) currentFragment).onPaymentError(errorCode, response);
        }
    }
}
