package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize buttons
        Button addProductButton = findViewById(R.id.btn_add_product);
        Button editProductButton = findViewById(R.id.btn_edit_product);
        Button viewOrdersButton = findViewById(R.id.btn_view_orders);
        Button logoutButton = findViewById(R.id.btn_logout);

        // Set click listeners for each button
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Add Food Products Activity
                Intent intent = new Intent(AdminActivity.this, AddFoodProductsActivity.class);
                startActivity(intent);
            }
        });

        editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Edit Food Items Activity
                Intent intent = new Intent(AdminActivity.this, EditFoodItemsActivity.class);
                startActivity(intent);
            }
        });

        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open View Orders Activity
                Intent intent = new Intent(AdminActivity.this, ViewOrdersActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminActivity.this, UserLogin.class);
                startActivity(intent);
                finish(); // Close the admin activity
            }
        });
    }
}
