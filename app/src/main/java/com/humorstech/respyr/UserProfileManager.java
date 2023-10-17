package com.humorstech.respyr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.humorstech.respyr.utills.ActiveProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserProfileManager {

    private Activity activity;
    private Context context;
    private  UserUpdateCallback callback;

    public UserProfileManager(Activity activity, UserUpdateCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.callback = callback;
    }

    public  interface UserUpdateCallback {
        void onUserProfileDataFetchStarted();
        void onUserProfileDataFetchSuccess();
        void onUserProfileDataFetchFailure();

        void onUserProfileDataFetchCompleted();

        void handleProfileException(String error);
    }

    public void fetchUpdateUserData(String loginId, String profileId) {
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.fetchProfileDataByProfileId, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // Called when the request starts
                callback.onUserProfileDataFetchStarted();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Called when the request is successful
                String response = new String(responseBody);

                // Handle different scenarios based on the response
                if (statusCode == 200) {
                    // HTTP status code 200 indicates success
                    callback.onUserProfileDataFetchSuccess();
                    saveData(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Called when the request fails
                String response = error.getMessage();
                // Handle other failure scenarios as needed
                if (statusCode == 404) {
                    // HTTP status code 404 indicates resource not found
                    callback.handleProfileException("404");
                } else {
                    // Handle other failure status codes as needed
                    callback.handleProfileException("Please ensure that your internet connection is active and working properly.");
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

            SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
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
            callback.handleProfileException(e.getMessage());
            e.printStackTrace();
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

            SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
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
            callback.handleProfileException(e.getMessage());
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

            SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
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
            callback.handleProfileException(e.getMessage());
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


            SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ActiveProfile.BLOOD_TEST, String.valueOf(bloodReportAgeStr));
            editor.putString(ActiveProfile.DIABETIC, String.valueOf(diabeticStr));
            editor.putString(ActiveProfile.DIABETIC_VALUES, String.valueOf(diabeticValueStr));
            editor.putString(ActiveProfile.MEDICAL_CONDITION, String.valueOf(medicalConditionStr));
            editor.putString(ActiveProfile.MEDICAL_DATA_LAST_UPDATED, String.valueOf(lastUpDateStr));

            editor.apply();
            callback.onUserProfileDataFetchCompleted();

        } catch (JSONException e) {
            callback.handleProfileException(e.getMessage());
            e.printStackTrace();

        }
    }
}
