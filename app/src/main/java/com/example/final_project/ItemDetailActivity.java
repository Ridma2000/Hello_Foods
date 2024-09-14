package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ItemDetailActivity extends AppCompatActivity {
    private ImageView itemImageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private FirebaseAuth mAuth;
    private TextView quantityTextView;
    private TextView priceTextView;
    private Button decreaseButton;
    private Button increaseButton;
    private Button addToCartButton;
    private Button goToCartButton;  // Button to navigate to the cart

    private int quantity = 0; // Default quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve user email
        String userEmail = getUserEmailFromFirebase();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            getSupportActionBar().setTitle("Item Description");
            toolbar.setTitleTextColor(Color.WHITE);
        }

        // Initialize views
        itemImageView = findViewById(R.id.item_image);
        titleTextView = findViewById(R.id.item_title);
        priceTextView = findViewById(R.id.price);
        descriptionTextView = findViewById(R.id.item_description);
        quantityTextView = findViewById(R.id.text_quantity);
        decreaseButton = findViewById(R.id.button_decrease);
        increaseButton = findViewById(R.id.button_increase);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        goToCartButton = findViewById(R.id.go_to_cart_button); // Initialize the Go to Cart button

        // Get data from intent
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("item_image");
        String title = intent.getStringExtra("item_title");
        String price = intent.getStringExtra("price");
        String description = intent.getStringExtra("item_description");

        // Set data to views
        Glide.with(this).load(imageUrl).into(itemImageView);
        titleTextView.setText(title);
        priceTextView.setText("Price: " + price);
        descriptionTextView.setText(description);

        // Handle quantity increase
        increaseButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });

        // Handle quantity decrease
        decreaseButton.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        // Handle add to cart button click
        addToCartButton.setOnClickListener(v -> {
            // Retrieve user email from Firebase (replace with your method to get user email)
           // Implement this method as per your setup

            // Retrieve or create product list from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
            Gson gson = new Gson();

             // Assuming 'title' is the product name and 'quantity' is the quantity
            FoodProduct product = new FoodProduct();
            product.setName(title);
            product.setDescription(description);
            product.setPrice(Double.valueOf(price));
            product.setQuantity(quantity);
            product.setImageUrl(imageUrl);
            Global.foodProductArrayList.add(product);

            // Convert the updated list to JSON and save it back to SharedPreferences
            String json = gson.toJson(Global.foodProductArrayList);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(userEmail, json);
            editor.apply(); // Save the changes

            // Start the CartActivity
            Intent cartIntent = new Intent(ItemDetailActivity.this, MainActivity.class);
            startActivity(cartIntent);
        });


        // Handle go to cart button click
        goToCartButton.setOnClickListener(v -> {
            // Directly navigate to the CartActivity
            Intent cartIntent = new Intent(ItemDetailActivity.this, CartActivity.class);
            startActivity(cartIntent);
        });
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
