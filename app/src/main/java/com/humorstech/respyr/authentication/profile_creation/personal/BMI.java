package com.humorstech.respyr.authentication.profile_creation.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.profile_creation.lifestyle.Water;
import com.humorstech.respyr.utills.ProfileCreationData;

import java.text.DecimalFormat;
import java.util.Objects;

public class BMI extends AppCompatActivity {


    // Constants for BMI categories
    private static final double UNDERWEIGHT_THRESHOLD = 18.5;
    private static final double NORMAL_THRESHOLD_MIN = 18.5;
    private static final double NORMAL_THRESHOLD_MAX = 24.9;
    private static final double OVERWEIGHT_THRESHOLD_MIN = 25;
    private static final double OVERWEIGHT_THRESHOLD_MAX = 29.9;
    private static final double OBESE_THRESHOLD_MIN = 30;
    private static final double OBESE_THRESHOLD_MAX = 39.9;
    private static final double MORBIDLY_OBESE_THRESHOLD = 40.0;

    private TextView txtBmiScore, txtBmiMessage, txtBmiFooterTitle, txtBmiFooterSubTitle;
    private ImageView imgBmiGraph;
    private Button buttonNext;

    private double bmi;

    private String userHeight, userWeight;

    String[] bmiTitles = {
            "Strong",
            "Balance",
            "Progress",
            "Transform",
            "Revive"
    };

    String[] bmiSubTitles = {
            "Track and Achieve Your Ideal BMI",
            "Monitor Your BMI for Optimal Wellness",
            "Track Your BMI and Reach Your Goals",
            "Manage Your BMI and Unlock a Healthier You",
            "Harness the Power of BMI Tracking for Lasting Change"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        StatusBarColor statusBarColor = new StatusBarColor(BMI.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        init();
    }

    private void init() {
        initVars();
        onClicks();
        getData();
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
        userHeight = sharedPreferences.getString(ProfileCreationData.HEIGHT, "0"); // Default to 0
        userWeight = sharedPreferences.getString(ProfileCreationData.WEIGHT, "0"); // Default to 0
        if (!Objects.equals(userHeight, "0") && !userWeight.equals("0")){
            displayBmi();
        }
    }

    private void initVars() {
        txtBmiScore = findViewById(R.id.txt_bmi_score);
        txtBmiMessage = findViewById(R.id.txt_bmi_message);
        imgBmiGraph = findViewById(R.id.img_bmi_graph);
        buttonNext = findViewById(R.id.button_next);
        txtBmiFooterTitle = findViewById(R.id.txt_bmi_footer_title);
        txtBmiFooterSubTitle = findViewById(R.id.txt_bmi_footer_sub_title);
    }

    private void onClicks() {
        buttonNext.setOnClickListener(v -> {
            if (bmi != 0) {

                SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ProfileCreationData.BMI, String.valueOf(roundTwoDecimals(bmi)));
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Water.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(BMI.this);

            }
        });
    }

    private double findBMI(double weight, double height) {
        return weight / ((height / 100) * (height / 100));
    }

    private void displayBmi() {
        try {
            double height = Double.parseDouble(userHeight);
            double weight = Double.parseDouble(userWeight);
            bmi = findBMI(weight, height);
            txtBmiScore.setText(String.valueOf(roundTwoDecimals(bmi)));
            categorizeBmi(bmi);
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }
    }

    private void categorizeBmi(double bmi) {
        if (bmi < UNDERWEIGHT_THRESHOLD) {
            setBmiCategoryUI("Under Weight", R.color.under_weight, R.drawable.bmi1, 0);
        } else if (bmi >= NORMAL_THRESHOLD_MIN && bmi <= NORMAL_THRESHOLD_MAX) {
            setBmiCategoryUI("Normal", R.color.normal, R.drawable.bmi2, 1);
        } else if (bmi >= OVERWEIGHT_THRESHOLD_MIN && bmi <= OVERWEIGHT_THRESHOLD_MAX) {
            setBmiCategoryUI("Over Weight", R.color.overweight, R.drawable.bmi3, 2);
        } else if (bmi >= OBESE_THRESHOLD_MIN && bmi <= OBESE_THRESHOLD_MAX) {
            setBmiCategoryUI("Obese", R.color.obese, R.drawable.bmi4, 3);
        } else if (bmi > MORBIDLY_OBESE_THRESHOLD) {
            setBmiCategoryUI("Morbidly Obese", R.color.morbidly_obese, R.drawable.bmi5, 4);
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void setBmiCategoryUI(String category, int backgroundColor, int imageResource, int categoryIndex) {
        txtBmiMessage.setText(category);
        txtBmiMessage.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(backgroundColor));
        imgBmiGraph.setImageResource(imageResource);
        txtBmiMessage.setTextColor(getResources().getColor(R.color.white));
        txtBmiFooterTitle.setText(bmiTitles[categoryIndex]);
        txtBmiFooterSubTitle.setText(bmiSubTitles[categoryIndex]);
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("##.#");
        return Double.parseDouble(twoDForm.format(d));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(BMI.this);
    }

}
