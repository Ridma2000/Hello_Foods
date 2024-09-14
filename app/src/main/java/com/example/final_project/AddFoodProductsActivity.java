package com.example.final_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFoodProductsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    public static FoodProduct[] foodProductList;

    private ImageView imageView;
    private EditText editTextName;
    private EditText editTextPrice;
    private EditText editTextDescription;
    private Button buttonUpload;
    private Button buttonAddProduct;
    private Button buttonDecrease;
    private Button buttonIncrease;
    private TextView textQuantity;

    private Uri imageUri;
    private int quantity = 0;  // Default quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_products);

        imageView = findViewById(R.id.img_upload_food);
        editTextName = findViewById(R.id.edit_food_name);
        editTextPrice = findViewById(R.id.edit_food_price);
        editTextDescription = findViewById(R.id.edit_food_description);
        buttonUpload = findViewById(R.id.btn_upload_image);
        buttonAddProduct = findViewById(R.id.btn_submit);
        buttonDecrease = findViewById(R.id.button_decrease);
        buttonIncrease = findViewById(R.id.button_increase);
        textQuantity = findViewById(R.id.text_quantity);

        // Handle image upload
        buttonUpload.setOnClickListener(v -> openFileChooser());

        // Handle adding the product
        buttonAddProduct.setOnClickListener(v -> addProduct());

        // Handle quantity change
        buttonDecrease.setOnClickListener(v -> changeQuantity(-1));
        buttonIncrease.setOnClickListener(v -> changeQuantity(1));
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void addProduct() {
        String name = editTextName.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the image to Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("food_images/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Create a FoodProduct object with the image URL and quantity
                    FoodProduct product = new FoodProduct(name, price, quantity, description, imageUrl);

                    // Save the product information to Firebase Realtime Database
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("food_items");
                    String productId = databaseReference.push().getKey();

                    if (productId != null) {
                        databaseReference.child(name).setValue(product).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddFoodProductsActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                clearFields();
                            } else {
                                Toast.makeText(AddFoodProductsActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }))
                .addOnFailureListener(exception -> {
                    Toast.makeText(AddFoodProductsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void changeQuantity(int delta) {
        quantity += delta;
        if (quantity < 0) {
            quantity = 0; // Prevent negative quantity
        }
        textQuantity.setText(String.valueOf(quantity));
    }

    private void clearFields() {
        imageView.setImageURI(null);
        editTextName.setText("");
        editTextPrice.setText("");
        editTextDescription.setText("");
        textQuantity.setText("0");
        imageUri = null;
        quantity = 0; // Reset quantity
    }
}
