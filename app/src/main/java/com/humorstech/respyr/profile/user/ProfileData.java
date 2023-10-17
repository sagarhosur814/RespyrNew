package com.humorstech.respyr.profile.user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.profile.Profile;
import com.humorstech.respyr.profile.history.BloodReportHistory;
import com.humorstech.respyr.profile.profile_edit.HobbiesEdit;
import com.humorstech.respyr.profile.profile_edit.MedicalHistoryEdit;
import com.humorstech.respyr.profile.profile_edit.MentalStateEdit;
import com.humorstech.respyr.profile.profile_edit.PersonalInfoEdit;
import com.humorstech.respyr.profile.profile_edit.SleepingSheduleEdit;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.LoginUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileData extends AppCompatActivity {

    private static final String TAG = "ProfileData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_data);
        StatusBarColor statusBarColor= new StatusBarColor(  ProfileData.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();
    }
    private void init(){
        menuItemsClick();
        onClicks();


    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences2 = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        String loginId = sharedPreferences2.getString(LoginUtils.LOGIN_ID, null);

        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        String profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, null);


        if (loginId!=null && profileId!=null){
            refreshProfileData(loginId, profileId);
        }

    }

    private void menuItemsClick(){
        LinearLayout llMenu1 =findViewById(R.id.ll_menu_1);
        LinearLayout llMenu2 =findViewById(R.id.ll_menu_2);
        LinearLayout llMenu3 =findViewById(R.id.ll_menu_3);
        LinearLayout llMenu4 =findViewById(R.id.ll_menu_4);
        LinearLayout llMenu5 =findViewById(R.id.ll_menu_5);
        LinearLayout llMenu6 =findViewById(R.id.ll_menu_6);
        LinearLayout llDeleteProfile =findViewById(R.id.ll_delete_profile);

        llMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PersonalInfoEdit.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(ProfileData.this);
            }
        });
        llMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HobbiesEdit.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(ProfileData.this);
            }
        });
        llMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SleepingSheduleEdit.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(ProfileData.this);
            }
        });
        llMenu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MentalStateEdit.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(ProfileData.this);
            }
        });
        llMenu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BloodReportHistory.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(ProfileData.this);
            }
        });
        llMenu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedicalHistoryEdit.class);
                startActivity(intent);
                Animatoo1.animateSlideLeft(ProfileData.this);
            }
        });

        llDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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


    private void refreshProfileData(String loginId, String profileId){
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.fetchProfileDataByProfileId, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                System.out.println("response --->" + response);
                saveData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    // Handle a 404 Not Found error.
                    Toast.makeText(getApplicationContext(), "Server not found. Please check your internet connection.", Toast.LENGTH_LONG).show();
                } else if (statusCode >= 500) {
                    // Handle server errors.
                    Toast.makeText(getApplicationContext(), "Server error. Please try again later.", Toast.LENGTH_LONG).show();
                } else if (error != null) {
                    // Handle other errors with an error response body.
                    String errorMessage = new String(responseBody);
                    Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }  else {
                    // Handle other unknown errors.
                    Toast.makeText(getApplicationContext(), "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void saveData(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONObject dataPersonalInfo = jsonObject.getJSONObject("data_personal_info");
            JSONObject dataHabitsInfo= jsonObject.getJSONObject("data_habits");
            JSONObject dataMedicalInfo = jsonObject.getJSONObject("data_blood_report");

            SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ActiveProfile.DATA_PERSONAL_INFORMATION, String.valueOf(dataPersonalInfo));
            editor.putString(ActiveProfile.DATA_HABITS_INFORMATION, String.valueOf(dataHabitsInfo));
            editor.putString(ActiveProfile.DATA_MEDICAL_INFORMATION, String.valueOf(dataMedicalInfo));
            editor.apply();


            // set profile
            decodePersonalData(String.valueOf(dataPersonalInfo));
            decodeHobbiesData(String.valueOf(dataHabitsInfo));
            decodeMedicalData(String.valueOf(dataMedicalInfo));



        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),String.valueOf(e.getMessage()), Toast.LENGTH_SHORT ).show();

        }
    }

    @SuppressLint("SetTextI18n")
    private void decodePersonalData(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String phone = jsonObject.getString("phone");
            String name = jsonObject.getString("name");
            String email = jsonObject.getString("email");
            String gender = jsonObject.getString("gender");
            String dob = jsonObject.getString("dob");
            String age = jsonObject.getString("age");
            String height = jsonObject.getString("height");
            String weight = jsonObject.getString("weight");

            SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ActiveProfile.PHONE_NUMBER, String.valueOf(phone));
            editor.putString(ActiveProfile.NAME, String.valueOf(name));
            editor.putString(ActiveProfile.EMAIL, String.valueOf(email));
            editor.putString(ActiveProfile.GENDER, String.valueOf(gender));
            editor.putString(ActiveProfile.DOB, String.valueOf(dob));
            editor.putString(ActiveProfile.AGE, String.valueOf(age));
            editor.putString(ActiveProfile.HEIGHT, String.valueOf(height));
            editor.putString(ActiveProfile.WEIGHT, String.valueOf(weight));
            editor.apply();





        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    private void decodeHobbiesData(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String waterConsumptionStr = jsonObject.getString("water_consume");
            String alcoholStr = jsonObject.getString("alcoholic");
            String eatingStr = jsonObject.getString("non_veg");
            String smokingStr = jsonObject.getString("smoking");
            String exerciseStr = jsonObject.getString("exercise");
            String sleepHoursStr = jsonObject.getString("sleep_hours");
            String mentalConditionStr = jsonObject.getString("mental_conditions");
            String lifeStyleScoreStr = jsonObject.getString("life_stytle_score");
            String lastUpdateStr = jsonObject.getString("dttm");

            SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ActiveProfile.WATER_CONSUMPTION, String.valueOf(waterConsumptionStr));
            editor.putString(ActiveProfile.ALCOHOL, String.valueOf(alcoholStr));
            editor.putString(ActiveProfile.SMOKING, String.valueOf(smokingStr));
            editor.putString(ActiveProfile.NOV_VEG, String.valueOf(eatingStr));
            editor.putString(ActiveProfile.EXERCISE, String.valueOf(exerciseStr));
            editor.putString(ActiveProfile.SLEEP_HOURS, String.valueOf(sleepHoursStr));
            editor.putString(ActiveProfile.MENTAL_CONDITIONS, String.valueOf(mentalConditionStr));
            editor.putString(ActiveProfile.LIFESTYLE_SCORE, String.valueOf(lifeStyleScoreStr));
            editor.putString(ActiveProfile.HABITS_LAST_UPDATED, String.valueOf(lastUpdateStr));
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    private void decodeMedicalData(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String bloodReportAgeStr = jsonObject.getString("blood_report_age");
            String diabeticStr = jsonObject.getString("diabetic");
            String diabeticValueStr = jsonObject.getString("diabetic_values");
            String medicalConditionStr = jsonObject.getString("conditions");
            String lastUpDateStr = jsonObject.getString("dttm");


            SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ActiveProfile.BLOOD_TEST, String.valueOf(bloodReportAgeStr));
            editor.putString(ActiveProfile.DIABETIC, String.valueOf(diabeticStr));
            editor.putString(ActiveProfile.DIABETIC_VALUES, String.valueOf(diabeticValueStr));
            editor.putString(ActiveProfile.MEDICAL_CONDITION, String.valueOf(medicalConditionStr));
            editor.putString(ActiveProfile.MEDICAL_DATA_LAST_UPDATED, String.valueOf(lastUpDateStr));

            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}