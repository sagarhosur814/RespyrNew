package com.humorstech.respyr.authentication.profile_creation.lifestyle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.utills.ProfileCreationData;

public class Exercise extends AppCompatActivity {

    private int exerciseCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        StatusBarColor statusBarColor = new StatusBarColor(Exercise.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
        init();
    }


    private void init(){
        performExercise();
        onClicks();

        LinearLayout linearLayout = findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(), linearLayout, 8, 5);
    }

    private void onClicks(){
        Button buttonNext =findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ProfileCreationData.EXERCISE_IN_MINUTES, String.valueOf(exerciseCount));
                editor.apply();


                Intent intent = new Intent(getApplicationContext(), Food.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(Exercise.this);
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
    @SuppressLint("SetTextI18n")
    private void performExercise(){
        Button buttonExerciseMinus =findViewById(R.id.button_exc_minus);
        Button buttonExercisePlus =findViewById(R.id.button_exc_add);
        TextView txtExerciseCount =findViewById(R.id.txt_exercise);
        txtExerciseCount.setText(exerciseCount +" minutes");

        buttonExerciseMinus.setOnClickListener(v -> {

            txtExerciseCount.setText("");
            if (exerciseCount>0){
                exerciseCount= exerciseCount-15;
            }
            txtExerciseCount.setText(exerciseCount +" minutes");
        });

        buttonExercisePlus.setOnClickListener(v -> {
            txtExerciseCount.setText("");
            exerciseCount=exerciseCount+15 ;
            txtExerciseCount.setText(exerciseCount +" minutes");
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(Exercise.this);
    }
}