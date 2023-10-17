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
import android.widget.ScrollView;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.authentication.profile_creation.personal.Gender;
import com.humorstech.respyr.utills.ProfileCreationData;

public class Hobbies extends AppCompatActivity {

    private RadioGroup rgAlcohol, rgSmoking, rgEat, rgExc;
    private String alcohol = "Never", smoking = "Never", eat = "Never", exc = "sedentary";

    private TextView txtErr1, txtErr2, txtErr3, txtErr4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);
        StatusBarColor statusBarColor = new StatusBarColor(Hobbies.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();
    }

    private void init() {
        initVars();
        radioButtons();
        onClick();
    }

    private void initVars() {
        rgAlcohol = findViewById(R.id.rg_alcohol);
        rgSmoking = findViewById(R.id.rg_smoking);
        rgEat = findViewById(R.id.rg_eat);
        rgExc = findViewById(R.id.rg_exc);

        txtErr1 = findViewById(R.id.txt_err_1);
        txtErr2 = findViewById(R.id.txt_err_2);
        txtErr3 = findViewById(R.id.txt_err_3);
        txtErr4 = findViewById(R.id.txt_err_4);

        txtErr1.setVisibility(View.GONE);
        txtErr2.setVisibility(View.GONE);
        txtErr3.setVisibility(View.GONE);
        txtErr4.setVisibility(View.GONE);



        LinearLayout linearLayout = findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(), linearLayout, 8, 2);
    }

    private void onClick() {
        Button buttonNext = findViewById(R.id.button_next);
        Button buttonBack = findViewById(R.id.button_back);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("NA".equals(alcohol)) {
                    txtErr1.setVisibility(View.VISIBLE);
                    showTxtOnError(txtErr1, "Please select one of the options");
                } else if ("NA".equals(smoking)) {
                    txtErr2.setVisibility(View.VISIBLE);
                    showTxtOnError(txtErr2, "Please select one of the options");
                } else if ("NA".equals(exc)) {
                    txtErr4.setVisibility(View.VISIBLE);
                    showTxtOnError(txtErr4, "Please select one of the options");
                } else if ("NA".equals(eat)) {
                    txtErr3.setVisibility(View.VISIBLE);
                    showTxtOnError(txtErr3, "Please select one of the options");
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ProfileCreationData.ALCOHOL, alcohol);
                    editor.putString(ProfileCreationData.SMOKING, smoking);
                    editor.putString(ProfileCreationData.EXERCISE, exc);
                    editor.putString(ProfileCreationData.NOV_VEG, eat);


                    Intent intent;
                    if ("Never".equals(smoking)) {
                        editor.putString(ProfileCreationData.CIGARETTES_UNIT, "0");

                        if ("Never".equals(alcohol)) {
                            editor.putString(ProfileCreationData.ALCOHOL_UNIT, "0");
                            intent = new Intent(getApplicationContext(), Exercise.class);
                        } else {
                            intent = new Intent(getApplicationContext(), Alchohol.class);
                        }
                        editor.apply();
                        startActivity(intent);
                        Animatoo1.animateSlideLeft(Hobbies.this);
                    } else {

                        editor.apply();
                        intent = new Intent(getApplicationContext(), Cigarettes.class);
                        startActivity(intent);
                        Animatoo1.animateSlideLeft(Hobbies.this);
                    }
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void radioButtons() {
        rgAlcohol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                txtErr1.setVisibility(View.GONE);
                alcohol = radioButton.getText().toString();
            }
        });

        rgSmoking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                txtErr2.setVisibility(View.GONE);
                smoking = radioButton.getText().toString();
            }
        });

        rgExc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                txtErr4.setVisibility(View.GONE);
                String exerciseStr = radioButton.getText().toString();
                exc=getExercise(exerciseStr);

            }
        });

        rgEat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                txtErr3.setVisibility(View.GONE);
                eat = radioButton.getText().toString();
            }
        });
    }

    private void showTxtOnError(TextView textView, String message) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(message);
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, textView.getBottom());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateFade(Hobbies.this);
    }

    private String getExercise(String exercise){

        switch (exercise){
            case "Sedentary" : return  "sedentary";
            case "Lightly" : return  "lightly_active";
            case "Moderately" : return  "moderately_active";
            case "Actively" : return  "very_active";
            case "Very Actively" : return  "extra_active";
            default: return "";

        }
    }



}
