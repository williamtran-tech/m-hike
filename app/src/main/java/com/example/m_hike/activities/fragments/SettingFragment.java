package com.example.m_hike.activities.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;
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
        View settingFragmentView = inflater.inflate(R.layout.fragment_setting, container, false);

        AppCompatButton resetBtn = settingFragmentView.findViewById(R.id.resetDataBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a dialog to confirm the reset
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Reset Data");
                builder.setMessage("Are you sure you want to reset all data?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // TODO: Reset data
                    DatabaseHelper.resetDatabase();
                    Toast.makeText(getContext(), "Data reset successfully", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                });

                builder.create().show();
            }
        });

        // Inflate the layout for this fragment
        return settingFragmentView;

    }
}