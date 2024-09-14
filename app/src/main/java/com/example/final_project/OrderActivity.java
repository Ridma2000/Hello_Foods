package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView orderRecyclerView;
    private ItemAdapter orderAdapter;
    private OrderDetails orderDetails;
    private TextView name;
    private TextView email;
    private TextView contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderRecyclerView = findViewById(R.id.recyclerView);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve order details from SharedPreferences
        SharedPreferences orderPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        String orderJson = orderPreferences.getString("order_data", "");
        // orderid contain userEmail
        if (!orderJson.isEmpty()) {
            Gson gson = new Gson();
            orderDetails = gson.fromJson(orderJson, OrderDetails.class);

            // Log to verify JSON data
            Log.d("OrderActivity", "Order JSON: " + orderJson);

            // Display order details in RecyclerView
            if (orderDetails != null) {
                orderAdapter = new ItemAdapter(this, orderDetails.getCartItems());
                orderRecyclerView.setAdapter(orderAdapter);
                name.setText(orderDetails.getUserDetails().getName());
                email.setText(orderDetails.getUserDetails().getEmail());
                contact.setText(orderDetails.getUserDetails().getContact());
                // Optionally display customer data elsewhere in the UI
                // Example: TextView customerName = findViewById(R.id.customer_name);
                // customerName.setText(orderDetails.getUserDetails().getName());
            }
        }
    }
}
