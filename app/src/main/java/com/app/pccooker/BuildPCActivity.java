package com.app.pccooker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.app.pccooker.models.PCComponent;
import java.util.ArrayList;
import java.util.List;

public class BuildPCActivity extends AppCompatActivity {
    private RecyclerView categoriesRecyclerView;
    private DatabaseReference databaseRef;
    private CategoryAdapter categoryAdapter;
    private List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_pc);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("components");

        // Initialize RecyclerView
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
            // Handle category click by fetching components
            fetchComponentsByCategory(category);
        });
        
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void fetchComponentsByCategory(String category) {
        databaseRef.orderByChild("category").equalTo(category)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<PCComponent> components = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PCComponent component = snapshot.getValue(PCComponent.class);
                        if (component != null) {
                            components.add(component);
                        }
                    }
                    // Show components in a new activity or dialog
                    showComponents(components);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle error
                }
            });
    }

    private void showComponents(List<PCComponent> components) {
        // TODO: Create a new activity or dialog to display components
        // This will be implemented in the next step
    }
}