package com.app.pccooker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPanelActivity extends AppCompatActivity {

    private EditText componentNameInput, componentPriceInput, componentDescriptionInput;
    private Spinner categorySpinner;
    private Button addComponentButton, updatePriceButton;
    private RecyclerView componentsRecyclerView;
    private TextView totalComponentsText;
    
    private FirebaseFirestore db;
    private List<ComponentModel> allComponents = new ArrayList<>();
    private AdminComponentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadAllComponents();
        setupClickListeners();
    }

    private void initViews() {
        componentNameInput = findViewById(R.id.componentNameInput);
        componentPriceInput = findViewById(R.id.componentPriceInput);
        componentDescriptionInput = findViewById(R.id.componentDescriptionInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        addComponentButton = findViewById(R.id.addComponentButton);
        updatePriceButton = findViewById(R.id.updatePriceButton);
        componentsRecyclerView = findViewById(R.id.componentsRecyclerView);
        totalComponentsText = findViewById(R.id.totalComponentsText);

        // Setup category spinner
        String[] categories = {"PROCESSOR", "GRAPHIC CARD", "RAM", "MOTHERBOARD", "STORAGE", "POWER SUPPLY", "CABINET"};
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        componentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminComponentAdapter(this, allComponents, new AdminComponentAdapter.OnComponentActionListener() {
            @Override
            public void onEditPrice(ComponentModel component) {
                showPriceEditDialog(component);
            }

            @Override
            public void onToggleStock(ComponentModel component) {
                toggleComponentStock(component);
            }

            @Override
            public void onDeleteComponent(ComponentModel component) {
                deleteComponent(component);
            }
        });
        componentsRecyclerView.setAdapter(adapter);
    }

    private void loadAllComponents() {
        String[] categories = {"PROCESSOR", "GRAPHIC CARD", "RAM", "MOTHERBOARD", "STORAGE", "POWER SUPPLY", "CABINET"};
        
        allComponents.clear();
        int[] loadedCategories = {0};
        int totalCategories = categories.length;

        for (String category : categories) {
            db.collection("pc_components")
                .document(category)
                .collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            ComponentModel component = doc.toObject(ComponentModel.class);
                            if (component != null) {
                                component.setId(doc.getId());
                                allComponents.add(component);
                            }
                        } catch (Exception e) {
                            // Skip this component if deserialization fails
                            System.err.println("Failed to deserialize component in admin panel: " + e.getMessage());
                        }
                    }
                    
                    loadedCategories[0]++;
                    if (loadedCategories[0] == totalCategories) {
                        updateUI();
                    }
                })
                .addOnFailureListener(e -> {
                    loadedCategories[0]++;
                    if (loadedCategories[0] == totalCategories) {
                        updateUI();
                    }
                });
        }
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();
        totalComponentsText.setText("Total Components: " + allComponents.size());
    }

    private void setupClickListeners() {
        addComponentButton.setOnClickListener(v -> addNewComponent());
        updatePriceButton.setOnClickListener(v -> updateAllPrices());
    }

    private void addNewComponent() {
        String name = componentNameInput.getText().toString().trim();
        String priceStr = componentPriceInput.getText().toString().trim();
        String description = componentDescriptionInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            
            ComponentModel newComponent = new ComponentModel();
            newComponent.setName(name);
            newComponent.setPrice(price);
            newComponent.setDescription(description);
            newComponent.setCategory(category);
            newComponent.setBrand("Brand"); // Default
            newComponent.setModel("Model"); // Default
            newComponent.setRating(4.0); // Default
            newComponent.setRatingCount(0);
            newComponent.setInStock(true);
            newComponent.setImageUrl("https://via.placeholder.com/300x300?text=" + name.replace(" ", "+"));
            newComponent.setProductUrl("https://amazon.in/dp/example");

            // Add to Firebase
            db.collection("pc_components")
                .document(category)
                .collection("items")
                .add(newComponent)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Component added successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadAllComponents();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add component", Toast.LENGTH_SHORT).show();
                });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        componentNameInput.setText("");
        componentPriceInput.setText("");
        componentDescriptionInput.setText("");
    }

    private void showPriceEditDialog(ComponentModel component) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Price: " + component.getName());

        final EditText priceInput = new EditText(this);
        priceInput.setText(String.valueOf((int) component.getPrice()));
        priceInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(priceInput);

        builder.setPositiveButton("Update", (dialog, which) -> {
            try {
                double newPrice = Double.parseDouble(priceInput.getText().toString());
                updateComponentPrice(component, newPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateComponentPrice(ComponentModel component, double newPrice) {
        db.collection("pc_components")
            .document(component.getCategory())
            .collection("items")
            .document(component.getId())
            .update("price", newPrice)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Price updated successfully", Toast.LENGTH_SHORT).show();
                loadAllComponents();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to update price", Toast.LENGTH_SHORT).show();
            });
    }

    private void toggleComponentStock(ComponentModel component) {
        boolean newStockStatus = !component.isInStock();
        
        db.collection("pc_components")
            .document(component.getCategory())
            .collection("items")
            .document(component.getId())
            .update("inStock", newStockStatus)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Stock status updated", Toast.LENGTH_SHORT).show();
                loadAllComponents();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to update stock status", Toast.LENGTH_SHORT).show();
            });
    }

    private void deleteComponent(ComponentModel component) {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Delete Component")
            .setMessage("Are you sure you want to delete " + component.getName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                db.collection("pc_components")
                    .document(component.getCategory())
                    .collection("items")
                    .document(component.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Component deleted", Toast.LENGTH_SHORT).show();
                        loadAllComponents();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete component", Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void updateAllPrices() {
        // This would typically connect to external APIs to get current prices
        Toast.makeText(this, "Price update feature coming soon!", Toast.LENGTH_SHORT).show();
    }
} 