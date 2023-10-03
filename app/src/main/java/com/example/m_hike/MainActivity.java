package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.modules.CompassHandler;

public class MainActivity extends AppCompatActivity {
    private CompassHandler compassHandler;

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

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.onUpgrade(dbHelper.getDatabase(), 1, 2);

        // Compass
        TextView compassValue = (TextView) findViewById(R.id.compassValueTxt);
        ImageView compass = (ImageView) findViewById(R.id.compassImg);

        compassHandler = new CompassHandler(this, compassValue, compass);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Init compass
        compassHandler.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop compass
        compassHandler.stop();
    }

    private void saveDetails() {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EditText nameTxt = findViewById(R.id.nameEditTxt);
        EditText emailTxt = findViewById(R.id.emailEditTxt);
        EditText locationTxt = findViewById(R.id.locationEditTxt);

        String name = nameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String location = locationTxt.getText().toString();
        long personId = dbHelper.insertTest(name, location, email);

        // Make the device vibrate
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(400);

        // Notify
        Toast.makeText(this, "Person has been created with ID: " + personId, Toast.LENGTH_SHORT - 500).show();

        // Clear fields
        nameTxt.setText("");
        emailTxt.setText("");
        locationTxt.setText("");
    }
}