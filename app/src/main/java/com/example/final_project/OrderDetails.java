package com.example.final_project;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class OrderDetails {
    private UserDetails userDetails;
    private List<FoodProduct> cartItems;

    public OrderDetails(UserDetails userDetails, List<FoodProduct> cartItems) {
        this.userDetails = userDetails;
        this.cartItems = cartItems;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public List<FoodProduct> getCartItems() {
        return cartItems;
    }

    private double calculateTotalAmount(List<FoodProduct> orderItems) {
        double totalAmount = 0;
        for (FoodProduct item : orderItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        return totalAmount;
    }

}
