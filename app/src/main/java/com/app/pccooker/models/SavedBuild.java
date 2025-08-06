package com.app.pccooker.models;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class for saved PC builds
 */
public class SavedBuild {
    @PropertyName("id")
    private String id = "";
    
    @PropertyName("buildName")
    private String buildName = "";
    
    @PropertyName("description")
    private String description = "";
    
    @PropertyName("components")
    private Map<String, Object> components = new HashMap<>();
    
    @PropertyName("totalCost")
    private double totalCost = 0.0;
    
    @PropertyName("createdDate")
    private Date createdDate = new Date();
    
    @PropertyName("lastModified")
    private Date lastModified = new Date();
    
    @PropertyName("buildType")
    private String buildType = "CUSTOM";
    
    @PropertyName("performanceScore")
    private double performanceScore = 0.0;
    
    @PropertyName("isPublic")
    private boolean isPublic = false;
    
    @PropertyName("tags")
    private java.util.List<String> tags = new java.util.ArrayList<>();
    
    // Default constructor for Firebase
    public SavedBuild() {}
    
    public SavedBuild(String buildName, Map<String, Object> components, double totalCost) {
        this.buildName = buildName;
        this.components = components;
        this.totalCost = totalCost;
        this.createdDate = new Date();
        this.lastModified = new Date();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getBuildName() { return buildName; }
    public void setBuildName(String buildName) { this.buildName = buildName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getComponents() { return components; }
    public void setComponents(Map<String, Object> components) { this.components = components; }
    
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public Date getLastModified() { return lastModified; }
    public void setLastModified(Date lastModified) { this.lastModified = lastModified; }
    
    public String getBuildType() { return buildType; }
    public void setBuildType(String buildType) { this.buildType = buildType; }
    
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
    
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    
    public java.util.List<String> getTags() { return tags; }
    public void setTags(java.util.List<String> tags) { this.tags = tags; }
    
    // Helper methods
    public int getComponentCount() {
        return components != null ? components.size() : 0;
    }
    
    public String getFormattedCost() {
        return "₹" + String.format("%,.0f", totalCost);
    }
    
    public String getFormattedDate() {
        if (lastModified != null) {
            long timeDiff = System.currentTimeMillis() - lastModified.getTime();
            long days = timeDiff / (1000 * 60 * 60 * 24);
            
            if (days == 0) {
                return "Today";
            } else if (days == 1) {
                return "Yesterday";
            } else if (days < 7) {
                return days + " days ago";
            } else if (days < 30) {
                return (days / 7) + " weeks ago";
            } else {
                return (days / 30) + " months ago";
            }
        }
        return "Unknown";
    }
    
    public String getPerformanceGrade() {
        if (performanceScore >= 90) return "A+";
        if (performanceScore >= 80) return "A";
        if (performanceScore >= 70) return "B+";
        if (performanceScore >= 60) return "B";
        if (performanceScore >= 50) return "C+";
        if (performanceScore >= 40) return "C";
        return "D";
    }
    
    public String getMainComponent() {
        if (components == null || components.isEmpty()) return "Empty Build";
        
        // Try to find the most expensive component
        String mainComponent = "Custom Build";
        double maxPrice = 0;
        
        for (Map.Entry<String, Object> entry : components.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> component = (Map<String, Object>) entry.getValue();
                Object priceObj = component.get("price");
                if (priceObj instanceof Number) {
                    double price = ((Number) priceObj).doubleValue();
                    if (price > maxPrice) {
                        maxPrice = price;
                        Object nameObj = component.get("name");
                        if (nameObj instanceof String) {
                            String name = (String) nameObj;
                            // Truncate long names
                            if (name.length() > 30) {
                                name = name.substring(0, 27) + "...";
                            }
                            mainComponent = name;
                        }
                    }
                }
            }
        }
        
        return mainComponent;
    }
}