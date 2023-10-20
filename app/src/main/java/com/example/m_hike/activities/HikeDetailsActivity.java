package com.example.m_hike.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HikeDetailsActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int SELECT_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);

        getSupportActionBar().hide();
        // Fill out the data from extra
        Intent intent = getIntent();
        Integer hikeId = intent.getIntExtra("hikeId", 0);
        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        Float duration = intent.getFloatExtra("duration", 0);
        Float distance = intent.getFloatExtra("distance",0);
        Integer difficultyId = intent.getIntExtra("diffId", 0);
        String difficultyName = intent.getStringExtra("diffName");
        ColorStateList difficultyColor = intent.getParcelableExtra("diffColor");
        String availableParking = intent.getStringExtra("availableParking");

        // Set the data
        ImageView imageView = findViewById(R.id.imageView);
//        imageView.setImageResource(R.drawable.hike);
        TextView nameTxt = findViewById(R.id.name);
        TextView locationTxt = findViewById(R.id.location);
        TextView durationTxt = findViewById(R.id.duration);
        TextView distanceTxt = findViewById(R.id.distance);
        Chip difficulty = findViewById(R.id.ratingChip);
//        TextView availableParkingTxt = findViewById(R.id.availableParking);
        TextView dateTxt = findViewById(R.id.date);

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date1;
        try {
            date1 = inputFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        assert date1 != null;
        String outputDate = dateFormat.format(date1);

        nameTxt.setText(name);
        locationTxt.setText(location);
        durationTxt.setText(duration.toString() + " h");
        distanceTxt.setText(distance.toString() + " km");
        difficulty.setText(difficultyName);
        difficulty.setChipBackgroundColor(difficultyColor);
        difficulty.setTextColor(getColor(R.color.white));
//        availableParkingTxt.setText(availableParking);
        dateTxt.setText(outputDate);;

        AppCompatButton captureBtn = findViewById(R.id.captureBtn);
        AppCompatButton galleryBtn = findViewById(R.id.galleryBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open camera
                askCameraPermission();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });
    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is Required", Toast.LENGTH_SHORT - 300).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                File file = new File(currentPhotoPath);

                Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT - 300).show();
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageURI(Uri.fromFile(file));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Camera permission is Required", Toast.LENGTH_SHORT - 300).show();
            }
        }
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED)  {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/hike_images");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Error", "Error occurred while creating the File");
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.m_hike.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }
}