package com.example.final_project;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewOrdersActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private Spinner spinnerOrders;
    private TextView textViewOrderDetails;
    private Button buttonConfirmOrder;

    private List<Order> ordersList; // List to store all orders
    private Order selectedOrder; // Currently selected order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        spinnerOrders = findViewById(R.id.spinnerOrders);
        textViewOrderDetails = findViewById(R.id.textViewOrderDetails);
        buttonConfirmOrder = findViewById(R.id.buttonConfirmOrder);

        // Retrieve all orders from SharedPreferences
        SharedPreferences orderPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        ordersList = new ArrayList<>();
        Map<String, ?> allOrders = orderPreferences.getAll();

        Gson gson = new Gson();
        for (Map.Entry<String, ?> entry : allOrders.entrySet()) {
            String orderJson = entry.getValue().toString();
            OrderDetails orderDetails = gson.fromJson(orderJson, OrderDetails.class);
            if (orderDetails != null ) {
                ordersList.add(new Order(entry.getKey(), orderDetails.getUserDetails().getName(), orderDetails.getCartItems().toString(), calculateTotalAmount(orderDetails.getCartItems()),orderDetails.getUserDetails()));
            }
        }

        // Set up the spinner with order data
        ArrayAdapter<Order> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ordersList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrders.setAdapter(adapter);

        // Handle spinner item selection
        spinnerOrders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrder = (Order) parent.getItemAtPosition(position);
                displayOrderDetails(selectedOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textViewOrderDetails.setText("No order selected.");
            }
        });

        // Handle confirm order button click
        buttonConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder(selectedOrder);
            }
        });
    }
    // Method to calculate the total amount of an order
    private double calculateTotalAmount(List<FoodProduct> orderItems) {
        double totalAmount = 0;
        for (FoodProduct item : orderItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        return totalAmount;
    }

    // Method to display the selected order's details
    private void displayOrderDetails(Order order) {
        String details = "Order ID: " + order.getOrderId() + "\n" +
                "Customer Name: " + order.getCustomerName() + "\n" +
                "Items: " + order.getOrderItems() + "\n" +
                "Total: Rs." + order.getTotalAmount() + "\n" +
                "Contact No " + order.getUserDetails().getContact();
        textViewOrderDetails.setText(details);
    }

    // Method to confirm the order and notify the customer
    private void confirmOrder(Order order) {
        if (order != null && order.getOrderId() != null) {
            // Firebase update code...

            // Check for SMS permission
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Request SMS permission
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                // Permission already granted, send the SMS
                sendOrderConfirmationSMS(order.getUserDetails().getContact());
            }

            // Notify the user that the order is confirmed
            Toast.makeText(ViewOrdersActivity.this, "Order confirmed! Message sent to " + order.getCustomerName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ViewOrdersActivity.this, "No order selected to confirm.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, send the SMS.
                    sendOrderConfirmationSMS(selectedOrder.getUserDetails().getContact());
                } else {
                    // Permission denied, show a message to the user.
                    Toast.makeText(getApplicationContext(), "SMS permission denied.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    private void sendOrderConfirmationSMS(String customerContactNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String shopContactNumber = "0713464048"; // Replace with your shop's contact number
            String message = "Your order is ready, come and pick up.";
            smsManager.sendTextMessage(customerContactNumber, shopContactNumber, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
