package com.example.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName, editUsername, editPassword;
    private TextView textEmail;
    private Button saveButton, fetchButton, deleteButton;
    private ImageButton togglePasswordButton;
    private boolean isPasswordVisible = false;
    private DatabaseReference userRef;
    private String originalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            getSupportActionBar().setTitle("My Profile");
            toolbar.setTitleTextColor(Color.WHITE);
        }

        // Initialize views
        editName = findViewById(R.id.edit_name);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        textEmail = findViewById(R.id.text_email);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        togglePasswordButton = findViewById(R.id.toggle_password_button);

        loadUserProfile(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        togglePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void loadUserProfile(String username) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (email != null && email.equals(username)) {
                        String userName = userSnapshot.child("username").getValue(String.class);
                        String name = userSnapshot.child("name").getValue(String.class);
                        String password = userSnapshot.child("password").getValue(String.class);

                        editUsername.setText(userName);
                        editName.setText(name);
                        editPassword.setText(password);
                        textEmail.setText(email); // Email is displayed but not editable

                        originalUsername = username; // Store the original username
                        userFound = true;
                        break; // Exit loop once user is found
                    }
                }

                if (!userFound) {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void saveUserProfile() {
        String newName = editName.getText().toString().trim();
        String newUsername = editUsername.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        if (newName.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newUsername.equals(originalUsername)) {
            updateUsername(newName, newUsername, newPassword);
        } else {
            updateUserProfile(newName, newPassword);
        }
    }

    private void updateUsername(String newName, String newUsername, String newPassword) {
        DatabaseReference newUserRef = FirebaseDatabase.getInstance().getReference("users").child(newUsername);
        newUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(ProfileActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                } else {
                    // Copy the data to the new username
                    newUserRef.child("name").setValue(newName);
                    newUserRef.child("password").setValue(newPassword);
                    newUserRef.child("email").setValue(textEmail.getText().toString().trim());

                    // Delete the old username
                    userRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            originalUsername = newUsername; // Update the original username reference
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void updateUserProfile(String newName, String newPassword) {
        userRef.child("name").setValue(newName);
        userRef.child("password").setValue(newPassword);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUserAccount();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUserAccount() {
        String username = editUsername.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, UserLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Close the ProfileActivity
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            togglePasswordButton.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Show password
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            togglePasswordButton.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        // Move cursor to the end of the text
        editPassword.setSelection(editPassword.getText().length());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
