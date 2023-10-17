package com.example.m_hike.activities.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.activities.MainActivity;
import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;

public class IntroActivity extends AppCompatActivity {
    private Button startBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        dbHelper = new DatabaseHelper(getApplicationContext());
//        dbHelper.onUpgrade(dbHelper.getDatabase(), 1, 2);

        // Hide the action bar
        getSupportActionBar().hide();

        if (!dbHelper.getHikes().isEmpty()){
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
        } else {
            initView();
        }
    }

    private void initView() {
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(view -> {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            // transition to main activity
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}
