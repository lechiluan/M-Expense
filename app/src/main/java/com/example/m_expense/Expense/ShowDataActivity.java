package com.example.m_expense.Expense;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_expense.R;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ShowDataActivity extends AppCompatActivity {

    TextView displayData;
    ArrayList<String> savedList = new ArrayList<>();
    String fileName = "DataExpense.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        displayData = findViewById(R.id.dataDisplay);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedList = readFile(fileName);
        displayData.setText(savedList.get(0));
    }

    public ArrayList<String> readFile(String file) {
        ArrayList<String> text = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            text = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
        }
        return text;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}