package com.example.m_hike.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.models.Observation;

import java.util.ArrayList;

public class ObservationListAdapter extends RecyclerView.Adapter<ObservationListAdapter.ViewHolder> {
    private final ArrayList<Observation> observationList;
    private final Context context;

    public ObservationListAdapter(Context context, ArrayList<Observation> observationList) {
        this.observationList = observationList;
        this.context = context;
    }
    public ObservationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.observation_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationListAdapter.ViewHolder holder, int position) {
        Observation observation = observationList.get(position);

        TextView captionTxt = holder.itemView.findViewById(R.id.caption);
        TextView dateTxt = holder.itemView.findViewById(R.id.date);
        ImageView observationPicture = holder.itemView.findViewById(R.id.observationPicture);

        captionTxt.setText(observation.getCaption());
        dateTxt.setText(observation.getDate().toString());
//        Convert byte[] to bitmap and set
        Bitmap bitmap = BitmapFactory.decodeByteArray(observation.getImage(), 0, observation.getImage().length);
        observationPicture.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
