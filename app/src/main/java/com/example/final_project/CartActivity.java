package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartListView;
    String userEmail;
    private ItemAdapter itemAdapter;
    private FirebaseAuth mAuth;
    private List<FoodProduct> cartItems;
    private Button confirmOrderButton;
    private TextView customerName, customerEmail, customerContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();

        // Retrieve user email
        userEmail = getUserEmailFromFirebase();

        confirmOrderButton = findViewById(R.id.confirm_order_button);
        customerName = findViewById(R.id.customer);
        customerEmail = findViewById(R.id.customer_email);
        customerContact = findViewById(R.id.customer_contact);
        cartListView = findViewById(R.id.recyclerView);

        // Initialize cart items and adapter
        cartItems = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, cartItems);
        cartListView.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager
        cartListView.setAdapter(itemAdapter);  // Set the adapter to the RecyclerView

        // Retrieve product list from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(userEmail, "[]"); // Default to empty JSON array
        Type type = new TypeToken<ArrayList<FoodProduct>>() {}.getType();
        ArrayList<FoodProduct> productList = gson.fromJson(json, type);

        // Log to verify JSON data
        Log.d("CartActivity", "Stored JSON: " + json);

        // Update the cart items and notify the adapter
        if (productList != null && !productList.isEmpty()) {
            cartItems.clear();
            cartItems.addAll(productList);
            itemAdapter.notifyDataSetChanged();  // Notify the adapter about the updated data
        }

        confirmOrderButton.setOnClickListener(v -> confirmOrder());
    }


    private String getUserEmailFromFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        } else {
            // Handle the case when no user is logged in
            return "guest@example.com"; // Default or handle accordingly
        }
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("product_name", "No name");
        int quantity = sharedPreferences.getInt("product_quantity", 0);

        if (!name.equals("No name")) {
            FoodProduct item = new FoodProduct(name, 0, quantity, "", "");
            cartItems.add(item);
            itemAdapter.updateItemList(cartItems);
        }
    }

    private void removeItemFromCart(FoodProduct item) {
        cartItems.remove(item);
        itemAdapter.updateItemList(cartItems);
    }

    private void confirmOrder() {
        // Get customer data
        String name = customerName.getText().toString();
        String email = customerEmail.getText().toString();
        String contact = customerContact.getText().toString();

        // Validate customer data
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            // Display a toast message
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
            if (contact.isEmpty()) {
                Toast.makeText(this, "Please enter your contact number", Toast.LENGTH_SHORT).show();
            }
            return; // Do not proceed if validation fails
        }

        // Store customer and order details in SharedPreferences
        SharedPreferences orderPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor orderEditor = orderPreferences.edit();

        UserDetails userDetails = new UserDetails(name, email, contact);
        OrderDetails orderDetails = new OrderDetails(userDetails, cartItems);

        Gson gson = new Gson();
        String orderJson = gson.toJson(orderDetails);
        orderEditor.putString("order_data", orderJson);
        orderEditor.apply();

        // Generate a unique order ID
        String orderId =  userEmail + "_Order_" + System.currentTimeMillis();
        orderEditor.putString(orderId, orderJson);
        orderEditor.apply();

        // Clear the cart preferences
        SharedPreferences cartPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor cartEditor = cartPreferences.edit();
        cartEditor.clear();
        cartEditor.apply();

        // Navigate to OrderActivity
        Intent intent = new Intent(CartActivity.this, OrderActivity.class);
        startActivity(intent);
        finish();
    }
}

