package com.example.m_expense.Trip;

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

public class UpdateTripActivity extends AppCompatActivity {

    // UI elements
    TextInputEditText name, destination, desc;
    EditText dateStart, dateEnd;
    Trip selectedTrip;
    Button btnSave;
    Calendar calendar;
    RadioGroup radioGroup;
    RadioButton yes, no, selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();

        getAndDisplayInfo(); // get selected trip info and display it
        // set date picker
        DatePickerStart();
        DatePickerEnd();
        // when click save button
        whenClickSave();
    }

    private void whenClickSave() {
        btnSave.setOnClickListener(view -> checkCredentials());
    }
    private void checkCredentials(){
        String tripName = Objects.requireNonNull(name.getText()).toString().trim();
        String location = Objects.requireNonNull(destination.getText()).toString().trim();
        String dateS = dateStart.getText().toString().trim();
        String dateE = dateEnd.getText().toString().trim();

        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());

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
            //method call for insert function
            updateTrip();
        }
    }

    private void updateTrip() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String risk = selectedRadioButton.getText().toString();

        selectedTrip.setName(Objects.requireNonNull(name.getText()).toString().trim());
        selectedTrip.setDes(Objects.requireNonNull(destination.getText()).toString().trim());
        selectedTrip.setDateFrom(dateStart.getText().toString().trim());
        selectedTrip.setDateTo(dateEnd.getText().toString().trim());
        selectedTrip.setRisk(risk);
        selectedTrip.setDesc(Objects.requireNonNull(desc.getText()).toString().trim());

        long result = myDB.updateTrip(selectedTrip);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UpdateTripActivity.this, TripActivity.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }
    private void DatePickerEnd() {
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePickerTo = new DatePickerDialog.OnDateSetListener() {
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
        dateEnd.setOnClickListener(view -> new DatePickerDialog(UpdateTripActivity.this, datePickerTo, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void DatePickerStart() {
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePickerFrom = new DatePickerDialog.OnDateSetListener() {
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
        dateStart.setOnClickListener(view -> new DatePickerDialog(UpdateTripActivity.this, datePickerFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void findAllElements() {
        name = findViewById(R.id.tripName);
        destination = findViewById(R.id.tripDestination);
        desc = findViewById(R.id.description);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);
        yes = findViewById(R.id.radioYes);
        no = findViewById(R.id.radioNo);
        btnSave = findViewById(R.id.tripBtnUpdate);
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

    private void getAndDisplayInfo() {
        // Display the selected trip's information
        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip");
        name.setText(selectedTrip.getName());
        destination.setText(selectedTrip.getDes());
        dateStart.setText(selectedTrip.getDateFrom());
        dateEnd.setText(selectedTrip.getDateTo());
        desc.setText(selectedTrip.getDesc());

        if (selectedTrip.getRisk().equals("Yes")) {
            yes.setChecked(true);
        } else {
            no.setChecked(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update \"" + selectedTrip.getName() + "\"");
    }
}