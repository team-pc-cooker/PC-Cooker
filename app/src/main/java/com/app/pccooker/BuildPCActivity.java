package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BuildPCActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_pc);

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize categories
        categories = new ArrayList<>();
        categories.add("PROCESSOR");
        categories.add("CPU COOLER");
        categories.add("MOTHERBOARD");
        categories.add("GPU");
        categories.add("MEMORY");
        categories.add("STORAGE");
        categories.add("POWER SUPPLY");
        categories.add("CABINET");
        categories.add("OTHER PARTS");

        // Set up adapter with click listener
        categoryAdapter = new CategoryAdapter(categories, category -> {
            // Convert category to lowercase (to match Firestore document ID)
            String categoryKey = category.toLowerCase().replace(" ", "_");

            Intent intent = new Intent(BuildPCActivity.this, CategoryComponentFragment.class);
            intent.putExtra("categoryName", categoryKey);  // e.g., "processor"
            startActivity(intent);
        });

        categoriesRecyclerView.setAdapter(categoryAdapter);
    }
}
