package com.example.m_hike.activities.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.m_hike.R;
import com.example.m_hike.adapters.HikeListAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Difficulty;
import com.example.m_hike.models.Hike;
import com.example.m_hike.modules.CompassHandler;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private DatabaseHelper dbHelper;
    public static Difficulty filter;
    private CompassHandler compassHandler; // Decorator
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
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
        navBar.setVisibility(View.VISIBLE);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView homeBackground = homeFragmentView.findViewById(R.id.homeBackground);
        AppCompatButton addHikeButton = homeFragmentView.findViewById(R.id.addHikeBtn);
        EditText searchHike = homeFragmentView.findViewById(R.id.searchView);
        Spinner spinner = homeFragmentView.findViewById(R.id.spinner);

        // Set spinner
        // Turn database difficulties into array
        ArrayList<String> difficulties = new ArrayList<>();
        for (Difficulty difficulty : dbHelper.getDifficulties()) {
            difficulties.add(difficulty.getName());
        }
        difficulties.add(0, "All");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final HikeListAdapter[] hikeAdapter = new HikeListAdapter[1];
        // Handle spinner selection
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<Hike> hikeList = new ArrayList<>();
                searchHike.setText("");
                String difficulty = adapterView.getItemAtPosition(i).toString();
                if (!difficulty.equals("All")) {
                    Difficulty difficultyObj = dbHelper.getDifficulties().get(i - 1);
                    filter = difficultyObj;
                    try {
                        hikeList = dbHelper.getHikesByDifficulty(difficultyObj);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        hikeList = dbHelper.getHikes();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    filter = null;
                }
                // Sort hikes by date
                hikeList.sort((hike1, hike2) -> hike2.getDate().compareTo(hike1.getDate()));
                hikeAdapter[0] = new HikeListAdapter(getContext(), hikeList);

                // Set layout manager this RecyclerView will use
                RecyclerView recyclerView = homeFragmentView.findViewById(R.id.hikeRecyclerView);

                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(hikeAdapter[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Display decorator compass
        ImageView compassImg = homeFragmentView.findViewById(R.id.compassImg);
        compassHandler = new CompassHandler(getContext(), compassImg);

        try {
            if (!dbHelper.getHikes().isEmpty()) {
                // Search for hikes
                searchHike.addTextChangedListener(new TextWatcher() {
                      @Override
                      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                      }

                      @Override
                      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                          if (hikeAdapter[0] != null) {
                              if (charSequence != null) {
                                  hikeAdapter[0].getFilter().filter(charSequence);
                              }
                          }
                      }

                      @Override
                      public void afterTextChanged(Editable editable) {

                      }
                  }
                );
            } else {
                searchHike.setVisibility(View.GONE);
                homeBackground.setVisibility(View.GONE);
                CardView cardView = homeFragmentView.findViewById(R.id.emptyView);
                cardView.setVisibility(View.VISIBLE);
                addHikeButton.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Inflate the layout for this fragment
        return homeFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        compassHandler.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        compassHandler.stop();
    }


}