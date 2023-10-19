package com.example.m_hike.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.m_hike.R;

public class HikeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);

        getSupportActionBar().hide();
    }
}