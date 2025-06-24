package com.app.pccooker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.PCComponent;

import java.util.ArrayList;
import java.util.List;

public class ComponentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComponentAdapter adapter;
    private List<PCComponent> componentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        componentList = new ArrayList<>();

        adapter = new ComponentAdapter(componentList, new ComponentAdapter.OnComponentActionListener() {
            @Override
            public void onSelectClicked(PCComponent component) {
                // Add selected component to cart
                CartManager.getInstance().addToCart(component);
            }

            @Override
            public void onSaveForLaterClicked(PCComponent component) {
                // Optional: Handle "save for later" if needed
                // CartManager.getInstance().saveForLater(component);
            }
        });

        recyclerView.setAdapter(adapter);

        loadSampleComponents();
    }

    private void loadSampleComponents() {
        // Sample data (replace with real data or Firestore fetch)
        componentList.add(new PCComponent("1", "Ryzen 5 5600X", "Fast 6-core processor", "https://example.com/image.jpg", "PROCESSOR", 20000, 1));
        componentList.add(new PCComponent("2", "Intel i5 12400F", "Efficient and powerful", "https://example.com/image2.jpg", "PROCESSOR", 18000, 1));

        adapter.notifyDataSetChanged();
    }
}
