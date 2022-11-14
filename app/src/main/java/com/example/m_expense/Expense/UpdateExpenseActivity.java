package com.example.m_expense.Expense;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.m_expense.Database.MyDatabaseHelper;
import com.example.m_expense.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UpdateExpenseActivity extends AppCompatActivity {

    // UI elements
    AutoCompleteTextView typeExpense;
    EditText dateInput, amount, note, location;
    Button btnSave;
    ImageView buttonLocation;
    String[] typeExpenseList;
    ArrayAdapter<String> adapter;
    Calendar calendar;
    Expense selectedExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expense);

        // set status bar color
        setStatusColor();

        Intent intent = getIntent();
        selectedExpense = (Expense) intent.getSerializableExtra("selectedExpense");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update \"" + selectedExpense.getTypeExpense() + "\"");

        // find all elements
        findAllElements();
        getAndDisplayInfo(); // get selected trip info and display it
        datePicker();
        //Dropdown type expense
        dropDownTypeExpense();
        btnSave.setOnClickListener(view -> checkCredentials());
        whenClickLocation();
    }

    private void whenClickLocation() {
        buttonLocation.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }
            else {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Location Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try {
                    String city = hereLocation(Location.getLatitude(), Location.getLongitude());
                    location.setText(city);
                }
                catch (Exception e){
                    Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                    }else{
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        Location Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try {
                            String city = hereLocation(Location.getLatitude(), Location.getLongitude());
                            location.setText(city);
                        }
                        catch (Exception e){
                            Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String hereLocation(double latitude, double longitude) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        cityName = adr.getLocality();
                        cityName = cityName + ", " + adr.getAdminArea() + ", " + adr.getCountryName();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }
    private void dropDownTypeExpense() {
        typeExpenseList = getResources().getStringArray(R.array.typeExpense);
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, typeExpenseList
        );
        typeExpense.setAdapter(adapter);
    }

    private void datePicker() {
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
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
                dateInput.setText(sdf.format(calendar.getTime()));

            }
        };
        dateInput.setOnClickListener(view -> new DatePickerDialog(UpdateExpenseActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void findAllElements() {
        dateInput = findViewById(R.id.expenseDate);
        typeExpense = findViewById(R.id.itemListTypeExpenses);
        amount = findViewById(R.id.expenseAmount);
        note = findViewById(R.id.expenseNote);
        btnSave = findViewById(R.id.expenseBtnUpdate);
        buttonLocation = findViewById(R.id.button_location);
        location = findViewById(R.id.location);
    }

    private void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }

    private void checkCredentials() {
        String type = typeExpense.getText().toString().trim();
        String money = amount.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String Location = location.getText().toString().trim();

        if (type.isEmpty()) {
            typeExpense.setError("This is a required field");
            typeExpense.requestFocus();
        } else if (money.isEmpty()) {
            showError(amount);
        } else if (date.isEmpty()) {
            showError(dateInput);
        } else if (Location.isEmpty()) {
            showError(location);
        }
        else {
            typeExpense.setError(null);
            amount.setError(null);
            dateInput.setError(null);
            updateExpense();
        }
    }

    private void updateExpense() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);

        selectedExpense.setTypeExpense(typeExpense.getText().toString().trim());
        selectedExpense.setAmount(Float.parseFloat(amount.getText().toString().trim()));
        selectedExpense.setDate(dateInput.getText().toString().trim());
        selectedExpense.setNote(note.getText().toString().trim());
        selectedExpense.setLocation(location.getText().toString().trim());

        long result = myDB.updateExpense(selectedExpense);

        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finishActivity(1);
        }

    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }

    private void getAndDisplayInfo() {
        typeExpense.setText(selectedExpense.getTypeExpense());
        amount.setText(String.valueOf(selectedExpense.getAmount()));
        dateInput.setText(selectedExpense.getDate());
        note.setText(selectedExpense.getNote());
        location.setText(selectedExpense.getLocation());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}