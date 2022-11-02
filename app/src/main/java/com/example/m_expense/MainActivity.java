package com.example.m_expense;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
//    private static int SPLASH_SCREEN = 5000;
    //    Variable
    Animation topAnim, bottomAnim;
    ImageView imageLogo;
    Button btnStart;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TripActivity.class));
                finish();
            }
        });
    }
}