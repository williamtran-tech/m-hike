package com.example.m_hike.activities.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Difficulty;

import java.text.ParseException;

public class HomeFragment extends Fragment {
    private DatabaseHelper dbHelper;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity().getBaseContext());
        try {
            Log.d("Hikes: ", dbHelper.getHikes().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        for (Difficulty difficulty : dbHelper.getDifficulties()) {
            Log.d("Difficulty: ", difficulty.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}