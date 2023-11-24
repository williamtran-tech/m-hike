package com.example.m_hike.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.activities.fragments.AddFragment;
import com.example.m_hike.activities.fragments.HomeFragment;
import com.example.m_hike.activities.fragments.SettingFragment;
import com.example.m_hike.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide action bar
        getSupportActionBar().hide();

        // Fragment
        binding =  ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set default fragment
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home && !(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment)) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.add && !(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof AddFragment)) {
                replaceFragment(new AddFragment());
                return true;
            } else if (item.getItemId() == R.id.setting && !(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof SettingFragment)) {
                replaceFragment(new SettingFragment());
                return true;
            }
            return false;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Add this transaction to the back stack.
        currentFragment = fragment;
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Integer backCount = 0;
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (currentFragment instanceof HomeFragment) {
                backCount++;
                if (backCount == 2) {
                    finish();
                } else {
                    Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                }
            } else {
                backCount = 0;
                getSupportFragmentManager().popBackStack();
                currentFragment = new HomeFragment();
            }
        }
    }

}