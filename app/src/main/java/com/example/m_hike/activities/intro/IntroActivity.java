package com.example.m_hike.activities.intro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.m_hike.activities.MainActivity;
import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Hike;

import java.text.ParseException;
import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {
    private Button startBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        dbHelper = new DatabaseHelper(getApplicationContext());

        // Hide the action bar
        getSupportActionBar().hide();

        boolean flag;
        try {
            ArrayList<Hike> test = dbHelper.getHikes();
            if (test.isEmpty()) {
                flag = false;
                Log.d("Hikes: ", "Empty");
            } else {
                flag = true;
            }
        } catch (RuntimeException e) {
            flag = false;
            Log.d("Hikes: ", "Empty");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (flag){
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            initView();
        }
    }

    private void initView() {
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(view -> {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            // transition to main activity
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
            finish();
        });
    }
}
