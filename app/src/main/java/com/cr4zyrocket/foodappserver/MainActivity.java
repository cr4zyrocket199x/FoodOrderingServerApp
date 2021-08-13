package com.cr4zyrocket.foodappserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tvSlogan;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn=findViewById(R.id.btnSignIn);

        //Slogan
        tvSlogan=findViewById(R.id.txtSlogan);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        tvSlogan.setTypeface(typeface);

        btnSignIn.setOnClickListener(v -> {
            Intent intentSignIn=new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intentSignIn);
        });
    }
}