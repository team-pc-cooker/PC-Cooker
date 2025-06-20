package com.app.pccooker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.PCComponent;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ComponentListActivity extends AppCompatActivity {
    private RecyclerView componentsRecyclerView;
    private TextView categoryTitleText;
    private DatabaseReference databaseRef;
    private ComponentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);

        String category = getIntent().getStringExtra("category");
        
        categoryTitleText = findViewById(R.id.categoryTitle);
        categoryTitleText.setText(category);

        componentsRecyclerView = findViewById(R.id.componentsRecyclerView);
        componentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseRef = FirebaseDatabase.getInstance().getReference("components");
        adapter = new ComponentAdapter(new ArrayList<>());
        componentsRecyclerView.setAdapter(adapter);

        loadComponents(category);
    }

    private void loadComponents(String category) {
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
                        adapter.updateComponents(components);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }
}