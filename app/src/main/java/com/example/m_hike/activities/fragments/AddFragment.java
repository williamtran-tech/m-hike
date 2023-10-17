package com.example.m_hike.activities.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView date;
    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View addFragmentView = inflater.inflate(R.layout.fragment_add, container, false);
        dbHelper = new DatabaseHelper(getActivity().getBaseContext());
        date = (TextView) addFragmentView.findViewById(R.id.editTextDate);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy");
        date.setText(sdf.format(cal.getTime()));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerDialogTheme();
                datePicker.show(getFragmentManager(), "date picker");
            }
        }
        );

        Button saveBtn = (Button) addFragmentView.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });

        // Change opacity of the background image when scroll
        ImageView background = (ImageView) addFragmentView.findViewById(R.id.imageView);
        ScrollView scrollView = (ScrollView) addFragmentView.findViewById(R.id.scrollView);
        // Changing alpha of the background image when scroll
        // Define the max scroll amount
        final int MAX_SCROLL_FACTOR = 700;
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Calculate the alpha based on the scroll position
            int scrollY = scrollView.getScrollY();
            float alpha = 1.0f - Math.min(1.0f, (float) scrollY / MAX_SCROLL_FACTOR); // Adjust the threshold as needed
            // Update the overlay view's alpha
            background.setAlpha(alpha);
        });
        // Inflate the layout for this fragment
        return addFragmentView;
    }

    private void saveDetails() {
        EditText nameTxt = getActivity().findViewById(R.id.nameEditTxt);
        TextView date = getActivity().findViewById(R.id.editTextDate);

        EditText locationTxt = getActivity().findViewById(R.id.locationEditTxt);

        String name = nameTxt.getText().toString();
        String location = locationTxt.getText().toString();
//        Date insertDate = new Date(date.getYear(), date.getMonth(), date.getDayOfMonth());
        Date insertDate = new Date();
        long personId = dbHelper.insertHike(name, location, insertDate);


        // Make the device vibrate
        Vibrator vibrator = (Vibrator) getActivity().getBaseContext().getSystemService(getActivity().getBaseContext().VIBRATOR_SERVICE);
        vibrator.vibrate(400);

        // Notify
        Toast.makeText(getActivity(), "Hikes: " + dbHelper.getHikes(), Toast.LENGTH_SHORT - 500).show();

        // Clear fields
        nameTxt.setText("");
        locationTxt.setText("");

        Log.d("Get Hikes:", dbHelper.getHikes());
    }

    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);
            return datepickerdialog;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            TextView date = getActivity().findViewById(R.id.editTextDate);
            // Get the date of week
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String dayOfWeekString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getActivity().getResources().getConfiguration().locale);

            date.setText(dayOfWeekString + ", " + day + "/" + (month + 1) + "/" + year);
        }
    }
}