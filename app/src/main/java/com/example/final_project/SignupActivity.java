package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword, signupConfirmPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() || !validateUsername() || !validatePassword() || !validateConfirmPassword()) {
                    return;
                }

                database = FirebaseDatabase.getInstance();

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                // Create a new user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getReference("users");

                                // Creating a unique key for the new user document
                                String userId = userRef.push().getKey();
                                HelperClass helperClass = new HelperClass(name, email, username, password);

                                if (userId != null) {
                                    userRef.child(userId).setValue(helperClass).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(SignupActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                                            // Navigate to the login activity
                                            Intent intent = new Intent(SignupActivity.this, UserLogin.class);
                                            startActivity(intent);
                                        } else {
                                            // Failed to save data
                                            Toast.makeText(SignupActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, UserLogin.class);
                startActivity(intent);
            }
        });
    }

    private Boolean validateEmail() {
        String val = signupEmail.getText().toString();
        if (val.isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            signupEmail.setError("Please enter a valid email address");
            return false;
        } else {
            signupEmail.setError(null);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = signupUsername.getText().toString();
        if (val.isEmpty()) {
            signupUsername.setError("Username cannot be empty");
            return false;
        } else if (val.contains(" ")) {
            signupUsername.setError("Username cannot contain spaces");
            return false;
        } else {
            signupUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = signupPassword.getText().toString();
        if (val.isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            return false;
        } else {
            signupPassword.setError(null);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String password = signupPassword.getText().toString();
        String confirmPassword = signupConfirmPassword.getText().toString();
        if (confirmPassword.isEmpty()) {
            signupConfirmPassword.setError("Confirm password cannot be empty");
            return false;
        } else if (!password.equals(confirmPassword)) {
            signupConfirmPassword.setError("Passwords do not match");
            return false;
        } else {
            signupConfirmPassword.setError(null);
            return true;
        }
    }
}
