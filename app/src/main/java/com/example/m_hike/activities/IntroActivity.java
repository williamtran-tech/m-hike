package com.example.m_hike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.MainActivity;
import com.example.m_hike.R;

public class IntroActivity extends AppCompatActivity {
    private Button startBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Hide the action bar
        getSupportActionBar().hide();

        initView();
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
