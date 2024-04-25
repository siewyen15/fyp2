package com.example.fyp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class NewProfileActivity extends AppCompatActivity {

    EditText editName, editEmail, editUsername, editPassword;
    Button saveButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String nameUser = intent.getStringExtra("name");
        String emailUser = intent.getStringExtra("email");
        String usernameUser = intent.getStringExtra("username");
        String passwordUser = intent.getStringExtra("password");

        // Display retrieved data
        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editUsername.setText(usernameUser);
        editPassword.setText(passwordUser);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save changes to Firestore
                saveProfileDataToFirestore();
            }
        });
    }

    private void saveProfileDataToFirestore() {
        // Get updated data from EditText fields
        String updatedName = editName.getText().toString();
        String updatedEmail = editEmail.getText().toString();
        String updatedUsername = editUsername.getText().toString();
        String updatedPassword = editPassword.getText().toString();

        // Create a map with user profile data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", updatedName);
        userData.put("email", updatedEmail);
        userData.put("username", updatedUsername);
        userData.put("password", updatedPassword);

        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Save profile data to Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // Initialize Firestore
            firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("Profile")
                    .document("UserInfo")
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        // Profile data saved successfully
                        Toast.makeText(NewProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        navigateToNextActivity();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(NewProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void navigateToNextActivity() {
        // Navigate back to LoginActivity
        Intent intent = new Intent(NewProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        // Finish the activity
        finish();
    }
}
