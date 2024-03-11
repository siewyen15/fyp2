package com.example.fyp2;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fyp2.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    ActivityMainBinding binding;

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
        binding.bottomNavigationView.setOnItemSelectedListener(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); // assuming your FrameLayout has id frame_layout
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nutrition) {
            replaceFragment(new NutritionFragment());
        } else if (itemId == R.id.health) {
            replaceFragment(new HealthFragment());
        } else if (itemId == R.id.community) {
            replaceFragment(new CommunityFragment());
        } else if (itemId == R.id.profile) {
            replaceFragment(new ProfileFragment());
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // No need to handle onBackPressed since there's no FloatingActionButton
        super.onBackPressed();
    }
}
