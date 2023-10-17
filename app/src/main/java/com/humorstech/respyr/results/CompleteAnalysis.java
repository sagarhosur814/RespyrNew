package com.humorstech.respyr.results;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.SmoothScrollSnapHelper;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.reading.BlowScoreAnalysis;
import com.humorstech.respyr.reading.DiabeticAnalysis;
import com.humorstech.respyr.reading.LifeStyleAnalysis;
import com.humorstech.respyr.reading.VitalAnalysis;
import com.humorstech.respyr.results.activity.ActivityScore;
import com.humorstech.respyr.share.Share2;
import com.humorstech.respyr.suggestion.BreakFastSuggestionDataModel;
import com.humorstech.respyr.suggestion.FoodSuggestionAdapterBasedOnTime;
import com.humorstech.respyr.suggestion.Suggestion;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class CompleteAnalysis extends AppCompatActivity {

    private String resultDate, resultTime;

    final int targetSectionId0 = R.id.main;
    final int targetSectionId1 = R.id.card_diabetic;
    final int targetSectionId2 = R.id.card_blow;
    final int targetSectionId3 = R.id.card_vital;
    final int targetSectionId4 = R.id.card_life_style;
    final int targetSectionId5 = R.id.card_nutrition;

    private NestedScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_analysis);

        StatusBarColor statusBarColor= new StatusBarColor(CompleteAnalysis.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();
    }

    private void init(){
        initVars();
        performScores();
        onClicks();
    }

    private void initVars(){
        scrollView=findViewById(R.id.scroll_view);
    }

    private void onClicks(){
        ImageButton buttonBack =findViewById(R.id.button_back);
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
        Animatoo1.animateSlideRight(CompleteAnalysis.this);
    }

    private void performScores(){
        // set in result page

        SharedPreferences sharedPreferences = getSharedPreferences(ActiveResultData.TITLE, Context.MODE_PRIVATE);
        //////////////////////// Main Scores
        String profileName = sharedPreferences.getString(ActiveResultData.PROFILE_NAME, null);
        String finalOverallHealthScoreStr = sharedPreferences.getString(ActiveResultData.OVERALL_HEALTH_SCORE, null);
        String finalDiabeticScoreStr = sharedPreferences.getString(ActiveResultData.DIABETIC_SCORE, null);
        String finalVitalScoreStr = sharedPreferences.getString(ActiveResultData.VITAL_SCORE, null);
        String finalRespiratoryScoreStr = sharedPreferences.getString(ActiveResultData.RESPIRATORY_SCORE, null);
        String finalLiverScoreStr = sharedPreferences.getString(ActiveResultData.LIVER_SCORE, null);
        String finalActivityScoreStr = sharedPreferences.getString(ActiveResultData.ACTIVITY_SCORE, null);
        String finalNutritionScoreStr = sharedPreferences.getString(ActiveResultData.NUTRITION_SCORE, null);
        resultDate = sharedPreferences.getString(ActiveResultData.DATE, null);
        resultTime = sharedPreferences.getString(ActiveResultData.TIME, null);


        double finalOverallHealthScore = Double.parseDouble(finalOverallHealthScoreStr);
        double finalDiabeticScore = Double.parseDouble(finalDiabeticScoreStr);
        double finalVitalScore = Double.parseDouble(finalVitalScoreStr);
        double finalRespiratoryScore = Double.parseDouble(finalRespiratoryScoreStr);
        double finalLiverScore = Double.parseDouble(finalLiverScoreStr);
        double finalActivityScore = Double.parseDouble(finalActivityScoreStr);
        double finalNutritionScore = Double.parseDouble(finalNutritionScoreStr);



        performHealthScore(profileName, finalOverallHealthScore);
        performDiabeticScore(profileName, finalDiabeticScore);
        performVitalScore(profileName, finalVitalScore);
        performBlowScore(profileName, finalRespiratoryScore);
        performLiverScore(profileName, finalLiverScore);
        performLifeStyleScore(profileName, finalActivityScore);
        performNutritionScore(profileName, finalNutritionScore);
        performFoodSuggestionBasedOnTime();
    }

    private void performHealthScore(String profileName, double healthScore){

        TextView txtHealthScoreProfileName = findViewById(R.id.txt_profile_name);
        TextView txtHealthScore = findViewById(R.id.txt_overall_health_score);
        RoundedProgressBar progressHealthScore =findViewById(R.id.progress_overall_health_score);

        ImageButton imgDiabeticShare = findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=findViewById(R.id.button_health_score_view_trend);


        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("overall_health_score", healthScore);

            }
        });





        double roundedValue = Math.round(healthScore);

        if (healthScore - Math.floor(healthScore) >= 0.5) {
            roundedValue = Math.ceil(healthScore);
        }


        // set progress bar percentage
        int scoreNew = (int) roundedValue;
        assert progressHealthScore != null;
        progressHealthScore.setProgressPercentage(scoreNew, true);


        // set profile name
        assert txtHealthScore != null;
        assert txtHealthScoreProfileName != null;
        txtHealthScore.setText(String.valueOf(scoreNew) + "%");
        txtHealthScoreProfileName.setText(profileName +"," +"\nYour Overall Health Score is");




        /// set color according to health score ranges
        if (healthScore <= 100 && healthScore >= 80) {
            progressHealthScore.setProgressDrawableColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
            txtHealthScore.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
        } else if (healthScore >= 71  && healthScore < 80) {
            progressHealthScore.setProgressDrawableColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));
            txtHealthScore.setTextColor(getApplicationContext().getResources().getColor(R.color.yellow));

        } else if (healthScore >= 0 && healthScore <= 70) {
            progressHealthScore.setProgressDrawableColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            txtHealthScore.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
        }

    }

    private void performDiabeticScore(String profileName, double diabeticScore){

        TextView txtDiabeticProfileName = findViewById(R.id.txt_diabetic_score_profile_name);
        TextView txtDiabeticScore = findViewById(R.id.txt_diabetic_score);
        TextView txtDiabeticMessage = findViewById(R.id.txt_diabetic_message);
        TextView txtDiabeticSubMessage = findViewById(R.id.txt_diabetic_sub_message);
        ImageView txtImgDiabeticStatus = findViewById(R.id.img_diabetic_warning);

        ImageButton imgDiabeticShare = findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=findViewById(R.id.button_diabetic_track_life_style);




        RoundedProgressBar diabeticProgress =findViewById(R.id.progress_diabetic_score);


        DiabeticAnalysis.setDiabetic(getApplicationContext(),
                diabeticScore, // diabetic score
                profileName, // profile name
                diabeticProgress,
                txtDiabeticScore,
                txtDiabeticProfileName,
                txtDiabeticSubMessage,
                txtDiabeticMessage,
                txtImgDiabeticStatus );

        Button buttonViewTrend=findViewById(R.id.button_view_trend_diabetic);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_diabetic_score", diabeticScore);
            }
        });

        imgDiabeticShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Share2.class);
                intent.putExtra("score_name", "Diabetic Score");
                intent.putExtra("score_value", txtDiabeticScore.getText().toString().replace("%", ""));
                intent.putExtra("score_status",txtDiabeticMessage.getText().toString());
                intent.putExtra("score_message",txtDiabeticSubMessage.getText().toString());
                intent.putExtra("date",resultDate);
                intent.putExtra("time",resultTime);
                startActivity(intent);
                Animatoo1.animateSlideLeft(CompleteAnalysis.this);
            }
        });

        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout specificView = findViewById(R.id.card_life_style); // Replace with the ID of the LinearLayout you want to scroll to
                specificView.requestFocus();
                scrollView.requestChildFocus(specificView, specificView);
            }
        });


    }
    private void performVitalScore(String profileName, double vitalScore){
        TextView txtVitalProfileName = findViewById(R.id.txt_vital_score_profile_name);
        TextView txtVitalScore = findViewById(R.id.txt_vital_score);
        TextView txtVitalMessage = findViewById(R.id.txt_vital_message);
        TextView txtVitalSubMessage = findViewById(R.id.txt_vital_sub_message);
        ImageView imgVitalStatus = findViewById(R.id.img_vital_warning);

        RoundedProgressBar vitalProgress =findViewById(R.id.progress_vital_score);


        ImageButton imgButtonSahreVital = findViewById(R.id.button_share_vital);
        Button buttonTrackLifeStyle=findViewById(R.id.button_vital_scroll);

        VitalAnalysis.setVital(
                getApplicationContext(),
                vitalScore,
                profileName,
                vitalProgress,
                txtVitalScore,
                txtVitalProfileName,
                txtVitalSubMessage,
                txtVitalMessage,
                imgVitalStatus );


        Button buttonViewTrend=findViewById(R.id.button_view_trend_vital_score);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_vital_score", vitalScore);
            }
        });


        imgButtonSahreVital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Share2.class);
                intent.putExtra("score_name", "Vital Score");
                intent.putExtra("score_value", txtVitalScore.getText().toString().replace("%", ""));
                intent.putExtra("score_status",txtVitalMessage.getText().toString());
                intent.putExtra("score_message",txtVitalSubMessage.getText().toString());
                intent.putExtra("date",resultDate);
                intent.putExtra("time",resultTime);
                startActivity(intent);
                Animatoo1.animateSlideLeft(CompleteAnalysis.this);
            }
        });


        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout specificView = findViewById(R.id.card_life_style); // Replace with the ID of the LinearLayout you want to scroll to
                specificView.requestFocus();
                scrollView.requestChildFocus(specificView, specificView);
            }
        });

    }
    private void performBlowScore(String profileName, double blowScore){
        TextView txtBlowProfileName = findViewById(R.id.txt_blow_score_profile_name);
        TextView txtBlowScore = findViewById(R.id.txt_blow_score);
        TextView txtBlowScoreMessage = findViewById(R.id.txt_blow_message);
        TextView txtBlowScoreSubMessage = findViewById(R.id.txt_blow_sub_message);
        ImageView imgBlowStatus = findViewById(R.id.img_blow_warning);
        RoundedProgressBar blowProgress =findViewById(R.id.progress_blow_score);

        ImageButton imgShareBlow = findViewById(R.id.button_share_blow);
        Button buttonTrackLifeStyle=findViewById(R.id.button_blow_scroll);


        BlowScoreAnalysis.setBlow(getApplicationContext(),
                blowScore,
                profileName,
                blowProgress,
                txtBlowScore,
                txtBlowProfileName,
                txtBlowScoreSubMessage,
                txtBlowScoreMessage,
                imgBlowStatus );



        Button buttonViewTrend=findViewById(R.id.button_view_trend_blow);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_respiratory_score", blowScore);
            }
        });

        imgShareBlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Share2.class);
                intent.putExtra("score_name", "Respiratory Score");
                intent.putExtra("score_value", txtBlowScore.getText().toString().replace("%", ""));
                intent.putExtra("score_status",txtBlowScoreMessage.getText().toString());
                intent.putExtra("score_message",txtBlowScoreSubMessage.getText().toString());
                intent.putExtra("date",resultDate);
                intent.putExtra("time",resultTime);
                startActivity(intent);
                Animatoo1.animateSlideLeft(CompleteAnalysis.this);
            }
        });


        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout specificView = findViewById(R.id.card_life_style); // Replace with the ID of the LinearLayout you want to scroll to
                specificView.requestFocus();
                scrollView.requestChildFocus(specificView, specificView);
            }
        });


    }

    private void performLiverScore(String profileName, double liverScore){
        TextView txtLiverProfileName = findViewById(R.id.txt_liver_score_profile_name);
        TextView txtLiverScore = findViewById(R.id.txt_liver_score);
        TextView txtLiverScoreMessage = findViewById(R.id.txt_liver_message);
        TextView txtLiverScoreSubMessage = findViewById(R.id.txt_liver_sub_message);
        ImageView imgLiverStatus = findViewById(R.id.img_liver_warning);
        RoundedProgressBar blowProgress =findViewById(R.id.progress_liver_score);

        ImageButton imgShareBlow = findViewById(R.id.button_share_liver);
        Button buttonTrackLifeStyle=findViewById(R.id.button_liver_scroll);


        BlowScoreAnalysis.setBlow(getApplicationContext(),
                liverScore,
                profileName,
                blowProgress,
                txtLiverScore,
                txtLiverProfileName,
                txtLiverScoreSubMessage,
                txtLiverScoreMessage,
                imgLiverStatus );



        Button buttonViewTrend=findViewById(R.id.button_view_trend_liver);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_liver_score", liverScore);
            }
        });

        imgShareBlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Share2.class);
                intent.putExtra("score_name", "Liver Score");
                intent.putExtra("score_value", txtLiverScore.getText().toString().replace("%", ""));
                intent.putExtra("score_status",txtLiverScoreMessage.getText().toString());
                intent.putExtra("score_message",txtLiverScoreSubMessage.getText().toString());
                intent.putExtra("date",resultDate);
                intent.putExtra("time",resultTime);
                startActivity(intent);
                Animatoo1.animateSlideLeft(CompleteAnalysis.this);
            }
        });


        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout specificView = findViewById(R.id.card_life_style); // Replace with the ID of the LinearLayout you want to scroll to
                specificView.requestFocus();
                scrollView.requestChildFocus(specificView, specificView);
            }
        });


    }
    private void performLifeStyleScore(String profileName, double lifeStyleScore){
        TextView txtLifeStyleProfileName =findViewById(R.id.txt_life_style_score_profile_name);
        TextView txtLifeStyleScore = findViewById(R.id.txt_lifestyle_score);
        TextView txtLifeStyleMessage = findViewById(R.id.txt_lifestyle_message);
        TextView txtLifeStyleSubMessage = findViewById(R.id.txt_lifestyle_sub_message);
        ImageView imgLifeStyleStatus = findViewById(R.id.img_lifestyle_warning);
        RoundedProgressBar blowProgress =findViewById(R.id.progress_lifestyle_score);

        ImageButton imgShareActivity = findViewById(R.id.txt_share_actviity);
        Button buttonTrackLifeStyle=findViewById(R.id.button_tract_activity_score);




        LifeStyleAnalysis.setLifeStyle(getApplicationContext(),
                lifeStyleScore,
                profileName,
                blowProgress,
                txtLifeStyleScore,
                txtLifeStyleProfileName,
                txtLifeStyleSubMessage,
                txtLifeStyleMessage,
                imgLifeStyleStatus );


        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityScore.class);
                startActivity(intent);
            }
        });

        Button buttonViewTrend=findViewById(R.id.button_view_trend_lifestyle);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_activity_score", lifeStyleScore);
            }
        });


        imgShareActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Share2.class);
                intent.putExtra("score_name", "Activity Score");
                intent.putExtra("score_value", txtLifeStyleScore.getText().toString().replace("%", ""));
                intent.putExtra("score_status",txtLifeStyleMessage.getText().toString());
                intent.putExtra("score_message",txtLifeStyleSubMessage.getText().toString());
                intent.putExtra("date",resultDate);
                intent.putExtra("time",resultTime);
                startActivity(intent);
                Animatoo1.animateSlideLeft(CompleteAnalysis.this);
            }
        });

    }

    private void performNutritionScore(String profileName, double nutritionScore){
        TextView txtNutritionProfileName = findViewById(R.id.txt_nutrition_score_profile_name);
        TextView txtNutritionScore = findViewById(R.id.txt_nutrition_score);
        TextView txtNutritionMessage = findViewById(R.id.txt_nutrition_message);
        TextView txtNutritionSubMessage = findViewById(R.id.txt_nutrition_sub_message);
        ImageView imgNutritionStatus = findViewById(R.id.img_lifestyle_warning);
        RoundedProgressBar nutritionProgress =findViewById(R.id.progress_nutrition_score);

        ImageButton imgShareNutrition = findViewById(R.id.img_button_share_nutrition);
        Button buttonTrackNutrition=findViewById(R.id.button_track_nutrition);


        LifeStyleAnalysis.setNutrition(getApplicationContext(),
                nutritionScore,
                profileName,
                nutritionProgress,
                txtNutritionScore,
                txtNutritionProfileName,
                txtNutritionSubMessage,
                txtNutritionMessage,
                imgNutritionStatus );


        Button buttonViewTrend=findViewById(R.id.button_view_trend_nutrition);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_nutrition_score", nutritionScore);
            }
        });

        buttonTrackNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NutritionScore.class);
                startActivity(intent);
            }
        });


        imgShareNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Share2.class);
                intent.putExtra("score_name", "Activity Score");
                intent.putExtra("score_value", txtNutritionScore.getText().toString().replace("%", ""));
                intent.putExtra("score_status",txtNutritionMessage.getText().toString());
                intent.putExtra("score_message",txtNutritionSubMessage.getText().toString());
                intent.putExtra("date",resultDate);
                intent.putExtra("time",resultTime);
                startActivity(intent);
                Animatoo1.animateSlideLeft(CompleteAnalysis.this);
            }
        });

    }

    private void setSuggestionList(RecyclerView recyclerView, String data, ScrollingPagerIndicator recyclerIndicator) {
        List<BreakFastSuggestionDataModel> foodItems = new ArrayList<>();
        String[] lines = data.split("\n");

        try {
            for (int i = 1; i < lines.length; i++) {
                String[] parts = lines[i].split(": ");
                if (parts.length != 2) {
                    // Handle data format error
                    continue; // Skip this line and move to the next one
                }

                String name = parts[0];
                String[] values = parts[1].split(", ");
                if (values.length != 5) {
                    // Handle values format error
                    continue; // Skip this line and move to the next one
                }

                double calories = Double.parseDouble(values[0].replaceAll("[^0-9]", ""));
                double carbohydrates = Double.parseDouble(values[1].replaceAll("[^0-9]", ""));
                double protein = Double.parseDouble(values[2].replaceAll("[^0-9]", ""));
                double fat = Double.parseDouble(values[3].replaceAll("[^0-9.]", ""));
                double fiber = Double.parseDouble(values[4].replaceAll("[^0-9.]", ""));

                BreakFastSuggestionDataModel foodItem = new BreakFastSuggestionDataModel(name, calories, carbohydrates, protein, fat, fiber);
                foodItems.add(foodItem);
            }

            FoodSuggestionAdapterBasedOnTime adapter = new FoodSuggestionAdapterBasedOnTime(foodItems, CompleteAnalysis.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);

            // Set dots
            recyclerIndicator.attachToRecyclerView(recyclerView);

            // Attach the custom SnapHelper
            SmoothScrollSnapHelper snapHelper = new SmoothScrollSnapHelper(adapter);
            snapHelper.attachToRecyclerView(recyclerView);

        } catch (NumberFormatException e) {
            // Handle number parsing error
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    private void performFoodSuggestionBasedOnTime(){

        TextView txtMealTime = findViewById(R.id.txt_meal_time);
        TextView txtTime1 = findViewById(R.id.txt_time_1);
        TextView txtTime2 = findViewById(R.id.txt_time_2);
        TextView txtTime3 = findViewById(R.id.txt_time_3);

        RecyclerView recyclerView =findViewById(R.id.food_suggestion_list);
        ScrollingPagerIndicator indicator =findViewById(R.id.indicator);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
        String breakfastSuggestions = preferences.getString(ActiveResultData.BREAKFAST_SUGGESTION, null);
        String lunchSuggestions = preferences.getString(ActiveResultData.LUNCH_SUGGESTION, null);
        String dinnerSuggestions = preferences.getString(ActiveResultData.DINNER_SUGGESTION, null);



        assert txtTime1 != null;
        txtTime1.setText(TimeBasedMeal.getFormattedTime());
        assert txtTime2 != null;
        txtTime2.setText(TimeBasedMeal.getFormattedTime());
        assert txtTime3 != null;
        txtTime3.setText(TimeBasedMeal.getFormattedTime());


        switch (TimeBasedMeal.getMealTime()){
            case  "breakfast" : txtMealTime.setText("It's breakfast time");
                setSuggestionList(recyclerView , breakfastSuggestions, indicator);
                break;
            case  "lunch" : txtMealTime.setText("It's lunch time");
                setSuggestionList(recyclerView , lunchSuggestions, indicator);
                break;
            case  "snack" : txtMealTime.setText("It's snack time");
                setSuggestionList(recyclerView , breakfastSuggestions, indicator);
                break;
            case  "dinner" : txtMealTime.setText("It's dinner time");
                setSuggestionList(recyclerView , dinnerSuggestions, indicator);break;

        }


        Button buttonViewSuggestion =findViewById(R.id.button_view_all_suggestion);
        assert buttonViewSuggestion != null;
        buttonViewSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(getApplicationContext(), Suggestion.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }





    public void showBottomSheet(String dataKey, double currentScore) {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(CompleteAnalysis.this,dataKey,currentScore);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }



}