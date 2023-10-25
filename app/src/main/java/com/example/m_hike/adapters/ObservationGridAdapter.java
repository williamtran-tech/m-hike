package com.example.m_hike.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.m_hike.models.Observation;

import java.util.List;

public class ObservationGridAdapter extends BaseAdapter {
    private final List<Observation> observations;

    public ObservationGridAdapter(List<Observation> observations) {
        this.observations = observations;
    }

    @Override
    public int getCount() {
        return observations.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return observations.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

//        View v = inflater.inflate(R.layout.observation_grid_item, viewGroup, false);
        return view;
    }
}
