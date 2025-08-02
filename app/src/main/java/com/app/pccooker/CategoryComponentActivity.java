package com.app.pccooker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class CategoryComponentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_component);

        String category = getIntent().getStringExtra("categoryName");
        if (category != null) {
            // Convert category name to proper format
            String formattedCategory = category.toUpperCase().replace("_", " ");
            
            // Create and add the fragment
            CategoryComponentFragment fragment = CategoryComponentFragment.newInstance(formattedCategory);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
} 