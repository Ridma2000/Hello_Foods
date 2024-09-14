package com.example.final_project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditFoodItemsActivity extends AppCompatActivity {

    private ListView listViewFoodItems;
    private EditText editTextPrice, editTextQuantity;
    private List<FoodProduct> foodProductList;
    private ArrayAdapter<String> adapter;
    private String selectedFoodItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_items);

        listViewFoodItems = findViewById(R.id.listViewFoodItems);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        foodProductList = new ArrayList<>();

        fetchFoodItemsFromFirebase();

        listViewFoodItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FoodProduct selectedFood = foodProductList.get(position);
                selectedFoodItemId = selectedFood.getName(); // Assuming FoodProduct has a unique ID
                editTextPrice.setText(String.valueOf(selectedFood.getPrice()));
                editTextQuantity.setText(String.valueOf(selectedFood.getQuantity())); // Updated to use Long
            }
        });
    }

    private void fetchFoodItemsFromFirebase() {
        DatabaseReference foodItemsRef = FirebaseDatabase.getInstance().getReference("food_items");

        foodItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodProductList.clear();
                List<String> foodNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodProduct foodProduct = snapshot.getValue(FoodProduct.class);
                    if (foodProduct != null) {
                        foodProductList.add(foodProduct);
                        if(foodProduct.getName() != null) {
                            foodNames.add(foodProduct.getName());
                        }
                        else{
                            foodNames.add(".");
                        }
                    }
                }
                adapter = new ArrayAdapter<>(EditFoodItemsActivity.this, android.R.layout.simple_list_item_1, foodNames);
                listViewFoodItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditFoodItemsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSaveChanges(View view) {
        String updatedPrice = editTextPrice.getText().toString();
        String updatedQuantity = editTextQuantity.getText().toString();

        if (selectedFoodItemId != null) {
            DatabaseReference foodItemRef = FirebaseDatabase.getInstance().getReference("food_items").child(selectedFoodItemId);
            try {
                foodItemRef.child("price").setValue(Double.parseDouble(updatedPrice));
                foodItemRef.child("quantity").setValue(Integer.parseInt(updatedQuantity)); // Ensure quantity is Long

                Toast.makeText(EditFoodItemsActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(EditFoodItemsActivity.this, "Invalid input for price or quantity", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditFoodItemsActivity.this, "Please select an item first", Toast.LENGTH_SHORT).show();
        }
    }
}
