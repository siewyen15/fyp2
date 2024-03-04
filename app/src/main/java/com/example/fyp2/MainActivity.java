package com.example.fyp2;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import com.example.fyp2.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Replace the fragment with HomeFragment initially
        replaceFragment(new HomeFragment());

        // Set background of bottomNavigationView to null
        binding.bottomNavigationView.setBackground(null);

        // Set the listener for bottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
                // Set the background tint of FloatingActionButton to darken color
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)));
            } else if (itemId == R.id.nutrition) {
                replaceFragment(new NutritionFragment());
                // Reset the background tint of FloatingActionButton
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            } else if (itemId == R.id.health) {
                replaceFragment(new HealthFragment());
                // Reset the background tint of FloatingActionButton
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            } else if (itemId == R.id.community) {
                replaceFragment(new CommunityFragment());
                // Reset the background tint of FloatingActionButton
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
                // Reset the background tint of FloatingActionButton
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            }
            return true;
        });

        // Find FloatingActionButton
        fab = findViewById(R.id.fab);

        // Set OnClickListener to simulate click action for FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                replaceFragment(new HomeFragment());
                // Set the background tint of FloatingActionButton to darken color
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
            }
        });

        // Simulate click action on FloatingActionButton to display HomeFragment initially
        fab.performClick();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}


