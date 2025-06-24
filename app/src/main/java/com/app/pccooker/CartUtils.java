package com.app.pccooker;

import android.content.Context;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartUtils {

    public static void updateBadgeCount(Context context, BottomNavigationView bottomNavigationView) {
        if (bottomNavigationView == null) return;

        int count = CartManager.getInstance().getItemCount();

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
        } else {
            badge.clearNumber();
            badge.setVisible(false);
        }

        if (context instanceof MainActivity) {
            ((MainActivity) context).setCartBadge(count);
        }
    }
}
