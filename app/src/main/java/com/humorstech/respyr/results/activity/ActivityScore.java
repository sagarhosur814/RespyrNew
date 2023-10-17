package com.humorstech.respyr.results.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.hadiidbouk.charts.ChartProgressBar;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.reading.LifeStyleAnalysis;
import com.humorstech.respyr.reading.Stuffs;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ActivityScore extends AppCompatActivity {



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        StatusBarColor statusBarColor= new StatusBarColor(ActivityScore.this);
        statusBarColor.setColor(getResources().getColor(R.color.light_secondary));

        init();
    }

    private void init(){
        initVars();

         onClicks();

        SharedPreferences sharedPreferences = getSharedPreferences(ActiveResultData.TITLE, Context.MODE_PRIVATE);
        String profileId = sharedPreferences.getString(ActiveResultData.PROFILE_ID, null);
        String loginId = sharedPreferences.getString(ActiveResultData.LOGIN_ID, null);
        String profileName = sharedPreferences.getString(ActiveResultData.PROFILE_NAME, null);
        String lifestyleScoreStr = sharedPreferences.getString(ActiveResultData.ACTIVITY_SCORE, null);



        double activityScore = Double.parseDouble(lifestyleScoreStr);
        performLifeStyleScore(profileName, activityScore);

        fetchJson(loginId, profileId);




    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    private void initVars(){

    }



    private void onClicks(){
        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());


        Button buttonViewTrend=findViewById(R.id.button_view_trend);
        buttonViewTrend.setOnClickListener(v -> {
           // viewTrend();
        });

    }



    private void performLifeStyleScore(String profileName, double lifeStyleScore){
        TextView txtLifeStyleProfileName = findViewById(R.id.txt_life_style_score_profile_name);
        TextView txtLifeStyleScore = findViewById(R.id.txt_lifestyle_score);
        TextView txtLifeStyleMessage = findViewById(R.id.txt_lifestyle_message);
        TextView txtLifeStyleSubMessage = findViewById(R.id.txt_lifestyle_sub_message);
        ImageView imgLifeStyleStatus = findViewById(R.id.img_lifestyle_warning);
        RoundedProgressBar lifestyleProgress =findViewById(R.id.progress_overall_health_score);



        LifeStyleAnalysis.setLifeStyle(getApplicationContext(),
                lifeStyleScore,
                profileName,
                lifestyleProgress,
                txtLifeStyleScore,
                txtLifeStyleProfileName,
                txtLifeStyleSubMessage,
                txtLifeStyleMessage,
                imgLifeStyleStatus );


        Button buttonViewTrend=findViewById(R.id.button_view_trend);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(v -> {
           // showBottomSheet("activityscore", lifeStyleScore);
        });
    }

















    private void handleException(Exception e) {
        e.printStackTrace();
        showErrorDialog("An error occurred: " + e.getMessage());
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

    }

    private void performExerciseChart(JSONArray jsonData){


        try {
            // Parse the JSON array
            JSONArray jsonArray = new JSONArray(jsonData);

            // Iterate through the array and extract values
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String day = object.getString("day");
                JSONObject data = object.getJSONObject("data");
                String time = data.getString("time");
                int exerciseTime = data.getInt("exercise_time");

                // Print extracted values
                System.out.println("Day: " + day);
                System.out.println("Time: " + time);
                System.out.println("Exercise Time: " + exerciseTime);
                System.out.println("-------------");
            }

            BarChart exerciseChart =findViewById(R.id.exerciseChart);
            BarChart waterIntakeChart =findViewById(R.id.waterIntakeChart);
            ExerciseChart.drawChart(ActivityScore.this, exerciseChart,jsonArray);
            WaterIntakeChart.drawChart(ActivityScore.this, waterIntakeChart,jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




    private void fetchJson(String loginId, String profileId){
        AsyncHttpClient client = new AsyncHttpClient();
        String apiUrl = "https://humorstech.com/humors_app/app_final/trends/daily_routine_trend_weekly2.php?" +
                "login_id=RESPYR003&profile_id=profile1&start_date=10/08/2023&end_date=10/14/2023";
        RequestParams params = new RequestParams();
        params.put("login_id",  loginId);
        params.put("profile_id", profileId);
        params.put("profile_id", "profile1");
        params.put("profile_id", "profile1");
        // Perform the GET request
        client.get(apiUrl, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // Successfully got a response
                String response = new String(responseBody);

                Toast.makeText(ActivityScore.this, response, Toast.LENGTH_SHORT).show();
                decodeJson(response);
                // Handle the response here
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // Request failed
                String errorMessage = new String(responseBody);
                // Handle the error here
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void  decodeJson(String jsonData){

        ArrayList<com.hadiidbouk.charts.BarData> dataList = new ArrayList<>();
        com.hadiidbouk.charts.BarData data = new com.hadiidbouk.charts.BarData("Sun", 3.4f, "3.4€");


        try {
            // Parse the JSON array
            JSONArray jsonArray = new JSONArray(jsonData);

            // Loop through the array and parse each object
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract data from the object
                String day = jsonObject.getString("day");
                JSONObject data1 = jsonObject.getJSONObject("data");
                String time = data1.getString("time");
                String dayName = data1.optString("dayname", ""); // Handle optional field
                int sleepHours = data1.optInt("sleep_hours", -1); // Handle optional field with default value
                int exerciseTime = data1.optInt("exercise_time", -1); // Handle optional field with default value
                int waterIntake = data1.optInt("water_intake", -1); // Handle optional field with default value
                int smokeUnits = data1.optInt("smoke_units", -1); // Handle optional field with default value
                int alcoholUnits = data1.optInt("alcohol_units", -1); // Handle optional field with default value


                // Print the extracted data using Log
                Log.d("JSONData", "Day: " + day);
                Log.d("JSONData", "Time: " + time);
                Log.d("JSONData", "Day Name: " + dayName);
                Log.d("JSONData", "Sleep Hours: " + sleepHours);
                Log.d("JSONData", "Exercise Time: " + exerciseTime);
                Log.d("JSONData", "Water Intake: " + waterIntake);
                Log.d("JSONData", "Smoke Units: " + smokeUnits);
                Log.d("JSONData", "Alcohol Units: " + alcoholUnits);

                data = new com.hadiidbouk.charts.BarData(Stuffs.dateToDayName(day), (float)sleepHours, "");
                dataList.add(data);

                ChartProgressBar mChart = (ChartProgressBar) findViewById(R.id.ChartProgressBar);
                SleepChart.drawChart(mChart,dataList);


                performExerciseChart(jsonArray);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }



    }




}

