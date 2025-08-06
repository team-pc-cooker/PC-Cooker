package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;
import com.app.pccooker.adapters.ChatAdapter;
import com.app.pccooker.models.ChatMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AIAssistantFragment extends Fragment {

    private EditText userInputEditText;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private LinearLayout chatContainer;
    private ScrollView scrollView;
    
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;
    
    // Component compatibility rules
    private Map<String, List<String>> compatibilityRules = new HashMap<>();
    private Map<String, List<String>> budgetCategories = new HashMap<>();

    private Map<String, ComponentModel> selectedComponents = new HashMap<>();
    private double userBudget = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);
        
        db = FirebaseFirestore.getInstance();
        initializeViews(view);
        setupCompatibilityRules();
        setupBudgetCategories();
        setupChatAdapter();
        showWelcomeMessage();
        
        return view;
    }

    private void initializeViews(View view) {
        userInputEditText = view.findViewById(R.id.userInputEditText);
        sendButton = view.findViewById(R.id.sendButton);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatContainer = view.findViewById(R.id.chatContainer);
        scrollView = view.findViewById(R.id.scrollView);
        
        sendButton.setOnClickListener(v -> processUserInput());
        
        // Enter key to send
        userInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            processUserInput();
            return true;
        });
    }

    private void setupCompatibilityRules() {
        // CPU Socket Compatibility
        compatibilityRules.put("LGA1200", List.of("Intel 10th Gen", "Intel 11th Gen"));
        compatibilityRules.put("LGA1700", List.of("Intel 12th Gen", "Intel 13th Gen", "Intel 14th Gen"));
        compatibilityRules.put("AM4", List.of("AMD Ryzen 3000", "AMD Ryzen 5000"));
        compatibilityRules.put("AM5", List.of("AMD Ryzen 7000"));
        
        // RAM Compatibility
        compatibilityRules.put("DDR4", List.of("DDR4-2133", "DDR4-2400", "DDR4-2666", "DDR4-3000", "DDR4-3200", "DDR4-3600"));
        compatibilityRules.put("DDR5", List.of("DDR5-4800", "DDR5-5200", "DDR5-5600", "DDR5-6000"));
        
        // Motherboard Chipset Compatibility
        compatibilityRules.put("H610", List.of("Intel 12th Gen", "Intel 13th Gen"));
        compatibilityRules.put("B660", List.of("Intel 12th Gen", "Intel 13th Gen"));
        compatibilityRules.put("Z690", List.of("Intel 12th Gen", "Intel 13th Gen"));
        compatibilityRules.put("B550", List.of("AMD Ryzen 3000", "AMD Ryzen 5000"));
        compatibilityRules.put("X570", List.of("AMD Ryzen 3000", "AMD Ryzen 5000"));
        compatibilityRules.put("B650", List.of("AMD Ryzen 7000"));
        compatibilityRules.put("X670", List.of("AMD Ryzen 7000"));
    }

    private void setupBudgetCategories() {
        budgetCategories.put("budget", List.of("₹20,000 - ₹50,000", "Basic gaming", "Office work", "Web browsing"));
        budgetCategories.put("mid_range", List.of("₹50,000 - ₹1,00,000", "Gaming", "Content creation", "Streaming"));
        budgetCategories.put("high_end", List.of("₹1,00,000 - ₹2,00,000", "High-end gaming", "Professional work", "4K gaming"));
        budgetCategories.put("enthusiast", List.of("₹2,00,000+", "Extreme gaming", "Professional editing", "Workstation"));
    }

    private void setupChatAdapter() {
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void showWelcomeMessage() {
        String welcomeMessage = "👋 Hi! I'm your PC Building Assistant. I can help you:\n\n" +
                "• Build a PC within your budget\n" +
                "• Check component compatibility\n" +
                "• Recommend parts for specific use cases\n" +
                "• Answer technical questions\n\n" +
                "Just tell me your budget and requirements!";
        
        addMessage(welcomeMessage, false);
    }

    private void processUserInput() {
        String userInput = userInputEditText.getText().toString().trim();
        if (userInput.isEmpty()) return;
        
        addMessage(userInput, true);
        userInputEditText.setText("");
        
        // Process the input and generate response
        String response = generateAIResponse(userInput);
        addMessage(response, false);
    }

    private String generateAIResponse(String userInput) {
        String input = userInput.toLowerCase();
        
        // Check for specific AMD requests first
        if (input.contains("amd") && (input.contains("3") || input.contains("ryzen 3"))) {
            return generateSpecificAMDRecommendation("3");
        }
        if (input.contains("amd") && (input.contains("5") || input.contains("ryzen 5"))) {
            return generateSpecificAMDRecommendation("5");
        }
        if (input.contains("amd") && (input.contains("7") || input.contains("ryzen 7"))) {
            return generateSpecificAMDRecommendation("7");
        }
        if (input.contains("amd") && (input.contains("9") || input.contains("ryzen 9"))) {
            return generateSpecificAMDRecommendation("9");
        }
        
        // Check for specific Intel requests
        if (input.contains("intel") && (input.contains("i3") || input.contains("core i3"))) {
            return generateSpecificIntelRecommendation("i3");
        }
        if (input.contains("intel") && (input.contains("i5") || input.contains("core i5"))) {
            return generateSpecificIntelRecommendation("i5");
        }
        if (input.contains("intel") && (input.contains("i7") || input.contains("core i7"))) {
            return generateSpecificIntelRecommendation("i7");
        }
        if (input.contains("intel") && (input.contains("i9") || input.contains("core i9"))) {
            return generateSpecificIntelRecommendation("i9");
        }
        
        // Budget-based recommendations
        if (input.contains("budget") || input.contains("₹") || input.contains("rs") || input.contains("rupee") || 
            input.contains("build") || input.contains("pc") || input.contains("computer")) {
            return generateBudgetRecommendation(input);
        }
        
        // Specific component requests
        if (input.contains("processor") || input.contains("cpu")) {
            return generateProcessorRecommendation(input);
        }
        
        if (input.contains("motherboard") || input.contains("mobo")) {
            return generateMotherboardRecommendation(input);
        }
        
        if (input.contains("ram") || input.contains("memory")) {
            return generateRAMRecommendation(input);
        }
        
        if (input.contains("graphics") || input.contains("gpu") || input.contains("card")) {
            return generateGPURecommendation(input);
        }
        
        // Gaming PC requests
        if (input.contains("gaming") || input.contains("game")) {
            return generateGamingPCRecommendation(input);
        }
        
        // Compatibility questions
        if (input.contains("compatible") || input.contains("compatibility")) {
            return generateCompatibilityResponse(input);
        }
        
        // General help
        return generateGeneralResponse(input);
    }

    private String generateBudgetRecommendation(String input) {
        // Extract budget amount
        double budget = extractBudgetAmount(input);
        
        if (budget <= 0) {
            return "Please specify your budget amount (e.g., ₹50,000 or 50k). I can help you build a PC within your budget!";
        }
        
        // Search for actual components from database
        searchComponentsForBudget(budget);
        
        StringBuilder response = new StringBuilder();
        response.append("💰 **Budget PC Build - ₹").append(String.format("%.0f", budget)).append("**\n\n");
        
        if (budget < 30000) {
            response.append("⚠️ **Budget Too Low**\n\n");
            response.append("For ₹").append(String.format("%.0f", budget)).append(", you can only build a very basic system.\n");
            response.append("**Recommendation:** Increase budget to at least ₹40,000 for a decent gaming PC.\n\n");
            response.append("I'm searching for available components within your budget...");
            
        } else {
            response.append("**Searching for budget-friendly components...**\n\n");
            response.append("I'll find the best components within your ₹").append(String.format("%.0f", budget)).append(" budget.\n");
            response.append("This will include:\n");
            response.append("• Processor (CPU)\n");
            response.append("• Motherboard\n");
            response.append("• RAM\n");
            response.append("• Storage\n");
            response.append("• Power Supply\n");
            response.append("• Cabinet\n");
            response.append("• Graphics Card (optional)\n\n");
            response.append("Please wait while I search for compatible components...");
        }
        
        return response.toString();
    }
    
    private void searchComponentsForBudget(double budget) {
        db.collection("components")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<ComponentModel> allComponents = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ComponentModel component = document.toObject(ComponentModel.class);
                    if (component != null) {
                        component.setId(document.getId());
                        allComponents.add(component);
                    }
                }
                
                // Build PC within budget using actual components
                buildPCWithActualComponents(allComponents, budget);
            })
            .addOnFailureListener(e -> {
                addMessage("Sorry, I couldn't search for components. Please try again.", false);
            });
    }
    
    private void buildPCWithActualComponents(List<ComponentModel> allComponents, double budget) {
        Map<String, ComponentModel> selectedComponents = new HashMap<>();
        double totalCost = 0;
        double remainingBudget = budget;
        
        StringBuilder buildSummary = new StringBuilder();
        buildSummary.append("🔧 **PC Build for ₹").append(String.format("%.0f", budget)).append("**\n\n");
        
        // Step 1: Select the CHEAPEST motherboard first (foundation)
        ComponentModel selectedMobo = selectCheapestComponentFromDatabase(allComponents, "MOTHERBOARD", remainingBudget);
        if (selectedMobo != null) {
            selectedComponents.put("MOTHERBOARD", selectedMobo);
            totalCost += selectedMobo.getPrice();
            remainingBudget -= selectedMobo.getPrice();
            buildSummary.append("**Motherboard:** ").append(selectedMobo.getName()).append(" - ₹").append(String.format("%.0f", selectedMobo.getPrice())).append("\n");
        }
        
        // Step 2: Select CHEAPEST compatible CPU for the selected motherboard
        ComponentModel selectedCPU = selectCheapestCompatibleCPUFromDatabase(allComponents, selectedMobo, remainingBudget);
        if (selectedCPU != null) {
            selectedComponents.put("PROCESSOR", selectedCPU);
            totalCost += selectedCPU.getPrice();
            remainingBudget -= selectedCPU.getPrice();
            buildSummary.append("**CPU:** ").append(selectedCPU.getName()).append(" - ₹").append(String.format("%.0f", selectedCPU.getPrice())).append("\n");
        }
        
        // Step 3: Select CHEAPEST compatible RAM for the motherboard
        ComponentModel selectedRAM = selectCheapestCompatibleRAMFromDatabase(allComponents, selectedMobo, remainingBudget);
        if (selectedRAM != null) {
            selectedComponents.put("RAM", selectedRAM);
            totalCost += selectedRAM.getPrice();
            remainingBudget -= selectedRAM.getPrice();
            buildSummary.append("**RAM:** ").append(selectedRAM.getName()).append(" - ₹").append(String.format("%.0f", selectedRAM.getPrice())).append("\n");
        }
        
        // Step 4: Select CHEAPEST storage
        ComponentModel selectedStorage = selectCheapestComponentFromDatabase(allComponents, "STORAGE", remainingBudget);
        if (selectedStorage != null) {
            selectedComponents.put("STORAGE", selectedStorage);
            totalCost += selectedStorage.getPrice();
            remainingBudget -= selectedStorage.getPrice();
            buildSummary.append("**Storage:** ").append(selectedStorage.getName()).append(" - ₹").append(String.format("%.0f", selectedStorage.getPrice())).append("\n");
        }
        
        // Step 5: Select CHEAPEST PSU
        ComponentModel selectedPSU = selectCheapestComponentFromDatabase(allComponents, "POWER SUPPLY", remainingBudget);
        if (selectedPSU != null) {
            selectedComponents.put("POWER SUPPLY", selectedPSU);
            totalCost += selectedPSU.getPrice();
            remainingBudget -= selectedPSU.getPrice();
            buildSummary.append("**PSU:** ").append(selectedPSU.getName()).append(" - ₹").append(String.format("%.0f", selectedPSU.getPrice())).append("\n");
        }
        
        // Step 6: Select CHEAPEST case
        ComponentModel selectedCase = selectCheapestComponentFromDatabase(allComponents, "CABINET", remainingBudget);
        if (selectedCase != null) {
            selectedComponents.put("CABINET", selectedCase);
            totalCost += selectedCase.getPrice();
            remainingBudget -= selectedCase.getPrice();
            buildSummary.append("**Case:** ").append(selectedCase.getName()).append(" - ₹").append(String.format("%.0f", selectedCase.getPrice())).append("\n");
        }
        
        // Step 7: If budget allows, add CHEAPEST GPU (optional)
        if (remainingBudget > 0) {
            ComponentModel selectedGPU = selectCheapestComponentFromDatabase(allComponents, "GRAPHIC CARD", remainingBudget);
            if (selectedGPU != null) {
                selectedComponents.put("GRAPHIC CARD", selectedGPU);
                totalCost += selectedGPU.getPrice();
                remainingBudget -= selectedGPU.getPrice();
                buildSummary.append("**GPU:** ").append(selectedGPU.getName()).append(" - ₹").append(String.format("%.0f", selectedGPU.getPrice())).append("\n");
            }
        }
        
        buildSummary.append("\n**Total Cost:** ₹").append(String.format("%.0f", totalCost));
        buildSummary.append("\n**Remaining Budget:** ₹").append(String.format("%.0f", remainingBudget));
        
        if (totalCost > 0) {
            buildSummary.append("\n\n✅ **Build Complete!**\n");
            buildSummary.append("Components have been selected from our database.\n");
            buildSummary.append("Click 'Build This PC' to add these components to your cart.");
            
            // Store the selected components for later use
            storeSelectedComponents(selectedComponents, budget);
        } else {
            buildSummary.append("\n\n❌ **No suitable components found**\n");
            buildSummary.append("Please try increasing your budget or contact support.");
        }
        
        addMessage(buildSummary.toString(), false);
    }
    
    private ComponentModel selectCheapestComponentFromDatabase(List<ComponentModel> components, String category, double maxBudget) {
        List<ComponentModel> categoryComponents = components.stream()
            .filter(c -> c.getCategory().equals(category) && c.getPrice() <= maxBudget)
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice())) // Sort by price ASC (cheapest first)
            .collect(Collectors.toList());
        
        return categoryComponents.isEmpty() ? null : categoryComponents.get(0);
    }
    
    private ComponentModel selectCheapestCompatibleCPUFromDatabase(List<ComponentModel> components, ComponentModel mobo, double maxBudget) {
        if (mobo == null) {
            return selectCheapestComponentFromDatabase(components, "PROCESSOR", maxBudget);
        }
        
        String moboSocket = mobo.getSpecifications().get("Socket");
        if (moboSocket == null) {
            return selectCheapestComponentFromDatabase(components, "PROCESSOR", maxBudget);
        }
        
        List<ComponentModel> compatibleCPUs = components.stream()
            .filter(c -> c.getCategory().equals("PROCESSOR") &&
                        c.getPrice() <= maxBudget &&
                        c.getSpecifications().get("Socket") != null &&
                        c.getSpecifications().get("Socket").contains(moboSocket))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice())) // Sort by price ASC (cheapest first)
            .collect(Collectors.toList());
        
        return compatibleCPUs.isEmpty() ? selectCheapestComponentFromDatabase(components, "PROCESSOR", maxBudget) : compatibleCPUs.get(0);
    }
    
    private ComponentModel selectCheapestCompatibleRAMFromDatabase(List<ComponentModel> components, ComponentModel mobo, double maxBudget) {
        if (mobo == null) {
            return selectCheapestComponentFromDatabase(components, "RAM", maxBudget);
        }
        
        String moboSpec = mobo.getSpecifications().get("RAM") != null ?
            mobo.getSpecifications().get("RAM").toLowerCase() : "ddr4";
        String ramType = moboSpec.contains("ddr4") ? "DDR4" : moboSpec.contains("ddr5") ? "DDR5" : "DDR4";
        
        List<ComponentModel> compatibleRAM = components.stream()
            .filter(c -> c.getCategory().equals("RAM") &&
                        c.getPrice() <= maxBudget &&
                        c.getSpecifications().get("Type") != null &&
                        c.getSpecifications().get("Type").contains(ramType))
            .sorted((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice())) // Sort by price ASC (cheapest first)
            .collect(Collectors.toList());
        
        return compatibleRAM.isEmpty() ? selectCheapestComponentFromDatabase(components, "RAM", maxBudget) : compatibleRAM.get(0);
    }
    
    private void storeSelectedComponents(Map<String, ComponentModel> selectedComponents, double budget) {
        // Store the selected components for later use when user clicks "Build This PC"
        this.selectedComponents = selectedComponents;
        this.userBudget = budget;
    }

    private double extractBudgetAmount(String input) {
        // Remove all spaces and convert to lowercase
        String cleanInput = input.replaceAll("\\s+", "").toLowerCase();
        
        // Patterns to match different budget formats
        String[] patterns = {
            "₹(\\d+(?:\\.\\d+)?)(?:k|thousand)?",  // ₹50k, ₹50.5k, ₹50000
            "rs\\.?(\\d+(?:\\.\\d+)?)(?:k|thousand)?",  // rs50k, rs.50k, rs50000
            "rupees?(\\d+(?:\\.\\d+)?)(?:k|thousand)?",  // rupees50k, rupee50k
            "(\\d+(?:\\.\\d+)?)k",  // 50k, 50.5k
            "(\\d+(?:\\.\\d+)?)thousand",  // 50thousand
            "(\\d+(?:\\.\\d+)?)lakh",  // 1lakh = 100000
            "(\\d+(?:\\.\\d+)?)lac",  // 1lac = 100000
            "(\\d{4,})"  // Any 4+ digit number (assuming it's in rupees)
        };
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(cleanInput);
            
            if (m.find()) {
                try {
                    double amount = Double.parseDouble(m.group(1));
                    
                    // Convert based on suffix
                    if (cleanInput.contains("k") || cleanInput.contains("thousand")) {
                        amount *= 1000;
                    } else if (cleanInput.contains("lakh") || cleanInput.contains("lac")) {
                        amount *= 100000;
                    }
                    
                    // Validate reasonable range
                    if (amount >= 1000 && amount <= 1000000) {
                        return amount;
                    }
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }
        
        return 0; // No valid budget found
    }

    private String generateProcessorRecommendation(String input) {
        StringBuilder response = new StringBuilder();
        response.append("🖥️ **Processor Recommendations**\n\n");
        
        if (input.contains("budget") || input.contains("cheap")) {
            response.append("**Budget Options:**\n");
            response.append("• Intel Core i3-12100F (~₹8,000)\n");
            response.append("• AMD Ryzen 5 5600G (~₹12,000)\n");
            response.append("• Intel Core i5-10400F (~₹10,000)\n\n");
            response.append("**Best Value:** Intel Core i3-12100F\n");
            response.append("• 4 cores, 8 threads\n");
            response.append("• Great for gaming and office work\n");
            response.append("• Compatible with LGA1700 motherboards\n");
            
        } else if (input.contains("gaming") || input.contains("mid")) {
            response.append("**Gaming Options:**\n");
            response.append("• Intel Core i5-12400F (~₹15,000)\n");
            response.append("• AMD Ryzen 5 5600X (~₹18,000)\n");
            response.append("• Intel Core i5-12600K (~₹22,000)\n\n");
            response.append("**Best Value:** Intel Core i5-12400F\n");
            response.append("• 6 cores, 12 threads\n");
            response.append("• Excellent gaming performance\n");
            response.append("• Great price-to-performance ratio\n");
            
        } else if (input.contains("high") || input.contains("premium")) {
            response.append("**High-end Options:**\n");
            response.append("• Intel Core i7-12700K (~₹35,000)\n");
            response.append("• AMD Ryzen 7 5800X3D (~₹40,000)\n");
            response.append("• Intel Core i9-13900K (~₹55,000)\n\n");
            response.append("**Best Gaming:** AMD Ryzen 7 5800X3D\n");
            response.append("• 8 cores, 16 threads\n");
            response.append("• 3D V-Cache for gaming\n");
            response.append("• Excellent gaming performance\n");
        }
        
        return response.toString();
    }

    private String generateCompatibilityResponse(String input) {
        StringBuilder response = new StringBuilder();
        response.append("🔧 **Component Compatibility Guide**\n\n");
        
        if (input.contains("motherboard") || input.contains("cpu")) {
            response.append("**CPU-Motherboard Compatibility:**\n\n");
            response.append("**Intel Sockets:**\n");
            response.append("• LGA1200: 10th & 11th Gen Intel\n");
            response.append("• LGA1700: 12th, 13th & 14th Gen Intel\n\n");
            response.append("**AMD Sockets:**\n");
            response.append("• AM4: Ryzen 3000 & 5000 series\n");
            response.append("• AM5: Ryzen 7000 series\n\n");
            response.append("**Important:** Always check motherboard chipset compatibility!\n");
            
        } else if (input.contains("ram") || input.contains("memory")) {
            response.append("**RAM Compatibility:**\n\n");
            response.append("**DDR4:**\n");
            response.append("• Compatible with Intel 10th-12th Gen\n");
            response.append("• Compatible with AMD Ryzen 3000-5000\n");
            response.append("• Speeds: 2133-3600 MHz\n\n");
            response.append("**DDR5:**\n");
            response.append("• Compatible with Intel 12th-14th Gen\n");
            response.append("• Compatible with AMD Ryzen 7000\n");
            response.append("• Speeds: 4800-6000+ MHz\n\n");
            response.append("**Note:** DDR4 and DDR5 are NOT compatible!\n");
        }
        
        return response.toString();
    }

    private String generateGeneralResponse(String input) {
        return "I understand you're asking about: \"" + input + "\"\n\n" +
                "I can help you with:\n" +
                "• Budget PC builds\n" +
                "• Component recommendations\n" +
                "• Compatibility checking\n" +
                "• Gaming PC setups\n" +
                "• Performance comparisons\n\n" +
                "Please be more specific about your requirements and budget!";
    }

    private void addMessage(String message, boolean isUser) {
        ChatMessage chatMessage = new ChatMessage(message, isUser);
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        
        // Scroll to bottom
        chatRecyclerView.post(() -> chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1));
    }

    // Helper methods for other recommendations
    private String generateMotherboardRecommendation(String input) {
        return "🔌 **Motherboard Recommendations**\n\n" +
                "**Budget:** H610 (Intel) or A520 (AMD)\n" +
                "**Mid-range:** B660 (Intel) or B550 (AMD)\n" +
                "**High-end:** Z690 (Intel) or X570 (AMD)\n\n" +
                "Always check CPU socket compatibility!";
    }

    private String generateRAMRecommendation(String input) {
        return "💾 **RAM Recommendations**\n\n" +
                "**Budget:** 8GB DDR4-3200\n" +
                "**Gaming:** 16GB DDR4-3600\n" +
                "**High-end:** 32GB DDR5-5600\n\n" +
                "Check motherboard memory compatibility!";
    }

    private String generateGPURecommendation(String input) {
        return "🎮 **Graphics Card Recommendations**\n\n" +
                "**Budget:** GTX 1650 or RX 6500 XT\n" +
                "**Mid-range:** RTX 3060 or RX 6600\n" +
                "**High-end:** RTX 4070 or RX 7700 XT\n\n" +
                "Consider your monitor resolution and refresh rate!";
    }

    private String generateGamingPCRecommendation(String input) {
        return "🎮 **Gaming PC Recommendations**\n\n" +
                "**1080p Gaming:** RTX 3060 + i5-12400F\n" +
                "**1440p Gaming:** RTX 4070 + i7-12700K\n" +
                "**4K Gaming:** RTX 4080 + i9-13900K\n\n" +
                "What's your target resolution and budget?";
    }
    
    private String generateSpecificAMDRecommendation(String series) {
        StringBuilder response = new StringBuilder();
        response.append("🖥️ **AMD Ryzen ").append(series).append(" Recommendations**\n\n");
        
        switch (series) {
            case "3":
                response.append("**AMD Ryzen 3 Series (Budget Gaming):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Ryzen 3 3200G** (~₹8,500)\n");
                response.append("  - 4 cores, 4 threads\n");
                response.append("  - Integrated Vega 8 graphics\n");
                response.append("  - Perfect for budget builds\n");
                response.append("  - Socket: AM4\n\n");
                response.append("• **Ryzen 3 3300X** (~₹12,000)\n");
                response.append("  - 4 cores, 8 threads\n");
                response.append("  - Better gaming performance\n");
                response.append("  - Requires dedicated GPU\n");
                response.append("  - Socket: AM4\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• B450, B550, X470, X570 (AM4)\n");
                response.append("• Recommended: MSI B550M Pro-VDH\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3200 or DDR4-3600\n");
                response.append("• 8GB or 16GB recommended\n");
                break;
                
            case "5":
                response.append("**AMD Ryzen 5 Series (Mid-range Gaming):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Ryzen 5 5600G** (~₹15,000)\n");
                response.append("  - 6 cores, 12 threads\n");
                response.append("  - Integrated Vega 7 graphics\n");
                response.append("  - Great for gaming without GPU\n");
                response.append("  - Socket: AM4\n\n");
                response.append("• **Ryzen 5 5600X** (~₹18,000)\n");
                response.append("  - 6 cores, 12 threads\n");
                response.append("  - Excellent gaming performance\n");
                response.append("  - Requires dedicated GPU\n");
                response.append("  - Socket: AM4\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• B550, X570 (AM4)\n");
                response.append("• Recommended: ASUS ROG B550-F\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3600 (sweet spot)\n");
                response.append("• 16GB recommended\n");
                break;
                
            case "7":
                response.append("**AMD Ryzen 7 Series (High-end Gaming):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Ryzen 7 5800X** (~₹28,000)\n");
                response.append("  - 8 cores, 16 threads\n");
                response.append("  - Excellent gaming performance\n");
                response.append("  - Great for streaming\n");
                response.append("  - Socket: AM4\n\n");
                response.append("• **Ryzen 7 5800X3D** (~₹40,000)\n");
                response.append("  - 8 cores, 16 threads\n");
                response.append("  - 3D V-Cache for gaming\n");
                response.append("  - Best gaming CPU in AM4\n");
                response.append("  - Socket: AM4\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• X570 (recommended for overclocking)\n");
                response.append("• B550 (good for stock performance)\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3600 or DDR4-4000\n");
                response.append("• 32GB recommended\n");
                break;
                
            case "9":
                response.append("**AMD Ryzen 9 Series (Enthusiast/Workstation):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Ryzen 9 5900X** (~₹45,000)\n");
                response.append("  - 12 cores, 24 threads\n");
                response.append("  - Excellent for content creation\n");
                response.append("  - Great gaming performance\n");
                response.append("  - Socket: AM4\n\n");
                response.append("• **Ryzen 9 5950X** (~₹65,000)\n");
                response.append("  - 16 cores, 32 threads\n");
                response.append("  - Ultimate workstation CPU\n");
                response.append("  - Professional workloads\n");
                response.append("  - Socket: AM4\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• X570 (required for full features)\n");
                response.append("• High-end VRM recommended\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3600 or DDR4-4000\n");
                response.append("• 64GB recommended\n");
                break;
        }
        
        return response.toString();
    }
    
    private String generateSpecificIntelRecommendation(String series) {
        StringBuilder response = new StringBuilder();
        response.append("🖥️ **Intel Core ").append(series.toUpperCase()).append(" Recommendations**\n\n");
        
        switch (series) {
            case "i3":
                response.append("**Intel Core i3 Series (Budget Gaming):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Core i3-12100F** (~₹8,000)\n");
                response.append("  - 4 cores, 8 threads\n");
                response.append("  - Great budget gaming CPU\n");
                response.append("  - Socket: LGA1700\n");
                response.append("  - Requires dedicated GPU\n\n");
                response.append("• **Core i3-13100F** (~₹10,000)\n");
                response.append("  - 4 cores, 8 threads\n");
                response.append("  - 13th Gen improvements\n");
                response.append("  - Socket: LGA1700\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• H610, B660, Z690 (LGA1700)\n");
                response.append("• Recommended: MSI B660M Pro\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3200 or DDR5-4800\n");
                response.append("• 8GB or 16GB recommended\n");
                break;
                
            case "i5":
                response.append("**Intel Core i5 Series (Mid-range Gaming):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Core i5-12400F** (~₹15,000)\n");
                response.append("  - 6 cores, 12 threads\n");
                response.append("  - Excellent gaming performance\n");
                response.append("  - Socket: LGA1700\n");
                response.append("  - Requires dedicated GPU\n\n");
                response.append("• **Core i5-12600K** (~₹22,000)\n");
                response.append("  - 6P + 4E cores, 16 threads\n");
                response.append("  - Unlocked for overclocking\n");
                response.append("  - Socket: LGA1700\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• B660, Z690 (LGA1700)\n");
                response.append("• Recommended: ASUS ROG B660-F\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3600 or DDR5-5600\n");
                response.append("• 16GB recommended\n");
                break;
                
            case "i7":
                response.append("**Intel Core i7 Series (High-end Gaming):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Core i7-12700K** (~₹35,000)\n");
                response.append("  - 8P + 4E cores, 20 threads\n");
                response.append("  - Excellent gaming performance\n");
                response.append("  - Socket: LGA1700\n");
                response.append("  - Unlocked for overclocking\n\n");
                response.append("• **Core i7-13700K** (~₹40,000)\n");
                response.append("  - 8P + 8E cores, 24 threads\n");
                response.append("  - 13th Gen improvements\n");
                response.append("  - Socket: LGA1700\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• Z690, Z790 (LGA1700)\n");
                response.append("• High-end VRM recommended\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-3600 or DDR5-6000\n");
                response.append("• 32GB recommended\n");
                break;
                
            case "i9":
                response.append("**Intel Core i9 Series (Enthusiast/Workstation):**\n\n");
                response.append("**Best Options:**\n");
                response.append("• **Core i9-12900K** (~₹55,000)\n");
                response.append("  - 8P + 8E cores, 24 threads\n");
                response.append("  - Ultimate gaming CPU\n");
                response.append("  - Socket: LGA1700\n");
                response.append("  - Unlocked for overclocking\n\n");
                response.append("• **Core i9-13900K** (~₹65,000)\n");
                response.append("  - 8P + 16E cores, 32 threads\n");
                response.append("  - 13th Gen improvements\n");
                response.append("  - Socket: LGA1700\n\n");
                response.append("**Compatible Motherboards:**\n");
                response.append("• Z690, Z790 (LGA1700)\n");
                response.append("• Premium VRM required\n\n");
                response.append("**Compatible RAM:**\n");
                response.append("• DDR4-4000 or DDR5-6400\n");
                response.append("• 64GB recommended\n");
                break;
        }
        
        return response.toString();
    }


} 