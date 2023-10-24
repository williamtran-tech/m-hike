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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.m_hike.R;
import com.example.m_hike.adapters.HikeListAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Difficulty;
import com.example.m_hike.models.Hike;
import com.example.m_hike.modules.CompassHandler;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private HikeListAdapter hikeListAdapter; // Declare the ListView
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
        TextView searchHike = homeFragmentView.findViewById(R.id.searchView);

        // Display decorator compass
        ImageView compassImg = homeFragmentView.findViewById(R.id.compassImg);
        compassHandler = new CompassHandler(getContext(), compassImg);

        try {
            if (!dbHelper.getHikes().isEmpty()) {
                ArrayList<Hike> hikeList;
                try {
                    hikeList = dbHelper.getHikes();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                HikeListAdapter hikeAdapter = new HikeListAdapter(getContext(), hikeList);

                homeBackground.setVisibility(View.VISIBLE);
                addHikeButton.setVisibility(View.GONE);
                searchHike.setVisibility(View.VISIBLE);
                // Set layout manager this RecyclerView will use
                RecyclerView recyclerView = homeFragmentView.findViewById(R.id.hikeRecyclerView);

//                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
//                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(hikeAdapter);

//                // Set the list of contacts to the ListView
//                ListView hikeRecycleView = homeFragmentView.findViewById(R.id.hikeRecyclerView);
//                hikeRecycleView.setAdapter((hikeListAdapter));
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