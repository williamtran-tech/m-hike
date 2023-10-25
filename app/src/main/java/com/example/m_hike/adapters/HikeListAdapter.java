package com.example.m_hike.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.activities.HikeDetailsActivity;
import com.example.m_hike.activities.fragments.AddFragment;
import com.example.m_hike.activities.fragments.HomeFragment;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.models.Difficulty;
import com.example.m_hike.models.Hike;
import com.example.m_hike.models.Observation;
import com.google.android.material.chip.Chip;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HikeListAdapter extends RecyclerView.Adapter<HikeListAdapter.ViewHolder> implements Filterable {
    private ArrayList<Hike> hikeList;
    private final Context context;
    private Hike deletedHike;
    private DatePickerDialog datePickerDialog;
    private Calendar updatedDate;
    private TextView dateEditTxt;
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
    public void onBindViewHolder(@NonNull HikeListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Hike hike = hikeList.get(position);
//        ImageView imageView = holder.itemView.findViewById(R.id.imageView);
        TextView nameTxt = holder.itemView.findViewById(R.id.name);
        TextView locationTxt = holder.itemView.findViewById(R.id.location);
        TextView duration = holder.itemView.findViewById(R.id.duration);
        TextView distance = holder.itemView.findViewById(R.id.distance);
        Chip ratingChip = holder.itemView.findViewById(R.id.ratingChip);
        TextView date = holder.itemView.findViewById(R.id.date);

        holder.itemView.setId(hike.getId());
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
                if (hike1.getDescription() != null) {
                    intent.putExtra("description", hike1.getDescription());
                } else {
                    intent.putExtra("description", "");
                }
                view.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            Activity activity = (Activity) context;
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onLongClick(View view) {
                Log.d("Long click", "Long click");
                Log.d("Description", hike.getDescription() + " SSS");

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Hike Details");
                builder.setPositiveButton("Update", (dialog, which) -> {
                    // Handle the update action here
                    // Show another dialog to update caption

                    View layout = activity.getLayoutInflater().inflate(R.layout.update_hike, null);
                    AlertDialog.Builder updateDialogBuilder = new AlertDialog.Builder(context);
                    updateDialogBuilder.setView(layout);

                    AlertDialog updateDialog = updateDialogBuilder.create();

                    updateDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.round_dialog));
                    updateDialog.show();

                    EditText nameEdit = updateDialog.findViewById(R.id.nameEditTxt);
                    dateEditTxt = updateDialog.findViewById(R.id.editTextDate);

                    RadioGroup radioGroup = updateDialog.findViewById(R.id.parkingRadioGroup);
                    EditText locationEdit = updateDialog.findViewById(R.id.locationEditTxt);
                    EditText durationEdit = updateDialog.findViewById(R.id.durationEditTxt);
                    EditText distanceEdit = updateDialog.findViewById(R.id.distanceEditTxt);
                    RatingBar ratingBar = updateDialog.findViewById(R.id.ratingBar);
                    Log.d("Hike", hike.getName());
                    nameEdit.setText(hike.getName());
                    Log.d("Date:", hike.getDate().toString());
                    // Simple date format
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

                    dateEditTxt.setText(outputDate);
                    if (hike.isAvailableParking()) {
                        radioGroup.check(R.id.yesRadioButton);
                    } else {
                        radioGroup.check(R.id.noRadioButton);
                    }
                    locationEdit.setText(hike.getLocation());
                    durationEdit.setText(hike.getDuration().toString());
                    distanceEdit.setText(hike.getDistance().toString());
                    ratingBar.setRating(hike.getDifficulty().getId());
                    boolean availableParking = radioGroup.getCheckedRadioButtonId() == R.id.yesRadioButton;

                    dateEditTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get day month, year of hike
                            updatedDate = Calendar.getInstance();
                            updatedDate.setTime(hike.getDate());
                            int year = updatedDate.get(Calendar.YEAR);
                            int month = updatedDate.get(Calendar.MONTH);
                            int day = updatedDate.get(Calendar.DAY_OF_MONTH);

                            openDatePickerDialog(year, month, day);
                        }
                    });

                    AppCompatButton saveBtn = updateDialog.findViewById(R.id.saveBtn);
                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Update the caption
                            Hike updatedHike = null;
                            // parse String date to db
                            Date date;
                            if (updatedDate != null) {
                                date = updatedDate.getTime();
                            } else {
                                date = hike.getDate();
                            }
                            try {
                                updatedHike = DatabaseHelper.updateHike(hike.getId(), nameEdit.getText().toString(), date, locationEdit.getText().toString(), availableParking, Float.parseFloat(durationEdit.getText().toString()), Float.parseFloat(distanceEdit.getText().toString()), (int) ratingBar.getRating(), hike.getDescription(),null);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d("Update hike Successfully", String.valueOf(updatedHike.getName()));
                            updatedDate = null;
                            // Update the hike in the list
                            try {
                                hikeList = DatabaseHelper.getHikes();
                                // Sort hikes by date
                                hikeList.sort((hike1, hike2) -> hike2.getDate().compareTo(hike1.getDate()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            notifyDataSetChanged();

                            Toast.makeText(context, "Hike details updated", Toast.LENGTH_SHORT - 500).show();
                            updateDialog.dismiss();
                        }
                    });
//                    updateCaptionBuilder.setPositiveButton("Update", (dialog1, which1) -> {
//                        // Update the caption
//                        Hike updatedHike = null;
//                        String dateStr = dateEditTxt.getText().toString();
//                        // parse String date to db
//                        Date date;
//                        if (updatedDate != null) {
//                            date = updatedDate.getTime();
//                        } else {
//                            date = hike.getDate();
//                        }
//                        try {
//                            updatedHike = DatabaseHelper.updateHike(hike.getId(), nameEdit.getText().toString(), date, locationEdit.getText().toString(), availableParking, Float.parseFloat(durationEdit.getText().toString()), Float.parseFloat(distanceEdit.getText().toString()), (int) ratingBar.getRating(), hike.getDescription(),null);
//                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
//                        }
//                        Log.d("Update hike Successfully", String.valueOf(updatedHike.getName()));
//                        updatedDate = null;
//                        // Update the hike in the list
//                        try {
//                            hikeList = DatabaseHelper.getHikes();
//                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
//                        }
//                        notifyDataSetChanged();
//
//                        Toast.makeText(context, "Hike details updated", Toast.LENGTH_SHORT - 500).show();
//                    });
//                    updateCaptionBuilder.setNegativeButton("Cancel", (dialog1, which1) -> {
//                        // Handle nothing
//                    });
                    updateDialog.show();
                });
                builder.setNegativeButton("Delete", (dialog, which) -> {
                    // Handle the delete action here
                    try {
                        deletedHike = DatabaseHelper.deleteHike(hike.getId());
                        Log.d("Deleted Hike Successfully", String.valueOf(hike.getId()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    // Remove the hike position from the list
                    hikeList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, hikeList.size());

                    // Display a button undo the delete action
                    ConstraintLayout undoBtn = activity.findViewById(R.id.undoHikeBtn);
                    undoBtn.setVisibility(View.VISIBLE);
                    ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                    AppCompatButton undoButton = activity.findViewById(R.id.undoButton);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(3000);
                    progressBar.setProgress(0);
                    CountDownTimer countDownTimer = new CountDownTimer(3000, 10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int remainingTime = (int) millisUntilFinished;
                            progressBar.setProgress(remainingTime);
                        }

                        @Override
                        public void onFinish() {
                            progressBar.setVisibility(View.INVISIBLE);
                            undoBtn.setVisibility(View.GONE);
                            DatabaseHelper.forceDeleteHike(deletedHike.getId());
                            Log.d("Force Deleted Hike Successfully", String.valueOf(deletedHike.getId()));
                            deletedHike = null;
                        }
                    };
                    countDownTimer.start();

                    undoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Undo Delete Hike", String.valueOf(hike.getId()));
                            Hike updatedHike;
                            // Undo the delete action
                            try {
                                updatedHike = DatabaseHelper.updateHike(hike.getId(), hike.getName(), hike.getDate(), hike.getLocation(), hike.isAvailableParking(), hike.getDuration(), hike.getDistance(), hike.getDifficulty().getId(), hike.getDescription(), null);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            // Cancel the countdown
                            countDownTimer.cancel();
                            // Hide the undo button
                            progressBar.setVisibility(View.INVISIBLE);
                            undoBtn.setVisibility(View.GONE);
                            // Add the hike back to the list
                            hikeList.add(position, updatedHike);
                            Log.d("Updated Hike name: ", updatedHike.getName());
                            Log.d("Position", String.valueOf(position));
                            Log.d("Hike ID in list", String.valueOf(hikeList.get(position).getId()));
                            notifyItemInserted(position);
                            notifyItemRangeChanged(position, hikeList.size());

                            // This is the key for motherfacker RecyclerView not messing up the ID of the hike
                            try {
                                hikeList = DatabaseHelper.getHikes();
                                hikeList.sort((hike1, hike2) -> hike2.getDate().compareTo(hike1.getDate()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            deletedHike = null;
                        }
                    });
                });
                builder.setNeutralButton("Cancel", (dialog, which) -> {
                    // Handle nothing
                });
                builder.show();

                return true;
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchTxt = charSequence.toString();
                if (searchTxt.isEmpty()) {
                    try {
                        if (HomeFragment.filter != null) {
                            hikeList = DatabaseHelper.getHikesByDifficulty(HomeFragment.filter);
                        } else {
                            hikeList = DatabaseHelper.getHikes();
                        }
                        // Sort hikes by date
                        hikeList.sort((hike1, hike2) -> hike2.getDate().compareTo(hike1.getDate()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ArrayList<Hike> filteredList = new ArrayList<>();
                    for (Hike hike : hikeList) {
                        if (hike.getName().toLowerCase().contains(searchTxt.toLowerCase())) {
                            filteredList.add(hike);
                        }
                    }
                    hikeList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = hikeList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                hikeList = (ArrayList<Hike>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    private void openDatePickerDialog(int year, int month, int day) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog and set the date set listener
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                // Handle the selected date
                String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = inputFormat.parse(selectedDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String outputDate = dateFormat.format(date);
                dateEditTxt.setText(outputDate);
                updatedDate.setTime(date);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

}
