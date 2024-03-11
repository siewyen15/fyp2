package com.example.fyp2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewProfileActivity extends AppCompatActivity {

    EditText editName, editEmail, editUsername, editPassword;
    Button saveButton;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

//        reference = FirebaseDatabase.getInstance().getReference("users");
        reference = FirebaseDatabase.getInstance().getReference().child("users").child("userId");

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
                // Save changes to Firebase
                saveDataToFirebase();
                // Navigate back to ProfileFragment
                Intent intent = new Intent(NewProfileActivity.this, LoginActivity.class);
                intent.putExtra("name", editName.getText().toString());
                intent.putExtra("email", editEmail.getText().toString());
                intent.putExtra("username", editUsername.getText().toString());
                intent.putExtra("password", editPassword.getText().toString());
                startActivity(intent);
                // Finish the activity
                finish();
            }
        });
    }

    private void saveDataToFirebase() {
        // Get updated data from EditText fields
        String updatedName = editName.getText().toString();
        String updatedEmail = editEmail.getText().toString();
        String updatedUsername = editUsername.getText().toString();
        String updatedPassword = editPassword.getText().toString();

        // Update data in Firebase
        String userId = reference.push().getKey();
        reference.child(userId).child("name").setValue(updatedName);
        reference.child(userId).child("email").setValue(updatedEmail);
        reference.child(userId).child("username").setValue(updatedUsername);
        reference.child(userId).child("password").setValue(updatedPassword);
    }
}
