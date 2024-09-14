package com.example.final_project;

public class FoodProduct {
    private String name;
    private double price;
    private int quantity;
    private String description;
    private String imageUrl;

    public FoodProduct() {
        // Default constructor required for calls to DataSnapshot.getValue(FoodProduct.class)
    }

    public FoodProduct(String name, double price, int quantity, String description, String imageUrl) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Override
    public String toString() {
        return name; // or any other field that you want to display
    }
}
