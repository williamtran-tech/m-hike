package com.example.m_hike.activities.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.m_hike.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingFragment extends Fragment {
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
        navBar.setVisibility(View.VISIBLE);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }
}