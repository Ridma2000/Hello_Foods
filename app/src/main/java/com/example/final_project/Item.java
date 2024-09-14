package com.example.final_project;

import java.util.List;

public class Item {
    private String title;
    private List<String> picUrl;
    //private double price;
    private String price;

    private double oldPrice;  // Added oldPrice field
    private String category;  // Category to filter
    private String description;  // Added description field

    // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    public Item() {}

    // Updated constructor with oldPrice and description
    public Item(String title, List<String> picUrl, String price, double oldPrice, String category, String description) {
        this.title = title;
        this.picUrl = picUrl;
        this.price = price;
        this.oldPrice = oldPrice;
        this.category = category;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price; // this.price should be accessible
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageUrl() {
        return new byte[0];
    }


}
