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
import com.example.m_expense.Trip.Trip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
// Reference
//https://www.youtube.com/watch?v=rKnzzrdhb9g
//https://stackoverflow.com/questions/18221614/how-i-can-get-the-city-name-of-my-current-position


public class AddExpenseActivity extends AppCompatActivity {

    // UI elements
    Trip selectedTrip;
    ImageView buttonLocation;
    AutoCompleteTextView typeExpense;
    String[] typeExpenseList;
    ArrayAdapter<String> adapter;
    TextInputEditText amount, note, location;
    EditText date;
    Button btnAdd;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Intent intent = getIntent(); // get intent from previous activity
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip"); // get selected trip from previous activity
        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();
        // set date picker
        dropDownMenu();
        datePicker();
        // when click add button
        whenClickAdd();
        // when click location button
        whenClickLocation();
    }

    private void whenClickAdd() {
        btnAdd.setOnClickListener(v -> checkCredentials());
    }

    private void whenClickLocation() {
        buttonLocation.setOnClickListener(v -> {
            // check permission for location
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
        // check permission for location
        if (requestCode == 1000) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                }else{
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Location Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(Location.getLatitude(), Location.getLongitude()); // get city name
                        location.setText(city); // set location
                    }
                    catch (Exception e){
                        Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String hereLocation(double latitude, double longitude) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault()); // get city name
        List<Address> addresses; // list of address
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 10); // get address from latitude and longitude
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) { // check if city name is not null
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

    private void dropDownMenu() {
        //Dropdown type expense
        typeExpenseList = getResources().getStringArray(R.array.typeExpense);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeExpenseList); // set adapter for dropdown
        typeExpense.setAdapter(adapter);
    }

    private void datePicker() {
        calendar = Calendar.getInstance(); // get current date
        // Date picker dialog for date field
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
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
                date.setText(sdf.format(calendar.getTime()));
            }
        };
        date.setOnClickListener(view -> new DatePickerDialog(AddExpenseActivity.this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    public void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add expense for \"" + selectedTrip.getName() + "\"");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow(); // set status bar color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // clear FLAG_TRANSLUCENT_STATUS flag
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }

    private void findAllElements() {
        date = findViewById(R.id.expenseDate);
        note = findViewById(R.id.expenseNote);
        amount = findViewById(R.id.expenseAmount);
        typeExpense = findViewById(R.id.itemListTypeExpenses);
        btnAdd = findViewById(R.id.expenseBtnAdd);
        location = findViewById(R.id.txtlocation);
        buttonLocation = findViewById(R.id.button_location);
    }

    private void checkCredentials() {
        String type = typeExpense.getText().toString().trim();
        String amountInput = Objects.requireNonNull(amount.getText()).toString().trim();
        String dateExpense = date.getText().toString().trim();
        String Location = Objects.requireNonNull(location.getText()).toString().trim();
        if (type.isEmpty()) {
            typeExpense.setError("This is a required field");
            typeExpense.requestFocus();
        } else if (amountInput.isEmpty()) {
            showError(amount);
        } else if (dateExpense.isEmpty()) {
            showError(date);
        } else if (Location.isEmpty()) {
            showError(location);
        }
        else {
            typeExpense.setError(null);
            amount.setError(null);
            date.setError(null);
            addExpense();
        }
    }

    private void addExpense() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);
        Expense expense = new Expense();
        // get data from input
        String removedFormat = (Objects.requireNonNull(amount.getText()).toString().trim()).replace(",","");
        expense.setTypeExpense(typeExpense.getText().toString().trim());
        expense.setAmount(Float.parseFloat(removedFormat));
        expense.setDate(date.getText().toString().trim());
        expense.setNote(Objects.requireNonNull(note.getText()).toString().trim());
        expense.setTripID(selectedTrip.getId());
        expense.setLocation(Objects.requireNonNull(location.getText()).toString().trim());

        long result = myDB.addExpense(expense);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddExpenseActivity.this, ExpenseActivity.class);
            intent.putExtra("selectedTrip", selectedTrip);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}