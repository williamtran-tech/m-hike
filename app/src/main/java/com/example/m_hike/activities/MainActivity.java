package com.example.m_hike.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.activities.fragments.AddFragment;
import com.example.m_hike.activities.fragments.HomeFragment;
import com.example.m_hike.activities.fragments.SettingFragment;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.databinding.ActivityMainBinding;
import com.example.m_hike.modules.CompassHandler;

public class MainActivity extends AppCompatActivity {
    private CompassHandler compassHandler;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide action bar
        getSupportActionBar().hide();
//        Button saveBtn = (Button) findViewById(R.id.saveBtn);
//        saveBtn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveDetails();
//            }
//        });

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.onUpgrade(dbHelper.getDatabase(), 1, 2);


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
                dbHelper.onUpgrade(dbHelper.getDatabase(), 1, 2);
                replaceFragment(new SettingFragment());
                return true;
            }
            return false;
        });

        // Compass
//        TextView compassValue = (TextView) findViewById(R.id.compassValueTxt);
//        ImageView compass = (ImageView) findViewById(R.id.compassImg);
//
//        compassHandler = new CompassHandler(this, compassValue, compass);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        if (fragment instanceof HomeFragment)
//            fragmentTransaction.setCustomAnimations(R.anim.bottom_down, R.anim.bottom_up);
//        else if (fragment instanceof AddFragment)
//            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//        else if (fragment instanceof SettingFragment)
//            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Init compass
//        compassHandler.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop compass
//        compassHandler.stop();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}