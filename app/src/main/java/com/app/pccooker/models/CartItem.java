package com.app.pccooker.models;

public class CartItem {
    private String id;
    private String name;
    private String imageUrl;
    private double price;
    private int quantity;
    private String description;
    private double rating;

    public CartItem() {} // Needed for Firebase

    public CartItem(String id, String name, String imageUrl, double price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = 1; // Default quantity
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    /**
     * Convert CartItem to ComponentModel for compatibility
     */
    public ComponentModel toComponentModel() {
        ComponentModel component = new ComponentModel();
        component.setId(this.id);
        component.setName(this.name);
        component.setImageUrl(this.imageUrl);
        component.setPrice(this.price);
        component.setDescription(this.description);
        component.setRating(this.rating);
        return component;
    }
}
