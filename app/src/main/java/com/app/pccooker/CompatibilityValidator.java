package com.app.pccooker;

import android.util.Log;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced Component Compatibility Validation System
 * Provides detailed compatibility checking and smart alerts for PC builds
 */
public class CompatibilityValidator {
    
    private static final String TAG = "CompatibilityValidator";
    
    // Socket compatibility mappings
    private static final Map<String, Set<String>> SOCKET_COMPATIBILITY = new HashMap<String, Set<String>>() {{
        // AMD Sockets
        put("AM4", new HashSet<>(Arrays.asList("AMD")));
        put("AM5", new HashSet<>(Arrays.asList("AMD")));
        put("TR4", new HashSet<>(Arrays.asList("AMD")));
        put("sTRX4", new HashSet<>(Arrays.asList("AMD")));
        
        // Intel Sockets
        put("LGA1700", new HashSet<>(Arrays.asList("Intel")));
        put("LGA1200", new HashSet<>(Arrays.asList("Intel")));
        put("LGA1151", new HashSet<>(Arrays.asList("Intel")));
        put("LGA1155", new HashSet<>(Arrays.asList("Intel")));
        put("LGA1150", new HashSet<>(Arrays.asList("Intel")));
        put("LGA2066", new HashSet<>(Arrays.asList("Intel")));
    }};
    
    // RAM compatibility with sockets
    private static final Map<String, Set<String>> SOCKET_RAM_COMPATIBILITY = new HashMap<String, Set<String>>() {{
        // AMD
        put("AM5", new HashSet<>(Arrays.asList("DDR5")));
        put("AM4", new HashSet<>(Arrays.asList("DDR4")));
        
        // Intel
        put("LGA1700", new HashSet<>(Arrays.asList("DDR4", "DDR5")));
        put("LGA1200", new HashSet<>(Arrays.asList("DDR4")));
        put("LGA1151", new HashSet<>(Arrays.asList("DDR4")));
        put("LGA1155", new HashSet<>(Arrays.asList("DDR3")));
        put("LGA1150", new HashSet<>(Arrays.asList("DDR3")));
        put("LGA2066", new HashSet<>(Arrays.asList("DDR4")));
    }};
    
    // Power requirements (rough estimates in watts)
    private static final Map<String, Integer> COMPONENT_POWER_USAGE = new HashMap<String, Integer>() {{
        put("PROCESSOR_LOW", 65);
        put("PROCESSOR_MID", 105);
        put("PROCESSOR_HIGH", 150);
        put("GRAPHIC_CARD_LOW", 75);
        put("GRAPHIC_CARD_MID", 150);
        put("GRAPHIC_CARD_HIGH", 300);
        put("MOTHERBOARD", 50);
        put("MEMORY", 10);
        put("STORAGE_HDD", 10);
        put("STORAGE_SSD", 5);
        put("CASE_FANS", 15);
    }};
    
    public static class ValidationResult {
        public boolean isCompatible;
        public List<CompatibilityIssue> issues;
        public List<String> warnings;
        public List<String> suggestions;
        public int estimatedPowerUsage;
        public int recommendedPSU;
        
        public ValidationResult() {
            issues = new ArrayList<>();
            warnings = new ArrayList<>();
            suggestions = new ArrayList<>();
        }
    }
    
    public static class CompatibilityIssue {
        public enum Severity { CRITICAL, WARNING, INFO }
        
        public Severity severity;
        public String title;
        public String description;
        public String solution;
        public List<String> affectedComponents;
        
        public CompatibilityIssue(Severity severity, String title, String description, String solution) {
            this.severity = severity;
            this.title = title;
            this.description = description;
            this.solution = solution;
            this.affectedComponents = new ArrayList<>();
        }
    }
    
    /**
     * Validates compatibility of all components in a build
     */
    public static ValidationResult validateBuild(Map<String, ComponentModel> selectedComponents) {
        ValidationResult result = new ValidationResult();
        
        Log.d(TAG, "Starting compatibility validation for " + selectedComponents.size() + " components");
        
        // Check CPU-Motherboard compatibility
        validateCPUMotherboardCompatibility(selectedComponents, result);
        
        // Check RAM-Motherboard compatibility
        validateRAMCompatibility(selectedComponents, result);
        
        // Check power supply adequacy
        validatePowerSupply(selectedComponents, result);
        
        // Check storage compatibility
        validateStorageCompatibility(selectedComponents, result);
        
        // Check GPU compatibility
        validateGPUCompatibility(selectedComponents, result);
        
        // Check thermal considerations
        validateThermalConsiderations(selectedComponents, result);
        
        // Generate general suggestions
        generateGeneralSuggestions(selectedComponents, result);
        
        // Overall compatibility
        result.isCompatible = result.issues.stream().noneMatch(issue -> 
            issue.severity == CompatibilityIssue.Severity.CRITICAL);
        
        Log.d(TAG, "Validation complete. Compatible: " + result.isCompatible + 
              ", Issues: " + result.issues.size() + ", Warnings: " + result.warnings.size());
        
        return result;
    }
    
    private static void validateCPUMotherboardCompatibility(Map<String, ComponentModel> components, ValidationResult result) {
        ComponentModel cpu = components.get("PROCESSOR");
        ComponentModel motherboard = components.get("MOTHERBOARD");
        
        if (cpu == null || motherboard == null) return;
        
        Map<String, String> cpuSpecs = cpu.getSpecifications();
        Map<String, String> moboSpecs = motherboard.getSpecifications();
        
        if (cpuSpecs == null || moboSpecs == null) return;
        
        String cpuSocket = cpuSpecs.get("Socket");
        String moboSocket = moboSpecs.get("Socket");
        String cpuBrand = cpu.getBrand();
        
        // Check socket compatibility
        if (cpuSocket != null && moboSocket != null) {
            if (!cpuSocket.equals(moboSocket)) {
                CompatibilityIssue issue = new CompatibilityIssue(
                    CompatibilityIssue.Severity.CRITICAL,
                    "Socket Mismatch",
                    "CPU socket (" + cpuSocket + ") does not match motherboard socket (" + moboSocket + ")",
                    "Select a CPU and motherboard with matching sockets"
                );
                issue.affectedComponents.add("PROCESSOR");
                issue.affectedComponents.add("MOTHERBOARD");
                result.issues.add(issue);
            }
        }
        
        // Check brand-socket compatibility
        if (cpuBrand != null && moboSocket != null) {
            Set<String> compatibleBrands = SOCKET_COMPATIBILITY.get(moboSocket);
            if (compatibleBrands != null && !compatibleBrands.contains(cpuBrand)) {
                CompatibilityIssue issue = new CompatibilityIssue(
                    CompatibilityIssue.Severity.CRITICAL,
                    "Brand-Socket Incompatibility",
                    cpuBrand + " CPUs are not compatible with " + moboSocket + " socket",
                    "Choose a motherboard with a socket compatible with " + cpuBrand + " processors"
                );
                issue.affectedComponents.add("PROCESSOR");
                issue.affectedComponents.add("MOTHERBOARD");
                result.issues.add(issue);
            }
        }
    }
    
    private static void validateRAMCompatibility(Map<String, ComponentModel> components, ValidationResult result) {
        ComponentModel motherboard = components.get("MOTHERBOARD");
        ComponentModel memory = components.get("MEMORY");
        
        if (motherboard == null || memory == null) return;
        
        Map<String, String> moboSpecs = motherboard.getSpecifications();
        Map<String, String> ramSpecs = memory.getSpecifications();
        
        if (moboSpecs == null || ramSpecs == null) return;
        
        String moboRAM = moboSpecs.get("RAM");
        String ramType = ramSpecs.get("Type");
        String moboSocket = moboSpecs.get("Socket");
        
        // Check RAM type compatibility
        if (moboRAM != null && ramType != null) {
            if (!moboRAM.equals(ramType)) {
                CompatibilityIssue issue = new CompatibilityIssue(
                    CompatibilityIssue.Severity.CRITICAL,
                    "RAM Type Mismatch",
                    "Motherboard supports " + moboRAM + " but selected RAM is " + ramType,
                    "Select RAM that matches the motherboard's supported type"
                );
                issue.affectedComponents.add("MOTHERBOARD");
                issue.affectedComponents.add("MEMORY");
                result.issues.add(issue);
            }
        }
        
        // Check socket-RAM compatibility
        if (moboSocket != null && ramType != null) {
            Set<String> compatibleRAM = SOCKET_RAM_COMPATIBILITY.get(moboSocket);
            if (compatibleRAM != null && !compatibleRAM.contains(ramType)) {
                CompatibilityIssue issue = new CompatibilityIssue(
                    CompatibilityIssue.Severity.WARNING,
                    "Socket-RAM Compatibility",
                    moboSocket + " socket typically uses different RAM type than " + ramType,
                    "Verify that your motherboard specifically supports " + ramType
                );
                issue.affectedComponents.add("MOTHERBOARD");
                issue.affectedComponents.add("MEMORY");
                result.issues.add(issue);
            }
        }
        
        // Check RAM capacity
        String ramCapacity = ramSpecs.get("Capacity");
        String moboMaxRAM = moboSpecs.get("Max RAM");
        
        if (ramCapacity != null && moboMaxRAM != null) {
            try {
                int ramGB = Integer.parseInt(ramCapacity.replaceAll("[^0-9]", ""));
                int maxGB = Integer.parseInt(moboMaxRAM.replaceAll("[^0-9]", ""));
                
                if (ramGB > maxGB) {
                    CompatibilityIssue issue = new CompatibilityIssue(
                        CompatibilityIssue.Severity.WARNING,
                        "RAM Capacity Exceeds Motherboard Limit",
                        "Selected RAM (" + ramCapacity + ") exceeds motherboard maximum (" + moboMaxRAM + ")",
                        "Consider reducing RAM capacity or upgrading motherboard"
                    );
                    issue.affectedComponents.add("MOTHERBOARD");
                    issue.affectedComponents.add("MEMORY");
                    result.issues.add(issue);
                }
            } catch (NumberFormatException e) {
                // Ignore parsing errors
            }
        }
    }
    
    private static void validatePowerSupply(Map<String, ComponentModel> components, ValidationResult result) {
        ComponentModel psu = components.get("POWER SUPPLY");
        
        // Calculate estimated power usage
        int estimatedUsage = calculatePowerUsage(components);
        result.estimatedPowerUsage = estimatedUsage;
        result.recommendedPSU = (int) (estimatedUsage * 1.3); // 30% headroom
        
        if (psu != null) {
            Map<String, String> psuSpecs = psu.getSpecifications();
            if (psuSpecs != null) {
                String wattage = psuSpecs.get("Wattage");
                if (wattage != null) {
                    try {
                        int psuWatts = Integer.parseInt(wattage.replaceAll("[^0-9]", ""));
                        
                        if (psuWatts < estimatedUsage) {
                            CompatibilityIssue issue = new CompatibilityIssue(
                                CompatibilityIssue.Severity.CRITICAL,
                                "Insufficient Power Supply",
                                "PSU (" + psuWatts + "W) is insufficient for estimated usage (" + estimatedUsage + "W)",
                                "Upgrade to at least " + result.recommendedPSU + "W PSU"
                            );
                            issue.affectedComponents.add("POWER SUPPLY");
                            result.issues.add(issue);
                        } else if (psuWatts < result.recommendedPSU) {
                            result.warnings.add("PSU wattage is close to estimated usage. Consider " + result.recommendedPSU + "W for better efficiency.");
                        }
                    } catch (NumberFormatException e) {
                        result.warnings.add("Could not parse PSU wattage specification");
                    }
                }
            }
        } else {
            result.warnings.add("No power supply selected. You'll need at least " + result.recommendedPSU + "W PSU.");
        }
    }
    
    private static int calculatePowerUsage(Map<String, ComponentModel> components) {
        int totalUsage = 0;
        
        // Base system usage
        totalUsage += 50; // Motherboard, fans, etc.
        
        // CPU
        ComponentModel cpu = components.get("PROCESSOR");
        if (cpu != null) {
            if (cpu.getPrice() > 20000) totalUsage += COMPONENT_POWER_USAGE.get("PROCESSOR_HIGH");
            else if (cpu.getPrice() > 10000) totalUsage += COMPONENT_POWER_USAGE.get("PROCESSOR_MID");
            else totalUsage += COMPONENT_POWER_USAGE.get("PROCESSOR_LOW");
        }
        
        // GPU
        ComponentModel gpu = components.get("GRAPHIC CARD");
        if (gpu != null) {
            if (gpu.getPrice() > 40000) totalUsage += COMPONENT_POWER_USAGE.get("GRAPHIC_CARD_HIGH");
            else if (gpu.getPrice() > 20000) totalUsage += COMPONENT_POWER_USAGE.get("GRAPHIC_CARD_MID");
            else totalUsage += COMPONENT_POWER_USAGE.get("GRAPHIC_CARD_LOW");
        }
        
        // Memory
        ComponentModel memory = components.get("MEMORY");
        if (memory != null) {
            Map<String, String> ramSpecs = memory.getSpecifications();
            if (ramSpecs != null) {
                String capacity = ramSpecs.get("Capacity");
                if (capacity != null && capacity.contains("GB")) {
                    try {
                        int ramGB = Integer.parseInt(capacity.replaceAll("[^0-9]", ""));
                        totalUsage += (ramGB / 8) * COMPONENT_POWER_USAGE.get("MEMORY");
                    } catch (NumberFormatException ignored) {
                        totalUsage += COMPONENT_POWER_USAGE.get("MEMORY");
                    }
                }
            }
        }
        
        // Storage
        ComponentModel storage = components.get("STORAGE");
        if (storage != null) {
            Map<String, String> storageSpecs = storage.getSpecifications();
            if (storageSpecs != null) {
                String type = storageSpecs.get("Type");
                if (type != null && type.toLowerCase().contains("ssd")) {
                    totalUsage += COMPONENT_POWER_USAGE.get("STORAGE_SSD");
                } else {
                    totalUsage += COMPONENT_POWER_USAGE.get("STORAGE_HDD");
                }
            }
        }
        
        return totalUsage;
    }
    
    private static void validateStorageCompatibility(Map<String, ComponentModel> components, ValidationResult result) {
        ComponentModel storage = components.get("STORAGE");
        ComponentModel motherboard = components.get("MOTHERBOARD");
        
        if (storage == null) {
            result.warnings.add("No storage device selected. Your system needs at least one storage device.");
            return;
        }
        
        Map<String, String> storageSpecs = storage.getSpecifications();
        if (storageSpecs != null) {
            String storageType = storageSpecs.get("Type");
            String interface_ = storageSpecs.get("Interface");
            
            // Check for NVMe compatibility
            if (interface_ != null && interface_.contains("NVMe") && motherboard != null) {
                result.suggestions.add("Ensure your motherboard has an M.2 slot for NVMe SSD installation.");
            }
            
            // Storage capacity recommendations
            String capacity = storageSpecs.get("Capacity");
            if (capacity != null) {
                if (capacity.contains("128GB") || capacity.contains("256GB")) {
                    result.suggestions.add("Consider larger storage capacity (500GB+) for better long-term usability.");
                }
            }
        }
    }
    
    private static void validateGPUCompatibility(Map<String, ComponentModel> components, ValidationResult result) {
        ComponentModel gpu = components.get("GRAPHIC CARD");
        ComponentModel motherboard = components.get("MOTHERBOARD");
        ComponentModel cpu = components.get("PROCESSOR");
        
        if (gpu == null) {
            if (cpu != null) {
                Map<String, String> cpuSpecs = cpu.getSpecifications();
                if (cpuSpecs != null && !cpuSpecs.containsKey("Integrated Graphics")) {
                    result.warnings.add("No dedicated GPU selected. Ensure your CPU has integrated graphics for display output.");
                }
            }
            return;
        }
        
        // Check GPU-CPU balance
        if (cpu != null) {
            double gpuPrice = gpu.getPrice();
            double cpuPrice = cpu.getPrice();
            
            if (gpuPrice > cpuPrice * 3) {
                result.suggestions.add("Your GPU is significantly more expensive than your CPU. Consider upgrading the CPU to avoid bottlenecking.");
            } else if (cpuPrice > gpuPrice * 2 && gpuPrice > 10000) {
                result.suggestions.add("Your CPU is much more expensive than your GPU. Consider upgrading the GPU for better gaming performance.");
            }
        }
        
        // GPU power and size considerations
        Map<String, String> gpuSpecs = gpu.getSpecifications();
        if (gpuSpecs != null) {
            String power = gpuSpecs.get("Power");
            if (power != null) {
                try {
                    int gpuPower = Integer.parseInt(power.replaceAll("[^0-9]", ""));
                    if (gpuPower > 250) {
                        result.suggestions.add("High-power GPU selected. Ensure adequate PSU capacity and case airflow.");
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }
    
    private static void validateThermalConsiderations(Map<String, ComponentModel> components, ValidationResult result) {
        ComponentModel cpu = components.get("PROCESSOR");
        ComponentModel gpu = components.get("GRAPHIC CARD");
        ComponentModel cooler = components.get("CPU COOLER");
        ComponentModel case_ = components.get("CASE");
        
        // Check for CPU cooler
        if (cpu != null && cooler == null) {
            result.warnings.add("No CPU cooler selected. Most CPUs require a dedicated cooling solution.");
        }
        
        // High-performance component thermal warnings
        boolean highThermalLoad = false;
        
        if (cpu != null && cpu.getPrice() > 25000) {
            highThermalLoad = true;
        }
        
        if (gpu != null && gpu.getPrice() > 40000) {
            highThermalLoad = true;
        }
        
        if (highThermalLoad) {
            result.suggestions.add("High-performance components selected. Ensure adequate case airflow and consider premium cooling solutions.");
        }
        
        // Case airflow suggestions
        if (case_ == null) {
            result.warnings.add("No case selected. Choose a case with good airflow for optimal thermal performance.");
        }
    }
    
    private static void generateGeneralSuggestions(Map<String, ComponentModel> components, ValidationResult result) {
        int componentCount = components.size();
        
        if (componentCount < 4) {
            result.suggestions.add("Consider adding more components for a complete build (CPU, motherboard, RAM, storage minimum).");
        }
        
        // Budget balance suggestions
        double totalCost = components.values().stream().mapToDouble(ComponentModel::getPrice).sum();
        
        if (totalCost > 0) {
            ComponentModel mostExpensive = components.values().stream()
                .max(Comparator.comparingDouble(ComponentModel::getPrice))
                .orElse(null);
            
            if (mostExpensive != null && mostExpensive.getPrice() > totalCost * 0.5) {
                result.suggestions.add("One component consumes over 50% of your budget. Consider rebalancing for better overall performance.");
            }
        }
        
        // Brand consistency suggestions
        Map<String, Long> brandCounts = components.values().stream()
            .collect(Collectors.groupingBy(ComponentModel::getBrand, Collectors.counting()));
        
        if (brandCounts.size() > 5) {
            result.suggestions.add("Consider consolidating brands for potentially better compatibility and warranty support.");
        }
    }
}