package com.example.m_hike.activities.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private TextView dateEditTxt;
    private Calendar saveDate;
    private DatePickerDialog datePickerDialog;
    private MapView mapView;
    private GoogleMap ggMap;

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
        dateEditTxt = (TextView) addFragmentView.findViewById(R.id.editTextDate);
        Calendar cal = Calendar.getInstance();
        saveDate = cal;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        dateEditTxt.setText(sdf.format(cal.getTime()));
        dateEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
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
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        EditText nameTxt = getActivity().findViewById(R.id.nameEditTxt);
        TextView date = getActivity().findViewById(R.id.editTextDate);
        EditText locationTxt = getActivity().findViewById(R.id.locationEditTxt);
        RatingBar ratingBar = getActivity().findViewById(R.id.ratingBar);
        EditText duration = getActivity().findViewById(R.id.durationEditTxt);
        EditText distance = getActivity().findViewById(R.id.distanceEditTxt);
        RadioGroup radioGroup = getActivity().findViewById(R.id.parkingRadioGroup);

        String name = nameTxt.getText().toString();
        String location = locationTxt.getText().toString();
        boolean availableParking = radioGroup.getCheckedRadioButtonId() == R.id.yesRadioButton;
        int rating = (int) ratingBar.getRating();
        if (name.isEmpty() || location.isEmpty() || duration.getText().toString().isEmpty() || distance.getText().toString().isEmpty() || rating == 0) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Float durationFloat = Float.parseFloat(duration.getText().toString());
        Float distanceFloat = Float.parseFloat(distance.getText().toString());
        Hike hike = dbHelper.insertHike(name, saveDate.getTime(), location, availableParking, rating, durationFloat, distanceFloat);


        // Make the device vibrate
        Vibrator vibrator = (Vibrator) getActivity().getBaseContext().getSystemService(getActivity().getBaseContext().VIBRATOR_SERVICE);
        vibrator.vibrate(400);

        // Clear fields
        nameTxt.setText("");
        locationTxt.setText("");
        ratingBar.setRating(0);
        duration.setText("");
        distance.setText("");

        Log.d("Get Hikes:", dbHelper.getHikes().toString());

        // Go to home fragment
        Fragment homeFragment = new HomeFragment();
        // add transition
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, homeFragment).commit();
        // Notify
        Toast.makeText(getActivity(), "Hikes " + hike.getName() + " added successfully", Toast.LENGTH_SHORT - 500).show();
    }

//    LOCATION
    @SuppressLint("MissingPermission")
    public void getLastLocation() {

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

                            // Open google maps dialog
                            View googleMapsDialogView = getLayoutInflater().inflate(R.layout.map_dialog, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setView(googleMapsDialogView);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.round_dialog));
                            dialog.show();

                            AppCompatButton saveBtn = dialog.findViewById(R.id.saveBtn);
                            TextView locationTxtView = dialog.findViewById(R.id.locationTxt);
                            locationTxtView.setText(address);

                            // Set the map
                            MapView mapView = googleMapsDialogView.findViewById(R.id.mapView);
                            mapView.onCreate(null);
                            mapView.onResume();

                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude()); // San Francisco
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15)); // 15 is the zoom level
                                    googleMap.clear();
                                    markLocation(googleMap,initialLocation, "Location");
                                    Geocoder geocoder = new Geocoder(getActivity().getBaseContext(), getResources().getConfiguration().locale);

                                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(LatLng latLng) {
                                            googleMap.clear();
                                            markLocation(googleMap,latLng, "Location");
                                            String address = LocationAddress.getAddressFromLocation(getActivity().getBaseContext(), latLng.latitude, latLng.longitude);
                                            if (address != null) {
                                                locationTxtView.setText(address);
                                            } else {
                                                Toast.makeText(getActivity(), "Address not found", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EditText locationTxt = getActivity().findViewById(R.id.locationEditTxt);
                                    locationTxt.setText(locationTxtView.getText().toString());
                                    dialog.dismiss();
                                }
                            });
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

    public void markLocation(GoogleMap ggMap, LatLng location, String title){
        if (ggMap != null) {
            // Add a marker at the specified location with the given title
            ggMap.addMarker(new MarkerOptions().position(location).title(title));
        }
    }

    @SuppressLint("MissingPermission")
    public void requestNewLocationData() {

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

    public LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d("location", "onLocationResult: " + mLastLocation);
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    public boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    public void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    public boolean isLocationEnabled() {
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

    private void openDatePickerDialog(int year, int month, int day) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog and set the date set listener
        datePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                // Handle the selected date
                String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = inputFormat.parse(selectedDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String outputDate = dateFormat.format(date);
                dateEditTxt.setText(outputDate);
                saveDate.setTime(date);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}