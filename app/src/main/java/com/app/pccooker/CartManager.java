package com.app.pccooker;

import android.content.Context;
import android.widget.Toast;
import com.app.pccooker.ComponentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private final List<ComponentModel> cartItems = new ArrayList<>();
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context context;

    private CartManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void addToCart(ComponentModel component) {
        // Check if component already exists in cart
        for (ComponentModel item : cartItems) {
            if (item.getId().equals(component.getId())) {
                Toast.makeText(context, "Component already in cart", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        cartItems.add(component);
        saveCartToFirebase();
        Toast.makeText(context, component.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    public void removeFromCart(String componentId) {
        cartItems.removeIf(item -> item.getId().equals(componentId));
        saveCartToFirebase();
    }

    public void clearCart() {
        cartItems.clear();
        saveCartToFirebase();
    }

    public List<ComponentModel> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getCartTotal() {
        double total = 0;
        for (ComponentModel item : cartItems) {
            total += item.getPrice();
        }
        return total;
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public boolean isInCart(ComponentModel component) {
        for (ComponentModel item : cartItems) {
            if (item.getId().equals(component.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSavedForLater(ComponentModel component) {
        // TODO: Implement saved for later functionality
        return false;
    }

    public void saveForLater(ComponentModel component) {
        // Add to saved items in Firebase
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        Map<String, Object> savedItem = new HashMap<>();
        savedItem.put("id", component.getId());
        savedItem.put("name", component.getName());
        savedItem.put("category", component.getCategory());
        savedItem.put("price", component.getPrice());
        savedItem.put("brand", component.getBrand());
        savedItem.put("imageUrl", component.getImageUrl());
        savedItem.put("amazonUrl", component.getAmazonUrl());
        savedItem.put("description", component.getDescription());
        savedItem.put("rating", component.getRating());
        savedItem.put("ratingCount", component.getRatingCount());
        savedItem.put("inStock", component.isInStock());
        savedItem.put("model", component.getModel());
        savedItem.put("specifications", component.getSpecifications());

        db.collection("users").document(userId)
            .collection("saved_items")
            .add(savedItem)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(context, component.getName() + " saved for later", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to save item", Toast.LENGTH_SHORT).show();
            });
    }

    public void updateQuantity(String componentId, int newQuantity) {
        // Update quantity in Firebase
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        db.collection("users").document(userId)
            .collection("cart")
            .document("items")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> cartData = (List<Map<String, Object>>) documentSnapshot.getData().get("items");
                    if (cartData != null) {
                        for (Map<String, Object> item : cartData) {
                            if (componentId.equals(item.get("id"))) {
                                item.put("quantity", newQuantity);
                                break;
                            }
                        }
                        
                        // Update the cart
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("items", cartData);
                        
                        db.collection("users").document(userId)
                            .collection("cart")
                            .document("items")
                            .set(updateData);
                    }
                }
            });
    }

    private void saveCartToFirebase() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        List<Map<String, Object>> cartData = new ArrayList<>();
        for (ComponentModel component : cartItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", component.getId());
            itemData.put("name", component.getName());
            itemData.put("category", component.getCategory());
            itemData.put("price", component.getPrice());
            itemData.put("brand", component.getBrand());
            itemData.put("imageUrl", component.getImageUrl());
            itemData.put("amazonUrl", component.getAmazonUrl());
            itemData.put("description", component.getDescription());
            itemData.put("rating", component.getRating());
            itemData.put("ratingCount", component.getRatingCount());
            itemData.put("inStock", component.isInStock());
            itemData.put("model", component.getModel());
            itemData.put("specifications", component.getSpecifications());
            cartData.add(itemData);
        }

        db.collection("users").document(userId)
            .collection("cart")
            .document("items")
            .set(cartData)
            .addOnSuccessListener(aVoid -> {
                // Cart saved successfully
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to save cart", Toast.LENGTH_SHORT).show();
            });
    }

    public void loadCartFromFirebase(OnCartLoadedListener listener) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            if (listener != null) listener.onCartLoaded(new ArrayList<>());
            return;
        }

        db.collection("users").document(userId)
            .collection("cart")
            .document("items")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                cartItems.clear();
                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    List<Map<String, Object>> cartData = (List<Map<String, Object>>) documentSnapshot.getData().get("items");
                    if (cartData != null) {
                        for (Map<String, Object> itemData : cartData) {
                            ComponentModel component = new ComponentModel(
                                (String) itemData.get("id"),
                                (String) itemData.get("name"),
                                (String) itemData.get("category"),
                                ((Number) itemData.get("price")).doubleValue(),
                                ((Number) itemData.get("rating")).doubleValue(),
                                ((Number) itemData.get("ratingCount")).intValue(),
                                (String) itemData.get("imageUrl"),
                                (String) itemData.get("amazonUrl"),
                                (String) itemData.get("description"),
                                (Map<String, String>) itemData.get("specifications"),
                                (Boolean) itemData.get("inStock"),
                                (String) itemData.get("brand"),
                                (String) itemData.get("model")
                            );
                            cartItems.add(component);
                        }
                    }
                }
                if (listener != null) listener.onCartLoaded(new ArrayList<>(cartItems));
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onCartLoaded(new ArrayList<>());
            });
    }

    public interface OnCartLoadedListener {
        void onCartLoaded(List<ComponentModel> cartItems);
    }
}
