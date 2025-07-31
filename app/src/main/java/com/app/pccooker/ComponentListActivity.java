package com.app.pccooker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        loadComponentsFromFirebase();
    }

    private void loadComponentsFromFirebase() {
        // Fetch components from Firebase
        FirebaseFirestore.getInstance()
                .collection("pc_components")
                .document("PROCESSOR") // Default to PROCESSOR for now
                .collection("items")
                .get()
                .addOnSuccessListener(itemsQuery -> {
                    componentList.clear();
                    
                    for (QueryDocumentSnapshot doc : itemsQuery) {
                        try {
                            ComponentModel component = doc.toObject(ComponentModel.class);
                            if (component != null) {
                                componentList.add(component);
                            }
                        } catch (Exception e) {
                            Log.e("ComponentList", "Error parsing component", e);
                        }
                    }
                    
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("ComponentList", "Failed to fetch components", e);
                    Toast.makeText(this, "Failed to load components", Toast.LENGTH_SHORT).show();
                });
    }
}
