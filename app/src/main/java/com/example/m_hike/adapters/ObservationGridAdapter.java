package com.example.m_hike.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.m_hike.R;
import com.example.m_hike.models.Observation;

import java.util.List;

public class ObservationGridAdapter extends BaseAdapter {
    private final List<Observation> observations;
    private Context context;

    public ObservationGridAdapter(List<Observation> observations, Context context) {
        this.observations = observations;
        this.context = context;
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
        View itemView = LayoutInflater.from(context).inflate(R.layout.observation_grid_item, viewGroup, false);
        ImageView imageView = itemView.findViewById(R.id.observationPicture);
        Bitmap bitmap = BitmapFactory.decodeByteArray(observations.get(i).getImage(), 0, observations.get(i).getImage().length);
        imageView.setImageBitmap(bitmap);
        return itemView;
    }
}
