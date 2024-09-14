package com.example.final_project;

public class UserDetails {
    private String name;
    private String email;
    private String contact;

    public UserDetails(String name, String email, String contact) {
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }
}
