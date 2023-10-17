package com.humorstech.respyr.results;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FinalResult extends AppCompatActivity {


    private LinearLayout llMain;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);
        // set status bar color
        StatusBarColor statusBarColor= new StatusBarColor(FinalResult.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        llMain=findViewById(R.id.main_layout);

        init();
        onClicks();


    }


    public void onImageButtonClick(View view) {
        System.out.println("Clicked");
    }

    private void onClicks() {
        ImageButton buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button buttonViewAnalysis =findViewById(R.id.button_view_analysis);
        buttonViewAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompleteAnalysis.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(FinalResult.this);
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){

        try {

            setCurrentDateAndTime();

            SharedPreferences sharedPreferences = getSharedPreferences(ActiveResultData.TITLE, Context.MODE_PRIVATE);
            //////////////////////// Main Scores
            String profileName = sharedPreferences.getString(ActiveResultData.PROFILE_NAME, "Sagar");
            String finalOverallHealthScoreStr = sharedPreferences.getString(ActiveResultData.OVERALL_HEALTH_SCORE, "70");
            String finalDiabeticScoreStr = sharedPreferences.getString(ActiveResultData.DIABETIC_SCORE, "80");
            String finalVitalScoreStr = sharedPreferences.getString(ActiveResultData.VITAL_SCORE, "70");
            String finalRespiratoryScoreStr = sharedPreferences.getString(ActiveResultData.RESPIRATORY_SCORE, "70");
            String finalActivityScoreStr = sharedPreferences.getString(ActiveResultData.ACTIVITY_SCORE, "70");
            String finalNutritionScoreStr = sharedPreferences.getString(ActiveResultData.NUTRITION_SCORE, "70");
            String finalLiverScoreStr = sharedPreferences.getString(ActiveResultData.LIVER_SCORE, "70");


            double finalOverallHealthScore = Double.parseDouble(finalOverallHealthScoreStr);
            double finalDiabeticScore = Double.parseDouble(finalDiabeticScoreStr);
            double finalVitalScore = Double.parseDouble(finalVitalScoreStr);
            double finalRespiratoryScore = Double.parseDouble(finalRespiratoryScoreStr);
            double finalActivityScore = Double.parseDouble(finalActivityScoreStr);
            double finalNutritionScore = Double.parseDouble(finalNutritionScoreStr);
            double finalLiverScore = Double.parseDouble(finalLiverScoreStr);

            perform3Scores(finalRespiratoryScore, finalVitalScore,finalDiabeticScore,finalLiverScore);
            performScores(finalActivityScore,finalNutritionScore);
            performHealthScore(profileName, finalOverallHealthScore);

        }catch (Exception e){
            llMain.setVisibility(View.GONE);
            handleException("An error occurred while fetch the result. Please try again");
        }



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Animatoo1.animateSlideRight(FinalResult.this);
    }

    private void handleException(String s) {
        showErrorDialog("An error occurred: " + s);
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);

        // Set a positive button with a click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform an action when the "OK" button is clicked
                // For example, you can close the dialog or perform another task.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    private void setCurrentDateAndTime(){
        TextView txtCurrentDate = findViewById(R.id.txt_current_date);
        TextView txtCurrentTime= findViewById(R.id.txt_current_time);

        // Get the current date and time
        LocalDateTime currentDateTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();

            // Define the desired date and time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, d MMM yyyy");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("H:mm a");

            // Format the current date and time
            String currentDate = currentDateTime.format(formatter);
            String currentTime = currentDateTime.format(formatter2);
            txtCurrentDate.setText(currentDate);
            txtCurrentTime.setText(currentTime);
        }
    }


    @SuppressLint("SetTextI18n")
    private void performHealthScore(String profileName, double healthScore){

        TextView txtHealthScoreProfileName = findViewById(R.id.txt_profile_name);
        TextView txtHealthScore = findViewById(R.id.txt_overall_health_score);
        RoundedProgressBar progressHealthScore =findViewById(R.id.progress_overall_health_score);



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
        txtHealthScoreProfileName.setText(profileName +"," +"\nYour Overall Health  Score is");




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

    private void perform3Scores(double lungsScore, double vitalScore, double diabeticScore, double liverScore){

        TextView txtLungsScore =findViewById(R.id.txt_lungs_score);
        TextView txtLungsStatus =findViewById(R.id.txt_lungs_status);
        TextView txtLungsMessage=findViewById(R.id.txt_lungs_message);
        RoundedProgressBar progressLungs =findViewById(R.id.progress_lungs);

        TextView txtVitalScore =findViewById(R.id.txt_vital_score);
        TextView txtVitalStatus=findViewById(R.id.txt_vitals_status);
        TextView txtVitalMessage =findViewById(R.id.txt_vitals_message);
        RoundedProgressBar  progressVital=findViewById(R.id.progress_vitals);

        TextView txtDiabeticScore =findViewById(R.id.txt_diabetic_score);
        TextView txtDiabeticStatus =findViewById(R.id.txt_diabetic_status);
        TextView txtDiabeticMessage=findViewById(R.id.txt_diabetic_message);
        RoundedProgressBar  progressDiabetic=findViewById(R.id.progress_diabetic);


        TextView txtLiverScore =findViewById(R.id.txt_liver_score);
        TextView txtLiverScoreStatus =findViewById(R.id.txt_liver_status);
        TextView txtLiverScoreMessage =findViewById(R.id.txt_liver_message);
        RoundedProgressBar  progressLiver =findViewById(R.id.progress_liver);

        setProgress(lungsScore, txtLungsScore, progressLungs, txtLungsStatus,txtLungsMessage);
        setProgress(vitalScore, txtVitalScore, progressVital, txtVitalStatus,txtVitalMessage);
        setProgress(diabeticScore, txtDiabeticScore, progressDiabetic, txtDiabeticStatus,txtDiabeticMessage);
        setProgress(liverScore, txtLiverScore, progressLiver, txtLiverScoreStatus,txtLiverScoreMessage);
    }

    private void setProgress(double score, TextView textScore, RoundedProgressBar roundedProgressBar, TextView status,TextView message ){


        final int unHealthy = 70;
        final int moderate = 80;
        final int healthy = 100;
        final String[] titles = {"Good!","Fair!","Poor!"};
        final String[] subTitles = {"Everything looks good!","Monitor daily for a week!","Come back in a week!"};



        // set progress to text and progress bar
        double roundedValue = Math.round(score);

        if (score - Math.floor(score) >= 0.5) {
            roundedValue = Math.ceil(score);
        }

        textScore.setText(String.valueOf((int)roundedValue)+"%");
        roundedProgressBar.setProgressPercentage(roundedValue, true);



        if(score<=unHealthy){
            status.setText(titles[2]);
            message.setText(subTitles[2]);
            status.setTextColor(getResources().getColor(R.color.red));
            textScore.setTextColor(getResources().getColor(R.color.red));

            roundedProgressBar.setProgressDrawableColor(getResources().getColor(R.color.red));
        }else if (score<moderate){
            status.setText(titles[1]);
            message.setText(subTitles[1]);
            status.setTextColor(getResources().getColor(R.color.yellow));
            textScore.setTextColor(getResources().getColor(R.color.yellow));

            roundedProgressBar.setProgressDrawableColor(getResources().getColor(R.color.yellow));
        }else if(score<=healthy){
            status.setText(titles[0]);
            message.setText(subTitles[0]);
            status.setTextColor(getResources().getColor(R.color.green));
            textScore.setTextColor(getResources().getColor(R.color.green));

            roundedProgressBar.setProgressDrawableColor(getResources().getColor(R.color.green));
        }
    }


    private void performScores(double lifeStyleScore, double nutritionScore){

        TextView txtLifeStyleScore =findViewById(R.id.txt_activity_score);
        TextView txtLifeStyleStatus =findViewById(R.id.txt_activity_status);
        TextView txtLifeStyleMessage=findViewById(R.id.txt_activity_message);
        RoundedProgressBar  progressLifeStyle =findViewById(R.id.progress_activity);

        TextView txtNutritionScore =findViewById(R.id.txt_nutrition_score);
        TextView txtNutritionStatus =findViewById(R.id.txt_nutrition_status);
        TextView txtNutritionMessage=findViewById(R.id.txt_nutrition_message);
        RoundedProgressBar  progressNutrition=findViewById(R.id.progress_nutrition);

        setLifeStyleScores(1 ,lifeStyleScore,txtLifeStyleScore,progressLifeStyle,txtLifeStyleStatus,txtLifeStyleMessage );
        setLifeStyleScores(2 ,nutritionScore,txtNutritionScore,progressNutrition,txtNutritionStatus,txtNutritionMessage );
    }

    private void setLifeStyleScores(int i ,double score,TextView txtScore,RoundedProgressBar roundedProgressBar, TextView txtStatus, TextView txtMessage ){

        final int unHealthy = 70;
        final int moderate = 80;
        final int healthy = 100;
        final String[] titles = {"Good!","Fair!","Poor!"};
        final String[] subTitles = {"You are on fire","Keep pushing","Start moving"};
        final String[] subTitles1 = {"Focus on food","Eat better","Fueling up right"};



        // set progress to text and progress bar
        double roundedValue = Math.round(score);

        if (score - Math.floor(score) >= 0.5) {
            roundedValue = Math.ceil(score);
        }
        txtScore.setText(String.valueOf((int)roundedValue)+"%");
        roundedProgressBar.setProgressPercentage(roundedValue, true);


        if(score<=unHealthy){
            txtStatus.setText(titles[2]);

            if (i==1){
                txtMessage.setText(subTitles[2]);
            }else{
                txtMessage.setText(subTitles1[2]);
            }

            txtStatus.setTextColor(getResources().getColor(R.color.red));
            txtScore.setTextColor(getResources().getColor(R.color.red));

            roundedProgressBar.setProgressDrawableColor(getResources().getColor(R.color.red));
        }else if (score<moderate){
            txtStatus.setText(titles[1]);

            if (i==1){
                txtMessage.setText(subTitles[1]);
            }else{
                txtMessage.setText(subTitles1[1]);
            }


            txtStatus.setTextColor(getResources().getColor(R.color.yellow));
            txtScore.setTextColor(getResources().getColor(R.color.yellow));

            roundedProgressBar.setProgressDrawableColor(getResources().getColor(R.color.yellow));
        }else if(score<=healthy){
            txtStatus.setText(titles[0]);


            if (i==1){
                txtMessage.setText(subTitles[0]);
            }else{
                txtMessage.setText(subTitles1[0]);
            }


            txtStatus.setTextColor(getResources().getColor(R.color.green));
            txtScore.setTextColor(getResources().getColor(R.color.green));

            roundedProgressBar.setProgressDrawableColor(getResources().getColor(R.color.green));
        }
    }


}