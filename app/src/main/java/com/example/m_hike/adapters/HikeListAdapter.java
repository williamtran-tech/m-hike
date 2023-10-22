package com.example.m_hike.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.activities.HikeDetailsActivity;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Hike;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HikeListAdapter extends RecyclerView.Adapter<HikeListAdapter.ViewHolder> {
    private final ArrayList<Hike> hikeList;
    private final Context context;
    public HikeListAdapter(Context context,ArrayList<Hike> hikeList) {
        this.hikeList = hikeList;
        this.context = context;
    }

    @Override
    public HikeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hike_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return hikeList.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HikeListAdapter.ViewHolder holder, int position) {
        Hike hike = hikeList.get(position);
//        ImageView imageView = holder.itemView.findViewById(R.id.imageView);
        TextView nameTxt = holder.itemView.findViewById(R.id.name);
        TextView locationTxt = holder.itemView.findViewById(R.id.location);
        TextView duration = holder.itemView.findViewById(R.id.duration);
        TextView distance = holder.itemView.findViewById(R.id.distance);
        Chip ratingChip = holder.itemView.findViewById(R.id.ratingChip);
        TextView date = holder.itemView.findViewById(R.id.date);

        nameTxt.setText(hike.getName());
        locationTxt.setText(hike.getLocation());
        duration.setText(hike.getDuration().toString() + " h");
        distance.setText(hike.getDistance().toString() + " km");
        ratingChip.setText(hike.getDifficulty().getName());
        switch (hike.getDifficulty().getId()) {
            case 1:
                ratingChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.very_easy)));
                ratingChip.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case 2:
                ratingChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.easy)));
                ratingChip.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case 3:
                ratingChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.medium)));
                ratingChip.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case 4:
                ratingChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.hard)));
                ratingChip.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case 5:
                ratingChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.extreme)));
                ratingChip.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
        }
        String outputDate = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date1 = null;
        try {
            date1 = inputFormat.parse(hike.getDate().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        outputDate = dateFormat.format(date1);

        date.setText(outputDate);

        // Set a click listener for the CardView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HikeDetailsActivity.class);
                intent.putExtra("hikeId", hike.getId());
                intent.putExtra("name", hike.getName());
                intent.putExtra("location", hike.getLocation());
                intent.putExtra("date", hike.getDate().toString());
                intent.putExtra("availableParking", hike.isAvailableParking());
                intent.putExtra("duration", hike.getDuration());
                intent.putExtra("distance", hike.getDistance());
                intent.putExtra("diffId", hike.getDifficulty().getId());
                intent.putExtra("diffName", hike.getDifficulty().getName());
                intent.putExtra("diffColor", ratingChip.getChipBackgroundColor());

                Hike hike1;
                try {
                    hike1 = DatabaseHelper.getHike(hike.getId());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (hike1.getDescription() == null || hike1.getDescription().isEmpty()) {
                    intent.putExtra("description", "");
                    Log.d("Empty", "Empty");
                } else {
                    intent.putExtra("description", hike1.getDescription());
                }
                view.getContext().startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
