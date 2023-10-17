package com.humorstech.respyr.authentication.profile_creation.lifestyle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.utills.ProfileCreationData;

public class Alchohol extends AppCompatActivity {

    private int alcoholConsumption=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alchohol);

        StatusBarColor statusBarColor = new StatusBarColor(Alchohol.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        init();
    }

    private void init(){
        performAlcohol();
        onClick();


        LinearLayout linearLayout = findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(), linearLayout, 8, 4);
    }
    private void performAlcohol(){

        RadioButton rYes = findViewById(R.id.radio_button_alcohol_yes);
        RadioButton rNo = findViewById(R.id.radio_button_alcohol_no);

        if (alcoholConsumption<=0){
            rYes.setChecked(false);
            rNo.setChecked(true);
        }else{
            rYes.setChecked(true);
            rNo.setChecked(false);
        }


        RadioGroup radioGroup = findViewById(R.id.radio_group_alcohol_taken);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button_alcohol_yes) {
                alcoholConsumption=1;

            } else if (checkedId == R.id.radio_button_alcohol_no) {
                alcoholConsumption=0;
            }
        });
    }

    private void onClick(){
        Button buttonNext =findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ProfileCreationData.ALCOHOL_UNIT, String.valueOf(alcoholConsumption));
                editor.apply();


                Intent intent = new Intent(getApplicationContext(), Exercise.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(Alchohol.this);
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
        Animatoo1.animateSlideRight(Alchohol.this);
    }
}