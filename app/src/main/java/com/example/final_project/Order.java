package com.example.final_project;

public class Order {
    private String orderId;
    private String customerName;
    private String orderItems;
    private double totalAmount;
    private UserDetails userDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public Order(String orderId, String customerName, String orderItems, double totalAmount, UserDetails userDetails) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.userDetails = userDetails;
    }

    public Order(String orderId, String customerName, String orderItems, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderItems() {
        return orderItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Order ID: " + orderId;
    }
}

