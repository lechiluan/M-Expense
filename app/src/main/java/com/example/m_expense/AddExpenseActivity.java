package com.example.m_expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddExpenseActivity extends AppCompatActivity {

    Trip selectedTrip;
    AutoCompleteTextView typeExpense;
    String[] typeExpenseList;
    ArrayAdapter<String> adapter;

    TextInputEditText amount, note;
    EditText date;

    Button btnSave;
    Calendar calendar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));


        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add expense for \"" + selectedTrip.getName() + "\"");


        date = findViewById(R.id.expenseDate);
        note = findViewById(R.id.expenseNote);
        amount = findViewById(R.id.expenseAmount);
        typeExpense = findViewById(R.id.itemListTypeExpenses);

        //Dropdown type expense
        typeExpenseList = getResources().getStringArray(R.array.typeExpense);
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, typeExpenseList
        );
        typeExpense.setAdapter(adapter);


        calendar = Calendar.getInstance();

        //Date Picker for EditText Date
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


        //setOnClickListener to show date picker
        date.setOnClickListener(view -> new DatePickerDialog(AddExpenseActivity.this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        btnSave = findViewById(R.id.expenseBtnAdd);
        btnSave.setOnClickListener(v -> checkCredentials());
    }

    private void checkCredentials() {
        String type = typeExpense.getText().toString().trim();
        String amountInput = amount.getText().toString().trim();
        String dateExpense = date.getText().toString().trim();

        if (type.isEmpty()) {
            typeExpense.setError("This is a required field");
            typeExpense.requestFocus();
        } else if (amountInput.isEmpty()) {
            showError(amount, "This is a required field");
        } else if (dateExpense.isEmpty()) {
            showError(date, "This is a required field");
        } else {
            addExpense();
        }
    }

    private void addExpense() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);
        Expense expense = new Expense();

        String removedFormat = (amount.getText().toString().trim()).replace(",","");

        expense.setTypeExpense(typeExpense.getText().toString().trim());
        expense.setAmount(Float.parseFloat(removedFormat));
        expense.setDate(date.getText().toString().trim());
        expense.setNote(note.getText().toString().trim());
        expense.setTripID(selectedTrip.getId());

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

    private void showError(EditText input, String s) {
        input.setError(s);
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