package com.app.pccooker;

import android.content.Context;
import android.widget.Toast;
import com.app.pccooker.models.ComponentModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentManager {
    
    private static ComponentManager instance;
    private final FirebaseFirestore db;
    private final Map<String, ComponentModel> selectedComponents = new HashMap<>();
    private final Context context;
    
    private ComponentManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }
    
    public static ComponentManager getInstance(Context context) {
        if (instance == null) {
            instance = new ComponentManager(context);
        }
        return instance;
    }
    
    public void autoSelectComponents(double budget, String useCase, OnAutoSelectCallback callback) {
        // Budget distribution for different components based on use-case
        Map<String, Double> budgetDistribution = new HashMap<>();
        if (useCase != null && useCase.equals("gaming")) {
            budgetDistribution.put("PROCESSOR", budget * 0.20);
            budgetDistribution.put("GRAPHIC CARD", budget * 0.35);
            budgetDistribution.put("MOTHERBOARD", budget * 0.10);
            budgetDistribution.put("RAM", budget * 0.10);
            budgetDistribution.put("STORAGE", budget * 0.10);
            budgetDistribution.put("POWER SUPPLY", budget * 0.05);
            budgetDistribution.put("CABINET", budget * 0.05);
            budgetDistribution.put("CPU COOLER", budget * 0.03);
            budgetDistribution.put("MONITOR", budget * 0.02);
        } else if (useCase != null && useCase.equals("editing")) {
            budgetDistribution.put("PROCESSOR", budget * 0.30);
            budgetDistribution.put("GRAPHIC CARD", budget * 0.20);
            budgetDistribution.put("MOTHERBOARD", budget * 0.15);
            budgetDistribution.put("RAM", budget * 0.15);
            budgetDistribution.put("STORAGE", budget * 0.10);
            budgetDistribution.put("POWER SUPPLY", budget * 0.05);
            budgetDistribution.put("CABINET", budget * 0.03);
            budgetDistribution.put("CPU COOLER", budget * 0.02);
        } else if (useCase != null && useCase.equals("work")) {
            budgetDistribution.put("PROCESSOR", budget * 0.30);
            budgetDistribution.put("GRAPHIC CARD", budget * 0.10);
            budgetDistribution.put("MOTHERBOARD", budget * 0.20);
            budgetDistribution.put("RAM", budget * 0.15);
            budgetDistribution.put("STORAGE", budget * 0.15);
            budgetDistribution.put("POWER SUPPLY", budget * 0.05);
            budgetDistribution.put("CABINET", budget * 0.03);
            budgetDistribution.put("CPU COOLER", budget * 0.02);
        } else {
            // Balanced build - Select ALL required components
            budgetDistribution.put("PROCESSOR", budget * 0.25);
            budgetDistribution.put("GRAPHIC CARD", budget * 0.25);
            budgetDistribution.put("MOTHERBOARD", budget * 0.15);
            budgetDistribution.put("RAM", budget * 0.15);
            budgetDistribution.put("STORAGE", budget * 0.10);
            budgetDistribution.put("POWER SUPPLY", budget * 0.05);
            budgetDistribution.put("CABINET", budget * 0.03);
            budgetDistribution.put("CPU COOLER", budget * 0.02);
        }
        
        // Ensure all main components have a specified budget
        List<String> categories = Arrays.asList("PROCESSOR", "GRAPHIC CARD", "MOTHERBOARD", "RAM", "STORAGE", "POWER SUPPLY", "CABINET");
        for (String category : categories) {
            budgetDistribution.putIfAbsent(category, budget / categories.size());
        }

        List<ComponentModel> autoSelectedComponents = new ArrayList<>();
        final int[] completedRequests = {0};
        final int totalRequests = budgetDistribution.size();
        final boolean[] hasError = {false};
        
        // If no components found in Firebase, create sample components
        if (totalRequests == 0) {
            callback.onError("Invalid budget distribution");
            return;
        }
        
        // Add timeout mechanism
        final android.os.Handler timeoutHandler = new android.os.Handler();
        final Runnable timeoutRunnable = () -> {
            if (completedRequests[0] < totalRequests) {
                hasError[0] = true;
                callback.onError("Request timed out. Please check your internet connection and try again.");
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 25000); // 25 second timeout
        
        for (Map.Entry<String, Double> entry : budgetDistribution.entrySet()) {
            String category = entry.getKey();
            double categoryBudget = entry.getValue();
            
            // Search Firebase for components in this category within budget
            try {
                db.collection("pc_components")
                    .document(category)
                    .collection("items")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (hasError[0]) return; // Don't process if timeout occurred
                        
                        ComponentModel bestComponent = null;
                        double bestValue = 0;
                        int componentsFound = 0;
                        
                        System.out.println("Searching in category: " + category + " with budget: " + categoryBudget);
                        
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            try {
                                ComponentModel component = doc.toObject(ComponentModel.class);
                                if (component != null) {
                                    componentsFound++;
                                    System.out.println("Found component: " + component.getName() + " Price: " + component.getPrice() + " Budget: " + categoryBudget);
                                    
                                    // Set default values if missing
                                    if (component.getName() == null || component.getName().isEmpty()) {
                                        component.setName("Unknown " + category);
                                    }
                                    if (component.getPrice() <= 0) {
                                        component.setPrice(categoryBudget * 0.8); // Default to 80% of budget
                                    }
                                    if (component.getRating() <= 0) {
                                        component.setRating(4.0); // Default rating
                                    }
                                    
                                    if (component.getPrice() <= categoryBudget) {
                                        // Select component with best value (rating/price ratio)
                                        double value = component.getRating() / component.getPrice();
                                        if (value > bestValue) {
                                            bestValue = value;
                                            bestComponent = component;
                                            bestComponent.setId(doc.getId());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Failed to deserialize component in " + category + ": " + e.getMessage());
                            }
                        }
                        
                        System.out.println("Components found in " + category + ": " + componentsFound);
                        
                        // If no components found in Firebase, create a sample component
                        if (bestComponent == null) {
                            bestComponent = createFallbackComponent(category, categoryBudget);
                            System.out.println("Created sample component for " + category);
                        }
                        
                        if (bestComponent != null) {
                            selectedComponents.put(category, bestComponent);
                            autoSelectedComponents.add(bestComponent);
                        }
                        
                        completedRequests[0]++;
                        if (completedRequests[0] == totalRequests) {
                            timeoutHandler.removeCallbacks(timeoutRunnable);
                            if (!autoSelectedComponents.isEmpty()) {
                                System.out.println("Auto-selected " + autoSelectedComponents.size() + " components");
                                callback.onSuccess(autoSelectedComponents);
                            } else {
                                callback.onError("No suitable components found for your budget. Please try increasing your budget or check your requirements.");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (hasError[0]) return; // Don't process if timeout occurred
                        
                        System.err.println("Firebase error for category " + category + ": " + e.getMessage());
                        
                        // Create fallback component on Firebase failure
                        ComponentModel sampleComponent = createFallbackComponent(category, categoryBudget);
                        if (sampleComponent != null) {
                            selectedComponents.put(category, sampleComponent);
                            autoSelectedComponents.add(sampleComponent);
                        }
                        
                        completedRequests[0]++;
                        if (completedRequests[0] == totalRequests) {
                            timeoutHandler.removeCallbacks(timeoutRunnable);
                            if (!autoSelectedComponents.isEmpty()) {
                                callback.onSuccess(autoSelectedComponents);
                            } else {
                                callback.onError("Unable to load components. Please check your internet connection and try again.");
                            }
                        }
                    });
            } catch (Exception e) {
                System.err.println("Exception in Firebase query for " + category + ": " + e.getMessage());
                
                // Create fallback component on exception
                ComponentModel sampleComponent = createFallbackComponent(category, categoryBudget);
                if (sampleComponent != null) {
                    selectedComponents.put(category, sampleComponent);
                    autoSelectedComponents.add(sampleComponent);
                }
                
                completedRequests[0]++;
                if (completedRequests[0] == totalRequests) {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    if (!autoSelectedComponents.isEmpty()) {
                        callback.onSuccess(autoSelectedComponents);
                    } else {
                        callback.onError("Error connecting to database. Please try again.");
                    }
                }
            }
        }
    }
    
    private ComponentModel createFallbackComponent(String category, double budget) {
        ComponentModel component = new ComponentModel();
        component.setCategory(category);
        component.setPrice(budget * 0.8); // 80% of budget
        component.setRating(4.0);
        component.setRatingCount(50);
        component.setInStock(true);
        component.setName("Recommended " + category);
        component.setBrand("Various");
        component.setDescription("Best value " + category.toLowerCase() + " within your budget");
        
        return component;
    }
    
    public void searchComponents(String category, double maxPrice, double minRating, OnSearchCallback callback) {
        db.collection("pc_components")
            .document(category)
            .collection("items")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<ComponentModel> components = new ArrayList<>();
                
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    try {
                        ComponentModel component = doc.toObject(ComponentModel.class);
                        if (component != null) {
                            component.setId(doc.getId());
                            
                            // Set default values for missing data
                            if (component.getName() == null || component.getName().isEmpty()) {
                                component.setName("Unnamed Component");
                            }
                            if (component.getBrand() == null || component.getBrand().isEmpty()) {
                                component.setBrand("Unknown Brand");
                            }
                            if (component.getDescription() == null || component.getDescription().isEmpty()) {
                                component.setDescription("No description available");
                            }
                            if (component.getPrice() <= 0) {
                                component.setPrice(1000); // Default price
                            }
                            if (component.getRating() <= 0) {
                                component.setRating(3.0); // Default rating
                            }
                            if (component.getRatingCount() <= 0) {
                                component.setRatingCount(10); // Default rating count
                            }
                            
                            // More lenient filtering - only filter by price if maxPrice > 0
                            boolean priceOk = maxPrice <= 0 || component.getPrice() <= maxPrice;
                            boolean ratingOk = minRating <= 0 || component.getRating() >= minRating;
                            
                            if (priceOk && ratingOk) {
                                components.add(component);
                            }
                        }
                    } catch (Exception e) {
                        // Skip this component if deserialization fails
                        System.err.println("Failed to deserialize component in " + category + ": " + e.getMessage());
                    }
                }
                
                if (components.isEmpty()) {
                    callback.onError("No components found matching your criteria");
                } else {
                    callback.onSuccess(components);
                }
            })
            .addOnFailureListener(e -> {
                callback.onError("Failed to load components: " + e.getMessage());
            });
    }
    
    public void selectComponent(String category, ComponentModel component) {
        selectedComponents.put(category, component);
        Toast.makeText(context, component.getName() + " selected", Toast.LENGTH_SHORT).show();
    }
    
    public ComponentModel getSelectedComponent(String category) {
        return selectedComponents.get(category);
    }
    
    public Map<String, ComponentModel> getAllSelectedComponents() {
        return new HashMap<>(selectedComponents);
    }
    
    public double getTotalPrice() {
        return selectedComponents.values().stream()
            .mapToDouble(ComponentModel::getPrice)
            .sum();
    }
    
    public void clearSelection() {
        selectedComponents.clear();
    }
    
    public boolean isComponentSelected(String category) {
        return selectedComponents.containsKey(category);
    }
    
    public void fetchAlternatives(String category, double budget, OnSearchCallback callback) {
        db.collection("pc_components")
            .document(category)
            .collection("items")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<ComponentModel> alternatives = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    try {
                        ComponentModel component = doc.toObject(ComponentModel.class);
                        if (component != null && component.getPrice() <= budget) {
                            component.setId(doc.getId());
                            alternatives.add(component);
                        }
                    } catch (Exception e) {
                        // Skip this component if deserialization fails
                    }
                }
                callback.onSuccess(alternatives);
            })
            .addOnFailureListener(e -> callback.onError("Failed to load alternatives: " + e.getMessage()));
    }

    public String getAIChoiceExplanation(String category, String useCase) {
        if (useCase == null) useCase = "general";
        switch (useCase) {
            case "gaming":
                if (category.equals("GRAPHIC CARD")) return "Best GPU for gaming performance within your budget.";
                if (category.equals("PROCESSOR")) return "Powerful CPU for smooth gaming.";
                break;
            case "editing":
                if (category.equals("PROCESSOR")) return "High-core CPU for editing tasks.";
                if (category.equals("RAM")) return "More RAM for smooth editing.";
                break;
            case "work":
                if (category.equals("PROCESSOR")) return "Efficient CPU for productivity.";
                if (category.equals("STORAGE")) return "Fast storage for quick file access.";
                break;
        }
        return "Best value for your build.";
    }
    
    public interface OnAutoSelectCallback {
        void onSuccess(List<ComponentModel> components);
        void onError(String message);
    }
    
    public interface OnSearchCallback {
        void onSuccess(List<ComponentModel> components);
        void onError(String message);
    }
} 