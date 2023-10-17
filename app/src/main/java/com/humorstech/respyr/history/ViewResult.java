package com.humorstech.respyr.history;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.results.FinalResult;
import com.humorstech.respyr.utills.ActiveProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.techiness.progressdialoglibrary.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ViewResult extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private LinearLayout mainLayout;
    private String profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        StatusBarColor statusBarColor= new StatusBarColor(ViewResult.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        profileName = sharedPreferences.getString(ActiveProfile.NAME, null);
        getIntentData();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        if (intent!=null){
           int id =  intent.getIntExtra("id", 0);
           if(id>0){
               if(profileName==null){
                   profileName = "undefined";
               }
               fetchTimeLineData(String.valueOf(id));
           }else{
               handleException("Something went wrong!!");
           }
        }
    }

    private void init(){
        initVars();
        onClick();
    }

    private void initVars(){
        mainLayout=findViewById(R.id.main_layout);
        mainLayout.setVisibility(View.GONE);
    }
    private void onClick(){
        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setToolBar(String dateAndTime){
        TextView txtResultTime =findViewById(R.id.txt_current_time);
        TextView txtResultDate =findViewById(R.id.txt_current_date);

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        SimpleDateFormat outputDateFormat1 = new SimpleDateFormat("hh:mm a", Locale.US);

        try {
            // Parse the input date string
            Date date = inputDateFormat.parse(dateAndTime);

            // Format the date in the desired output format
            String formattedDate = outputDateFormat.format(date);
            String formattedDate1 = outputDateFormat1.format(date);
            txtResultDate.setText(formattedDate);
            txtResultTime.setText(formattedDate1);

        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing error
        }



    }


    public void fetchTimeLineData(String id) {
        try {
            RequestParams params = new RequestParams();
            params.put("id", id);


            AsyncHttpClient client = new AsyncHttpClient();
            client.get(HTTP_URLS.FETCH_TIMELINE_DATA_BY_ID, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {

                    showLoading(ViewResult.this);
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                    dismissLoadingProgress();

                    String response = new String(responseBody).trim();

                    System.out.println("Response"+response);
                    if (response.equals("No data found for the specified ID.")){
                        handleException(response);
                    }else if(response.equals("ID not provided in the GET request.")){
                        handleException(response);
                    }else{
                        decodeJson(response);
                    }


                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissLoadingProgress();

                    // Check the status code to determine the specific error
                    if (statusCode == 0) {
                        // No internet connection
                        handleException("No internet connection. Please check your network.");
                    } else if (statusCode == 404) {
                        // Resource not found
                        handleException("Resource not found. Please check the URL.");
                    } else if (statusCode == 500) {
                        // Internal server error
                        handleException("Internal server error. Please try again later.");
                    } else {
                        // Other HTTP request failure
                        handleException("HTTP request failed with status code: " + statusCode);
                    }

                    // Log the error for debugging
                    if (error != null) {
                        error.printStackTrace();
                        handleException(error.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            // Handle other exceptions if needed
            e.printStackTrace();
            handleException(e.getMessage());
        }
    }


    private void decodeJson(String jsonData) {
        try {
            mainLayout.setVisibility(View.VISIBLE);
            // Parse the JSON data as an array
            JSONArray jsonArray = new JSONArray(jsonData);

            if (jsonArray.length() > 0) {
                // Assuming you want to process the first object in the array
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                // Extract values from the JSON object
                String dateAndTime = jsonObject.optString("dttm");
                setToolBar(dateAndTime);

                double overallHealthScore = parseDoubleFromJson(jsonObject, "healthScore");
                double respiratoryScore = parseDoubleFromJson(jsonObject, "blow_score");
                double diabeticScore = parseDoubleFromJson(jsonObject, "diabeticScore");
                double vitalScore = parseDoubleFromJson(jsonObject, "vitalScore");
                double activityScore = parseDoubleFromJson(jsonObject, "OverallLifeStyleScore");
                double nutritionScore = parseDoubleFromJson(jsonObject, "OverallNutrientScore");
                double liverScore = parseDoubleFromJson(jsonObject, "liver_main_score");

                perform3Scores(respiratoryScore, vitalScore,diabeticScore,liverScore);
                performScores(activityScore, nutritionScore);
                performHealthScore(profileName, overallHealthScore);

            } else {
                // Handle the case where the array is empty
                mainLayout.setVisibility(View.GONE);
                handleException("Empty JSON array.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing exception
            mainLayout.setVisibility(View.GONE);
            handleException(e.getMessage());
        }
    }

// Rest of the code, including the parseDoubleFromJson method, remains the same

    private double parseDoubleFromJson(JSONObject jsonObject, String key) {
        double parsedValue = 0.0; // Default value if parsing fails
        try {
            String valueStr = jsonObject.optString(key);
            parsedValue = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle the error or log it as needed
        }
        return parsedValue;
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





    private  void showLoading(Activity activity ){
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
    }


    private  void dismissLoadingProgress(){
        progressDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(ViewResult.this);
    }

    private void handleException(String s) {
        mainLayout.setVisibility(View.GONE);
        showErrorDialog("An error occurred: " + s);
    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);

        // Set a positive button with a click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

              onBackPressed();
            }
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

}