package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });
    }

    private void saveDetails() {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EditText nameTxt = (EditText) findViewById(R.id.nameEditTxt);
        EditText emailTxt = (EditText) findViewById(R.id.emailEditTxt);
        EditText locationTxt = (EditText) findViewById(R.id.locationEditTxt);

        String name = nameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String location = locationTxt.getText().toString();
        long personId = dbHelper.insertTest(name, location, email);

        // Notify
        Toast.makeText(this, "Person has been created with ID: " + personId, Toast.LENGTH_SHORT - 1000).show();
    }
}