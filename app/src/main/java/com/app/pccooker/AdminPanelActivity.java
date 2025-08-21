package com.app.pccooker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.adapters.AdminRequestAdapter;
import com.app.pccooker.models.ServiceRequest;
import com.app.pccooker.models.SellRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noDataText;
    private Button refreshButton;

    private FirebaseFirestore db;
    private AdminRequestAdapter adapter;
    private List<Object> allRequests;
    private String currentTab = "service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        initializeFirebase();
        initializeViews();
        setupToolbar();
        setupTabLayout();
        setupRecyclerView();
        loadRequests();
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noDataText = findViewById(R.id.noDataText);
        refreshButton = findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(v -> loadRequests());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Panel");
        }
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Service Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Sell Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Help Queries"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentTab = "service";
                        break;
                    case 1:
                        currentTab = "sell";
                        break;
                    case 2:
                        currentTab = "help";
                        break;
                }
                filterRequestsByTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        allRequests = new ArrayList<>();
        adapter = new AdminRequestAdapter(allRequests, this::showRequestDetails, this::updateRequestStatus);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        noDataText.setVisibility(View.GONE);
        allRequests.clear();

        // Load Service Requests
        db.collection("serviceRequests")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ServiceRequest request = document.toObject(ServiceRequest.class);
                    if (request != null) {
                        request.setRequestId(document.getId());
                        allRequests.add(request);
                    }
                }

                // Load Sell Requests
                db.collection("sellRequests")
                    .get()
                    .addOnSuccessListener(sellSnapshots -> {
                        for (QueryDocumentSnapshot document : sellSnapshots) {
                            SellRequest request = document.toObject(SellRequest.class);
                            if (request != null) {
                                request.setRequestId(document.getId());
                                allRequests.add(request);
                            }
                        }

                        // Load Help Queries (from HelpFragment)
                        loadHelpQueries();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load sell requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadHelpQueries();
                    });
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load service requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                loadHelpQueries();
            });
    }

    private void loadHelpQueries() {
        // For now, we'll create mock help queries
        // In a real implementation, you'd load these from Firestore
        progressBar.setVisibility(View.GONE);
        filterRequestsByTab();
    }

    private void filterRequestsByTab() {
        List<Object> filteredRequests = new ArrayList<>();
        
        for (Object request : allRequests) {
            if (currentTab.equals("service") && request instanceof ServiceRequest) {
                filteredRequests.add(request);
            } else if (currentTab.equals("sell") && request instanceof SellRequest) {
                filteredRequests.add(request);
            }
            // Help queries would be handled here
        }

        if (filteredRequests.isEmpty()) {
            noDataText.setVisibility(View.VISIBLE);
            noDataText.setText("No " + getTabTitle() + " found");
        } else {
            noDataText.setVisibility(View.GONE);
        }

        adapter.updateRequests(filteredRequests);
    }

    private String getTabTitle() {
        switch (currentTab) {
            case "service": return "Service Requests";
            case "sell": return "Sell Requests";
            case "help": return "Help Queries";
            default: return "Requests";
        }
    }

    private void showRequestDetails(Object request) {
        if (request instanceof ServiceRequest) {
            showServiceRequestDetails((ServiceRequest) request);
        } else if (request instanceof SellRequest) {
            showSellRequestDetails((SellRequest) request);
        }
    }

    private void showServiceRequestDetails(ServiceRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Service Request Details");

        StringBuilder details = new StringBuilder();
        details.append("Request ID: ").append(request.getRequestId()).append("\n\n");
        details.append("User ID: ").append(request.getUserId()).append("\n");
        details.append("PC Model: ").append(request.getPcModel()).append("\n");
        details.append("Year: ").append(request.getYear()).append("\n");
        details.append("Issue Type: ").append(request.getIssueType()).append("\n");
        details.append("Priority: ").append(request.getPriority()).append("\n");
        details.append("Contact: ").append(request.getContact()).append("\n");
        details.append("Status: ").append(request.getStatus()).append("\n");
        details.append("Submitted: ").append(request.getFormattedTimestamp()).append("\n\n");
        details.append("Description:\n").append(request.getDescription());

        if (request.hasMedia()) {
            details.append("\n\nMedia Files: ").append(request.getMediaCount());
        }

        builder.setMessage(details.toString());
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void showSellRequestDetails(SellRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sell Request Details");

        StringBuilder details = new StringBuilder();
        details.append("Request ID: ").append(request.getRequestId()).append("\n\n");
        details.append("User ID: ").append(request.getUserId()).append("\n");
        details.append("PC Type: ").append(request.getPcType()).append("\n");
        details.append("PC Model: ").append(request.getPcModel()).append("\n");
        details.append("Year: ").append(request.getYear()).append("\n");
        details.append("Condition: ").append(request.getCondition()).append("\n");
        details.append("Condition Rating: ").append(request.getConditionRating()).append("\n");
        details.append("Price: ").append(request.getFormattedPrice()).append("\n");
        details.append("Contact: ").append(request.getContact()).append("\n");
        details.append("Status: ").append(request.getStatus()).append("\n");
        details.append("Submitted: ").append(request.getFormattedTimestamp()).append("\n\n");
        details.append("Description:\n").append(request.getDescription());

        if (request.hasMedia()) {
            details.append("\n\nMedia Files: ").append(request.getMediaCount());
            details.append("\n\nClick 'View Media' to see uploaded photos/videos");
        }

        builder.setMessage(details.toString());
        
        if (request.hasMedia()) {
            builder.setNeutralButton("View Media", (dialog, which) -> {
                showMediaFiles(request);
            });
        }
        
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void showMediaFiles(SellRequest request) {
        if (request.getMediaUrls() == null || request.getMediaUrls().isEmpty()) {
            Toast.makeText(this, "No media files found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a custom dialog with media display
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Media Files - " + request.getPcModel());

        // Create a simple list of media files with clickable items
        String[] mediaItems = new String[request.getMediaUrls().size()];
        for (int i = 0; i < request.getMediaUrls().size(); i++) {
            String url = request.getMediaUrls().get(i);
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            mediaItems[i] = "📸 " + fileName;
        }

        builder.setItems(mediaItems, (dialog, which) -> {
            String mediaUrl = request.getMediaUrls().get(which);
            openMediaInBrowser(mediaUrl);
        });

        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void openMediaInBrowser(String mediaUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mediaUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open media: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRequestStatus(Object request) {
        String[] statusOptions = {"Pending", "In Progress", "Completed", "Rejected"};
        
        new AlertDialog.Builder(this)
            .setTitle("Update Status")
            .setItems(statusOptions, (dialog, which) -> {
                String newStatus = statusOptions[which];
                updateRequestStatusInFirestore(request, newStatus);
            })
            .show();
    }

    private void updateRequestStatusInFirestore(Object request, String newStatus) {
        String collectionName;
        String documentId;

        if (request instanceof ServiceRequest) {
            collectionName = "serviceRequests";
            documentId = ((ServiceRequest) request).getRequestId();
        } else if (request instanceof SellRequest) {
            collectionName = "sellRequests";
            documentId = ((SellRequest) request).getRequestId();
        } else {
            return;
        }

        db.collection(collectionName)
            .document(documentId)
            .update("status", newStatus)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                loadRequests(); // Refresh the list
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 