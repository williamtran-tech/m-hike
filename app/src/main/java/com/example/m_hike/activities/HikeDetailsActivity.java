package com.example.m_hike.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.DialogCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.adapters.ObservationGridAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Difficulty;
import com.example.m_hike.models.Hike;
import com.example.m_hike.models.Observation;
import com.google.android.material.chip.Chip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class HikeDetailsActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int SELECT_IMAGE = 1;
    private int hikeId;
    private Date hikeDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);

        getSupportActionBar().hide();
        displayHikeDetails();
        displayObservations();

        updateDescription();
        updateObservation();

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back
                // Add transition animation
                Intent intent  = new Intent(HikeDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
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
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT - 300).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                File file = new File(currentPhotoPath);

                Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT - 300).show();
                // Show preview observation
                Log.d("CurrentPhotoPath", currentPhotoPath);
                if (currentPhotoPath != null) {
                    // Show preview observation
                    CardView observationPreview = findViewById(R.id.observationPreview);
                    ImageView observationPicturePreview = findViewById(R.id.observationPicPreview);
                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    Bitmap rotateImage = rotateImage(bitmap, 90);

                    observationPicturePreview.setImageBitmap(rotateImage);
                    observationPreview.setVisibility(View.VISIBLE);
                    TextView caption = findViewById(R.id.observationCaptionPreview);
                    EditText captionEdit = findViewById(R.id.observationEditTxt);
                    caption.setText(captionEdit.getText().toString());

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Camera permission is Required", Toast.LENGTH_SHORT - 300).show();
            }
        }
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        Uri imageUri = data.getData();
                        Bitmap bitmap = getThumbnail(imageUri);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        File tempFile = createImageFile();
//
                        FileOutputStream out = new FileOutputStream(tempFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out); // You can change the format and quality as needed
                        out.flush();
                        out.close();
//
                        currentPhotoPath = tempFile.getAbsolutePath();
//
                        ExifInterface exif = new ExifInterface(currentPhotoPath); // Replace imagePath with your image path
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                        CardView observationPreview = findViewById(R.id.observationPreview);
                        ImageView observationPicturePreview = findViewById(R.id.observationPicPreview);
                        observationPicturePreview.setImageBitmap(bitmap);
                        observationPreview.setVisibility(View.VISIBLE);
                        TextView caption = findViewById(R.id.observationCaptionPreview);
                        EditText captionEdit = findViewById(R.id.observationEditTxt);
                        caption.setText(captionEdit.getText().toString());

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

                AppCompatButton saveObservationBtn = findViewById(R.id.saveObservationBtn);
                saveObservationBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayHikeDetails() {
        // Fill out the data from extra
        Intent intent = getIntent();
        Integer id = intent.getIntExtra("hikeId", 0);
        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        Float duration = intent.getFloatExtra("duration", 0);
        Float distance = intent.getFloatExtra("distance",0);
        Integer difficultyId = intent.getIntExtra("diffId", 0);
        String difficultyName = intent.getStringExtra("diffName");
        ColorStateList difficultyColor = intent.getParcelableExtra("diffColor");
        Boolean availableParking = intent.getBooleanExtra("availableParking", false);
        String description = intent.getStringExtra("description");

        // Set the data
        hikeId = id;
        ImageView imageView = findViewById(R.id.imageView);
//        imageView.setImageResource(R.drawable.hike);
        TextView nameTxt = findViewById(R.id.name);
        TextView locationTxt = findViewById(R.id.location);
        TextView durationTxt = findViewById(R.id.duration);
        TextView distanceTxt = findViewById(R.id.distance);
        Chip difficulty = findViewById(R.id.ratingChip);
//        TextView availableParkingTxt = findViewById(R.id.availableParking);
        TextView dateTxt = findViewById(R.id.date);
        Log.d("Description", description);
        if (!description.isEmpty()) {
            TextView descriptionDisplay = findViewById(R.id.description);
            descriptionDisplay.setText(description);
            descriptionDisplay.setTextColor(getColor(R.color.black));
            descriptionDisplay.setVisibility(View.VISIBLE);
            TextView descriptionEdit = findViewById(R.id.descriptionEditTxt);
            descriptionEdit.setText(description);
            descriptionEdit.setVisibility(View.GONE);
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date1;
        try {
            date1 = inputFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        hikeDate = date1;
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

    }

    private void displayObservations() {
        ArrayList<Observation> observations = DatabaseHelper.getObservations(hikeId);
        if (observations.size() > 0) {
            // Display as a grid
            GridView observationGrid = findViewById(R.id.observationGrid);
            ObservationGridAdapter adapter = new ObservationGridAdapter(observations, this);
            observationGrid.setAdapter(adapter);

            // Set the height of the grid -> Cause the grid to scroll (GridView inside Scroll View)
            setGridViewHeightBasedOnChildren(observationGrid, 3);
            observationGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("Observation", String.valueOf(i));
                    // New intent to display the observation
                    Intent intent = new Intent(HikeDetailsActivity.this, ObservationsActivity.class);
                    intent.putExtra("observationId", observations.get(i).getId());
                    intent.putExtra("hikeId", hikeId);

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        } else {
            // Display nothing
        }
    }

    private void updateDescription() {
        EditText description = findViewById(R.id.descriptionEditTxt);
        TextView descriptionDisplay = findViewById(R.id.description);
        AppCompatButton saveDescriptionBtn = findViewById(R.id.saveDescriptionBtn);
        descriptionDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show edit text
                description.setVisibility(View.VISIBLE);
                description.requestFocus();
                descriptionDisplay.setVisibility(View.GONE);

                // Show keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(description, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Scroll the screen to the bottom
                description.scrollTo(0, description.getBottom());
                saveDescriptionBtn.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveDescriptionBtn.setVisibility(View.VISIBLE);
                if (s.length() == 0) {
                    saveDescriptionBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveDescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save description
                description.clearFocus();

                // TODO: Save to the database
//                String name, Date date, String location, boolean availableParking, Integer difficulty, Float duration, Float distance, Integer id, String description
                Log.d("HikeId", String.valueOf(hikeId));

                Hike updatedHike;
                try {
                    updatedHike = DatabaseHelper.updateHike(
                            hikeId,
                            getIntent().getStringExtra("name"),
                            hikeDate,
                            getIntent().getStringExtra("location"),
                            getIntent().getBooleanExtra("availableParking", false),
                            getIntent().getFloatExtra("duration", 0),
                            getIntent().getFloatExtra("distance", 0),
                            getIntent().getIntExtra("diffId", 0),
                            description.getText().toString(), null);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                Log.d("Updated Hike", updatedHike.getDescription());
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                descriptionDisplay.setText(description.getText().toString());
                descriptionDisplay.setTextColor(getColor(R.color.black));
                descriptionDisplay.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(description.getWindowToken(), 0);
                Toast.makeText(HikeDetailsActivity.this, "Description saved", Toast.LENGTH_SHORT - 500).show();
                saveDescriptionBtn.setVisibility(View.GONE);
            }
        });
    }

    private void updateObservation() {
        EditText observation = findViewById(R.id.observationEditTxt);
        ImageButton captureBtn = findViewById(R.id.captureBtn);
        ImageButton galleryBtn = findViewById(R.id.galleryBtn);
        AppCompatButton saveObservationBtn = findViewById(R.id.saveObservationBtn);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open camera
                askCameraPermission();
            }
        });

        CardView observationPreview = findViewById(R.id.observationPreview);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });

        observation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Scroll the screen to the bottom
                observation.scrollTo(0, observation.getBottom());
                saveObservationBtn.setVisibility(View.GONE);
                captureBtn.setVisibility(View.VISIBLE);
                galleryBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveObservationBtn.setVisibility(View.VISIBLE);
                if (s.length() == 0) {
                    saveObservationBtn.setVisibility(View.GONE);
                }
                if (observationPreview.getVisibility() == View.VISIBLE) {
                    TextView captionPreview = findViewById(R.id.observationCaptionPreview);
                    captionPreview.setText(observation.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveObservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save observation
                observation.clearFocus();
                observationPreview.removeAllViews();
                observationPreview.setVisibility(View.GONE);

                Log.d("HikeId", String.valueOf(hikeId));

                // Create / Update observation
                // Hide the keyboard
                if (currentPhotoPath != null) {
                    File file = new File(currentPhotoPath);
                    byte[] image = new byte[(int) file.length()];
                    saveObservation(observation.getText().toString(), image);
                } else {
                    saveObservation(observation.getText().toString(), null);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                observation.setVisibility(View.VISIBLE);
                captureBtn.setVisibility(View.GONE);
                galleryBtn.setVisibility(View.GONE);
                saveObservationBtn.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(observation.getWindowToken(), 0);
                Toast.makeText(HikeDetailsActivity.this, "Observation saved", Toast.LENGTH_SHORT - 500).show();
            }
        });
    }

    private void saveObservation(String caption, byte[] image) {
        // Save observation
        Log.d("HikeId", String.valueOf(hikeId));
        Log.d("Caption", caption);

        // TODO: Save to the database
        Date currentDate = new Date();

        View observationView = getLayoutInflater().inflate(R.layout.observation_item, null);
        TextView captionTxt = observationView.findViewById(R.id.caption);
        captionTxt.setText(caption);
        ImageView observationPicture = observationView.findViewById(R.id.observationPicture);

        // Store the Captured image
        if (image != null) {
            // from byte[] to Bitmap
            byte[] imageByte = getBytesFromImagePath(currentPhotoPath);
            // Turn the image into a bitmap
            // Change size of the image
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            Bitmap rotateImage = rotateImage(bitmap, 90);
            // Display the image with rotated image
            observationPicture.setImageBitmap(rotateImage);
            try {
                DatabaseHelper.insertObservation(caption, currentDate, imageByte, 0, 0, hikeId);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            deleteImageInMediaStore();
            currentPhotoPath = null;
        } else {
            try {
                DatabaseHelper.insertObservation(caption, currentDate, null, 0, 0, hikeId);
                observationPicture.setVisibility(View.GONE);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        // Remove the old list and add the new one
        displayObservations();

        // Clear the text
        EditText observation = findViewById(R.id.observationEditTxt);
        observation.setText("");
    }

    public static final byte[] getBytesFromImagePath(String imagePath) {
        //Only decode image size. Not whole image
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        // Minimum width and height are > NEW_SIZE (e.g. 380 * 720)
        final int NEW_SIZE = 480;

        //Now we have image width and height. We should find the correct scale value. (power of 2)
        int width = option.outWidth;
        int height = option.outHeight;
        int scale = 1;
        while (width / 2 > NEW_SIZE || height / 2 > NEW_SIZE) {
            width /= 2;
            height /= 2;
            scale++;
        }
        //Decode again with inSampleSize
        option = new BitmapFactory.Options();
        option.inSampleSize = scale;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, option);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        bitmap.recycle();

        return stream.toByteArray();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void deleteImageInMediaStore(){
        File file = new File(currentPhotoPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 480) ? (originalSize / 480) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();

        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }
}