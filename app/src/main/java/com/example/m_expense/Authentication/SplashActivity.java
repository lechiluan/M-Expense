package com.example.m_expense.Authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.m_expense.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    ImageView imageLogo;
    Button btnStart;
    TextView textView, slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // set status bar color
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

//        Animation
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
//       Hooks
        imageLogo = findViewById(R.id.imageLogo);
        btnStart = findViewById(R.id.btnStart);
        textView = findViewById(R.id.textView);
        slogan = findViewById(R.id.slogan);

        imageLogo.setAnimation(topAnim);
        slogan.setAnimation(topAnim);
        textView.setAnimation(topAnim);
        btnStart.setAnimation(bottomAnim);
        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        });
    }
}