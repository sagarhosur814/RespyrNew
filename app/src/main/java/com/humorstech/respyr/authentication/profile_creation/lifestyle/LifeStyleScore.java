package com.humorstech.respyr.authentication.profile_creation.lifestyle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.MyVolleyRequest;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.profile_creation.medical_data.BloodTest;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.reading.BeforeReading;
import com.humorstech.respyr.utills.LifeStyleScoreUtils;
import com.humorstech.respyr.utills.LoginUtils;
import com.humorstech.respyr.utills.ProfileCreationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import cz.msebera.android.httpclient.Header;

public class LifeStyleScore extends AppCompatActivity {

    private String loginId, profileId;
    private String userGender, userDOB, userAge, userHeight, userWeight;
    private String waterConsumption, alcohol, smoking, exercise, nonVeg, foodIntake, foodName, breakfast, dinner, lunch, foodQuantity, sleepHours, cigarettesUnits, alcoholUnits, exerciseInMinutes;

    private String foodCategories, foodIds, foodImageLinks;
    private RadioButton star1, star2, star3, star4, star5;
    private Button buttonNext;
    private TextView txtLifeStyleScore, txtLifeStyleMessage, txtLifeStyleSubMessage, txtLog;

    private double OverallLifeStyleScore;

    private static final String TAG = "LifeStyleScore";

    private  ProgressDialog progressDialog;

    private LinearLayout layoutLifeStyleScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_style_score);

        StatusBarColor statusBarColor = new StatusBarColor(LifeStyleScore.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();
    }

    private void init() {
        initVars();
        getData();
        setListeners();
    }

    private void initVars() {

        progressDialog =  new ProgressDialog(LifeStyleScore.this);

        buttonNext = findViewById(R.id.button_next);
        txtLifeStyleScore = findViewById(R.id.txt_life_style_score);
        txtLifeStyleMessage = findViewById(R.id.txt_life_style_message);
        txtLifeStyleSubMessage = findViewById(R.id.txt_life_style_sub_message);

        layoutLifeStyleScore = findViewById(R.id.layout_lifestyle_score);

        txtLog = findViewById(R.id.txt_log);
        txtLog.setMovementMethod(new ScrollingMovementMethod());
    }

    private void getData() {
        SharedPreferences sharedPreferences2 = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        loginId = sharedPreferences2.getString(LoginUtils.LOGIN_ID, "");

        SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
        profileId = sharedPreferences.getString(ProfileCreationData.PROFILE_ID, "0");
        userGender = sharedPreferences.getString(ProfileCreationData.GENDER, "");
        userDOB = sharedPreferences.getString(ProfileCreationData.DOB, "");
        userAge = sharedPreferences.getString(ProfileCreationData.AGE, "");
        userHeight = sharedPreferences.getString(ProfileCreationData.HEIGHT, "");
        userWeight = sharedPreferences.getString(ProfileCreationData.WEIGHT, "");


        txtLog.append("Gender : " + userGender + "\n");
        txtLog.append("Date of Birth : " + userDOB + "\n");
        txtLog.append("Age : " + userAge + "\n");
        txtLog.append("Height : " + userHeight + "\n");
        txtLog.append("Weight : " + userWeight + "\n");

        waterConsumption = sharedPreferences.getString(ProfileCreationData.WATER_CONSUMPTION, "");
        sleepHours = sharedPreferences.getString(ProfileCreationData.SLEEP_HOURS, "");
        alcohol = sharedPreferences.getString(ProfileCreationData.ALCOHOL, "");
        smoking = sharedPreferences.getString(ProfileCreationData.SMOKING, "");
        exercise = sharedPreferences.getString(ProfileCreationData.EXERCISE, "");
        nonVeg = sharedPreferences.getString(ProfileCreationData.NOV_VEG, "");
        foodIntake = sharedPreferences.getString(ProfileCreationData.FOOD_INTAKE, "");
        foodName = sharedPreferences.getString(ProfileCreationData.FOOD_NAME, "");
        foodQuantity = sharedPreferences.getString(ProfileCreationData.FOOD_QUANTITY, "");
        breakfast = sharedPreferences.getString(ProfileCreationData.BREAKFAST, "");
        lunch = sharedPreferences.getString(ProfileCreationData.LUNCH, "");
        dinner = sharedPreferences.getString(ProfileCreationData.DINNER, "");
        cigarettesUnits = sharedPreferences.getString(ProfileCreationData.CIGARETTES_UNIT, "0");
        alcoholUnits = sharedPreferences.getString(ProfileCreationData.ALCOHOL_UNIT, "0");
        exerciseInMinutes = sharedPreferences.getString(ProfileCreationData.EXERCISE_IN_MINUTES, "");

        foodCategories = sharedPreferences.getString(ProfileCreationData.FOOD_CATEGORY, "");
        foodIds = sharedPreferences.getString(ProfileCreationData.FOOD_IDS, "");
        foodImageLinks = sharedPreferences.getString(ProfileCreationData.FOOD_IMAGES_LINKS, "");

        // Append the values to the TextView
        txtLog.append("Water Consumption: " + waterConsumption + "\n");
        txtLog.append("Alcohol: " + alcohol + "\n");
        txtLog.append("Smoking: " + smoking + "\n");
        txtLog.append("Exercise: " + exercise + "\n");
        txtLog.append("Non-Veg: " + nonVeg + "\n");
        txtLog.append("Food Intake: " + foodIntake + "\n");
        txtLog.append("Food Name: " + foodName + "\n");
        txtLog.append("Food Quantity: " + foodQuantity + "\n");
        txtLog.append("Breakfast: " + breakfast + "\n");
        txtLog.append("Lunch: " + lunch + "\n");
        txtLog.append("Dinner: " + dinner + "\n");
        txtLog.append("cigarettesUnits: " + cigarettesUnits + "\n");
        txtLog.append("alcoholUnits: " + alcoholUnits + "\n");
        txtLog.append("exerciseInMinutes: " + exerciseInMinutes + "\n");

        fetchLifeStyleScore();
    }

    private void setListeners() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), BloodTest.class);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    handleException(e.getMessage());
                }
            }
        });
    }

    private void fetchLifeStyleScore() {
        RequestParams params = new RequestParams();
        params.put("activity_level", exercise.toLowerCase());
        params.put("DB_Score", "100");
        params.put("breakfast", skipMeal(breakfast));
        params.put("lunch", skipMeal(lunch));
        params.put("dinner", skipMeal(dinner));
        params.put("VT_Score", "100");
        params.put("height", userHeight);
        params.put("age", userAge);
        params.put("weight", userWeight);
        params.put("gender", userGender);
        params.put("food_intake", foodIntake);
        params.put("food_name", foodName);
        params.put("food_quantity", foodQuantity);
        params.put("exercise_hours", exerciseInMinutes);
        params.put("sleep_hours", sleepHours);
        params.put("water_consumption", waterConsumption);
        params.put("alcohol_consumption", alcoholUnits);
        params.put("smoking_units", cigarettesUnits);
        params.put("day", "mon");
        params.put("type", "veg");
        params.put("ethnol", "0.2");
        params.put("h2", "0.2");
        params.put("blow_score", "0.2");

        txtLog.append("--------------------Params\n");
        txtLog.append(params + "\n");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://humorstech.com/humors/json_curl/life_style.php", params, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showLoading();
                layoutLifeStyleScore.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                dismissLoadingProgress();
                String response3 = new String(response);
                txtLog.append("--------------------Response\n");
                txtLog.append("response : \n");
                txtLog.append(response3 + "\n");
                decodeResultData(response3);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                dismissLoadingProgress();
                handleException("An error occurred while generating the lifestyle score; however, you can continue with profile creation.");
            }
        });
    }

    private void decodeResultData(String jsonData) {
        try {
            layoutLifeStyleScore.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            JsonArray dataArray = jsonObject.getAsJsonArray("data");
            for (int i = 0; i < dataArray.size(); i++) {
                JsonObject item = dataArray.get(i).getAsJsonObject();

                double healthScore = item.get("healthScore").getAsDouble();
                if (healthScore != 0) {
                    OverallLifeStyleScore = item.get("OverallLifeStyleScore").getAsDouble();
                    performLifeStyleScore(OverallLifeStyleScore);
                }
            }
        } catch (JsonParseException e) {
            txtLog.append("--------------------Exception\n");
            txtLog.append(e.getMessage() + "\n");
            handleException("An error occurred while generating the lifestyle score; however, you can continue with profile creation.");
        }
    }

    private void performLifeStyleScore(double OverallLifeStyleScore) {
        double roundedValue = Math.round(OverallLifeStyleScore);
        if (OverallLifeStyleScore - Math.floor(OverallLifeStyleScore) >= 0.5) {
            roundedValue = Math.ceil(OverallLifeStyleScore);
        }
        int lifeStyleScoreNew = (int) roundedValue;
        double rating = Math.round(lifeStyleScoreNew / 20.0f);
        setRating((int) rating);

        txtLifeStyleScore.setText(String.valueOf(lifeStyleScoreNew));

        SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ProfileCreationData.OVERALL_LIFESTYLE_SCORE, String.valueOf(lifeStyleScoreNew));
        editor.apply();

        if (lifeStyleScoreNew <= LifeStyleScoreUtils.unHealthy) {
            txtLifeStyleMessage.setText(LifeStyleScoreUtils.titles[2]);
            txtLifeStyleSubMessage.setText(LifeStyleScoreUtils.subTitles[2]);
        } else if (lifeStyleScoreNew < LifeStyleScoreUtils.moderate) {
            txtLifeStyleMessage.setText(LifeStyleScoreUtils.titles[1]);
            txtLifeStyleSubMessage.setText(LifeStyleScoreUtils.subTitles[1]);
        } else if (lifeStyleScoreNew <= LifeStyleScoreUtils.healthy) {
            txtLifeStyleMessage.setText(LifeStyleScoreUtils.titles[0]);
            txtLifeStyleSubMessage.setText(LifeStyleScoreUtils.subTitles[0]);
        }
    }

    private String skipMeal(String meal) {
        return meal.equals("true") ? "yes" : "no";
    }

    private void setRating(int rating) {
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        switch (rating) {
            case 1:
                star1.setChecked(true);
                star2.setChecked(false);
                star3.setChecked(false);
                star4.setChecked(false);
                star5.setChecked(false);
                break;
            case 2:
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(false);
                star4.setChecked(false);
                star5.setChecked(false);
                break;
            case 3:
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(true);
                star4.setChecked(false);
                star5.setChecked(false);
                break;
            case 4:
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(true);
                star4.setChecked(true);
                star5.setChecked(false);
                break;
            case 5:
                star1.setChecked(true);
                star2.setChecked(true);
                star3.setChecked(true);
                star4.setChecked(true);
                star5.setChecked(true);
                break;
        }
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

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(getApplicationContext(), BloodTest.class);
                    startActivity(intent);
                    Animatoo1.animateSlideLeft(LifeStyleScore.this);
                } catch (ActivityNotFoundException e) {
                    handleException(e.getMessage());
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


    private  void showLoading(  ){
        progressDialog = new ProgressDialog(LifeStyleScore.this);
        progressDialog.show();
    }


    private  void dismissLoadingProgress(){
        progressDialog.dismiss();
    }



}
