package com.app.pccooker;

import com.app.pccooker.models.PCComponent;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;


    private final List<PCComponent> savedItems = new ArrayList<>();



    private final List<PCComponent> cartItems = new ArrayList<>();
    private final List<PCComponent> savedForLater = new ArrayList<>();


    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(PCComponent component) {
        if (component == null || component.getId() == null) return;

        for (PCComponent item : cartItems) {
            if (component.getId().equals(item.getId())) {
                // Already added, do nothing
                return;
            }
        }

        PCComponent copy = new PCComponent(component);
        copy.setQuantity(1);
        cartItems.add(copy);
    }

    public void saveForLater(PCComponent component) {
        if (component == null || component.getId() == null) return;

        for (PCComponent item : savedItems) {
            if (component.getId().equals(item.getId())) {
                return; // Already saved
            }
        }

        savedItems.add(new PCComponent(component));
        removeFromCart(component);
    }

/*
    public void moveToCart(PCComponent component) {
        if (component == null || component.getId() == null) return;

        removeFromSaved(component);

        boolean exists = false;
        for (PCComponent item : cartItems) {
            if (component.getId().equals(item.getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            PCComponent copy = new PCComponent(component);
            copy.setQuantity(1);
            cartItems.add(copy);
        }
    }
*/

    public void removeFromCart(PCComponent component) {
        cartItems.removeIf(item -> component.getId().equals(item.getId()));
    }

    public void removeFromSaved(PCComponent component) {
        savedItems.removeIf(item -> component.getId().equals(item.getId()));
    }

    public List<PCComponent> getCartItems() {
        return cartItems;
    }

    public List<PCComponent> getSavedItems() {
        return savedItems;
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public int getCartTotal() {
        int total = 0;
        for (PCComponent item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public void updateQuantity(PCComponent component, int newQuantity) {
        for (PCComponent item : cartItems) {
            if (component.getId().equals(item.getId())) {
                item.setQuantity(newQuantity);
                break;
            }
        }
    }

    public void clearAll() {
        cartItems.clear();
        savedItems.clear();
    }

    // ✅ Get items saved for later
    public List<PCComponent> getSavedForLaterItems() {
        return savedForLater;
    }

    // ✅ Move item from cart to saved list
    public void moveToSaved(PCComponent component) {
        if (component != null && cartItems.removeIf(item -> item.getId().equals(component.getId()))) {
            savedForLater.add(new PCComponent(component));  // clone
        }
    }

    // ✅ Move item from saved to cart
    public void moveToCart(PCComponent component) {
        if (component != null && savedForLater.removeIf(item -> item.getId().equals(component.getId()))) {
            addToCart(component); // add back to cart
        }
    }

    // ✅ Check if an item is already in the cart
    public boolean isInCart(PCComponent component) {
        for (PCComponent item : cartItems) {
            if (item.getId() != null && item.getId().equals(component.getId())) {
                return true;
            }
        }
        return false;
    }

    // ✅ Check if an item is already saved for later
    public boolean isSavedForLater(PCComponent component) {
        for (PCComponent item : savedForLater) {
            if (item.getId() != null && item.getId().equals(component.getId())) {
                return true;
            }
        }
        return false;
    }

}
