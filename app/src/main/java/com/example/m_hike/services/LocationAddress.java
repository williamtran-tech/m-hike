package com.example.m_hike.services;

import android.location.Address;
import android.location.Geocoder;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class LocationAddress {
    public static String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context);
        String addressText = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                Log.d("Address: ", address.toString());
                addressText = address.getAddressLine(0); // Get the first address line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressText;
    }
}
