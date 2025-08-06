package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.ImageView;
import android.os.Handler;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.pccooker.ComponentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.util.Log;
import android.app.AlertDialog;

public class BuildPCFragment extends Fragment {

    private Map<String, ComponentModel> selectedComponents = new HashMap<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private SeekBar budgetSeekBar;
    private TextView budgetText;
    private TextView totalCostText;
    private TextView compatibilityStatusText;
    
    // Automatic build section views
    private LinearLayout autoBuildHeader;
    private LinearLayout autoBuildContent;
    private ImageView autoBuildExpandIcon;
    private boolean isAutoBuildExpanded = false;
    
    private Button amdButton;
    private Button intelButton;
    private Button am4Button;
    private Button am5Button;
    private Button ddr3Button;
    private Button hddButton;
    private Button ssdButton;
    private Button nvidiaButton;
    private Button amdGpuButton;
    
    private Button autoBuildButton;
    
    // Component selection buttons
    private Button processorButton;
    private Button coolerButton;
    private Button motherboardButton;
    private Button gpuButton;
    private Button memoryButton;
    private Button storageButton;
    private Button powerSupplyButton;
    private Button caseButton;
    private Button otherPartsButton;
    private ImageView saveBuildButton;
    private ImageView removeDuplicatesButton;
    private ImageView trendingButton;
    private ImageView buildManagerButton;

    private int currentBudget = 40000; // Default budget
    private String selectedProcessorType = "AMD";
    private String selectedSocketType = "AM4";
    private String selectedStorageType = "SSD";
    private String selectedGpuType = "NVIDIA";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_build_pc, container, false);
        
        auth = FirebaseAuth.getInstance();
        
        initializeViews(view);
        
        return view;
    }
    
    private void initializeViews(View view) {
        // Initialize views
        budgetSeekBar = view.findViewById(R.id.budgetSeekBar);
        budgetText = view.findViewById(R.id.budgetText);
        totalCostText = view.findViewById(R.id.totalCostText);
        compatibilityStatusText = view.findViewById(R.id.compatibilityStatusText);
        
        // Automatic build section views
        autoBuildHeader = view.findViewById(R.id.autoBuildHeader);
        autoBuildContent = view.findViewById(R.id.autoBuildContent);
        autoBuildExpandIcon = view.findViewById(R.id.autoBuildExpandIcon);
        
        // Component type selection buttons
        amdButton = view.findViewById(R.id.amdButton);
        intelButton = view.findViewById(R.id.intelButton);
        am4Button = view.findViewById(R.id.am4Button);
        am5Button = view.findViewById(R.id.am5Button);
        ddr3Button = view.findViewById(R.id.ddr3Button);
        hddButton = view.findViewById(R.id.hddButton);
        ssdButton = view.findViewById(R.id.ssdButton);
        nvidiaButton = view.findViewById(R.id.nvidiaButton);
        amdGpuButton = view.findViewById(R.id.amdGpuButton);
        
        // Auto build button
        autoBuildButton = view.findViewById(R.id.autoBuildButton);
        
        // Component selection buttons
        processorButton = view.findViewById(R.id.processorButton);
        coolerButton = view.findViewById(R.id.coolerButton);
        motherboardButton = view.findViewById(R.id.motherboardButton);
        gpuButton = view.findViewById(R.id.gpuButton);
        memoryButton = view.findViewById(R.id.memoryButton);
        storageButton = view.findViewById(R.id.storageButton);
        powerSupplyButton = view.findViewById(R.id.powerSupplyButton);
        caseButton = view.findViewById(R.id.caseButton);
        otherPartsButton = view.findViewById(R.id.otherPartsButton);
        
        // Save build button
        saveBuildButton = view.findViewById(R.id.saveBuildButton);
        
        // Remove duplicates button
        removeDuplicatesButton = view.findViewById(R.id.removeDuplicatesButton);
        
        // Trending button
        trendingButton = view.findViewById(R.id.trendingButton);
        
        // Build Manager button
        buildManagerButton = view.findViewById(R.id.buildManagerButton);
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        
        // Set up automatic build section
        setupAutoBuildSection();
        
        // Set up budget slider
        setupBudgetSlider();
        
        // Set up component type selection
        setupComponentTypeSelection();
        
        // Set up auto build functionality
        setupAutoBuild();
        
        // Set up component selection buttons
        setupComponentSelection();
        
        // Set up save functionality
        setupSaveFunctionality();
        
        // Set up other parts button
        setupOtherPartsButton();
        
        // Set up remove duplicates button
        setupRemoveDuplicatesButton();
        
        // Set up trending button
        setupTrendingButton();
        
        // Set up build manager button
        setupBuildManagerButton();
        
        // Load current build
        loadCurrentBuild();
    }
    
    private void setupAutoBuildSection() {
        // Make auto-build section collapsed by default
        autoBuildContent.setVisibility(View.GONE);
        isAutoBuildExpanded = false;
        autoBuildExpandIcon.setRotation(0); // Rotate arrow up
        
        autoBuildHeader.setOnClickListener(v -> {
            isAutoBuildExpanded = !isAutoBuildExpanded;
            if (isAutoBuildExpanded) {
                autoBuildContent.setVisibility(View.VISIBLE);
                autoBuildExpandIcon.setRotation(180); // Rotate arrow down
            } else {
                autoBuildContent.setVisibility(View.GONE);
                autoBuildExpandIcon.setRotation(0); // Rotate arrow up
            }
        });
    }
    
    private void setupBudgetSlider() {
        budgetSeekBar.setMax(100000); // Max 1 lakh
        budgetSeekBar.setProgress(40000); // Default 40k
        updateBudgetDisplay(40000);
        
        budgetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBudgetDisplay(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void updateBudgetDisplay(int budget) {
        currentBudget = budget;
        budgetText.setText("₹" + String.format("%,d", budget));
        updateBuildSummary();
    }
    
    private void setupComponentTypeSelection() {
        // Ensure buttons are properly initialized and clickable
        if (amdButton == null || intelButton == null || am4Button == null || am5Button == null ||
            hddButton == null || ssdButton == null || nvidiaButton == null || amdGpuButton == null) {
            Log.e("BuildPC", "One or more filter buttons are null!");
            return;
        }
        
        // Make sure buttons are clickable and focusable
        amdButton.setClickable(true);
        amdButton.setFocusable(true);
        intelButton.setClickable(true);
        intelButton.setFocusable(true);
        am4Button.setClickable(true);
        am4Button.setFocusable(true);
        am5Button.setClickable(true);
        am5Button.setFocusable(true);
        hddButton.setClickable(true);
        hddButton.setFocusable(true);
        ssdButton.setClickable(true);
        ssdButton.setFocusable(true);
        nvidiaButton.setClickable(true);
        nvidiaButton.setFocusable(true);
        amdGpuButton.setClickable(true);
        amdGpuButton.setFocusable(true);
        
        // Processor type selection - allow toggling
        amdButton.setOnClickListener(v -> {
            if ("AMD".equals(selectedProcessorType)) {
                selectedProcessorType = null; // Deselect
            } else {
                selectedProcessorType = "AMD";
            }
            Log.d("BuildPC", "AMD button clicked - selectedProcessorType: " + selectedProcessorType);
            updateButtonStates();
        });
        
        intelButton.setOnClickListener(v -> {
            if ("Intel".equals(selectedProcessorType)) {
                selectedProcessorType = null; // Deselect
            } else {
                selectedProcessorType = "Intel";
            }
            Log.d("BuildPC", "Intel button clicked - selectedProcessorType: " + selectedProcessorType);
            updateButtonStates();
        });
        
        // Socket type selection - allow toggling
        am4Button.setOnClickListener(v -> {
            if ("AM4".equals(selectedSocketType)) {
                selectedSocketType = null; // Deselect
            } else {
                selectedSocketType = "AM4";
            }
            Log.d("BuildPC", "AM4 button clicked - selectedSocketType: " + selectedSocketType);
            updateButtonStates();
        });
        
        am5Button.setOnClickListener(v -> {
            if ("AM5".equals(selectedSocketType)) {
                selectedSocketType = null; // Deselect
            } else {
                selectedSocketType = "AM5";
            }
            Log.d("BuildPC", "AM5 button clicked - selectedSocketType: " + selectedSocketType);
            updateButtonStates();
        });
        
        ddr3Button.setOnClickListener(v -> {
            if ("DDR3".equals(selectedSocketType)) {
                selectedSocketType = null; // Deselect
            } else {
                selectedSocketType = "DDR3";
            }
            Log.d("BuildPC", "DDR3 button clicked - selectedSocketType: " + selectedSocketType);
            updateButtonStates();
        });
        
        // Storage type selection - allow toggling
        hddButton.setOnClickListener(v -> {
            if ("HDD".equals(selectedStorageType)) {
                selectedStorageType = null; // Deselect
            } else {
                selectedStorageType = "HDD";
            }
            Log.d("BuildPC", "HDD button clicked - selectedStorageType: " + selectedStorageType);
            updateButtonStates();
        });
        
        ssdButton.setOnClickListener(v -> {
            if ("SSD".equals(selectedStorageType)) {
                selectedStorageType = null; // Deselect
            } else {
                selectedStorageType = "SSD";
            }
            Log.d("BuildPC", "SSD button clicked - selectedStorageType: " + selectedStorageType);
            updateButtonStates();
        });
        
        // GPU type selection - allow toggling
        nvidiaButton.setOnClickListener(v -> {
            if ("NVIDIA".equals(selectedGpuType)) {
                selectedGpuType = null; // Deselect
            } else {
                selectedGpuType = "NVIDIA";
            }
            Log.d("BuildPC", "NVIDIA button clicked - selectedGpuType: " + selectedGpuType);
            updateButtonStates();
        });
        
        amdGpuButton.setOnClickListener(v -> {
            if ("AMD".equals(selectedGpuType)) {
                selectedGpuType = null; // Deselect
            } else {
                selectedGpuType = "AMD";
            }
            Log.d("BuildPC", "AMD GPU button clicked - selectedGpuType: " + selectedGpuType);
            updateButtonStates();
        });
        
        // Debug: Check if buttons are properly initialized
        Log.d("BuildPC", "AMD Button: " + (amdButton != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "Intel Button: " + (intelButton != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "AM4 Button: " + (am4Button != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "AM5 Button: " + (am5Button != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "HDD Button: " + (hddButton != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "SSD Button: " + (ssdButton != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "NVIDIA Button: " + (nvidiaButton != null ? "Initialized" : "NULL"));
        Log.d("BuildPC", "AMD GPU Button: " + (amdGpuButton != null ? "Initialized" : "NULL"));
        
        // Update button states based on current selections
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        // Ensure buttons are not null before updating
        if (amdButton == null || intelButton == null || am4Button == null || am5Button == null ||
            hddButton == null || ssdButton == null || nvidiaButton == null || amdGpuButton == null) {
            Log.e("BuildPC", "Cannot update button states - buttons are null");
            return;
        }
        
        // Update processor type buttons
        boolean amdSelected = "AMD".equals(selectedProcessorType);
        boolean intelSelected = "Intel".equals(selectedProcessorType);
        
        amdButton.setSelected(amdSelected);
        intelButton.setSelected(intelSelected);
        
        // Update socket type buttons
        boolean am4Selected = "AM4".equals(selectedSocketType);
        boolean am5Selected = "AM5".equals(selectedSocketType);
        boolean ddr3Selected = "DDR3".equals(selectedSocketType);
        
        am4Button.setSelected(am4Selected);
        am5Button.setSelected(am5Selected);
        ddr3Button.setSelected(ddr3Selected);
        
        // Update storage type buttons
        boolean hddSelected = "HDD".equals(selectedStorageType);
        boolean ssdSelected = "SSD".equals(selectedStorageType);
        
        hddButton.setSelected(hddSelected);
        ssdButton.setSelected(ssdSelected);
        
        // Update GPU type buttons
        boolean nvidiaSelected = "NVIDIA".equals(selectedGpuType);
        boolean amdGpuSelected = "AMD".equals(selectedGpuType);
        
        nvidiaButton.setSelected(nvidiaSelected);
        amdGpuButton.setSelected(amdGpuSelected);
        
        // Log current selections
        Log.d("BuildPC", "Current selections - Processor: " + selectedProcessorType + 
              ", Socket: " + selectedSocketType + 
              ", Storage: " + selectedStorageType + 
              ", GPU: " + selectedGpuType);
        
        // Force refresh the button backgrounds and states
        amdButton.invalidate();
        intelButton.invalidate();
        am4Button.invalidate();
        am5Button.invalidate();
        ddr3Button.invalidate();
        hddButton.invalidate();
        ssdButton.invalidate();
        nvidiaButton.invalidate();
        amdGpuButton.invalidate();
        
        // Refresh button states
        amdButton.refreshDrawableState();
        intelButton.refreshDrawableState();
        am4Button.refreshDrawableState();
        am5Button.refreshDrawableState();
        ddr3Button.refreshDrawableState();
        hddButton.refreshDrawableState();
        ssdButton.refreshDrawableState();
        nvidiaButton.refreshDrawableState();
        amdGpuButton.refreshDrawableState();
        
        // Log button states for debugging
        Log.d("BuildPC", "Button states - AMD: " + amdSelected + ", Intel: " + intelSelected + 
              ", AM4: " + am4Selected + ", AM5: " + am5Selected + 
              ", DDR3: " + ddr3Selected + ", HDD: " + hddSelected + ", SSD: " + ssdSelected + 
              ", NVIDIA: " + nvidiaSelected + ", AMD GPU: " + amdGpuSelected);
        
        // Update build summary
        updateBuildSummary();
    }
    
    private void setupAutoBuild() {
        autoBuildButton.setOnClickListener(v -> {
            if (currentBudget < 10000) {
                Toast.makeText(getContext(), "Minimum budget required: ₹10,000", Toast.LENGTH_SHORT).show();
                return;
            }
            
            showAutoBuildProgress();
            autoBuildPCWithinBudget();
        });
    }
    
    private void showAutoBuildProgress() {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Building PC...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Hide after 2 seconds
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
        }, 2000);
    }
    
    private void setupComponentSelection() {
        processorButton.setOnClickListener(v -> openComponentSelection("PROCESSOR"));
        coolerButton.setOnClickListener(v -> openComponentSelection("CPU COOLER"));
        motherboardButton.setOnClickListener(v -> openComponentSelection("MOTHERBOARD"));
        gpuButton.setOnClickListener(v -> openComponentSelection("GRAPHIC CARD"));
        memoryButton.setOnClickListener(v -> openComponentSelection("RAM"));
        storageButton.setOnClickListener(v -> openComponentSelection("STORAGE"));
        powerSupplyButton.setOnClickListener(v -> openComponentSelection("POWER SUPPLY"));
        caseButton.setOnClickListener(v -> openComponentSelection("CABINET"));
        
        // Temporary button to add DDR3 components (for testing)
        // You can remove this after adding components to Firebase
        if (autoBuildButton != null) {
            autoBuildButton.setOnLongClickListener(v -> {
                addDDR3Components();
                return true;
            });
        }
    }
    
    private void openComponentSelection(String category) {
        // Navigate to component selection with category filter
        Intent intent = new Intent(getContext(), ComponentListActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("budget", currentBudget);
        intent.putExtra("processorType", selectedProcessorType);
        intent.putExtra("socketType", selectedSocketType);
        intent.putExtra("storageType", selectedStorageType);
        intent.putExtra("gpuType", selectedGpuType);
        startActivity(intent);
    }
    
    private void updateBuildSummary() {
        double totalCost = calculateTotalCost();
        boolean isCompatible = checkCompatibility();
        
        totalCostText.setText("₹" + String.format("%,.0f", totalCost));
        
        // Update status text to show current selections and compatibility warnings
        StringBuilder statusText = new StringBuilder();
        
        if (selectedProcessorType != null) {
            statusText.append("CPU: ").append(selectedProcessorType);
        }
        if (selectedSocketType != null) {
            if (statusText.length() > 0) statusText.append(", ");
            statusText.append("Socket: ").append(selectedSocketType);
        }
        if (selectedStorageType != null) {
            if (statusText.length() > 0) statusText.append(", ");
            statusText.append("Storage: ").append(selectedStorageType);
        }
        if (selectedGpuType != null) {
            if (statusText.length() > 0) statusText.append(", ");
            statusText.append("GPU: ").append(selectedGpuType);
        }
        
        // Add compatibility warnings
        String compatibilityWarning = checkCompatibilityWarnings();
        if (!compatibilityWarning.isEmpty()) {
            statusText.append("\n⚠️ ").append(compatibilityWarning);
        }
        
        compatibilityStatusText.setText(statusText.toString());
        
        if (isCompatible && compatibilityWarning.isEmpty()) {
            compatibilityStatusText.setTextColor(getResources().getColor(R.color.green));
        } else {
            compatibilityStatusText.setTextColor(getResources().getColor(R.color.red));
        }
    }
    
    private double calculateTotalCost() {
        double total = 0;
        for (ComponentModel component : selectedComponents.values()) {
            total += component.getPrice();
        }
        return total;
    }
    
    private boolean checkCompatibility() {
        if (selectedComponents.isEmpty()) return true;
        
        ComponentModel processor = selectedComponents.get("PROCESSOR");
        ComponentModel motherboard = selectedComponents.get("MOTHERBOARD");
        ComponentModel ram = selectedComponents.get("RAM");
        
        // Check processor-motherboard socket compatibility
        if (processor != null && motherboard != null) {
            String processorSocket = processor.getSpecifications().get("Socket");
            String motherboardSocket = motherboard.getSpecifications().get("Socket");
            if (!processorSocket.equals(motherboardSocket)) {
                return false;
            }
        }
        
        // Check motherboard-RAM compatibility
        if (motherboard != null && ram != null) {
            String motherboardRam = motherboard.getSpecifications().get("RAM");
            String ramType = ram.getSpecifications().get("Type");
            if (!motherboardRam.equals(ramType)) {
                return false;
            }
        }
        
        return true;
    }
    
    private String checkCompatibilityWarnings() {
        StringBuilder warnings = new StringBuilder();
        
        // Check processor and socket compatibility
        if ("Intel".equals(selectedProcessorType) && "AM4".equals(selectedSocketType)) {
            warnings.append("Intel processors don't use AM4 sockets. Consider LGA1155/LGA1150 for DDR3 or LGA1700 for DDR5.");
        }
        
        if ("Intel".equals(selectedProcessorType) && "AM5".equals(selectedSocketType)) {
            warnings.append("Intel processors don't use AM5 sockets. Consider LGA1700 for DDR5.");
        }
        
        if ("AMD".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
            warnings.append("AMD processors don't use DDR3. Consider AM4 for DDR4 or AM5 for DDR5.");
        }
        
        // Check budget compatibility
        if (currentBudget < 10000) {
            warnings.append("Budget below ₹10,000 may limit component options. Consider DDR3 builds.");
        }
        
        return warnings.toString();
    }
    
    private void autoBuildPCWithinBudget() {
        // Clear previous selections
        selectedComponents.clear();
        
        // Show loading message with user preferences
        String preferences = String.format("Building with: %s CPU, %s Socket, %s Storage, %s GPU", 
            selectedProcessorType, selectedSocketType, selectedStorageType, selectedGpuType);
        Toast.makeText(getContext(), preferences, Toast.LENGTH_LONG).show();
        
        Log.d("BuildPC", "Auto-build started with preferences: " + preferences);
        
        // Load components from database
        db.collection("components").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<ComponentModel> allComponents = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ComponentModel component = document.toObject(ComponentModel.class);
                    if (component != null) {
                        component.setId(document.getId());
                        allComponents.add(component);
                    }
                }
                
                if (allComponents.isEmpty()) {
                    Toast.makeText(getContext(), "No components found in database. Please add components first.", Toast.LENGTH_LONG).show();
                    return;
                }
                
                Log.d("BuildPC", "Found " + allComponents.size() + " components. Building PC within ₹" + String.format("%,d", currentBudget));
                
                // Use Smart Build Engine for intelligent component selection
                useSmartBuildEngine(allComponents);
                
            })
            .addOnFailureListener(e -> {
                String errorMessage = "Failed to load components: " + e.getMessage();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                Log.e("BuildPC", errorMessage, e);
                
                // Check if it's a permission error
                if (e.getMessage() != null && e.getMessage().contains("permission")) {
                    Toast.makeText(getContext(), "Firebase permission error. Please check security rules.", Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void autoSelectComponents(List<ComponentModel> allComponents) {
        double remainingBudget = currentBudget;
        double totalCost = 0;

        // Clear previous selections
        selectedComponents.clear();

        // Step 1: Select motherboard based on user preferences (AM4/AM5, DDR4/DDR5)
        ComponentModel selectedMobo = selectCompatibleMotherboard(allComponents, remainingBudget);
        if (selectedMobo == null) {
            Toast.makeText(getContext(), "No suitable motherboard found for ₹" + String.format("%,d", currentBudget), Toast.LENGTH_LONG).show();
            return;
        }
        selectedComponents.put("MOTHERBOARD", selectedMobo);
        totalCost += selectedMobo.getPrice();
        remainingBudget -= selectedMobo.getPrice();

        // Step 2: Select compatible CPU based on user preferences (AMD/Intel)
        ComponentModel selectedCPU = selectCompatibleCPU(allComponents, selectedMobo, remainingBudget);
        if (selectedCPU == null) {
            Toast.makeText(getContext(), "No compatible CPU found for the selected motherboard", Toast.LENGTH_LONG).show();
            return;
        }
        selectedComponents.put("PROCESSOR", selectedCPU);
        totalCost += selectedCPU.getPrice();
        remainingBudget -= selectedCPU.getPrice();

        // Step 3: Select compatible RAM (DDR3/DDR4/DDR5 based on motherboard)
        ComponentModel selectedRAM = selectCompatibleRAM(allComponents, selectedMobo, remainingBudget);
        if (selectedRAM != null) {
            selectedComponents.put("RAM", selectedRAM);
            totalCost += selectedRAM.getPrice();
            remainingBudget -= selectedRAM.getPrice();
        }

        // Step 4: Select storage based on user preference (HDD/SSD)
        ComponentModel selectedStorage = selectStorageByPreference(allComponents, remainingBudget);
        if (selectedStorage != null) {
            selectedComponents.put("STORAGE", selectedStorage);
            totalCost += selectedStorage.getPrice();
            remainingBudget -= selectedStorage.getPrice();
        }

        // Step 5: Select PSU
        ComponentModel selectedPSU = selectCheapestComponent(allComponents, "POWER SUPPLY", remainingBudget);
        if (selectedPSU != null) {
            selectedComponents.put("POWER SUPPLY", selectedPSU);
            totalCost += selectedPSU.getPrice();
            remainingBudget -= selectedPSU.getPrice();
        }

        // Step 6: Select case
        ComponentModel selectedCase = selectCheapestComponent(allComponents, "CABINET", remainingBudget);
        if (selectedCase != null) {
            selectedComponents.put("CABINET", selectedCase);
            totalCost += selectedCase.getPrice();
            remainingBudget -= selectedCase.getPrice();
        }

        // Step 7: Select GPU based on user preference (NVIDIA/AMD) if budget allows
        if (remainingBudget > 0) {
            ComponentModel selectedGPU = selectGPUByPreference(allComponents, remainingBudget);
            if (selectedGPU != null) {
                selectedComponents.put("GRAPHIC CARD", selectedGPU);
                totalCost += selectedGPU.getPrice();
                remainingBudget -= selectedGPU.getPrice();
            }
        }

        // Update UI
        updateBuildSummary();
        
        // Save the build
        saveCurrentBuild();
        
        // Add components to cart
        addComponentsToCart();

        // Show result
        if (selectedComponents.size() > 0) {
            String preferences = String.format("Built with: %s, %s, %s, %s", 
                selectedProcessorType, selectedSocketType, selectedStorageType, selectedGpuType);
            Toast.makeText(getContext(), "Auto-build complete! " + preferences + 
                "\nSelected " + selectedComponents.size() + " components. Total: ₹" + String.format("%.0f", totalCost) +
                " (Remaining: ₹" + String.format("%.0f", remainingBudget) + ")", Toast.LENGTH_LONG).show();
            
            // Show success dialog with option to proceed to cart
            showAutoBuildSuccessDialog(totalCost, remainingBudget);
        } else {
            Toast.makeText(getContext(), "No suitable components found for ₹" + String.format("%,d", currentBudget) +
                ". Please try increasing your budget.", Toast.LENGTH_LONG).show();
        }
    }

    // Select the CHEAPEST component in a category within budget
    private ComponentModel selectCheapestComponent(List<ComponentModel> components, String category, double maxBudget) {
        List<ComponentModel> categoryComponents = components.stream()
            .filter(c -> c.getCategory().equals(category) && c.getPrice() <= maxBudget)
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice())) // Sort by price ASC (cheapest first)
            .collect(Collectors.toList());
        
        return categoryComponents.isEmpty() ? null : categoryComponents.get(0);
    }

    // Select motherboard based on user preferences (AM4/AM5 for AMD, LGA sockets for Intel)
    private ComponentModel selectCompatibleMotherboard(List<ComponentModel> components, double maxBudget) {
        String targetSocket;
        String targetRamType;
        
        if ("AMD".equals(selectedProcessorType)) {
            // AMD processors use AM4 or AM5 sockets
            targetSocket = selectedSocketType; // AM4 or AM5
            targetRamType = selectedSocketType.equals("AM4") ? "DDR4" : "DDR5";
        } else {
            // Intel processors use LGA sockets
            if ("DDR3".equals(selectedSocketType)) {
                targetSocket = "LGA"; // Will match LGA1155, LGA1150, etc.
                targetRamType = "DDR3";
            } else if ("AM4".equals(selectedSocketType)) {
                targetSocket = "LGA"; // Will match LGA1151, LGA1200, etc.
                targetRamType = "DDR4";
            } else {
                targetSocket = "LGA"; // Will match LGA1700, etc.
                targetRamType = "DDR5";
            }
        }
        
        Log.d("BuildPC", "Looking for " + selectedProcessorType + " motherboard with socket: " + targetSocket + " and RAM: " + targetRamType);
        
        List<ComponentModel> compatibleMobos = components.stream()
            .filter(c -> c.getCategory().equals("MOTHERBOARD") &&
                        c.getPrice() <= maxBudget &&
                        c.getSpecifications().get("Socket") != null &&
                        c.getSpecifications().get("Socket").toUpperCase().contains(targetSocket.toUpperCase()) &&
                        c.getSpecifications().get("RAM") != null &&
                        c.getSpecifications().get("RAM").toUpperCase().contains(targetRamType.toUpperCase()))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()))
            .collect(Collectors.toList());
        
        if (compatibleMobos.isEmpty()) {
            Log.w("BuildPC", "No compatible motherboards found for " + targetSocket + " with " + targetRamType);
            // Fallback to any motherboard with the right socket
            List<ComponentModel> fallbackMobos = components.stream()
                .filter(c -> c.getCategory().equals("MOTHERBOARD") &&
                            c.getPrice() <= maxBudget &&
                            c.getSpecifications().get("Socket") != null &&
                            c.getSpecifications().get("Socket").toUpperCase().contains(targetSocket.toUpperCase()))
                .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()))
                .collect(Collectors.toList());
            return fallbackMobos.isEmpty() ? selectCheapestComponent(components, "MOTHERBOARD", maxBudget) : fallbackMobos.get(0);
        }
        
        Log.d("BuildPC", "Selected motherboard: " + compatibleMobos.get(0).getName());
        return compatibleMobos.get(0);
    }

    // Select compatible CPU based on user preferences (AMD/Intel) - STRICT FILTERING
    private ComponentModel selectCompatibleCPU(List<ComponentModel> components, ComponentModel mobo, double maxBudget) {
        if (mobo == null) {
            return selectCheapestComponent(components, "PROCESSOR", maxBudget);
        }

        String moboSocket = mobo.getSpecifications().get("Socket");
        if (moboSocket == null) {
            return selectCheapestComponent(components, "PROCESSOR", maxBudget);
        }

        Log.d("BuildPC", "Looking for " + selectedProcessorType + " CPU with socket " + moboSocket);

        // STRICT filtering: Only select CPUs that match the user's processor type preference
        List<ComponentModel> compatibleCPUs = components.stream()
            .filter(c -> c.getCategory().equals("PROCESSOR") &&
                        c.getPrice() <= maxBudget &&
                        c.getSpecifications().get("Socket") != null &&
                        c.getSpecifications().get("Socket").toUpperCase().contains(moboSocket.toUpperCase()) &&
                        c.getBrand() != null &&
                        c.getBrand().toUpperCase().contains(selectedProcessorType.toUpperCase()))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()))
            .collect(Collectors.toList());
        
        Log.d("BuildPC", "Found " + compatibleCPUs.size() + " compatible " + selectedProcessorType + " CPUs");
        
        if (!compatibleCPUs.isEmpty()) {
            ComponentModel selectedCPU = compatibleCPUs.get(0);
            Log.d("BuildPC", "Selected " + selectedProcessorType + " CPU: " + selectedCPU.getName() + " (Brand: " + selectedCPU.getBrand() + ")");
            return selectedCPU;
        }
        
        // If no CPUs match the user's processor type preference, show error instead of fallback
        Log.w("BuildPC", "No compatible CPUs found for " + selectedProcessorType + " with socket " + moboSocket);
        Toast.makeText(getContext(), "No " + selectedProcessorType + " processors found for " + moboSocket + " socket. Please try different preferences.", Toast.LENGTH_LONG).show();
        return null;
    }

    // Select compatible RAM (DDR3/DDR4/DDR5 based on motherboard)
    private ComponentModel selectCompatibleRAM(List<ComponentModel> components, ComponentModel mobo, double maxBudget) {
        if (mobo == null) {
            return selectCheapestComponent(components, "RAM", maxBudget);
        }

        String targetRamType;
        if ("AMD".equals(selectedProcessorType)) {
            targetRamType = selectedSocketType.equals("AM4") ? "DDR4" : "DDR5";
        } else {
            // For Intel, use the selected socket type for RAM
            if ("DDR3".equals(selectedSocketType)) {
                targetRamType = "DDR3";
            } else if ("AM4".equals(selectedSocketType)) {
                targetRamType = "DDR4";
            } else {
                targetRamType = "DDR5";
            }
        }
        
        List<ComponentModel> compatibleRAM = components.stream()
            .filter(c -> c.getCategory().equals("RAM") &&
                        c.getPrice() <= maxBudget &&
                        c.getSpecifications().get("Type") != null &&
                        c.getSpecifications().get("Type").toUpperCase().contains(targetRamType.toUpperCase()))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()))
            .collect(Collectors.toList());
        
        return compatibleRAM.isEmpty() ? selectCheapestComponent(components, "RAM", maxBudget) : compatibleRAM.get(0);
    }

    // Select storage based on user preference (HDD/SSD)
    private ComponentModel selectStorageByPreference(List<ComponentModel> components, double maxBudget) {
        List<ComponentModel> preferredStorage = components.stream()
            .filter(c -> c.getCategory().equals("STORAGE") &&
                        c.getPrice() <= maxBudget &&
                        c.getSpecifications().get("Type") != null &&
                        c.getSpecifications().get("Type").toUpperCase().contains(selectedStorageType))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()))
            .collect(Collectors.toList());
        
        if (preferredStorage.isEmpty()) {
            Log.w("BuildPC", "No " + selectedStorageType + " storage found, falling back to any storage");
            return selectCheapestComponent(components, "STORAGE", maxBudget);
        }
        
        Log.d("BuildPC", "Selected " + selectedStorageType + " storage: " + preferredStorage.get(0).getName());
        return preferredStorage.get(0);
    }

    // Select GPU based on user preference (NVIDIA/AMD)
    private ComponentModel selectGPUByPreference(List<ComponentModel> components, double maxBudget) {
        List<ComponentModel> preferredGPU = components.stream()
            .filter(c -> c.getCategory().equals("GRAPHIC CARD") &&
                        c.getPrice() <= maxBudget &&
                        c.getBrand() != null &&
                        c.getBrand().toUpperCase().contains(selectedGpuType.toUpperCase()))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()))
            .collect(Collectors.toList());
        
        if (preferredGPU.isEmpty()) {
            Log.w("BuildPC", "No " + selectedGpuType + " GPU found, falling back to any GPU");
            return selectCheapestComponent(components, "GRAPHIC CARD", maxBudget);
        }
        
        Log.d("BuildPC", "Selected " + selectedGpuType + " GPU: " + preferredGPU.get(0).getName());
        return preferredGPU.get(0);
    }
    
    private void loadCurrentBuild() {
        // Implementation for loading current build
    }
    
    private void saveCurrentBuild() {
        // Implementation for saving current build
    }
    
    private void addComponentsToCart() {
        // Implementation for adding components to cart
    }
    
    private void showAutoBuildSuccessDialog(double totalCost, double remainingBudget) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("🎉 Auto-Build Complete!")
               .setMessage("Successfully built PC within your budget!\n\n" +
                          "Total Cost: ₹" + String.format("%.0f", totalCost) + "\n" +
                          "Remaining Budget: ₹" + String.format("%.0f", remainingBudget) + "\n\n" +
                          "Components have been added to your cart. You can edit them before proceeding to checkout.")
               .setPositiveButton("View Cart", (dialog, which) -> {
                   // Navigate to cart
                   navigateToCart();
               })
               .setNegativeButton("Edit Build", (dialog, which) -> {
                   // Stay on current screen to edit
                   dialog.dismiss();
               })
               .setNeutralButton("View Summary", (dialog, which) -> {
                   // Show build summary
                   viewBuildSummary();
               })
               .setCancelable(false)
               .show();
    }
    
    private void navigateToCart() {
        // Navigate to cart fragment
        CartFragment cartFragment = new CartFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void viewBuildSummary() {
        if (selectedComponents.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one component first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Navigate to build summary
        BuildSummaryActivity.startActivity(requireContext(), selectedComponents, currentBudget);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadCurrentBuild();
    }

    private void setupSaveFunctionality() {
        saveBuildButton.setOnClickListener(v -> {
            if (selectedComponents.isEmpty()) {
                Toast.makeText(getContext(), "No components selected to save", Toast.LENGTH_SHORT).show();
                return;
            }
            showSaveBuildDialog();
        });
    }
    
    private void setupOtherPartsButton() {
        otherPartsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OtherPartsActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupRemoveDuplicatesButton() {
        removeDuplicatesButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RemoveDuplicatesActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupTrendingButton() {
        trendingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrendingComponentsActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupBuildManagerButton() {
        buildManagerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BuildManagerActivity.class);
            startActivity(intent);
        });
    }
    
    private void useSmartBuildEngine(List<ComponentModel> allComponents) {
        Log.d("BuildPC", "Using Smart Build Engine for intelligent component selection");
        
        // Use Smart Build Engine
        SmartBuildEngine.BuildResult result = SmartBuildEngine.buildSmartPC(
            allComponents,
            currentBudget,
            selectedProcessorType.equals("AMD") ? "AMD" : "Intel",
            selectedGpuType.equals("NVIDIA") ? "NVIDIA" : "AMD",
            selectedSocketType,
            selectedSocketType.contains("AM5") ? "DDR5" : (selectedSocketType.contains("AM4") ? "DDR4" : "DDR3"),
            selectedStorageType
        );
        
        if (result.selectedComponents.isEmpty()) {
            Toast.makeText(getContext(), "Could not build a PC within your budget and preferences", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Update selected components
        selectedComponents.clear();
        selectedComponents.putAll(result.selectedComponents);
        
        // Update UI like the original auto-build
        updateBuildSummary();
        
        // Save the build
        saveCurrentBuild();
        
        // Add components to cart
        addComponentsToCart();
        
        // Show enhanced build results with validation
        showSmartBuildResults(result);
        
        // Show original-style success dialog
        showAutoBuildSuccessDialog(result.totalCost, currentBudget - result.totalCost);
        
        Log.d("BuildPC", "Smart Build completed. Total cost: ₹" + String.format("%,d", (int)result.totalCost) + 
              ", Performance Score: " + String.format("%.1f", result.performanceScore));
    }
    
    private void showSmartBuildResults(SmartBuildEngine.BuildResult result) {
        // Validate the build for compatibility issues
        CompatibilityValidator.ValidationResult validation = CompatibilityValidator.validateBuild(result.selectedComponents);
        
        StringBuilder message = new StringBuilder();
        message.append("🎯 Smart Build Complete!\n\n");
        message.append("💰 Total Cost: ₹").append(String.format("%,d", (int)result.totalCost)).append("\n");
        message.append("🏆 Build Type: ").append(result.buildType).append("\n");
        message.append("⭐ Performance Score: ").append(String.format("%.1f/100", result.performanceScore)).append("\n\n");
        
        if (!validation.isCompatible) {
            message.append("⚠️ Compatibility Issues:\n");
            for (CompatibilityValidator.CompatibilityIssue issue : validation.issues) {
                if (issue.severity == CompatibilityValidator.CompatibilityIssue.Severity.CRITICAL) {
                    message.append("• ").append(issue.title).append("\n");
                }
            }
            message.append("\n");
        } else {
            message.append("✅ All components are compatible!\n\n");
        }
        
        if (!result.suggestions.isEmpty()) {
            message.append("💡 Suggestions:\n");
            for (String suggestion : result.suggestions) {
                message.append("• ").append(suggestion).append("\n");
            }
        }
        
        // Show as a Toast for quick feedback, like the original
        String preferences = String.format("Built with: %s, %s, %s, %s", 
            selectedProcessorType, selectedSocketType, selectedStorageType, selectedGpuType);
        Toast.makeText(getContext(), "Smart Auto-build complete! " + preferences + 
            "\nSelected " + result.selectedComponents.size() + " components. " +
            "Performance Score: " + String.format("%.1f/100", result.performanceScore) +
            (validation.isCompatible ? " ✅" : " ⚠️"), Toast.LENGTH_LONG).show();
        
        // Only show dialog if there are critical compatibility issues
        if (!validation.isCompatible) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("⚠️ Compatibility Issues Found");
            builder.setMessage("Your build has some compatibility issues. Would you like to view details?");
            builder.setPositiveButton("View Details", (dialog, which) -> {
                showCompatibilityDetails(validation);
            });
            builder.setNegativeButton("Continue Anyway", null);
            builder.show();
        }
    }
    
    private void showCompatibilityDetails(CompatibilityValidator.ValidationResult validation) {
        StringBuilder details = new StringBuilder();
        
        for (CompatibilityValidator.CompatibilityIssue issue : validation.issues) {
            details.append("⚠️ ").append(issue.title).append("\n");
            details.append(issue.description).append("\n");
            details.append("💡 Solution: ").append(issue.solution).append("\n\n");
        }
        
        if (!validation.warnings.isEmpty()) {
            details.append("⚡ Warnings:\n");
            for (String warning : validation.warnings) {
                details.append("• ").append(warning).append("\n");
            }
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Compatibility Details");
        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showSaveBuildDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Save Build");
        builder.setMessage("Enter a name for your build:");
        
        final android.widget.EditText input = new android.widget.EditText(getContext());
        input.setHint("My Custom Build");
        builder.setView(input);
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            String buildName = input.getText().toString().trim();
            if (buildName.isEmpty()) {
                buildName = "My Custom Build";
            }
            saveBuildToProfile(buildName);
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    private void saveBuildToProfile(String buildName) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login to save builds", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Map<String, Object> buildData = new HashMap<>();
        buildData.put("name", buildName);
        buildData.put("totalCost", calculateTotalCost());
        buildData.put("budget", currentBudget);
        buildData.put("timestamp", System.currentTimeMillis());
        
        // Convert selected components to a format suitable for Firestore
        Map<String, Object> componentsData = new HashMap<>();
        for (Map.Entry<String, ComponentModel> entry : selectedComponents.entrySet()) {
            ComponentModel component = entry.getValue();
            Map<String, Object> componentData = new HashMap<>();
            componentData.put("name", component.getName());
            componentData.put("category", component.getCategory());
            componentData.put("price", component.getPrice());
            componentData.put("brand", component.getBrand());
            componentData.put("specifications", component.getSpecifications());
            componentsData.put(entry.getKey(), componentData);
        }
        buildData.put("components", componentsData);
        
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
            .collection("savedBuilds")
            .add(buildData)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(getContext(), "Build saved successfully!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to save build: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    // Method to add DDR3 components (for testing)
    private void addDDR3Components() {
        Intent intent = new Intent(requireContext(), AddComponentActivity.class);
        startActivity(intent);
    }
}



