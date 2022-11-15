package com.example.m_expense.Expense;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.m_expense.Database.MyDatabaseHelper;
import com.example.m_expense.R;
import com.example.m_expense.Trip.Trip;
import com.example.m_expense.Trip.TripActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class ExpenseActivity extends AppCompatActivity {
    // UI elements
    TextView tripName, destination, dateFrom, dateTo, tripRisk, total;
    Trip selectedTrip;
    ImageView empty_imageview, btnAdd;
    List<Expense> expenses;
    ExpenseAdapter expenseAdapter;
    RecyclerView recyclerView;
    TextView no_data;

    MyDatabaseHelper myDB; // database helper class
    static String filename = "DataExpense";
    static String fileExtension = ".json";
    static String fullFileName = filename + fileExtension;
    ArrayList<String> savedList = new ArrayList<>(); // list of saved expense

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        // set status bar color
        setStatusColor();

        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip");
        myDB = new MyDatabaseHelper(this);
        expenses = new ArrayList<>();

        findAllElements();
        displayOrNot();
        // recycler view for expense list
        recyclerViewTrip();
        whenClickAdd();
        getDetails();
    }

    private void recyclerViewTrip() {
        expenseAdapter = new ExpenseAdapter(ExpenseActivity.this, this, expenses);
        recyclerView.setAdapter(expenseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExpenseActivity.this));
    }

    private void whenClickAdd() {
        btnAdd.setOnClickListener(view -> {
            Intent intents = new Intent(ExpenseActivity.this, AddExpenseActivity.class);
            intents.putExtra("selectedTrip", selectedTrip);
            startActivity(intents);
        });
    }

    private void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expense Management");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }

    private void findAllElements() {
        recyclerView = findViewById(R.id.recyclerView_expense);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        tripName = findViewById(R.id.expenseType);
        destination = findViewById(R.id.destination);
        dateFrom = findViewById(R.id.dateFrom);
        dateTo = findViewById(R.id.dateTo);
        tripRisk = findViewById(R.id.tripRisk);
        total = findViewById(R.id.totalExpense);
        btnAdd = findViewById(R.id.add_button_expense);
    }

    @SuppressLint("SetTextI18n")
    private void getDetails() {
        tripName.setText(selectedTrip.getName());
        destination.setText(selectedTrip.getDes());
        dateFrom.setText(selectedTrip.getDateFrom());
        dateTo.setText(selectedTrip.getDateTo());
        tripRisk.setText(selectedTrip.getRisk());

        Float totalExpenses = myDB.getTotalExpense(String.valueOf(selectedTrip.getId())); // get total expense
        total.setText(String.valueOf(totalExpenses));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void displayOrNot() {
        expenses = myDB.getAllExpense(selectedTrip.getId());
        if (expenses.size() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_expense, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDialog();
        }
        if(item.getItemId() == R.id.export_data){
            // check table expense is null
            if (expenses.size() == 0) {
                Toast.makeText(this, "No data to export !", Toast.LENGTH_SHORT).show();
            } else {
                confirmDialogExport();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportData(int id) {
        // clear all data in file saved
        savedList.clear();
        // get all expense of selected trip
        expenses = myDB.getAllExpense(id);

        Gson gson = new Gson();
        String json = gson.toJson(expenses);
        savedList.add(json);

        if(savedList(fullFileName, savedList)){
//            Toast.makeText(this, "Exported to " + getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Exported to " + "Download/" + fullFileName, Toast.LENGTH_LONG).show();
            startActivity(new Intent(ExpenseActivity.this, ShowDataActivity.class));
        }
        else{
            Toast.makeText(this, "Export is failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean savedList(String fullFileName, ArrayList<String> savedList) {
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fullFileName));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); // create an output stream writer object
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter); // create a buffered writer object to write to the file
            bufferedWriter.write(String.valueOf(savedList)); // write the URL to the file
            bufferedWriter.newLine(); // write a new line
            bufferedWriter.flush(); // flush the buffer
            bufferedWriter.close(); // close the buffered writer
            outputStreamWriter.close(); // close the output stream writer
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Export is failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all expenses?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(ExpenseActivity.this);
            myDB.deleteAllExpense();
            // Refresh Activity
            Toast.makeText(ExpenseActivity.this, "Deleted All Expenses is Successfully! ", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(getIntent());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {});
        builder.create().show();
    }
    private void confirmDialogExport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export All?");
        builder.setMessage("Do you want to export all expenses?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            // check fullFileName if exits will change filename variable
            if (checkFile(fullFileName)) {
                int count = 0; // count number of file exits
                do {
                    count++;
                    fullFileName = filename + "(" + count + ")" + fileExtension;
                } while (checkFile(fullFileName));
            }
            exportData(selectedTrip.getId());
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {});
        builder.create().show();
    }

    private boolean checkFile(String fullFileName) {
        File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fullFileName);
        return file.exists();
    }
}