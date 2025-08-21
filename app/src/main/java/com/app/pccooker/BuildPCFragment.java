package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.ImageView;
import android.os.Handler;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.pccooker.models.ComponentModel;
import com.app.pccooker.ui.UiNotifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.util.Log;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;

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
    private Button ddr3Button;
    private Button ddr4Button;
    private Button ddr5Button;
    private LinearLayout legacySocketLayout;
    private Button am4Button;
    private Button am5Button;
    private Button lga1700Button;
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
    
    // Floating chat assistant button
    private FloatingActionButton floatingChatButton;

    private int currentBudget = 40000; // Default budget
    private String selectedProcessorType = null; // No default selection
    private String selectedSocketType = null; // No default DDR selection
    private String selectedStorageType = "SSD";
    private String selectedGpuType = null; // No default GPU selection

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
        ddr3Button = view.findViewById(R.id.ddr3Button);
        ddr4Button = view.findViewById(R.id.ddr4Button);
        ddr5Button = view.findViewById(R.id.ddr5Button);
        legacySocketLayout = view.findViewById(R.id.legacySocketLayout);
        am4Button = view.findViewById(R.id.am4Button);
        am5Button = view.findViewById(R.id.am5Button);
        lga1700Button = view.findViewById(R.id.lga1700Button);
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
        
        // Floating chat assistant button
        floatingChatButton = view.findViewById(R.id.floatingChatButton);
        
        // Back button
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
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
        
        // Set up floating chat assistant button
        setupFloatingChatButton();
        
        // Load current build
        loadCurrentBuild();
        
        // Initialize button states
        updateButtonStates();
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
                
                // Removed annoying tip message
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
        if (amdButton == null || intelButton == null || ddr3Button == null || ddr4Button == null || ddr5Button == null ||
            hddButton == null || ssdButton == null || nvidiaButton == null || amdGpuButton == null) {
            Log.e("BuildPC", "One or more filter buttons are null!");
            return;
        }
        
        // Make sure buttons are clickable and focusable
        amdButton.setClickable(true);
        amdButton.setFocusable(true);
        intelButton.setClickable(true);
        intelButton.setFocusable(true);
        ddr3Button.setClickable(true);
        ddr3Button.setFocusable(true);
        ddr4Button.setClickable(true);
        ddr4Button.setFocusable(true);
        ddr5Button.setClickable(true);
        ddr5Button.setFocusable(true);
        hddButton.setClickable(true);
        hddButton.setFocusable(true);
        ssdButton.setClickable(true);
        ssdButton.setFocusable(true);
        nvidiaButton.setClickable(true);
        nvidiaButton.setFocusable(true);
        amdGpuButton.setClickable(true);
        amdGpuButton.setFocusable(true);
        
        // Processor type selection - EXCLUSIVE selection (only one can be selected)
        amdButton.setOnClickListener(v -> {
            if ("AMD".equals(selectedProcessorType)) {
                // Already selected - deselect
                selectedProcessorType = null;
                // AMD processor deselected
            } else {
                // Select AMD, deselect Intel
                selectedProcessorType = "AMD";
                // Auto-adjust DDR type based on budget if none selected
                if (selectedSocketType == null) {
                    if (currentBudget >= 50000) {
                        selectedSocketType = "DDR5"; // High-end builds
                    } else if (currentBudget >= 30000) {
                        selectedSocketType = "DDR4"; // Mid-range builds
                    } else {
                        selectedSocketType = "DDR3"; // Budget builds
                    }
                    // AMD selected - auto-selected DDR type for budget
                } else {
                    // AMD processor selected
                }
            }
            Log.d("BuildPC", "AMD button clicked - selectedProcessorType: " + selectedProcessorType);
            updateButtonStates();
        });
        
        intelButton.setOnClickListener(v -> {
            if ("Intel".equals(selectedProcessorType)) {
                // Already selected - deselect
                selectedProcessorType = null;
                // Intel processor deselected
            } else {
                // Select Intel, deselect AMD
                selectedProcessorType = "Intel";
                // Auto-adjust DDR type based on budget if none selected
                if (selectedSocketType == null) {
                    if (currentBudget >= 50000) {
                        selectedSocketType = "DDR5"; // High-end Intel builds (i7, i9, 12th+ gen)
                    } else if (currentBudget >= 30000) {
                        selectedSocketType = "DDR4"; // Mid-range Intel builds (i5, i7)
                    } else {
                        selectedSocketType = "DDR3"; // Budget Intel builds (i3, older gen)
                    }
                    // Intel selected - auto-selected DDR type for budget
                } else {
                    // Intel processor selected
                }
            }
            Log.d("BuildPC", "Intel button clicked - selectedProcessorType: " + selectedProcessorType);
            updateButtonStates();
        });
        
        // DDR type selection - EXCLUSIVE selection (only one can be selected)
        ddr3Button.setOnClickListener(v -> {
            if ("DDR3".equals(selectedSocketType)) {
                // Already selected - deselect
                selectedSocketType = null;
                // DDR3 deselected
            } else {
                // Select DDR3, deselect others
                selectedSocketType = "DDR3";
                // DDR3 selected - compatible with budget AMD and Intel builds
                
                // Show DDR3 recommendations after selection
                new Handler().postDelayed(() -> {
                    showDDR3Recommendations();
                }, 2000);
                
                // Auto-adjust processor type if none selected
                if (selectedProcessorType == null) {
                    selectedProcessorType = "Intel"; // DDR3 works best with Intel
                    // Auto-selected Intel processor for DDR3 compatibility
                    updateButtonStates();
                }
            }
            Log.d("BuildPC", "DDR3 button clicked - selectedSocketType: " + selectedSocketType);
            updateButtonStates();
        });
        
        // Add long press listener for DDR3 button to show detailed recommendations
        ddr3Button.setOnLongClickListener(v -> {
            showDDR3Recommendations();
            return true;
        });
        
        ddr4Button.setOnClickListener(v -> {
            if ("DDR4".equals(selectedSocketType)) {
                // Already selected - deselect
                selectedSocketType = null;
                // DDR4 deselected
            } else {
                // Select DDR4, deselect others
                selectedSocketType = "DDR4";
                // DDR4 selected - compatible with mid-range AMD and Intel builds
            }
            Log.d("BuildPC", "DDR4 button clicked - selectedSocketType: " + selectedSocketType);
            updateButtonStates();
        });
        
        ddr5Button.setOnClickListener(v -> {
            if ("DDR5".equals(selectedSocketType)) {
                // Already selected - deselect
                selectedSocketType = null;
                // DDR5 deselected
            } else {
                // Select DDR5, deselect others
                selectedSocketType = "DDR5";
                // DDR5 selected - compatible with high-end AMD and Intel builds
            }
            Log.d("BuildPC", "DDR5 button clicked - selectedSocketType: " + selectedSocketType);
            updateButtonStates();
        });
        
        // Legacy socket buttons (only shown when DDR3 is selected)
        am4Button.setOnClickListener(v -> {
            // AM4 is compatible with DDR4, but we're in DDR3 mode
            // AM4 requires DDR4. Please select DDR4 first.
        });
        
        am5Button.setOnClickListener(v -> {
            // AM5 is compatible with DDR5, but we're in DDR3 mode
            // AM5 requires DDR5. Please select DDR5 first.
        });
        
        lga1700Button.setOnClickListener(v -> {
            // LGA1700 is compatible with DDR4/DDR5, but we're in DDR3 mode
            // LGA1700 requires DDR4/DDR5. Please select DDR4 or DDR5 first.
        });
        
        // Storage type selection - EXCLUSIVE selection (only one can be selected)
        hddButton.setOnClickListener(v -> {
            if ("HDD".equals(selectedStorageType)) {
                // Already selected - deselect
                selectedStorageType = null;
                // HDD deselected
            } else {
                // Select HDD, deselect SSD
                selectedStorageType = "HDD";
                // HDD storage selected
            }
            Log.d("BuildPC", "HDD button clicked - selectedStorageType: " + selectedStorageType);
            updateButtonStates();
        });
        
        ssdButton.setOnClickListener(v -> {
            if ("SSD".equals(selectedStorageType)) {
                // Already selected - deselect
                selectedStorageType = "SSD"; // Keep SSD as default, don't allow deselection
                // SSD is the default storage type
            } else {
                // Select SSD, deselect HDD
                selectedStorageType = "SSD";
                // SSD storage selected
            }
            Log.d("BuildPC", "SSD button clicked - selectedStorageType: " + selectedStorageType);
            updateButtonStates();
        });
        
        // GPU type selection - EXCLUSIVE selection (only one can be selected)
        nvidiaButton.setOnClickListener(v -> {
            if ("NVIDIA".equals(selectedGpuType)) {
                // Already selected - deselect
                selectedGpuType = null;
                // NVIDIA GPU deselected
            } else {
                // Select NVIDIA, deselect AMD
                selectedGpuType = "NVIDIA";
                // NVIDIA GPU selected
            }
            Log.d("BuildPC", "NVIDIA button clicked - selectedGpuType: " + selectedGpuType);
            updateButtonStates();
        });
        
        amdGpuButton.setOnClickListener(v -> {
            if ("AMD".equals(selectedGpuType)) {
                // Already selected - deselect
                selectedGpuType = null;
                // AMD GPU deselected
            } else {
                // Select AMD, deselect NVIDIA
                selectedGpuType = "AMD";
                // AMD GPU selected
            }
            Log.d("BuildPC", "AMD GPU button clicked - selectedGpuType: " + selectedGpuType);
            updateButtonStates();
        });
        
        // Initialize button states
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        // Ensure buttons are not null before updating
        if (amdButton == null || intelButton == null || ddr3Button == null || ddr4Button == null || ddr5Button == null ||
            hddButton == null || ssdButton == null || nvidiaButton == null || amdGpuButton == null) {
            Log.e("BuildPC", "Cannot update button states - buttons are null");
            return;
        }
        
        // Update processor type buttons
        boolean amdSelected = "AMD".equals(selectedProcessorType);
        boolean intelSelected = "Intel".equals(selectedProcessorType);
        
        // Use background tinting for visual feedback
        if (amdSelected) {
            amdButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
            amdButton.setTextColor(Color.WHITE);
            amdButton.setElevation(8f); // Add elevation for selected state
        } else {
            amdButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            amdButton.setTextColor(Color.WHITE);
            amdButton.setElevation(2f); // Normal elevation
        }
        
        if (intelSelected) {
            intelButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
            intelButton.setTextColor(Color.WHITE);
            intelButton.setElevation(8f); // Add elevation for selected state
        } else {
            intelButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            intelButton.setTextColor(Color.WHITE);
            intelButton.setElevation(2f); // Normal elevation
        }
        
        // Update DDR type buttons
        boolean ddr3Selected = "DDR3".equals(selectedSocketType);
        boolean ddr4Selected = "DDR4".equals(selectedSocketType);
        boolean ddr5Selected = "DDR5".equals(selectedSocketType);
        
        if (ddr3Selected) {
            ddr3Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800"))); // Orange
            ddr3Button.setTextColor(Color.WHITE);
            ddr3Button.setElevation(8f); // Add elevation for selected state
        } else {
            ddr3Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            ddr3Button.setTextColor(Color.WHITE);
            ddr3Button.setElevation(2f); // Normal elevation
        }
        
        if (ddr4Selected) {
            ddr4Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800"))); // Orange
            ddr4Button.setTextColor(Color.WHITE);
            ddr4Button.setElevation(8f); // Add elevation for selected state
        } else {
            ddr4Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            ddr4Button.setTextColor(Color.WHITE);
            ddr4Button.setElevation(2f); // Normal elevation
        }
        
        if (ddr5Selected) {
            ddr5Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800"))); // Orange
            ddr5Button.setTextColor(Color.WHITE);
            ddr5Button.setElevation(8f); // Add elevation for selected state
        } else {
            ddr5Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            ddr5Button.setTextColor(Color.WHITE);
            ddr5Button.setElevation(2f); // Normal elevation
        }

        // Update storage type buttons
        boolean hddSelected = "HDD".equals(selectedStorageType);
        boolean ssdSelected = "SSD".equals(selectedStorageType);
        
        if (hddSelected) {
            hddButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C27B0"))); // Purple
            hddButton.setTextColor(Color.WHITE);
            hddButton.setElevation(8f); // Add elevation for selected state
        } else {
            hddButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            hddButton.setTextColor(Color.WHITE);
            hddButton.setElevation(2f); // Normal elevation
        }
        
        if (ssdSelected) {
            ssdButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C27B0"))); // Purple
            ssdButton.setTextColor(Color.WHITE);
            ssdButton.setElevation(8f); // Add elevation for selected state
        } else {
            ssdButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            ssdButton.setTextColor(Color.WHITE);
            ssdButton.setElevation(2f); // Normal elevation
        }
        
        // Update GPU type buttons
        boolean nvidiaSelected = "NVIDIA".equals(selectedGpuType);
        boolean amdGpuSelected = "AMD".equals(selectedGpuType);
        
        if (nvidiaSelected) {
            nvidiaButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E91E63"))); // Pink
            nvidiaButton.setTextColor(Color.WHITE);
            nvidiaButton.setElevation(8f); // Add elevation for selected state
        } else {
            nvidiaButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            nvidiaButton.setTextColor(Color.WHITE);
            nvidiaButton.setElevation(2f); // Normal elevation
        }
        
        if (amdGpuSelected) {
            amdGpuButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E91E63"))); // Pink
            amdGpuButton.setTextColor(Color.WHITE);
            amdGpuButton.setElevation(8f); // Add elevation for selected state
        } else {
            amdGpuButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            amdGpuButton.setTextColor(Color.WHITE);
            amdGpuButton.setElevation(2f); // Normal elevation
        }
        
        // Log current selections
        Log.d("BuildPC", "Current selections - Processor: " + selectedProcessorType + 
              ", DDR: " + selectedSocketType + 
              ", Storage: " + selectedStorageType + 
              ", GPU: " + selectedGpuType);
        
        // Log button states for debugging
        Log.d("BuildPC", "Button states - AMD: " + amdSelected + ", Intel: " + intelSelected + 
              ", DDR3: " + ddr3Selected + ", DDR4: " + ddr4Selected + ", DDR5: " + ddr5Selected + 
              ", HDD: " + hddSelected + ", SSD: " + ssdSelected + 
              ", NVIDIA: " + nvidiaSelected + ", AMD GPU: " + amdGpuSelected);
        
        // Update build summary
        updateBuildSummary();
        
        // Animate button state changes for better visual feedback
        animateButtonStateChanges();
    }
    
    private void animateButtonStateChanges() {
        // Add subtle scale animation to selected buttons
        if (amdButton != null && "AMD".equals(selectedProcessorType)) {
            amdButton.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (amdButton != null) {
            amdButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (intelButton != null && "Intel".equals(selectedProcessorType)) {
            intelButton.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (intelButton != null) {
            intelButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (ddr3Button != null && "DDR3".equals(selectedSocketType)) {
            ddr3Button.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (ddr3Button != null) {
            ddr3Button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (ddr4Button != null && "DDR4".equals(selectedSocketType)) {
            ddr4Button.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (ddr4Button != null) {
            ddr4Button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (ddr5Button != null && "DDR5".equals(selectedSocketType)) {
            ddr5Button.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (ddr5Button != null) {
            ddr5Button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (hddButton != null && "HDD".equals(selectedStorageType)) {
            hddButton.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (hddButton != null) {
            hddButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (ssdButton != null && "SSD".equals(selectedStorageType)) {
            ssdButton.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (ssdButton != null) {
            ssdButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (nvidiaButton != null && "NVIDIA".equals(selectedGpuType)) {
            nvidiaButton.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (nvidiaButton != null) {
            nvidiaButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
        
        if (amdGpuButton != null && "AMD".equals(selectedGpuType)) {
            amdGpuButton.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        } else if (amdGpuButton != null) {
            amdGpuButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
    }
    
    private void setupAutoBuild() {
        autoBuildButton.setOnClickListener(v -> {
            if (currentBudget < 10000) {
                UiNotifier.showShort(getContext(), "Minimum budget required: ₹10,000");
                return;
            }
            
            showAutoBuildProgress();
            autoBuildPCWithinBudget();
        });
        
        // Long press for DDR3 components
        autoBuildButton.setOnLongClickListener(v -> {
            addDDR3Components();
            return true;
        });
        
        // Double tap for compatibility summary
        final long[] lastTapTime = {0};
        final long DOUBLE_TAP_TIME_DELTA = 300; // milliseconds
        
        autoBuildButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTapTime[0] < DOUBLE_TAP_TIME_DELTA) {
                    // Double tap detected
                    showComprehensiveCompatibilitySummary();
                    lastTapTime[0] = 0; // Reset to prevent triple tap
                    return true;
                }
                lastTapTime[0] = currentTime;
            }
            return false;
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
        
        // Note: DDR3 components can be accessed via long-press on Auto-Build button
        // Compatibility summary can be accessed via double-tap on Auto-Build button
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
        
        // Hide the permanent status display
        compatibilityStatusText.setVisibility(View.GONE);
        
        // Status information is now shown in the build summary instead
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
        
        // Check processor and DDR type compatibility with more balanced messaging
        if ("Intel".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
            warnings.append("💡 Intel DDR3 builds are great for budget builds under ₹15,000!\n");
            warnings.append("✅ Perfect for: Basic computing, office work, light gaming\n");
            warnings.append("⚠️ Limited to: 2nd/3rd gen Intel processors, max 16GB RAM\n");
            warnings.append("🔧 Compatible sockets: LGA1155, LGA1150, LGA1156\n");
            warnings.append("💾 RAM: DDR3-1333/1600, max 16GB (2x8GB)\n\n");
        }
        
        if ("Intel".equals(selectedProcessorType) && "DDR4".equals(selectedSocketType)) {
            warnings.append("💡 Intel DDR4 builds offer good performance for mid-range builds!\n");
            warnings.append("✅ Perfect for: Gaming, content creation, multitasking\n");
            warnings.append("⚠️ Consider DDR5 for: Future-proofing, high-end builds\n");
            warnings.append("🔧 Compatible sockets: LGA1151, LGA1200, LGA1700\n");
            warnings.append("💾 RAM: DDR4-2133 to DDR4-3600, max 128GB\n\n");
        }
        
        if ("Intel".equals(selectedProcessorType) && "DDR5".equals(selectedSocketType)) {
            warnings.append("💡 Intel DDR5 builds are excellent for high-end performance!\n");
            warnings.append("✅ Perfect for: Gaming, streaming, content creation, workstation\n");
            warnings.append("🔧 Compatible sockets: LGA1700, LGA1851\n");
            warnings.append("💾 RAM: DDR5-4800 to DDR5-7200, max 128GB\n");
            warnings.append("💰 Recommended budget: ₹50,000+\n\n");
        }
        
        if ("AMD".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
            warnings.append("⚠️ AMD DDR3 builds are limited to older AM3+ socket processors\n");
            warnings.append("💡 Consider AM4 with DDR4 for better performance and value\n");
            warnings.append("🔧 Compatible sockets: AM3+ (limited options)\n");
            warnings.append("💾 RAM: DDR3-1333/1600, max 32GB\n\n");
        }
        
        if ("AMD".equals(selectedProcessorType) && "DDR4".equals(selectedSocketType)) {
            warnings.append("💡 AMD DDR4 builds are excellent for performance and value!\n");
            warnings.append("✅ Perfect for: Gaming, streaming, content creation\n");
            warnings.append("💡 Consider AM5 with DDR5 for: Future-proofing, high-end builds\n");
            warnings.append("🔧 Compatible sockets: AM4\n");
            warnings.append("💾 RAM: DDR4-2133 to DDR4-3600, max 128GB\n\n");
        }
        
        if ("AMD".equals(selectedProcessorType) && "DDR5".equals(selectedSocketType)) {
            warnings.append("💡 AMD DDR5 builds are cutting-edge for maximum performance!\n");
            warnings.append("✅ Perfect for: High-end gaming, content creation, workstation\n");
            warnings.append("🔧 Compatible sockets: AM5\n");
            warnings.append("💾 RAM: DDR5-4800 to DDR5-7200, max 128GB\n");
            warnings.append("💰 Recommended budget: ₹60,000+\n\n");
        }
        
        // Check budget compatibility with helpful suggestions
        if (currentBudget < 10000) {
            warnings.append("💰 Budget below ₹10,000 - DDR3 components recommended!\n");
            warnings.append("💡 Great options: Intel 2nd/3rd gen, H61 motherboards, DDR3 RAM\n");
            warnings.append("🎯 Target: Basic computing, office work, light browsing\n\n");
        } else if (currentBudget < 25000) {
            warnings.append("💰 Budget ₹10,000-25,000 - DDR4 components recommended!\n");
            warnings.append("💡 Great options: Intel 6th-10th gen, AMD Ryzen 3000 series\n");
            warnings.append("🎯 Target: Gaming, content creation, multitasking\n\n");
        } else if (currentBudget < 50000) {
            warnings.append("💰 Budget ₹25,000-50,000 - DDR4/DDR5 components recommended!\n");
            warnings.append("💡 Great options: Intel 12th-13th gen, AMD Ryzen 5000 series\n");
            warnings.append("🎯 Target: High-end gaming, streaming, content creation\n\n");
        } else {
            warnings.append("💰 Budget above ₹50,000 - DDR5 components recommended!\n");
            warnings.append("💡 Great options: Intel 13th-14th gen, AMD Ryzen 7000 series\n");
            warnings.append("🎯 Target: Professional work, high-end gaming, future-proofing\n\n");
        }
        
        return warnings.toString();
    }
    
    private void autoBuildPCWithinBudget() {
        // Check for incompatible selections before building
        String compatibilityWarning = checkCompatibilityWarnings();
        if (!compatibilityWarning.isEmpty()) {
            // Silently auto-adjust and proceed without pop-up
            autoAdjustIncompatibleSelections();
        }
        
        // Proceed with build
        proceedWithBuild();
    }
    
    private void proceedWithBuild() {
        // Clear previous selections
        selectedComponents.clear();
        
        // Show loading message with user preferences
        String preferences = String.format("Building with: %s CPU, %s DDR, %s Storage, %s GPU", 
            selectedProcessorType, selectedSocketType, selectedStorageType, selectedGpuType);
        UiNotifier.showLong(getContext(), preferences);
        
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
                    UiNotifier.showLong(getContext(), "No components found in database. Please add components first.");
                    return;
                }
                
                Log.d("BuildPC", "Found " + allComponents.size() + " components. Building PC within ₹" + String.format("%,d", currentBudget));
                
                // Use Smart Build Engine for intelligent component selection
                useSmartBuildEngine(allComponents);
                
            })
            .addOnFailureListener(e -> {
                String errorMessage = "Failed to load components: " + e.getMessage();
                UiNotifier.showLong(getContext(), errorMessage);
                Log.e("BuildPC", errorMessage, e);
                
                // Check if it's a permission error
                if (e.getMessage() != null && e.getMessage().contains("permission")) {
                    UiNotifier.showLong(getContext(), "Firebase permission error. Please check security rules.");
                }
            });
    }
    
    private void autoSelectComponents(List<ComponentModel> allComponents) {
        double remainingBudget = currentBudget;
        double totalCost = 0;

        // Clear previous selections
        selectedComponents.clear();

        // Determine RAM type from DDR selection
        String ramType = "DDR4"; // Default
        if (selectedSocketType != null) {
            if (selectedSocketType.contains("DDR5")) {
                ramType = "DDR5";
            } else if (selectedSocketType.contains("DDR4")) {
                ramType = "DDR4";
            } else if (selectedSocketType.contains("DDR3")) {
                ramType = "DDR3";
            }
        }

        // Step 1: Select motherboard based on DDR type and processor brand
        ComponentModel selectedMobo = selectCompatibleMotherboard(allComponents, remainingBudget);
        if (selectedMobo == null) {
            UiNotifier.showLong(getContext(), "No suitable " + ramType + " motherboard found for ₹" + String.format("%,d", currentBudget));
            return;
        }
        selectedComponents.put("MOTHERBOARD", selectedMobo);
        totalCost += selectedMobo.getPrice();
        remainingBudget -= selectedMobo.getPrice();

        // Step 2: Select compatible CPU based on DDR type and processor brand
        ComponentModel selectedCPU = selectCompatibleCPU(allComponents, selectedMobo, remainingBudget);
        if (selectedCPU == null) {
            UiNotifier.showLong(getContext(), "No compatible " + ramType + " CPU found for the selected motherboard");
            return;
        }
        selectedComponents.put("PROCESSOR", selectedCPU);
        totalCost += selectedCPU.getPrice();
        remainingBudget -= selectedCPU.getPrice();

        // Step 3: Select compatible RAM based on DDR type
        ComponentModel selectedRAM = selectCompatibleRAM(allComponents, selectedMobo, remainingBudget);
        if (selectedRAM == null) {
            UiNotifier.showLong(getContext(), "No compatible " + ramType + " RAM found");
            return;
        }
        selectedComponents.put("RAM", selectedRAM);
        totalCost += selectedRAM.getPrice();
        remainingBudget -= selectedRAM.getPrice();

        // Step 4: Select storage based on user preference
        ComponentModel selectedStorage = selectStorageByPreference(allComponents, remainingBudget);
        if (selectedStorage != null) {
            selectedComponents.put("STORAGE", selectedStorage);
            totalCost += selectedStorage.getPrice();
            remainingBudget -= selectedStorage.getPrice();
        }

        // Step 5: Select GPU based on user preference
        ComponentModel selectedGPU = selectGPUByPreference(allComponents, remainingBudget);
        if (selectedGPU != null) {
            selectedComponents.put("GRAPHIC CARD", selectedGPU);
            totalCost += selectedGPU.getPrice();
            remainingBudget -= selectedGPU.getPrice();
        }

        // Step 6: Select power supply
        ComponentModel selectedPSU = selectCheapestComponent(allComponents, "POWER SUPPLY", remainingBudget);
        if (selectedPSU != null) {
            selectedComponents.put("POWER SUPPLY", selectedPSU);
            totalCost += selectedPSU.getPrice();
            remainingBudget -= selectedPSU.getPrice();
        }

        // Step 7: Select case
        ComponentModel selectedCase = selectCheapestComponent(allComponents, "CABINET", remainingBudget);
        if (selectedCase != null) {
            selectedComponents.put("CABINET", selectedCase);
            totalCost += selectedCase.getPrice();
            remainingBudget -= selectedCase.getPrice();
        }

        // Update UI and show results
        updateBuildSummary();
        
        // Save the build
        saveCurrentBuild();
        
        // Add components to cart
        addComponentsToCart();
        
        // Show success dialog
        showAutoBuildSuccessDialog(totalCost, remainingBudget);
        
        Log.d("BuildPC", "Auto-build completed. Total cost: ₹" + String.format("%,d", (int)totalCost) + 
              ", Remaining budget: ₹" + String.format("%,d", (int)remainingBudget));
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
        // Add null safety for selectedProcessorType
        if (selectedProcessorType == null) {
            Log.w("BuildPC", "No processor type selected, using default Intel");
            selectedProcessorType = "Intel";
        }
        
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
        UiNotifier.showLong(getContext(), "No " + selectedProcessorType + " processors found for " + moboSocket + " socket. Please try different preferences.");
        return null;
    }

    // Select compatible RAM (DDR3/DDR4/DDR5 based on motherboard)
    private ComponentModel selectCompatibleRAM(List<ComponentModel> components, ComponentModel mobo, double maxBudget) {
        if (mobo == null) {
            return selectCheapestComponent(components, "RAM", maxBudget);
        }

        String targetRamType;
        // Add null safety for selectedProcessorType and selectedSocketType
        if (selectedProcessorType == null) {
            selectedProcessorType = "Intel"; // Default to Intel
        }
        if (selectedSocketType == null) {
            selectedSocketType = "AM4"; // Default to AM4
        }
        
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
        // Add null safety for selectedStorageType
        if (selectedStorageType == null) {
            selectedStorageType = "SSD"; // Default to SSD
        }
        
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
        // Add null safety for selectedGpuType
        if (selectedGpuType == null) {
            selectedGpuType = "NVIDIA"; // Default to NVIDIA
        }
        
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
        // Load the current build from SharedPreferences or Firebase
        if (auth.getCurrentUser() == null) {
            return; // No user logged in
        }
        
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
            .collection("currentBuild")
            .document("latest")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Map<String, Object> buildData = documentSnapshot.getData();
                    if (buildData != null) {
                        // Load budget
                        if (buildData.containsKey("budget")) {
                            currentBudget = ((Number) buildData.get("budget")).intValue();
                            updateBudgetDisplay(currentBudget);
                        }
                        
                        // Load component preferences
                        if (buildData.containsKey("processorType")) {
                            selectedProcessorType = (String) buildData.get("processorType");
                        }
                        if (buildData.containsKey("socketType")) {
                            selectedSocketType = (String) buildData.get("socketType");
                        }
                        if (buildData.containsKey("storageType")) {
                            selectedStorageType = (String) buildData.get("storageType");
                        }
                        if (buildData.containsKey("gpuType")) {
                            selectedGpuType = (String) buildData.get("gpuType");
                        }
                        
                        // Update UI
                        updateButtonStates();
                        updateBuildSummary();
                        
                        Log.d("BuildPC", "Current build loaded successfully");
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.w("BuildPC", "Failed to load current build", e);
            });
    }
    
    private void saveCurrentBuild() {
        // Save the current build to Firebase
        if (auth.getCurrentUser() == null) {
            return; // No user logged in
        }
        
        Map<String, Object> buildData = new HashMap<>();
        buildData.put("budget", currentBudget);
        buildData.put("processorType", selectedProcessorType);
        buildData.put("socketType", selectedSocketType);
        buildData.put("storageType", selectedStorageType);
        buildData.put("gpuType", selectedGpuType);
        buildData.put("timestamp", System.currentTimeMillis());
        buildData.put("totalCost", calculateTotalCost());
        
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
            .collection("currentBuild")
            .document("latest")
            .set(buildData)
            .addOnSuccessListener(aVoid -> {
                Log.d("BuildPC", "Current build saved successfully");
            })
            .addOnFailureListener(e -> {
                Log.w("BuildPC", "Failed to save current build", e);
            });
    }
    
    private void addComponentsToCart() {
        // Add selected components to the user's cart
        if (auth.getCurrentUser() == null) {
            UiNotifier.showShort(getContext(), "Please login to add components to cart");
            return;
        }
        
        if (selectedComponents.isEmpty()) {
            UiNotifier.showShort(getContext(), "No components selected to add to cart");
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        final int totalComponents = selectedComponents.size();
        final int[] addedCount = {0};
        
        for (Map.Entry<String, ComponentModel> entry : selectedComponents.entrySet()) {
            ComponentModel component = entry.getValue();
            
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("componentId", component.getId());
            cartItem.put("name", component.getName());
            cartItem.put("category", component.getCategory());
            cartItem.put("price", component.getPrice());
            cartItem.put("brand", component.getBrand());
            cartItem.put("specifications", component.getSpecifications());
            cartItem.put("addedAt", System.currentTimeMillis());
            cartItem.put("quantity", 1);
            
            db.collection("users").document(userId)
                .collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    addedCount[0]++;
                    if (addedCount[0] == totalComponents) {
                        // All components added successfully
                        UiNotifier.showShort(getContext(), 
                            "Added " + addedCount[0] + " components to cart successfully!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("BuildPC", "Failed to add component to cart: " + component.getName(), e);
                    UiNotifier.showShort(getContext(), 
                        "Failed to add " + component.getName() + " to cart");
                });
        }
        
        Log.d("BuildPC", "Adding " + selectedComponents.size() + " components to cart");
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
            UiNotifier.showShort(getContext(), "Please select at least one component first");
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
                UiNotifier.showShort(getContext(), "No components selected to save");
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
    
    private void setupFloatingChatButton() {
        floatingChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatbotActivity.class);
            startActivity(intent);
        });
    }
    
    private void useSmartBuildEngine(List<ComponentModel> allComponents) {
        Log.d("BuildPC", "Using Smart Build Engine for intelligent component selection");
        
        // Determine RAM type from DDR selection
        String ramType = "DDR4"; // Default
        if (selectedSocketType != null) {
            if (selectedSocketType.contains("DDR5")) {
                ramType = "DDR5";
            } else if (selectedSocketType.contains("DDR4")) {
                ramType = "DDR4";
            } else if (selectedSocketType.contains("DDR3")) {
                ramType = "DDR3";
            }
        }
        
        // Determine processor brand and generation based on budget
        String processorBrand = (selectedProcessorType != null && selectedProcessorType.equals("AMD")) ? "AMD" : "Intel";
        String gpuBrand = (selectedGpuType != null && selectedGpuType.equals("NVIDIA")) ? "NVIDIA" : "AMD";
        
        // Determine target processor generation based on budget
        String targetGeneration = determineTargetGeneration(currentBudget, processorBrand, ramType);
        
        Log.d("BuildPC", "Smart Build Parameters - Budget: ₹" + String.format("%,d", currentBudget) + 
              ", Processor: " + processorBrand + ", DDR: " + ramType + ", Target Gen: " + targetGeneration);
        
        SmartBuildEngine.BuildResult result = SmartBuildEngine.buildSmartPC(
            allComponents,
            currentBudget,
            processorBrand,
            gpuBrand,
            null, // No socket type needed with DDR approach
            ramType, // DDR type
            selectedStorageType != null ? selectedStorageType : "SSD",
            targetGeneration // Pass target generation for smarter selection
        );
        
        if (result.selectedComponents.isEmpty()) {
            UiNotifier.showLong(getContext(), "Could not build a PC within your budget and preferences");
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
    
    /**
     * Determine target processor generation based on budget and DDR type
     */
    private String determineTargetGeneration(int budget, String processorBrand, String ramType) {
        if ("AMD".equals(processorBrand)) {
            if ("DDR5".equals(ramType)) {
                if (budget >= 80000) return "Ryzen 9 7000"; // High-end DDR5
                else if (budget >= 50000) return "Ryzen 7 7000"; // Mid-high DDR5
                else return "Ryzen 5 7000"; // Mid DDR5
            } else if ("DDR4".equals(ramType)) {
                if (budget >= 60000) return "Ryzen 9 5000"; // High-end DDR4
                else if (budget >= 40000) return "Ryzen 7 5000"; // Mid-high DDR4
                else return "Ryzen 5 5000"; // Mid DDR4
            } else { // DDR3
                if (budget >= 40000) return "Ryzen 7 3000"; // High-end DDR3
                else if (budget >= 25000) return "Ryzen 5 3000"; // Mid DDR3
                else return "Ryzen 3 3000"; // Budget DDR3
            }
        } else { // Intel
            if ("DDR5".equals(ramType)) {
                if (budget >= 80000) return "i9 13th/14th Gen"; // High-end DDR5
                else if (budget >= 50000) return "i7 13th/14th Gen"; // Mid-high DDR5
                else return "i5 13th/14th Gen"; // Mid DDR5
            } else if ("DDR4".equals(ramType)) {
                if (budget >= 60000) return "i9 12th Gen"; // High-end DDR4
                else if (budget >= 40000) return "i7 12th Gen"; // Mid-high DDR4
                else return "i5 12th Gen"; // Mid DDR4
            } else { // DDR3
                if (budget >= 40000) return "i7 4th Gen"; // High-end DDR3
                else if (budget >= 25000) return "i5 4th Gen"; // Mid DDR3
                else return "i3 4th Gen"; // Budget DDR3
            }
        }
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
            selectedProcessorType != null ? selectedProcessorType : "Default",
            selectedSocketType != null ? selectedSocketType : "Default", 
            selectedStorageType != null ? selectedStorageType : "Default",
            selectedGpuType != null ? selectedGpuType : "Default");
        UiNotifier.showLong(getContext(), "Smart Auto-build complete! " + preferences + 
            "\nSelected " + result.selectedComponents.size() + " components. " +
            "Performance Score: " + String.format("%.1f/100", result.performanceScore) +
            (validation.isCompatible ? " ✅" : " ⚠️"));
        
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
            UiNotifier.showShort(getContext(), "Please login to save builds");
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
                UiNotifier.showShort(getContext(), "Build saved successfully!");
            })
            .addOnFailureListener(e -> {
                UiNotifier.showShort(getContext(), "Failed to save build: " + e.getMessage());
            });
    }

    // Method to auto-adjust incompatible selections
    private void autoAdjustIncompatibleSelections() {
        StringBuilder adjustmentMessage = new StringBuilder();
        
        // Auto-adjust Intel + DDR3 combinations
        if ("Intel".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
            if (currentBudget >= 50000) {
                // High budget - recommend modern DDR5
                selectedSocketType = "DDR5";
                adjustmentMessage.append("💰 High budget detected (₹").append(currentBudget).append(")\n");
                adjustmentMessage.append("🔄 Auto-adjusted: Intel + DDR5 for modern performance\n");
                adjustmentMessage.append("💡 DDR5 offers better future-proofing and performance\n\n");
            } else if (currentBudget >= 25000) {
                // Mid budget - recommend DDR4
                selectedSocketType = "DDR4";
                adjustmentMessage.append("💰 Mid budget detected (₹").append(currentBudget).append(")\n");
                adjustmentMessage.append("🔄 Auto-adjusted: Intel + DDR4 for balanced performance\n");
                adjustmentMessage.append("💡 DDR4 offers good performance at reasonable cost\n\n");
            } else {
                // Low budget - keep DDR3 but optimize socket
                selectedSocketType = "LGA1155";
                adjustmentMessage.append("💰 Budget build detected (₹").append(currentBudget).append(")\n");
                adjustmentMessage.append("🔄 Auto-adjusted: Intel + LGA1155 for DDR3 compatibility\n");
                adjustmentMessage.append("✅ DDR3 is perfect for budget builds under ₹15,000\n");
                adjustmentMessage.append("💡 Great for: Basic computing, office work, light gaming\n\n");
            }
        }
        
        // Auto-adjust AMD + DDR3 combinations
        if ("AMD".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
            if (currentBudget >= 30000) {
                // Switch to AM4 with DDR4 for better performance
                selectedSocketType = "AM4";
                adjustmentMessage.append("💰 Budget allows for AM4 upgrade (₹").append(currentBudget).append(")\n");
                adjustmentMessage.append("🔄 Auto-adjusted: AMD + AM4 for DDR4 compatibility\n");
                adjustmentMessage.append("💡 AM4 offers much better performance than AM3+\n\n");
            } else {
                // Keep DDR3 but inform about limitations
                adjustmentMessage.append("⚠️ AMD DDR3 builds limited to AM3+ socket\n");
                adjustmentMessage.append("💡 Consider Intel DDR3 for better budget options\n");
                adjustmentMessage.append("💡 Or increase budget for AM4 DDR4 build\n\n");
            }
        }
        
        // Auto-adjust storage type based on budget
        if (currentBudget < 15000 && "SSD".equals(selectedStorageType)) {
            selectedStorageType = "HDD";
            adjustmentMessage.append("💰 Budget optimization: Switched to HDD for more storage\n");
            adjustmentMessage.append("💡 HDD offers more GB per rupee for budget builds\n\n");
        }
        
        // Show comprehensive adjustment message
        if (adjustmentMessage.length() > 0) {
            // Silently apply adjustments without showing dialog
            updateButtonStates();
            updateBuildSummary();
        } else {
            // No adjustments needed
        }
    }
    
    // Method to add DDR3 components (for testing)
    private void addDDR3Components() {
        // Show a dialog explaining DDR3 components and providing options
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("DDR3 Budget Components");
        builder.setMessage("DDR3 components are available for budget builds including:\n\n" +
                         "• Intel 2nd/3rd gen processors (i3, i5, i7)\n" +
                         "• H61/H81 motherboards\n" +
                         "• DDR3 RAM modules\n" +
                         "• Budget graphics cards\n\n" +
                         "These components are perfect for entry-level builds under ₹15,000.");
        
        builder.setPositiveButton("View DDR3 Components", (dialog, which) -> {
            // Open component selection filtered for DDR3 compatible components
            Intent intent = new Intent(requireContext(), ComponentListActivity.class);
            intent.putExtra("category", "MOTHERBOARD"); // Start with motherboards
            intent.putExtra("budget", currentBudget);
            intent.putExtra("processorType", "Intel");
            intent.putExtra("socketType", "DDR3");
            intent.putExtra("storageType", selectedStorageType);
            intent.putExtra("gpuType", selectedGpuType);
            startActivity(intent);
        });
        
        builder.setNeutralButton("Run DDR3 Script", (dialog, which) -> {
            // Show information about running the Python script
            showDDR3ScriptInfo();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    // Method to get DDR3 component recommendations
    private void showDDR3Recommendations() {
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("💡 DDR3 Budget Build Recommendations:\n\n");
        
        if (currentBudget < 10000) {
            recommendations.append("💰 Ultra-Budget Build (Under ₹10,000):\n");
            recommendations.append("• Intel Core i3-2120/3220 (2nd/3rd gen)\n");
            recommendations.append("• H61 motherboard (LGA1155)\n");
            recommendations.append("• 4GB DDR3-1333 RAM\n");
            recommendations.append("• 500GB HDD\n");
            recommendations.append("• Integrated graphics\n");
            recommendations.append("• 300W power supply\n");
            recommendations.append("• Basic case\n\n");
        } else if (currentBudget < 15000) {
            recommendations.append("💰 Budget Build (₹10,000-15,000):\n");
            recommendations.append("• Intel Core i5-2400/3470 (2nd/3rd gen)\n");
            recommendations.append("• H61/H81 motherboard (LGA1155)\n");
            recommendations.append("• 8GB DDR3-1600 RAM\n");
            recommendations.append("• 1TB HDD or 120GB SSD\n");
            recommendations.append("• GT 730 or R7 240 graphics card\n");
            recommendations.append("• 400W power supply\n");
            recommendations.append("• ATX case\n\n");
        } else if (currentBudget < 20000) {
            recommendations.append("💰 Mid-Budget Build (₹15,000-20,000):\n");
            recommendations.append("• Intel Core i5-3570/4670 (3rd/4th gen)\n");
            recommendations.append("• H81/B85 motherboard (LGA1150)\n");
            recommendations.append("• 8GB DDR3-1600 RAM\n");
            recommendations.append("• 240GB SSD + 1TB HDD\n");
            recommendations.append("• GTX 750 Ti or R7 360 graphics card\n");
            recommendations.append("• 450W power supply\n");
            recommendations.append("• Gaming case\n\n");
        }
        
        recommendations.append("✅ DDR3 Advantages:\n");
        recommendations.append("• Very affordable components\n");
        recommendations.append("• Good for basic computing needs\n");
        recommendations.append("• Easy to find used parts\n");
        recommendations.append("• Perfect for entry-level builds under ₹15,000\n\n");
        
        recommendations.append("⚠️ DDR3 Limitations:\n");
        recommendations.append("• Limited to older processors\n");
        recommendations.append("• Max 16GB RAM support\n");
        recommendations.append("• Lower performance than modern DDR4/DDR5\n");
        recommendations.append("• Limited upgrade path\n\n");
        
        recommendations.append("🎯 Best Use Cases:\n");
        recommendations.append("• Office computers\n");
        recommendations.append("• Student builds\n");
        recommendations.append("• Basic web browsing\n");
        recommendations.append("• Light document editing\n");
        recommendations.append("• Entry-level gaming\n");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("DDR3 Component Recommendations");
        builder.setMessage(recommendations.toString());
        builder.setPositiveButton("Build DDR3 PC", (dialog, which) -> {
            // Auto-select DDR3 components and build
            selectedProcessorType = "Intel";
            selectedSocketType = "DDR3";
            selectedStorageType = "HDD"; // Better for budget
            selectedGpuType = "NVIDIA"; // Better budget options
            
            // Update UI
            updateButtonStates();
            updateBuildSummary();
            
            // Auto-build with DDR3 preferences
            autoBuildPCWithinBudget();
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void showDDR3ScriptInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("DDR3 Components Script");
        builder.setMessage("To add DDR3 components to the database:\n\n" +
                         "1. Run the Python script: add_ddr3_components.py\n" +
                         "2. Ensure serviceAccountKey.json is in the project root\n" +
                         "3. The script will add:\n" +
                         "   • Intel H61/H81 motherboards\n" +
                         "   • 2nd/3rd gen Intel processors\n" +
                         "   • DDR3 RAM modules\n" +
                         "   • Budget graphics cards\n\n" +
                         "After running the script, DDR3 components will be available in the app.");
        
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    // Method to show a temporary status message
    private void showTemporaryStatus(String message, int duration) {
        UiNotifier.show(getContext(), message, duration);
    }
    
    // Method to show a comprehensive temporary status message with all selections
    private void showComprehensiveStatus() {
        // Removed status display to prevent annoying overlays
        // Status information is now shown in the build summary instead
    }
    
    // Method to show comprehensive compatibility summary
    private void showComprehensiveCompatibilitySummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("🔍 Compatibility Summary for Your Build\n\n");
        
        // Current selections
        summary.append("📋 Current Selections:\n");
        summary.append("• Processor: ").append(selectedProcessorType != null ? selectedProcessorType : "Not selected").append("\n");
        summary.append("• DDR Type: ").append(selectedSocketType != null ? selectedSocketType : "Not selected").append("\n");
        summary.append("• Storage: ").append(selectedStorageType != null ? selectedStorageType : "Not selected").append("\n");
        summary.append("• GPU: ").append(selectedGpuType != null ? selectedGpuType : "Not selected").append("\n");
        summary.append("• Budget: ₹").append(String.format("%,d", currentBudget)).append("\n\n");
        
        // Compatibility analysis
        summary.append("✅ Compatibility Analysis:\n");
        
        if (selectedProcessorType != null && selectedSocketType != null) {
            if ("Intel".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
                summary.append("• Intel + DDR3: ✅ Compatible for budget builds\n");
                summary.append("• Performance: ⚠️ Limited but sufficient for basic tasks\n");
                summary.append("• Upgrade Path: ❌ Limited future options\n");
                summary.append("• Cost: 💰 Very affordable\n\n");
            } else if ("Intel".equals(selectedProcessorType) && "DDR4".equals(selectedSocketType)) {
                summary.append("• Intel + DDR4: ✅ Excellent compatibility\n");
                summary.append("• Performance: ⭐ Good for gaming and work\n");
                summary.append("• Upgrade Path: ✅ Moderate future options\n");
                summary.append("• Cost: 💰 Good value for money\n\n");
            } else if ("Intel".equals(selectedProcessorType) && "DDR5".equals(selectedSocketType)) {
                summary.append("• Intel + DDR5: ✅ Cutting-edge compatibility\n");
                summary.append("• Performance: ⭐⭐⭐ Maximum performance\n");
                summary.append("• Upgrade Path: ✅ Excellent future options\n");
                summary.append("• Cost: 💰 Premium pricing\n\n");
            } else if ("AMD".equals(selectedProcessorType) && "DDR3".equals(selectedSocketType)) {
                summary.append("• AMD + DDR3: ⚠️ Limited compatibility\n");
                summary.append("• Performance: ❌ Restricted by older socket\n");
                summary.append("• Upgrade Path: ❌ Very limited options\n");
                summary.append("• Cost: 💰 Affordable but not recommended\n\n");
            } else if ("AMD".equals(selectedProcessorType) && "DDR4".equals(selectedSocketType)) {
                summary.append("• AMD + DDR4: ✅ Excellent compatibility\n");
                summary.append("• Performance: ⭐⭐⭐ Great performance\n");
                summary.append("• Upgrade Path: ✅ Good future options\n");
                summary.append("• Cost: 💰 Excellent value for money\n\n");
            } else if ("AMD".equals(selectedProcessorType) && "DDR5".equals(selectedSocketType)) {
                summary.append("• AMD + DDR5: ✅ Cutting-edge compatibility\n");
                summary.append("• Performance: ⭐⭐⭐ Maximum performance\n");
                summary.append("• Upgrade Path: ✅ Excellent future options\n");
                summary.append("• Cost: 💰 Premium pricing\n\n");
            }
        }
        
        // Budget recommendations
        summary.append("💰 Budget Recommendations:\n");
        if (currentBudget < 15000) {
            summary.append("• DDR3 components recommended\n");
            summary.append("• Focus on essential components\n");
            summary.append("• Consider used parts for better value\n\n");
        } else if (currentBudget < 30000) {
            summary.append("• DDR4 components recommended\n");
            summary.append("• Good balance of performance and value\n");
            summary.append("• Modern components with upgrade path\n\n");
        } else if (currentBudget < 60000) {
            summary.append("• DDR4/DDR5 components recommended\n");
            summary.append("• High performance builds\n");
            summary.append("• Future-proof components\n\n");
        } else {
            summary.append("• DDR5 components recommended\n");
            summary.append("• Maximum performance builds\n");
            summary.append("• Cutting-edge technology\n\n");
        }
        
        // Action items
        summary.append("🎯 Recommended Actions:\n");
        if (selectedProcessorType == null) {
            summary.append("• Select a processor type (Intel/AMD)\n");
        }
        if (selectedSocketType == null) {
            summary.append("• Select DDR type based on budget\n");
        }
        if (selectedComponents.isEmpty()) {
            summary.append("• Use Auto-Build to select components\n");
        }
        
        summary.append("• Check compatibility warnings above\n");
        summary.append("• Consider your specific use case\n");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("🔍 Compatibility Summary");
        builder.setMessage(summary.toString());
        builder.setPositiveButton("Auto-Build PC", (dialog, which) -> {
            if (selectedComponents.isEmpty()) {
                autoBuildPCWithinBudget();
            } else {
                UiNotifier.showShort(getContext(), "Components already selected. Use 'Build PC' button.");
            }
        });
        builder.setNegativeButton("Close", null);
        builder.setNeutralButton("DDR3 Guide", (dialog, which) -> {
            showDDR3Recommendations();
        });
        builder.show();
    }
}



