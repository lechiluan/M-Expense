package com.example.m_expense;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    ImageView imageLogo;
    Button btnStart;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Animation
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
//       Hooks
        imageLogo = findViewById(R.id.imageLogo);
        btnStart = findViewById(R.id.btnStart);
        imageLogo.setAnimation(topAnim);
        btnStart.setAnimation(bottomAnim);
        textView = findViewById(R.id.textView);
        textView.setAnimation(bottomAnim);

        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        });
    }
}