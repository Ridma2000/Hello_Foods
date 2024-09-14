package com.example.final_project;

public class Banner {
    private String url;

    // Default constructor required for calls to DataSnapshot.getValue(Banner.class)
    public Banner() {
    }

    // Constructor
    public Banner(String url) {
        this.url = url;
    }

    // Getter and Setter
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
