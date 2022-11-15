package com.example.m_expense.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.m_expense.Database.MyDatabaseHelper;
import com.example.m_expense.R;
import com.example.m_expense.Trip.TripActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static String currentUser;
    EditText username, password;
    Button btnLogin;
    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.txtUsername);
        password = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        // Set status bar color
        setStatusColor();

        btnLogin.setOnClickListener(v -> checkInput());
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
    private void checkInput() {
        db = new MyDatabaseHelper(this);
        String Username = username.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if (Username.isEmpty()) {
            showError(username);
        } else if (Password.isEmpty()) {
            showError(password);
        }
        Boolean checkData = db.checkUserPass(Username, Password);
        if (checkData) {
            Toast.makeText(LoginActivity.this, "Log in is successfully !", Toast.LENGTH_SHORT).show();
            currentUser = Username;
            Intent intent = new Intent(getApplicationContext(), TripActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Username or Password is incorrect !", Toast.LENGTH_SHORT).show();
        }
    }
    private void showError(EditText input) {
        input.setError("This field cannot be empty");
        input.requestFocus();
    }
}