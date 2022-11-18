package com.example.m_expense.Trip;

import android.app.AlertDialog;
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

import com.example.m_expense.Database.MyDatabaseHelper;
import com.example.m_expense.Database.Trip;
import com.example.m_expense.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddTripActivity extends AppCompatActivity {

    // UI elements
    TextInputEditText name, destination, desc;
    EditText dateStart, dateEnd;
    Button btnAdd;
    Calendar calendar;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String risk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();
        // set date picker
        datePickerStart();
        datePickerEnd();
        // when click add button
        whenClickAdd();
    }

    private void whenClickAdd() {
        //setOnClickListener to show date picker
        btnAdd.setOnClickListener(v -> checkCredentials());
    }

    private void datePickerStart() {
        calendar = Calendar.getInstance(); // get current date
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
        dateStart.setOnClickListener(view -> new DatePickerDialog(AddTripActivity.this, datePickerStart, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void datePickerEnd() {
        calendar = Calendar.getInstance();
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
        dateEnd.setOnClickListener(view -> new DatePickerDialog(AddTripActivity.this, datePickerEnd, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    private void findAllElements() {
        name = findViewById(R.id.tripName);
        destination = findViewById(R.id.tripDestination);
        desc = findViewById(R.id.description);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);
        btnAdd = findViewById(R.id.tripBtnAdd);
    }

    private void checkCredentials() {
        String tripName = Objects.requireNonNull(name.getText()).toString().trim();
        String location = Objects.requireNonNull(destination.getText()).toString().trim();
        String dateS = dateStart.getText().toString().trim();
        String dateE = dateEnd.getText().toString().trim();
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        risk = selectedRadioButton.getText().toString();

        if (tripName.isEmpty()) {
            showError(name);
        } else if (location.isEmpty()) {
            showError(destination);
        } else if (dateS.isEmpty()) {
            showError(dateStart);
        } else if (dateS.compareTo(dateE) > 0) {
            showError(dateEnd);
            Toast.makeText(this, "Date End must be after Date Start! ", Toast.LENGTH_SHORT).show();
        } else if (dateE.isEmpty()) {
            showError(dateEnd);
        } else {
            name.setError(null);
            dateStart.setError(null);
            dateEnd.setError(null);
            destination.setError(null);
            confirmDataTrip();
        }
    }

    public void confirmDataTrip(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String tripName = Objects.requireNonNull(name.getText()).toString().trim();
        String location = Objects.requireNonNull(destination.getText()).toString().trim();
        String dateS = dateStart.getText().toString().trim();
        String dateE = dateEnd.getText().toString().trim();
        String description = Objects.requireNonNull(desc.getText()).toString().trim();
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        risk = selectedRadioButton.getText().toString();

        builder.setTitle("Do you want to add this trip?");
        builder.setMessage("Trip Name: "+tripName +
                "\nTrip Destination: " + location + "\nDate Start : " + dateS+
                "\nDate End: " + dateE + "\nDescription: "+ description + "\nTrip Risk Assessment: " + risk);
        builder.setPositiveButton("Add", (dialogInterface, i) -> addTrip());
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    private void addTrip() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(AddTripActivity.this);
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        risk = selectedRadioButton.getText().toString();

        Trip trip = new Trip();
        trip.setName(Objects.requireNonNull(name.getText()).toString().trim());
        trip.setDes(Objects.requireNonNull(destination.getText()).toString().trim());
        trip.setDesc(Objects.requireNonNull(desc.getText()).toString().trim());
        trip.setDateFrom(dateStart.getText().toString().trim());
        trip.setDateTo(dateEnd.getText().toString().trim());
        trip.setRisk(risk);


        long result = myDB.addTrip(trip);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddTripActivity.this, TripActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }
}