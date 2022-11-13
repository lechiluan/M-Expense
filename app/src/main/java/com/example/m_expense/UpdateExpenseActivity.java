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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class UpdateExpenseActivity extends AppCompatActivity {

    AutoCompleteTextView typeExpense;
    EditText dateInput, amount, note;
    Button btnSave;

    String[] typeExpenseList;
    ArrayAdapter<String> adapter;
    Calendar calendar;

    Expense selectedExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expense);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        Intent intent = getIntent();
        selectedExpense = (Expense) intent.getSerializableExtra("selectedExpense");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update \"" + selectedExpense.getTypeExpense() + "\"");

        dateInput = findViewById(R.id.expenseDate);
        typeExpense = findViewById(R.id.itemListTypeExpenses);
        amount = findViewById(R.id.expenseAmount);
        note = findViewById(R.id.expenseNote);
        btnSave = findViewById(R.id.expenseBtnUpdate);
        calendar = Calendar.getInstance();

        getAndDisplayInfo();

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

        //Dropdown type expense
        typeExpenseList = getResources().getStringArray(R.array.typeExpense);
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, typeExpenseList
        );
        typeExpense.setAdapter(adapter);

        btnSave.setOnClickListener(view -> checkCredentials());
    }

    private void checkCredentials() {
        String type = typeExpense.getText().toString().trim();
        String money = amount.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        if (type.isEmpty()) {
            typeExpense.setError("This is a required field");
            typeExpense.requestFocus();
        } else if (money.isEmpty()) {
            showError(amount, "This is a required field");
        } else if (date.isEmpty()) {
            showError(dateInput, "This is a required field");
        } else {
            updateExpense();
        }
    }

    private void updateExpense() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);

        selectedExpense.setTypeExpense(typeExpense.getText().toString().trim());
        selectedExpense.setAmount(Float.parseFloat(amount.getText().toString().trim()));
        selectedExpense.setDate(dateInput.getText().toString().trim());
        selectedExpense.setNote(note.getText().toString().trim());

        long result = myDB.updateExpense(selectedExpense);

        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finishActivity(1);
        }

    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void getAndDisplayInfo() {
        typeExpense.setText(selectedExpense.getTypeExpense());
        amount.setText(String.valueOf(selectedExpense.getAmount()));
        dateInput.setText(selectedExpense.getDate());
        note.setText(selectedExpense.getNote());
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