package com.example.m_expense.Trip;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_expense.Authentication.LoginActivity;
import com.example.m_expense.Database.MyDatabaseHelper;
import com.example.m_expense.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripActivity extends AppCompatActivity {

    // UI elements
    RecyclerView recyclerView;
    ImageView empty_imageview, btnAdd, btnVoice;
    TextView no_data;
    MyDatabaseHelper myDB;
    List<Trip> trips;
    TripAdapter tripAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        myDB = new MyDatabaseHelper(TripActivity.this);
        trips = new ArrayList<>();
        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();
        // when click add button
        whenClickAdd();
        // when click search trip
        searchTrip();
        // display all trips
        displayTrip();
        // when click voice button
        whenClickVoice();
        // recycler view for trip list
        recyclerViewTrip();
    }

    private void whenClickVoice() {
        btnVoice.setOnClickListener(v -> {
            // create intent to start speech recognizer
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
            startActivityForResult(intent, 100);
        });
    }

    private void recyclerViewTrip() {
        tripAdapter = new TripAdapter(TripActivity.this, this, trips); // create adapter
        recyclerView.setAdapter(tripAdapter); // set adapter to recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(TripActivity.this)); // set layout manager to position the items
    }

    private void searchTrip() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tripAdapter.getFilter().filter(query); // filter recycler view when query submitted
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                tripAdapter.getFilter().filter(newText); // filter recycler view when text is changed
                return false;
            }
        });
    }

    private void whenClickAdd() {
        btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(TripActivity.this, AddTripActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void findAllElements() {
        recyclerView = findViewById(R.id.recyclerView_trip);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        btnAdd = findViewById(R.id.add_button);
        searchView = findViewById(R.id.searchTrip);
        btnVoice = findViewById(R.id.search_voice);
    }

    private void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hi, " + LoginActivity.currentUser);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate(); // refresh activity
        }
        if(requestCode == 100 && resultCode == RESULT_OK){
            assert data != null;
            searchView.setQuery(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0), true); // set query to search view when voice recognized
        }
    }

    void displayTrip() {
        trips = myDB.getAllTrip();
        if (trips.size() == 0) {
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
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            if (trips.size() == 0) {
                Toast.makeText(this, "No data to delete", Toast.LENGTH_SHORT).show();
            } else {
                confirmDialogDelete();
            }
        }
        if(item.getItemId() == R.id.logout){
            confirmDialogLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure to delete all trips?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(TripActivity.this);
            myDB.deleteAllTrip();
            Toast.makeText(TripActivity.this, "Deleted All Trip is Successfully !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TripActivity.this, TripActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }
    private void confirmDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout?");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(TripActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {});
        builder.create().show();
    }
}