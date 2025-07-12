package com.app.pccooker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;

import java.util.ArrayList;
import java.util.List;

public class ComponentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComponentAdapter adapter;
    private List<ComponentModel> componentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        componentList = new ArrayList<>();

        adapter = new ComponentAdapter(this, componentList, new ComponentAdapter.OnComponentClickListener() {
            @Override
            public void onComponentClick(ComponentModel component) {
                // Add selected component to cart
                CartManager.getInstance(ComponentListActivity.this).addToCart(component);
            }
        });

        recyclerView.setAdapter(adapter);

        loadSampleComponents();
    }

    private void loadSampleComponents() {
        // Sample data (replace with real data or Firestore fetch)
        ComponentModel component1 = new ComponentModel();
        component1.setId("1");
        component1.setName("Ryzen 5 5600X");
        component1.setDescription("Fast 6-core processor");
        component1.setImageUrl("https://example.com/image.jpg");
        component1.setCategory("PROCESSOR");
        component1.setPrice(20000);
        componentList.add(component1);

        ComponentModel component2 = new ComponentModel();
        component2.setId("2");
        component2.setName("Intel i5 12400F");
        component2.setDescription("Efficient and powerful");
        component2.setImageUrl("https://example.com/image2.jpg");
        component2.setCategory("PROCESSOR");
        component2.setPrice(18000);
        componentList.add(component2);

        adapter.notifyDataSetChanged();
    }
}
