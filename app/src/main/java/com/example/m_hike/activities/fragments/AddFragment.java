package com.example.m_hike.activities.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Hike;
import com.example.m_hike.services.LocationAddress;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class AddFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private TextView date;

    //    LOCATION
    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getBaseContext());
        View addFragmentView = inflater.inflate(R.layout.fragment_add, container, false);

        dbHelper = new DatabaseHelper(getActivity().getBaseContext());
        date = (TextView) addFragmentView.findViewById(R.id.editTextDate);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy");
        date.setText(sdf.format(cal.getTime()));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerDialogTheme();
                datePicker.show(getFragmentManager(), "date picker");
            }
        }
        );
        ImageView locationBtn = (ImageView) addFragmentView.findViewById(R.id.locationImg);
        locationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                        requestPermissions();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                requestNewLocationData();
                getLastLocation();
            }
        });

        Button saveBtn = (Button) addFragmentView.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveDetails();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
        navBar.setVisibility(View.GONE);

        // Change opacity of the background image when scroll
        ImageView background = (ImageView) addFragmentView.findViewById(R.id.imageView);
        ScrollView scrollView = (ScrollView) addFragmentView.findViewById(R.id.scrollView);
        // Changing alpha of the background image when scroll
        // Define the max scroll amount
        final int MAX_SCROLL_FACTOR = 700;
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Calculate the alpha based on the scroll position
            int scrollY = scrollView.getScrollY();
            float alpha = 1.0f - Math.min(1.0f, (float) scrollY / MAX_SCROLL_FACTOR); // Adjust the threshold as needed
            // Update the overlay view's alpha
            background.setAlpha(alpha);
        });
        // Inflate the layout for this fragment
        return addFragmentView;
    }

    private void saveDetails() throws ParseException {
        EditText nameTxt = getActivity().findViewById(R.id.nameEditTxt);
        TextView date = getActivity().findViewById(R.id.editTextDate);
        EditText locationTxt = getActivity().findViewById(R.id.locationEditTxt);
        RatingBar ratingBar = getActivity().findViewById(R.id.ratingBar);
        EditText duration = getActivity().findViewById(R.id.durationEditTxt);
        EditText distance = getActivity().findViewById(R.id.distanceEditTxt);
        RadioGroup radioGroup = getActivity().findViewById(R.id.parkingRadioGroup);


        String name = nameTxt.getText().toString();
        // extract the date from the text view
        String dateStr = date.getText().toString();
        String d1 = dateStr.substring(dateStr.substring(0, dateStr.indexOf(", ")).length() + 2, dateStr.length());
        // convert the date string to date object
        Date dateObj = null;
        try {
            dateObj = new SimpleDateFormat("dd/MM/yyyy").parse(d1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String location = locationTxt.getText().toString();
        boolean availableParking = radioGroup.getCheckedRadioButtonId() == R.id.yesRadioButton;
        int rating = (int) ratingBar.getRating();
        if (name.isEmpty() || dateStr.isEmpty() || location.isEmpty() || duration.getText().toString().isEmpty() || distance.getText().toString().isEmpty() || rating == 0) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Float durationFloat = Float.parseFloat(duration.getText().toString());
        Float distanceFloat = Float.parseFloat(distance.getText().toString());
        Hike hike = dbHelper.insertHike(name, dateObj, location, availableParking, rating, durationFloat, distanceFloat);


        // Make the device vibrate
        Vibrator vibrator = (Vibrator) getActivity().getBaseContext().getSystemService(getActivity().getBaseContext().VIBRATOR_SERVICE);
        vibrator.vibrate(400);

        // Notify
        Toast.makeText(getActivity(), "Hikes " + hike.getName() + " added successfully", Toast.LENGTH_SHORT - 500).show();

        // Clear fields
        nameTxt.setText("");
        locationTxt.setText("");
        ratingBar.setRating(0);
        duration.setText("");
        distance.setText("");

        Log.d("Get Hikes:", dbHelper.getHikes().toString());
    }

    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);
            return datepickerdialog;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            TextView date = getActivity().findViewById(R.id.editTextDate);
            // Get the date of week
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String dayOfWeekString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getActivity().getResources().getConfiguration().locale);

            date.setText(dayOfWeekString + ", " + day + "/" + (month + 1) + "/" + year);
        }
    }

//    LOCATION
    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Log.d("location", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                            // Get the address from the coordinates
                            String address = LocationAddress.getAddressFromLocation(getActivity().getBaseContext(), location.getLatitude(), location.getLongitude());
                            if (address != null) {
                                EditText locationTxt = getActivity().findViewById(R.id.locationEditTxt);
                                locationTxt.setText(address);
                            } else {
                                Toast.makeText(getActivity(), "Address not found", Toast.LENGTH_SHORT).show();
                            }
//                            EditText locationTxt = getActivity().findViewById(R.id.locationEditTxt);
//                            locationTxt.setText("Latitude:" + coordinates[0] + ", longitude:" + coordinates[1]);
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d("location", "onLocationResult: " + mLastLocation);
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}