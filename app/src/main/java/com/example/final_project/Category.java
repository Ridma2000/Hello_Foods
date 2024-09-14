package com.example.final_project;

public class Category {
    private String title;
    private String picUrl;

    // Default constructor required for calls to DataSnapshot.getValue(Category.class)
    public Category() {}

    public Category(String title, String picUrl) {
        this.title = title;
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
