package com.app.pccooker;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.PropertyName;
import java.util.HashMap;
import java.util.Map;

public class ComponentModel implements Parcelable {
    @PropertyName("id")
    private String id = "";
    @PropertyName("name")
    private String name = "";
    @PropertyName("category")
    private String category = "";
    @PropertyName("price")
    private double price = 0.0;
    @PropertyName("rating")
    private double rating = 0.0;
    @PropertyName("ratingCount")
    private int ratingCount = 0;
    @PropertyName("imageUrl")
    private String imageUrl = "";
    @PropertyName("productUrl")
    private String productUrl = "";
    @PropertyName("description")
    private String description = "";
    @PropertyName("specifications")
    private Map<String, String> specifications = new HashMap<>();
    @PropertyName("inStock")
    private boolean inStock = true;
    @PropertyName("brand")
    private String brand = "";
    @PropertyName("model")
    private String model = "";

    // Default constructor for Firebase
    public ComponentModel() {}

    // Parcelable constructor
    protected ComponentModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        price = in.readDouble();
        rating = in.readDouble();
        ratingCount = in.readInt();
        imageUrl = in.readString();
        productUrl = in.readString();
        description = in.readString();
        inStock = in.readByte() != 0;
        brand = in.readString();
        model = in.readString();
        
        // Read specifications map
        int specSize = in.readInt();
        specifications = new HashMap<>();
        for (int i = 0; i < specSize; i++) {
            String key = in.readString();
            String value = in.readString();
            specifications.put(key, value);
        }
    }

    // Parcelable implementation
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeInt(ratingCount);
        dest.writeString(imageUrl);
        dest.writeString(productUrl);
        dest.writeString(description);
        dest.writeByte((byte) (inStock ? 1 : 0));
        dest.writeString(brand);
        dest.writeString(model);
        
        // Write specifications map
        dest.writeInt(specifications.size());
        for (Map.Entry<String, String> entry : specifications.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ComponentModel> CREATOR = new Creator<ComponentModel>() {
        @Override
        public ComponentModel createFromParcel(Parcel in) {
            return new ComponentModel(in);
        }

        @Override
        public ComponentModel[] newArray(int size) {
            return new ComponentModel[size];
        }
    };



    // Getters and setters (one per property)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, String> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, String> specifications) { this.specifications = specifications; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
} 