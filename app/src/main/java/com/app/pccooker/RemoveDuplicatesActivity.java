package com.app.pccooker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveDuplicatesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView statusText;
    private Button removeDuplicatesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_duplicates);

        db = FirebaseFirestore.getInstance();
        statusText = findViewById(R.id.statusText);
        removeDuplicatesButton = findViewById(R.id.removeDuplicatesButton);

        removeDuplicatesButton.setOnClickListener(v -> removeDuplicates());
    }

    private void removeDuplicates() {
        statusText.setText("Scanning for duplicates in all collections...");
        removeDuplicatesButton.setEnabled(false);

        // First, scan the old structure (components collection)
        scanOldStructure();
    }

    private void scanOldStructure() {
        statusText.setText("Scanning old structure (components collection)...");
        
        db.collection("components")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Map<String, List<String>> nameToIds = new HashMap<>();
                List<String> duplicatesToDelete = new ArrayList<>();

                // Group components by name
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ComponentModel component = document.toObject(ComponentModel.class);
                    if (component != null && component.getName() != null) {
                        String name = component.getName().trim().toLowerCase();
                        if (!nameToIds.containsKey(name)) {
                            nameToIds.put(name, new ArrayList<>());
                        }
                        nameToIds.get(name).add("components/" + document.getId());
                    }
                }

                // Find duplicates (keep the first one, mark others for deletion)
                for (Map.Entry<String, List<String>> entry : nameToIds.entrySet()) {
                    List<String> ids = entry.getValue();
                    if (ids.size() > 1) {
                        // Keep the first one, mark the rest for deletion
                        for (int i = 1; i < ids.size(); i++) {
                            duplicatesToDelete.add(ids.get(i));
                        }
                    }
                }

                if (duplicatesToDelete.isEmpty()) {
                    statusText.setText("No duplicates found in old structure. Scanning new structure...");
                    scanNewStructure();
                } else {
                    statusText.setText("Found " + duplicatesToDelete.size() + " duplicates in old structure. Removing...");
                    deleteDuplicates(duplicatesToDelete, () -> {
                        statusText.setText("Old structure cleaned. Scanning new structure...");
                        scanNewStructure();
                    });
                }
            })
            .addOnFailureListener(e -> {
                statusText.setText("Error scanning old structure: " + e.getMessage());
                removeDuplicatesButton.setEnabled(true);
                Toast.makeText(this, "Failed to scan old structure: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void scanNewStructure() {
        statusText.setText("Scanning new structure (pc_components)...");
        
        // Define all categories in the new structure
        String[] categories = {
            "PROCESSOR", "GRAPHIC CARD", "MOTHERBOARD", "MEMORY", 
            "STORAGE", "POWER SUPPLY", "CABINET", "CPU COOLER",
            "MONITOR", "KEYBOARD", "MOUSE", "HEADSET"
        };
        
        Map<String, List<String>> allNameToIds = new HashMap<>();
        final int[] categoriesScanned = {0};
        final int totalCategories = categories.length;
        
        for (String category : categories) {
            db.collection("pc_components")
                .document(category)
                .collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoriesScanned[0]++;
                    
                    // Group components by name
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ComponentModel component = document.toObject(ComponentModel.class);
                        if (component != null && component.getName() != null) {
                            String name = component.getName().trim().toLowerCase();
                            if (!allNameToIds.containsKey(name)) {
                                allNameToIds.put(name, new ArrayList<>());
                            }
                            allNameToIds.get(name).add("pc_components/" + category + "/items/" + document.getId());
                        }
                    }
                    
                    // Check if all categories have been scanned
                    if (categoriesScanned[0] == totalCategories) {
                        processNewStructureDuplicates(allNameToIds);
                    }
                })
                .addOnFailureListener(e -> {
                    categoriesScanned[0]++;
                    if (categoriesScanned[0] == totalCategories) {
                        processNewStructureDuplicates(allNameToIds);
                    }
                });
        }
    }

    private void processNewStructureDuplicates(Map<String, List<String>> allNameToIds) {
        List<String> duplicatesToDelete = new ArrayList<>();

        // Find duplicates (keep the first one, mark others for deletion)
        for (Map.Entry<String, List<String>> entry : allNameToIds.entrySet()) {
            List<String> ids = entry.getValue();
            if (ids.size() > 1) {
                // Keep the first one, mark the rest for deletion
                for (int i = 1; i < ids.size(); i++) {
                    duplicatesToDelete.add(ids.get(i));
                }
            }
        }

        if (duplicatesToDelete.isEmpty()) {
            statusText.setText("No duplicates found in new structure either!");
            removeDuplicatesButton.setEnabled(true);
            Toast.makeText(this, "No duplicates found in any structure", Toast.LENGTH_SHORT).show();
        } else {
            statusText.setText("Found " + duplicatesToDelete.size() + " duplicates in new structure. Removing...");
            deleteDuplicates(duplicatesToDelete, () -> {
                statusText.setText("All duplicates removed successfully!");
                removeDuplicatesButton.setEnabled(true);
                Toast.makeText(this, "All duplicates removed successfully!", Toast.LENGTH_LONG).show();
            });
        }
    }

    private void deleteDuplicates(List<String> duplicatePaths, Runnable onComplete) {
        final int[] deletedCount = {0};
        final int totalDuplicates = duplicatePaths.size();

        for (String path : duplicatePaths) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 2) {
                // Old structure: components/{id}
                db.collection(pathParts[0]).document(pathParts[1])
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        deletedCount[0]++;
                        statusText.setText("Deleted " + deletedCount[0] + " of " + totalDuplicates + " duplicates...");
                        
                        if (deletedCount[0] == totalDuplicates) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        deletedCount[0]++;
                        statusText.setText("Error deleting some duplicates: " + e.getMessage());
                        
                        if (deletedCount[0] == totalDuplicates) {
                            onComplete.run();
                        }
                    });
            } else if (pathParts.length == 4) {
                // New structure: pc_components/{category}/items/{id}
                db.collection(pathParts[0]).document(pathParts[1])
                    .collection(pathParts[2]).document(pathParts[3])
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        deletedCount[0]++;
                        statusText.setText("Deleted " + deletedCount[0] + " of " + totalDuplicates + " duplicates...");
                        
                        if (deletedCount[0] == totalDuplicates) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        deletedCount[0]++;
                        statusText.setText("Error deleting some duplicates: " + e.getMessage());
                        
                        if (deletedCount[0] == totalDuplicates) {
                            onComplete.run();
                        }
                    });
            }
        }
    }
} 