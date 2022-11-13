package com.example.m_expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddTripActivity extends AppCompatActivity {

    TextInputEditText name, destination, desc;
    EditText dateStart, dateEnd;

    Button btnSave;
    Calendar calendar;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String risk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        name = findViewById(R.id.tripName);
        destination = findViewById(R.id.tripDestination);
        desc = findViewById(R.id.description);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);


        calendar = Calendar.getInstance();

        //Date Picker for EditText Date From
        DatePickerDialog.OnDateSetListener datePickerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                dateStart.setText(sdf.format(calendar.getTime()));
            }
        };

        //Date Picker for EditText Date To
        DatePickerDialog.OnDateSetListener datePickerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                dateEnd.setText(sdf.format(calendar.getTime()));
            }
        };

        //setOnClickListener to show date picker
        dateStart.setOnClickListener(view -> new DatePickerDialog(AddTripActivity.this, datePickerStart, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        dateEnd.setOnClickListener(view -> new DatePickerDialog(AddTripActivity.this, datePickerEnd, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());


        btnSave = findViewById(R.id.tripBtnAdd);
        btnSave.setOnClickListener(v -> checkCredentials());
    }

    private void checkCredentials() {
        String tripName = name.getText().toString().trim();
        String location = destination.getText().toString().trim();
        String dateF = dateStart.getText().toString().trim();
        String dateT = dateEnd.getText().toString().trim();

        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        risk = selectedRadioButton.getText().toString();

        if (tripName.isEmpty()) {
            showError(name, "This is a required field");
        } else if (location.isEmpty()) {
            showError(destination, "This is a required field");
        } else if (dateF.isEmpty()) {
            showError(dateStart, "This is a required field");
        } else if (new Date(dateF).after(new Date(dateT))) {
            showError(dateEnd, "Invalid date");
        } else if (dateT.isEmpty()) {
            showError(dateEnd, "This is a required field");
        } else {
            //method call for insert function
            addTrip();
        }
    }

    private void addTrip() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(AddTripActivity.this);
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        risk = selectedRadioButton.getText().toString();

        Trip trip = new Trip();
        trip.setName(name.getText().toString().trim());
        trip.setDes(destination.getText().toString().trim());
        trip.setDesc(desc.getText().toString().trim());
        trip.setDateFrom(dateStart.getText().toString().trim());
        trip.setDateTo(dateEnd.getText().toString().trim());
        trip.setRisk(risk);


        long result = myDB.add(trip);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddTripActivity.this, TripActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }


}