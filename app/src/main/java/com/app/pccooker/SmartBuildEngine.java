package com.app.pccooker;

import android.util.Log;
import java.util.*;
import java.util.stream.Collectors;
import com.app.pccooker.models.ComponentModel;

/**
 * Enhanced Auto-Build Engine with Smart Budget Allocation and Compatibility Suggestions
 */
public class SmartBuildEngine {
    
    private static final String TAG = "SmartBuildEngine";
    
    // Budget allocation percentages for different build types
    private static final Map<String, Map<String, Double>> BUDGET_ALLOCATIONS = new HashMap<String, Map<String, Double>>() {{
        put("GAMING", new HashMap<String, Double>() {{
            put("PROCESSOR", 0.25);      // 25%
            put("GRAPHIC CARD", 0.35);   // 35%
            put("MOTHERBOARD", 0.12);    // 12%
            put("RAM", 0.10);            // 10%
            put("STORAGE", 0.08);        // 8%
            put("POWER SUPPLY", 0.06);   // 6%
            put("CASE", 0.04);           // 4%
        }});
        
        put("WORKSTATION", new HashMap<String, Double>() {{
            put("PROCESSOR", 0.40);      // 40%
            put("RAM", 0.20);            // 20%
            put("MOTHERBOARD", 0.15);    // 15%
            put("STORAGE", 0.10);        // 10%
            put("GRAPHIC CARD", 0.08);   // 8%
            put("POWER SUPPLY", 0.05);   // 5%
            put("CABINET", 0.02);        // 2%
        }});
        
        put("BUDGET", new HashMap<String, Double>() {{
            put("PROCESSOR", 0.30);      // 30%
            put("MOTHERBOARD", 0.20);    // 20%
            put("RAM", 0.15);            // 15%
            put("STORAGE", 0.12);        // 12%
            put("POWER SUPPLY", 0.10);   // 10%
            put("GRAPHIC CARD", 0.08);   // 8%
            put("CABINET", 0.05);        // 5%
        }});
        
        put("OFFICE", new HashMap<String, Double>() {{
            put("PROCESSOR", 0.25);      // 25%
            put("MOTHERBOARD", 0.20);    // 20%
            put("RAM", 0.18);            // 18%
            put("STORAGE", 0.15);        // 15%
            put("POWER SUPPLY", 0.12);   // 12%
            put("CABINET", 0.10);        // 10%
        }});
    }};
    
    // Component priority order for different build types
    private static final Map<String, List<String>> BUILD_PRIORITIES = new HashMap<String, List<String>>() {{
        put("GAMING", Arrays.asList("GRAPHIC CARD", "PROCESSOR", "MOTHERBOARD", "RAM", "STORAGE", "POWER SUPPLY", "CABINET"));
        put("WORKSTATION", Arrays.asList("PROCESSOR", "RAM", "MOTHERBOARD", "STORAGE", "GRAPHIC CARD", "POWER SUPPLY", "CABINET"));
        put("BUDGET", Arrays.asList("PROCESSOR", "MOTHERBOARD", "RAM", "STORAGE", "POWER SUPPLY", "CABINET", "GRAPHIC CARD"));
        put("OFFICE", Arrays.asList("PROCESSOR", "MOTHERBOARD", "RAM", "STORAGE", "POWER SUPPLY", "CABINET"));
    }};
    
    public static class BuildResult {
        public Map<String, ComponentModel> selectedComponents;
        public double totalCost;
        public String buildType;
        public List<String> suggestions;
        public List<String> warnings;
        public double performanceScore;
        
        public BuildResult() {
            selectedComponents = new HashMap<>();
            suggestions = new ArrayList<>();
            warnings = new ArrayList<>();
        }
    }
    
    public static BuildResult buildSmartPC(List<ComponentModel> allComponents, 
                                         double budget, 
                                         String preferredCPUBrand,
                                         String preferredGPUBrand,
                                         String preferredSocket,
                                         String preferredRAMType,
                                         String preferredStorage,
                                         String targetGeneration) {
        
        BuildResult result = new BuildResult();
        
        // Determine build type based on budget
        result.buildType = determineBuildType(budget);
        Log.d(TAG, "Determined build type: " + result.buildType + " for budget: ₹" + String.format("%,d", (int)budget) + 
              ", Target Generation: " + targetGeneration);
        
        // Get budget allocation for this build type
        Map<String, Double> allocation = BUDGET_ALLOCATIONS.get(result.buildType);
        if (allocation == null) {
            allocation = BUDGET_ALLOCATIONS.get("BUDGET"); // Default fallback
        }
        
        // Calculate component budgets
        Map<String, Double> componentBudgets = new HashMap<>();
        for (Map.Entry<String, Double> entry : allocation.entrySet()) {
            componentBudgets.put(entry.getKey(), budget * entry.getValue());
        }
        
        // Group components by category
        Map<String, List<ComponentModel>> componentsByCategory = groupComponentsByCategory(allComponents);
        
        // Select components based on priority and budget
        List<String> priority = BUILD_PRIORITIES.get(result.buildType);
        if (priority == null) {
            priority = BUILD_PRIORITIES.get("BUDGET"); // Default fallback
        }
        
        double remainingBudget = budget;
        
        for (String category : priority) {
            List<ComponentModel> categoryComponents = componentsByCategory.get(category);
            if (categoryComponents == null || categoryComponents.isEmpty()) {
                result.warnings.add("No " + category.toLowerCase() + " components available");
                continue;
            }
            
            double categoryBudget = componentBudgets.getOrDefault(category, remainingBudget * 0.1);
            
            ComponentModel selected = selectBestComponent(
                categoryComponents, 
                categoryBudget, 
                remainingBudget,
                category,
                preferredCPUBrand,
                preferredGPUBrand,
                preferredSocket,
                preferredRAMType,
                preferredStorage,
                targetGeneration,
                result.selectedComponents
            );
            
            if (selected != null) {
                result.selectedComponents.put(category, selected);
                result.totalCost += selected.getPrice();
                remainingBudget -= selected.getPrice();
                
                Log.d(TAG, "Selected " + category + ": " + selected.getName() + " - ₹" + String.format("%,d", (int)selected.getPrice()));
            } else {
                result.warnings.add("Could not find suitable " + category.toLowerCase() + " within budget");
            }
        }
        
        // Generate suggestions and calculate performance score
        generateSuggestions(result, remainingBudget, budget);
        result.performanceScore = calculatePerformanceScore(result.selectedComponents, result.buildType);
        
        return result;
    }
    
    private static String determineBuildType(double budget) {
        if (budget >= 80000) return "WORKSTATION";
        if (budget >= 50000) return "GAMING";
        if (budget >= 25000) return "BUDGET";
        return "OFFICE";
    }
    
    private static Map<String, List<ComponentModel>> groupComponentsByCategory(List<ComponentModel> components) {
        return components.stream()
            .filter(c -> c.isInStock())
            .collect(Collectors.groupingBy(ComponentModel::getCategory));
    }
    
    private static ComponentModel selectBestComponent(List<ComponentModel> components,
                                                    double categoryBudget,
                                                    double remainingBudget,
                                                    String category,
                                                    String preferredCPUBrand,
                                                    String preferredGPUBrand,
                                                    String preferredSocket,
                                                    String preferredRAMType,
                                                    String preferredStorage,
                                                    String targetGeneration,
                                                    Map<String, ComponentModel> selectedComponents) {
        
        // Filter compatible components
        List<ComponentModel> compatible = filterCompatibleComponents(
            components, category, preferredCPUBrand, preferredGPUBrand, 
            preferredSocket, preferredRAMType, preferredStorage, selectedComponents, targetGeneration
        );
        
        if (compatible.isEmpty()) {
            return null;
        }
        
        // Prioritize components that match target generation
        List<ComponentModel> prioritized = new ArrayList<>();
        List<ComponentModel> others = new ArrayList<>();
        
        for (ComponentModel component : compatible) {
            if (matchesTargetGeneration(component, targetGeneration, category)) {
                prioritized.add(component);
            } else {
                others.add(component);
            }
        }
        
        // Sort prioritized components by value score
        prioritized.sort((a, b) -> Double.compare(
            calculateValueScore(b, category), 
            calculateValueScore(a, category)
        ));
        
        // Sort other components by value score
        others.sort((a, b) -> Double.compare(
            calculateValueScore(b, category), 
            calculateValueScore(a, category)
        ));
        
        // Combine lists with prioritized first
        List<ComponentModel> sortedComponents = new ArrayList<>();
        sortedComponents.addAll(prioritized);
        sortedComponents.addAll(others);
        
        // Find best component within budget (prioritizing target generation matches)
        for (ComponentModel component : sortedComponents) {
            if (component.getPrice() <= Math.min(categoryBudget * 1.2, remainingBudget)) {
                return component;
            }
        }
        
        // If nothing found within category budget, try with remaining budget
        for (ComponentModel component : sortedComponents) {
            if (component.getPrice() <= remainingBudget) {
                return component;
            }
        }
        
        return null;
    }
    
    /**
     * Check if a component matches the target generation
     */
    private static boolean matchesTargetGeneration(ComponentModel component, String targetGeneration, String category) {
        if (targetGeneration == null || targetGeneration.isEmpty()) {
            return false;
        }
        
        String componentName = component.getName().toLowerCase();
        String componentDesc = component.getDescription().toLowerCase();
        String target = targetGeneration.toLowerCase();
        
        // For processors, check generation compatibility
        if ("PROCESSOR".equalsIgnoreCase(category)) {
            if (target.contains("ryzen 9") && componentName.contains("ryzen 9")) return true;
            if (target.contains("ryzen 7") && componentName.contains("ryzen 7")) return true;
            if (target.contains("ryzen 5") && componentName.contains("ryzen 5")) return true;
            if (target.contains("ryzen 3") && componentName.contains("ryzen 3")) return true;
            if (target.contains("i9") && componentName.contains("i9")) return true;
            if (target.contains("i7") && componentName.contains("i7")) return true;
            if (target.contains("i5") && componentName.contains("i5")) return true;
            if (target.contains("i3") && componentName.contains("i3")) return true;
            
            // Check generation numbers
            if (target.contains("7000") && componentName.contains("7000")) return true;
            if (target.contains("5000") && componentName.contains("5000")) return true;
            if (target.contains("3000") && componentName.contains("3000")) return true;
            if (target.contains("14th") && (componentName.contains("14th") || componentDesc.contains("14th"))) return true;
            if (target.contains("13th") && (componentName.contains("13th") || componentDesc.contains("13th"))) return true;
            if (target.contains("12th") && (componentName.contains("12th") || componentDesc.contains("12th"))) return true;
        }
        
        // For motherboards, check DDR compatibility
        if ("MOTHERBOARD".equalsIgnoreCase(category)) {
            if (target.contains("ddr5") && (componentName.contains("ddr5") || componentDesc.contains("ddr5"))) return true;
            if (target.contains("ddr4") && (componentName.contains("ddr4") || componentDesc.contains("ddr4"))) return true;
            if (target.contains("ddr3") && (componentName.contains("ddr3") || componentDesc.contains("ddr3"))) return true;
        }
        
        // For RAM, check DDR type
        if ("RAM".equalsIgnoreCase(category)) {
            if (target.contains("ddr5") && (componentName.contains("ddr5") || componentDesc.contains("ddr5"))) return true;
            if (target.contains("ddr4") && (componentName.contains("ddr4") || componentDesc.contains("ddr4"))) return true;
            if (target.contains("ddr3") && (componentName.contains("ddr3") || componentDesc.contains("ddr3"))) return true;
        }
        
        return false;
    }
    
    private static List<ComponentModel> filterCompatibleComponents(List<ComponentModel> components,
                                                                 String category,
                                                                 String preferredCPUBrand,
                                                                 String preferredGPUBrand,
                                                                 String preferredSocket,
                                                                 String preferredRAMType,
                                                                 String preferredStorage,
                                                                 Map<String, ComponentModel> selectedComponents,
                                                                 String targetGeneration) {
        
        return components.stream()
            .filter(c -> c.getCategory().equalsIgnoreCase(category))
            .filter(c -> c.isInStock())
            .filter(c -> isComponentCompatible(c, category, preferredCPUBrand, preferredGPUBrand, 
                                             preferredSocket, preferredRAMType, preferredStorage, selectedComponents, targetGeneration))
            .collect(Collectors.toList());
    }
    
    private static boolean isComponentCompatible(ComponentModel component,
                                               String category,
                                               String preferredCPUBrand,
                                               String preferredGPUBrand,
                                               String preferredSocket,
                                               String preferredRAMType,
                                               String preferredStorage,
                                               Map<String, ComponentModel> selectedComponents,
                                               String targetGeneration) {
        
        Map<String, String> specs = component.getSpecifications();
        if (specs == null) specs = new HashMap<>();
        
        switch (category.toUpperCase()) {
            case "PROCESSOR":
                // Check CPU brand preference
                if (preferredCPUBrand != null && !preferredCPUBrand.isEmpty()) {
                    if (!component.getBrand().equalsIgnoreCase(preferredCPUBrand)) {
                        return false;
                    }
                }
                
                // Check DDR type compatibility based on target generation
                if (preferredRAMType != null && !preferredRAMType.isEmpty()) {
                    String cpuName = component.getName().toLowerCase();
                    String cpuSpecs = component.getDescription().toLowerCase();
                    
                    // For Intel processors
                    if (preferredCPUBrand != null && preferredCPUBrand.equalsIgnoreCase("Intel")) {
                        if ("DDR5".equals(preferredRAMType)) {
                            // DDR5 compatible Intel processors (12th gen+)
                            if (!cpuName.contains("12th") && !cpuName.contains("13th") && !cpuName.contains("14th") && 
                                !cpuName.contains("15th") && !cpuSpecs.contains("12th") && !cpuSpecs.contains("13th") && 
                                !cpuSpecs.contains("14th") && !cpuSpecs.contains("15th")) {
                                return false;
                            }
                        } else if ("DDR4".equals(preferredRAMType)) {
                            // DDR4 compatible Intel processors (6th gen+)
                            if (!cpuName.contains("6th") && !cpuName.contains("7th") && !cpuName.contains("8th") && 
                                !cpuName.contains("9th") && !cpuName.contains("10th") && !cpuName.contains("11th") && 
                                !cpuName.contains("12th") && !cpuName.contains("13th") && !cpuSpecs.contains("6th") && 
                                !cpuSpecs.contains("7th") && !cpuSpecs.contains("8th") && !cpuSpecs.contains("9th") && 
                                !cpuSpecs.contains("10th") && !cpuSpecs.contains("11th") && !cpuSpecs.contains("12th") && 
                                !cpuSpecs.contains("13th")) {
                                return false;
                            }
                        } else if ("DDR3".equals(preferredRAMType)) {
                            // DDR3 compatible Intel processors (2nd-4th gen)
                            if (!cpuName.contains("2nd") && !cpuName.contains("3rd") && !cpuName.contains("4th") && 
                                !cpuSpecs.contains("2nd") && !cpuSpecs.contains("3rd") && !cpuSpecs.contains("4th")) {
                                return false;
                            }
                        }
                    }
                    
                    // For AMD processors
                    if (preferredCPUBrand != null && preferredCPUBrand.equalsIgnoreCase("AMD")) {
                        if ("DDR5".equals(preferredRAMType)) {
                            // DDR5 compatible AMD processors (Ryzen 7000 series)
                            if (!cpuName.contains("7000") && !cpuName.contains("ryzen 9") && !cpuName.contains("ryzen 7") && 
                                !cpuName.contains("ryzen 5") && !cpuSpecs.contains("7000")) {
                                return false;
                            }
                        } else if ("DDR4".equals(preferredRAMType)) {
                            // DDR4 compatible AMD processors (Ryzen 3000, 5000 series)
                            if (!cpuName.contains("3000") && !cpuName.contains("5000") && !cpuName.contains("ryzen 9") && 
                                !cpuName.contains("ryzen 7") && !cpuName.contains("ryzen 5") && !cpuName.contains("ryzen 3") && 
                                !cpuSpecs.contains("3000") && !cpuSpecs.contains("5000")) {
                                return false;
                            }
                        } else if ("DDR3".equals(preferredRAMType)) {
                            // DDR3 compatible AMD processors (older Ryzen, FX series)
                            if (!cpuName.contains("fx") && !cpuName.contains("athlon") && !cpuSpecs.contains("fx") && 
                                !cpuSpecs.contains("athlon")) {
                                return false;
                            }
                        }
                    }
                }
                break;
                
            case "GRAPHIC CARD":
                // Check GPU brand preference
                if (preferredGPUBrand != null && !preferredGPUBrand.isEmpty()) {
                    if (!component.getBrand().equalsIgnoreCase(preferredGPUBrand)) {
                        return false;
                    }
                }
                break;
                
            case "MOTHERBOARD":
                // Check DDR type compatibility
                if (preferredRAMType != null && !preferredRAMType.isEmpty()) {
                    String moboRAM = specs.get("RAM");
                    if (moboRAM != null && !moboRAM.equalsIgnoreCase(preferredRAMType)) {
                        return false;
                    }
                }
                
                // Check brand compatibility with selected CPU
                ComponentModel cpu = selectedComponents.get("PROCESSOR");
                if (cpu != null) {
                    String cpuBrand = cpu.getBrand();
                    String moboBrand = specs.get("Brand");
                    if (cpuBrand != null && moboBrand != null) {
                        // Intel CPUs should work with Intel-compatible motherboards
                        // AMD CPUs should work with AMD-compatible motherboards
                        if (cpuBrand.equalsIgnoreCase("Intel") && moboBrand.equalsIgnoreCase("AMD")) {
                            return false;
                        }
                        if (cpuBrand.equalsIgnoreCase("AMD") && moboBrand.equalsIgnoreCase("Intel")) {
                            return false;
                        }
                    }
                }
                break;
                
            case "RAM":
                // Check RAM type compatibility with DDR preference
                if (preferredRAMType != null && !preferredRAMType.isEmpty()) {
                    String ramType = specs.get("Type");
                    if (ramType != null && !ramType.equalsIgnoreCase(preferredRAMType)) {
                        return false;
                    }
                }
                break;
                
            case "STORAGE":
                // Check storage type preference
                if (preferredStorage != null && !preferredStorage.isEmpty()) {
                    String storageType = specs.get("Type");
                    if (storageType != null && !storageType.toLowerCase().contains(preferredStorage.toLowerCase())) {
                        return false;
                    }
                }
                break;
        }
        
        return true;
    }
    
    private static double calculateValueScore(ComponentModel component, String category) {
        double baseScore = component.getRating() * component.getRatingCount() / 100.0;
        double priceScore = 10000.0 / Math.max(component.getPrice(), 1.0);
        
        // Category-specific scoring
        switch (category.toUpperCase()) {
            case "PROCESSOR":
                Map<String, String> cpuSpecs = component.getSpecifications();
                if (cpuSpecs != null) {
                    String cores = cpuSpecs.get("Cores");
                    if (cores != null && cores.matches("\\d+")) {
                        baseScore *= Integer.parseInt(cores) / 4.0; // Normalize by 4 cores
                    }
                }
                break;
                
            case "GRAPHIC CARD":
                Map<String, String> gpuSpecs = component.getSpecifications();
                if (gpuSpecs != null) {
                    String memory = gpuSpecs.get("Memory");
                    if (memory != null && memory.contains("GB")) {
                        try {
                            int memGB = Integer.parseInt(memory.replaceAll("[^0-9]", ""));
                            baseScore *= memGB / 4.0; // Normalize by 4GB
                        } catch (NumberFormatException ignored) {}
                    }
                }
                break;
                
            case "RAM":
                Map<String, String> ramSpecs = component.getSpecifications();
                if (ramSpecs != null) {
                    String capacity = ramSpecs.get("Capacity");
                    if (capacity != null && capacity.contains("GB")) {
                        try {
                            int capGB = Integer.parseInt(capacity.replaceAll("[^0-9]", ""));
                            baseScore *= capGB / 8.0; // Normalize by 8GB
                        } catch (NumberFormatException ignored) {}
                    }
                }
                break;
        }
        
        return baseScore * priceScore;
    }
    
    private static void generateSuggestions(BuildResult result, double remainingBudget, double totalBudget) {
        if (remainingBudget > totalBudget * 0.1) {
            result.suggestions.add("You have ₹" + String.format("%,d", (int)remainingBudget) + " remaining. Consider upgrading your GPU or CPU.");
        }
        
        ComponentModel cpu = result.selectedComponents.get("PROCESSOR");
        ComponentModel gpu = result.selectedComponents.get("GRAPHIC CARD");
        
        if (cpu != null && gpu != null) {
            if (gpu.getPrice() > cpu.getPrice() * 2) {
                result.suggestions.add("Your GPU is significantly more expensive than your CPU. Consider balancing the build.");
            } else if (cpu.getPrice() > gpu.getPrice() * 3) {
                result.suggestions.add("Your CPU is much more expensive than your GPU. Consider upgrading the GPU for better gaming performance.");
            }
        }
        
        ComponentModel ram = result.selectedComponents.get("RAM");
        if (ram != null) {
            Map<String, String> ramSpecs = ram.getSpecifications();
            if (ramSpecs != null) {
                String capacity = ramSpecs.get("Capacity");
                if (capacity != null && capacity.contains("4GB")) {
                    result.suggestions.add("Consider upgrading to 8GB or 16GB RAM for better multitasking performance.");
                }
            }
        }
    }
    
    private static double calculatePerformanceScore(Map<String, ComponentModel> components, String buildType) {
        double score = 0.0;
        
        ComponentModel cpu = components.get("PROCESSOR");
        ComponentModel gpu = components.get("GRAPHIC CARD");
        ComponentModel ram = components.get("RAM");
        
        if (cpu != null) {
            score += cpu.getRating() * 20; // CPU contributes 20 points max
        }
        
        if (gpu != null) {
            score += gpu.getRating() * 25; // GPU contributes 25 points max
        }
        
        if (ram != null) {
            score += ram.getRating() * 15; // RAM contributes 15 points max
        }
        
        // Build type multiplier
        switch (buildType) {
            case "WORKSTATION":
                if (cpu != null) score *= 1.2; // Boost CPU importance
                break;
            case "GAMING":
                if (gpu != null) score *= 1.3; // Boost GPU importance
                break;
        }
        
        return Math.min(score, 100.0); // Cap at 100
    }
}