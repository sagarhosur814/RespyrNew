package com.humorstech.respyr.authentication.profile_creation.lifestyle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.utills.ProfileCreationData;

public class Cigarettes extends AppCompatActivity {
    private int cigarettesCount=0;
    private String alcoholStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cigarettes);
        StatusBarColor statusBarColor = new StatusBarColor(Cigarettes.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
        LinearLayout linearLayout =findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(),linearLayout, 5,4);

        init();

    }

    private void init(){
        performSmokeCounter();
        onClicks();


        // dots
        LinearLayout linearLayout = findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(), linearLayout, 8, 3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
        alcoholStr = sharedPreferences.getString(ProfileCreationData.ALCOHOL, "0");
    }

    private void performSmokeCounter(){
        ImageButton buttonSmokeMinus =findViewById(R.id.image_button_smoke_minus);
        ImageButton buttonSmokePlus =findViewById(R.id.image_button_smoke_plus);
        TextView txtCigarettes =findViewById(R.id.txt_cigarettes);

        txtCigarettes.setText(String.valueOf(cigarettesCount));

        buttonSmokeMinus.setOnClickListener(v -> {
            txtCigarettes.setText("");
            if (cigarettesCount>0){
                cigarettesCount--;
            }
            txtCigarettes.setText(String.valueOf(cigarettesCount));
        });

        buttonSmokePlus.setOnClickListener(v -> {
            txtCigarettes.setText("");
            cigarettesCount++;
            txtCigarettes.setText(String.valueOf(cigarettesCount));
        });

    }

    private void onClicks(){
        Button buttonNext =findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ProfileCreationData.CIGARETTES_UNIT, String.valueOf(cigarettesCount));

                editor.apply();

                if (alcoholStr.equals("Never")){
                    Intent intent = new Intent(getApplicationContext(), Exercise.class);
                    startActivity(intent);
                    Animatoo1.animateSlideLeft(Cigarettes.this);
                }else{
                    Intent intent = new Intent(getApplicationContext(), Alchohol.class);
                    startActivity(intent);
                    Animatoo1.animateSlideLeft(Cigarettes.this);
                }

            }
        });

        Button buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(Cigarettes.this);
    }
}