package com.app.pccooker;

import android.content.Context;
import android.widget.Toast;
import com.app.pccooker.ComponentModel;
import com.app.pccooker.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context context;
    private CartUpdateListener cartUpdateListener;

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
        for (CartItem item : cartItems) {
            if (item.getId().equals(component.getId())) {
                // Don't increment quantity - just show message
                Toast.makeText(context, component.getName() + " is already in cart", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Create new cart item with quantity 1
        CartItem cartItem = new CartItem(
            component.getId(),
            component.getName(),
            component.getImageUrl(),
            component.getPrice()
        );
        cartItem.setDescription(component.getDescription());
        cartItem.setRating(component.getRating());
        cartItem.setQuantity(1); // Always start with quantity 1
        
        cartItems.add(cartItem);
        saveCartToFirebase();
        Toast.makeText(context, component.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        
        // Notify MainActivity to update cart badge
        notifyCartUpdated();
    }

    public void removeFromCart(String componentId) {
        cartItems.removeIf(item -> item.getId().equals(componentId));
        saveCartToFirebase();
        notifyCartUpdated();
    }

    public void clearCart() {
        cartItems.clear();
        saveCartToFirebase();
        notifyCartUpdated();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public boolean isInCart(ComponentModel component) {
        for (CartItem item : cartItems) {
            if (item.getId().equals(component.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSavedForLater(ComponentModel component) {
        // This will be checked against Firebase saved items
        // For now, return false - will be implemented with proper Firebase check
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
        savedItem.put("amazonUrl", component.getProductUrl());
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
        for (CartItem item : cartItems) {
            if (item.getId().equals(componentId)) {
                item.setQuantity(newQuantity);
                break;
            }
        }
        saveCartToFirebase();
        notifyCartUpdated();
    }

    private void saveCartToFirebase() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        List<Map<String, Object>> cartData = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", cartItem.getId());
            itemData.put("name", cartItem.getName());
            itemData.put("imageUrl", cartItem.getImageUrl());
            itemData.put("price", cartItem.getPrice());
            itemData.put("quantity", cartItem.getQuantity());
            itemData.put("description", cartItem.getDescription());
            itemData.put("rating", cartItem.getRating());
            cartData.add(itemData);
        }

        Map<String, Object> cartDocument = new HashMap<>();
        cartDocument.put("items", cartData);

        db.collection("users").document(userId)
            .collection("cart")
            .document("items")
            .set(cartDocument)
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
            // Clear local cart when no user is logged in
            cartItems.clear();
            if (listener != null) listener.onCartLoaded(new ArrayList<>());
            return;
        }

        db.collection("users").document(userId)
            .collection("cart")
            .document("items")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                cartItems.clear(); // Always clear first to prevent duplicates
                
                // Only load items if document exists and has valid data
                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    Object itemsObj = documentSnapshot.getData().get("items");
                    if (itemsObj instanceof List) {
                        List<Map<String, Object>> cartData = (List<Map<String, Object>>) itemsObj;
                        
                        for (Map<String, Object> itemData : cartData) {
                            try {
                                // Validate required fields
                                String id = (String) itemData.get("id");
                                String name = (String) itemData.get("name");
                                if (id == null || name == null) continue;
                                
                                CartItem cartItem = new CartItem(
                                    id,
                                    name,
                                    (String) itemData.getOrDefault("imageUrl", ""),
                                    itemData.get("price") != null ? ((Number) itemData.get("price")).doubleValue() : 0.0
                                );
                                
                                // Set quantity (default to 1 if not present or invalid)
                                Object quantityObj = itemData.get("quantity");
                                int quantity = 1;
                                if (quantityObj instanceof Number) {
                                    quantity = Math.max(1, ((Number) quantityObj).intValue());
                                }
                                cartItem.setQuantity(quantity);
                                
                                // Set optional fields
                                if (itemData.get("description") != null) {
                                    cartItem.setDescription((String) itemData.get("description"));
                                }
                                if (itemData.get("rating") instanceof Number) {
                                    cartItem.setRating(((Number) itemData.get("rating")).doubleValue());
                                }
                                
                                cartItems.add(cartItem);
                            } catch (Exception e) {
                                // Skip invalid items
                                System.err.println("Error loading cart item: " + e.getMessage());
                            }
                        }
                    }
                }
                
                if (listener != null) listener.onCartLoaded(new ArrayList<>(cartItems));
            })
            .addOnFailureListener(e -> {
                System.err.println("Failed to load cart: " + e.getMessage());
                cartItems.clear(); // Clear on error
                if (listener != null) listener.onCartLoaded(new ArrayList<>());
            });
    }

    public interface OnCartLoadedListener {
        void onCartLoaded(List<CartItem> cartItems);
    }
    
    public interface CartUpdateListener {
        void onCartUpdated();
    }
    
    public void setCartUpdateListener(CartUpdateListener listener) {
        this.cartUpdateListener = listener;
    }
    
    private void notifyCartUpdated() {
        if (cartUpdateListener != null) {
            cartUpdateListener.onCartUpdated();
        }
    }
}
