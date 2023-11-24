package com.example.m_hike.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Observation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ObservationsActivity extends AppCompatActivity {

    private int hikeId;
    private int observationId;
    private boolean isChanged = false;
    private int[] observationPosition = new int[2];
    @Override
    public void onBackPressed() {
        if (isChanged) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            finish();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations);

        getSupportActionBar().hide();

        hikeId = getIntent().getIntExtra("hikeId", 0);
        observationId = getIntent().getIntExtra("observationId", 0);
        displayObservations();

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add transition animation
                // if any changes are made
                if (isChanged) {
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    finish();
                }

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void displayObservations() {
        ArrayList<Observation> observations = DatabaseHelper.getObservations(hikeId);

        // Display as a list
        LinearLayout observationList = findViewById(R.id.observationList);
        for (Observation observation : observations) {
            View observationView = getLayoutInflater().inflate(R.layout.observation_item, null);
            TextView caption = observationView.findViewById(R.id.caption);
            caption.setText(observation.getCaption());
            ImageView observationPicture = observationView.findViewById(R.id.observationPicture);
            TextView date = observationView.findViewById(R.id.date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            String outputDate = dateFormat.format(observation.getDate());
            date.setText(outputDate);
            if (observation.getImage() != null) {
                // Change size of the image
                // from byte[] to Bitmap
                // This is the image from path
                // Store the image in the database
                byte[] imageByte = observation.getImage();
                // Turn the image into a bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                Bitmap rotateImage = HikeDetailsActivity.rotateImage(bitmap, 0);
                // Display the image with rotated image
                observationPicture.setImageBitmap(rotateImage);
                Log.d("Image", bitmap.toString());
            } else {
                observationPicture.setVisibility(View.GONE);
            }
            observationView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    // Display a dialog to confirm
                    // Delete the observation
                    AlertDialog.Builder builder = new AlertDialog.Builder(ObservationsActivity.this);
                    builder.setTitle("Edit Observation");
                    builder.setPositiveButton("Update", (dialog, which) -> {
                        // Handle the update action here
                        // Show another dialog to update caption
                        AlertDialog.Builder updateCaptionBuilder = new AlertDialog.Builder(ObservationsActivity.this);
                        updateCaptionBuilder.setTitle("Update Caption");
                        EditText captionEdit = new EditText(ObservationsActivity.this);
                        captionEdit.setText(observation.getCaption());
                        updateCaptionBuilder.setView(captionEdit);

                        updateCaptionBuilder.setPositiveButton("Update", (dialog1, which1) -> {
                            // Update the caption
                            Observation updatedObservation = null;
                            try {
                                updatedObservation = DatabaseHelper.updateObservation(observation.getId(), captionEdit.getText().toString(), observation.getDate(), observation.getImage(), observation.getLongitude(), observation.getLatitude(), observation.getHike().getId(), null);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            // Remove the old list and add the new one
                            observationList.removeAllViews();
                            displayObservations();
                            Log.d("Update caption Successfully", String.valueOf(updatedObservation.getId()));
                        });
                        updateCaptionBuilder.setNegativeButton("Cancel", (dialog1, which1) -> {
                            // Handle nothing
                        });


                        updateCaptionBuilder.show();
                    });

                    builder.setNegativeButton("Delete", (dialog, which) -> {
                        // Handle the delete action here
                        Observation deletedObservation = DatabaseHelper.deleteObservation(observation.getId());
                        // Remove the old list and add the new one
                        observationList.removeAllViews();
                        displayObservations();
                        isChanged = true;
                        Log.d("Deleted Observation Successfully", String.valueOf(deletedObservation.getId()));
                        // Display a button undo the delete action
                        ConstraintLayout undoBtn = findViewById(R.id.undoBtn);
                        undoBtn.setVisibility(View.VISIBLE);
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        AppCompatButton undoButton = findViewById(R.id.undoButton);
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setMax(3000);
                        progressBar.setProgress(0);
                        CountDownTimer countDownTimer = new CountDownTimer(3000, 10) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                // Update the progress bar with the countdown
                                // Calculate the remaining time and update the progress bar
                                int remainingTime = (int) millisUntilFinished;
                                progressBar.setProgress(remainingTime);
                            }

                            @Override
                            public void onFinish() {
                                // Countdown is complete, show the Undo button
                                progressBar.setVisibility(View.INVISIBLE);
                                undoBtn.setVisibility(View.GONE);
                                DatabaseHelper.forceDeleteObservation(deletedObservation.getId());
                            }
                        };
                        countDownTimer.start();

                        undoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("Undo Delete Observation", String.valueOf(deletedObservation.getId()));
                                // Undo the delete action
                                try {
                                    DatabaseHelper.updateObservation(deletedObservation.getId(), deletedObservation.getCaption(), deletedObservation.getDate(), deletedObservation.getImage(), deletedObservation.getLongitude(), deletedObservation.getLatitude(), deletedObservation.getHike().getId(), null);
                                    isChanged = false;
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                // Cancel the countdown
                                countDownTimer.cancel();
                                // Hide the undo button
                                progressBar.setVisibility(View.INVISIBLE);
                                undoBtn.setVisibility(View.GONE);
                                // Remove the old list and add the new one
                                observationList.removeAllViews();
                                displayObservations();
                            }
                        });
                    });

                    builder.setNeutralButton("Cancel", (dialog, which) -> {
                        // Handle nothing
                    });
                    builder.show();

                    return true;
                }
            });
            observationList.addView(observationView);

            if (observation.getId() == observationId) {
                // Get the position of the observation
                final int selectedPosition = observationList.getChildCount() - 1; // The position of the selected observation
                observationView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Scroll to the selected observation
                        final ScrollView scrollView = findViewById(R.id.observationScroll);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollTo(0, observationList.getChildAt(selectedPosition).getTop());
                            }
                        });
                    }
                });
            }
        }
    }
}