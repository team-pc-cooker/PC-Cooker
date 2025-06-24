package com.app.pccooker.models;

public class PCComponent {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private int price;
    private int quantity;
    private int rating;

    // ✅ Empty constructor for Firebase and deserialization
    public PCComponent() {}

    // ✅ 7-argument constructor for typical use (no rating)
    public PCComponent(String id, String name, String description, String imageUrl, String category, int price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    // ✅ Full constructor with rating (optional use)
    public PCComponent(String id, String name, String description, int price, String imageUrl, int quantity, int rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.rating = rating;
    }

    // ✅ Copy constructor
    public PCComponent(PCComponent other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.imageUrl = other.imageUrl;
        this.price = other.price;
        this.quantity = other.quantity;
        this.category = other.category;
        this.rating = other.rating;
    }



    // --- Getters ---
    public String getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getImageUrl() { return imageUrl; }

    public String getCategory() { return category; }

    public int getPrice() { return price; }

    public int getQuantity() { return quantity; }

    public int getRating() { return rating; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void setCategory(String category) { this.category = category; }

    public void setPrice(int price) { this.price = price; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setRating(int rating) { this.rating = rating; }
}
