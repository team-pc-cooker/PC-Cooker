package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.adapters.SavedBuildsAdapter;
import com.app.pccooker.models.SavedBuild;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildManagerActivity extends AppCompatActivity {
    
    private static final String TAG = "BuildManager";
    
    private RecyclerView savedBuildsRecyclerView;
    private SavedBuildsAdapter adapter;
    private ProgressBar loadingProgress;
    private TextView noBuildssText;
    private Button createNewBuildButton;
    
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<SavedBuild> savedBuilds;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_manager);
        
        initializeViews();
        setupFirebase();
        setupRecyclerView();
        loadSavedBuilds();
    }
    
    private void initializeViews() {
        savedBuildsRecyclerView = findViewById(R.id.savedBuildsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        noBuildssText = findViewById(R.id.noBuildssText);
        createNewBuildButton = findViewById(R.id.createNewBuildButton);
        
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        // Create new build button
        createNewBuildButton.setOnClickListener(v -> createNewBuild());
    }
    
    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        savedBuilds = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        adapter = new SavedBuildsAdapter(this, savedBuilds, new SavedBuildsAdapter.OnBuildActionListener() {
            @Override
            public void onLoadBuild(SavedBuild build) {
                loadBuild(build);
            }
            
            @Override
            public void onEditBuild(SavedBuild build) {
                editBuild(build);
            }
            
            @Override
            public void onDeleteBuild(SavedBuild build) {
                deleteBuild(build);
            }
            
            @Override
            public void onDuplicateBuild(SavedBuild build) {
                duplicateBuild(build);
            }
            
            @Override
            public void onShareBuild(SavedBuild build) {
                shareBuild(build);
            }
        });
        
        savedBuildsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedBuildsRecyclerView.setAdapter(adapter);
    }
    
    private void loadSavedBuilds() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to view saved builds", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        showLoading(true);
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users")
                .document(userId)
                .collection("savedBuilds")
                .orderBy("lastModified", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedBuilds.clear();
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            SavedBuild build = document.toObject(SavedBuild.class);
                            build.setId(document.getId());
                            savedBuilds.add(build);
                            Log.d(TAG, "Loaded saved build: " + build.getBuildName());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing saved build: " + e.getMessage());
                        }
                    }
                    
                    Log.d(TAG, "Loaded " + savedBuilds.size() + " saved builds");
                    adapter.notifyDataSetChanged();
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading saved builds: " + e.getMessage());
                    Toast.makeText(this, "Failed to load saved builds", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                });
    }
    
    private void createNewBuild() {
        // Navigate to BuildPC activity
        Intent intent = new Intent(this, BuildPCActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
    
    private void loadBuild(SavedBuild build) {
        Log.d(TAG, "Loading build: " + build.getBuildName());
        
        // Create intent with build data
        Intent intent = new Intent(this, BuildPCActivity.class);
        intent.putExtra("loadBuild", true);
        intent.putExtra("buildId", build.getId());
        intent.putExtra("buildName", build.getBuildName());
        intent.putExtra("totalCost", build.getTotalCost());
        // Note: Components will be loaded from Firebase using buildId
        
        startActivity(intent);
    }
    
    private void editBuild(SavedBuild build) {
        Log.d(TAG, "Editing build: " + build.getBuildName());
        
        // Show edit dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Build");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_build, null);
        EditText nameEdit = dialogView.findViewById(R.id.buildNameEdit);
        EditText descriptionEdit = dialogView.findViewById(R.id.buildDescriptionEdit);
        
        nameEdit.setText(build.getBuildName());
        descriptionEdit.setText(build.getDescription());
        
        builder.setView(dialogView);
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameEdit.getText().toString().trim();
            String newDescription = descriptionEdit.getText().toString().trim();
            
            if (newName.isEmpty()) {
                Toast.makeText(this, "Build name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            updateBuildInfo(build, newName, newDescription);
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void updateBuildInfo(SavedBuild build, String newName, String newDescription) {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("buildName", newName);
        updates.put("description", newDescription);
        updates.put("lastModified", new Date());
        
        db.collection("users")
                .document(userId)
                .collection("savedBuilds")
                .document(build.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Build updated successfully");
                    Toast.makeText(this, "Build updated", Toast.LENGTH_SHORT).show();
                    
                    // Update local data
                    build.setBuildName(newName);
                    build.setDescription(newDescription);
                    build.setLastModified(new Date());
                    
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating build: " + e.getMessage());
                    Toast.makeText(this, "Failed to update build", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void deleteBuild(SavedBuild build) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Build");
        builder.setMessage("Are you sure you want to delete \"" + build.getBuildName() + "\"?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (auth.getCurrentUser() == null) return;
            
            String userId = auth.getCurrentUser().getUid();
            
            db.collection("users")
                    .document(userId)
                    .collection("savedBuilds")
                    .document(build.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Build deleted successfully");
                        Toast.makeText(this, "Build deleted", Toast.LENGTH_SHORT).show();
                        
                        // Remove from local list
                        savedBuilds.remove(build);
                        adapter.notifyDataSetChanged();
                        
                        // Show empty state if no builds left
                        if (savedBuilds.isEmpty()) {
                            showEmptyState();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error deleting build: " + e.getMessage());
                        Toast.makeText(this, "Failed to delete build", Toast.LENGTH_SHORT).show();
                    });
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void duplicateBuild(SavedBuild build) {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        // Create a copy of the build
        SavedBuild duplicatedBuild = new SavedBuild();
        duplicatedBuild.setBuildName(build.getBuildName() + " (Copy)");
        duplicatedBuild.setDescription(build.getDescription());
        duplicatedBuild.setComponents(build.getComponents());
        duplicatedBuild.setTotalCost(build.getTotalCost());
        duplicatedBuild.setCreatedDate(new Date());
        duplicatedBuild.setLastModified(new Date());
        duplicatedBuild.setBuildType(build.getBuildType());
        duplicatedBuild.setPerformanceScore(build.getPerformanceScore());
        
        db.collection("users")
                .document(userId)
                .collection("savedBuilds")
                .add(duplicatedBuild)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Build duplicated successfully");
                    Toast.makeText(this, "Build duplicated", Toast.LENGTH_SHORT).show();
                    
                    // Add to local list
                    duplicatedBuild.setId(documentReference.getId());
                    savedBuilds.add(0, duplicatedBuild); // Add at top
                    adapter.notifyItemInserted(0);
                    savedBuildsRecyclerView.scrollToPosition(0);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error duplicating build: " + e.getMessage());
                    Toast.makeText(this, "Failed to duplicate build", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void shareBuild(SavedBuild build) {
        // Create shareable text
        StringBuilder shareText = new StringBuilder();
        shareText.append("🖥️ PC Build: ").append(build.getBuildName()).append("\n");
        shareText.append("💰 Total Cost: ₹").append(String.format("%,.0f", build.getTotalCost())).append("\n\n");
        
        shareText.append("Components:\n");
        Map<String, Object> components = build.getComponents();
        if (components != null) {
            for (Map.Entry<String, Object> entry : components.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> component = (Map<String, Object>) entry.getValue();
                    String name = (String) component.get("name");
                    Double price = (Double) component.get("price");
                    
                    if (name != null && price != null) {
                        shareText.append("• ").append(entry.getKey()).append(": ")
                                .append(name).append(" - ₹").append(String.format("%,.0f", price)).append("\n");
                    }
                }
            }
        }
        
        shareText.append("\nBuilt with PC Cooker App 🚀");
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PC Build: " + build.getBuildName());
        
        startActivity(Intent.createChooser(shareIntent, "Share Build"));
    }
    
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        savedBuildsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        
        if (!show && savedBuilds.isEmpty()) {
            showEmptyState();
        } else {
            noBuildssText.setVisibility(View.GONE);
            createNewBuildButton.setVisibility(View.GONE);
        }
    }
    
    private void showEmptyState() {
        noBuildssText.setVisibility(View.VISIBLE);
        createNewBuildButton.setVisibility(View.VISIBLE);
        savedBuildsRecyclerView.setVisibility(View.GONE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh builds when returning to this activity
        loadSavedBuilds();
    }
}