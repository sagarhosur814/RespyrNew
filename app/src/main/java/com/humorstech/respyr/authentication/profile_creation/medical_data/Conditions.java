package com.humorstech.respyr.authentication.profile_creation.medical_data;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.MyVolleyRequest;
import com.humorstech.respyr.authentication.profile_creation.lifestyle.LifeStyleScore;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.authentication.profile_creation.ProfileCreationSuccess;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.LoginUtils;
import com.humorstech.respyr.utills.ProfileCreationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;
import cz.msebera.android.httpclient.Header;

public class Conditions extends AppCompatActivity {

    private CheckBox chkNone, chkDia,chkLipid, chkKidney, chkLiver, chkLungs;
    private Button buttonNext;

    private boolean ch1,ch2,ch3,ch4,ch5;
    private final StringBuilder stringBuilder = new StringBuilder();


    private static final String TAG = "Conditions";



    private ProgressDialog progressDialog;

    private String userLoginId,userProfileId, userPhoneNumber, userProfileName, userEmailAddress, userGender, userDateOfBirth,userAge,userHeight,userWeight,UserBMI;

    private String userWaterConsumptionDaily, userAlcoholConsumption, userSmoking, userEating, userExercise, userSleepingHours,userMentalCondition, userOverallLifeStyleScore;
    private String bloodTest,fastingBloodSugar;


    private String foodIntake,foodName,foodQuantity,foodCategories, foodIds, foodImageLinks;
    private String breakfast,lunch,dinner;
    private String cigarettesUnits,alcoholUnits,exerciseInMinutes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condtions);

        StatusBarColor statusBarColor = new StatusBarColor(Conditions.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        init();


    }


    private void init(){
        initVars();
        checkBoxListener();
        onClicks();
    }



    @Override
    protected void onStart() {
        super.onStart();

        ///////////// get login id
        SharedPreferences loginDetails = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        userLoginId = loginDetails.getString(LoginUtils.LOGIN_ID, null);
        userPhoneNumber = loginDetails.getString(LoginUtils.PHONE_NUMBER, null);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        //////////// get profile creation data--------------------------------------------------------------------------
        /// Personal Data
        SharedPreferences profileDetails = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
        userProfileId = profileDetails.getString(ProfileCreationData.PROFILE_ID, null);
        userProfileName = profileDetails.getString(ProfileCreationData.NAME, null);
        userEmailAddress = profileDetails.getString(ProfileCreationData.EMAIL, null);
        userGender = profileDetails.getString(ProfileCreationData.GENDER, null);

        userDateOfBirth = profileDetails.getString(ProfileCreationData.DOB, null);
        userAge = profileDetails.getString(ProfileCreationData.AGE, null);
        userHeight = profileDetails.getString(ProfileCreationData.HEIGHT, null);
        userWeight = profileDetails.getString(ProfileCreationData.WEIGHT, null);
        UserBMI = profileDetails.getString(ProfileCreationData.BMI, null);

//        Daily routine
        foodIntake = profileDetails.getString(ProfileCreationData.FOOD_INTAKE, "");
        foodName = profileDetails.getString(ProfileCreationData.FOOD_NAME, "");
        foodQuantity = profileDetails.getString(ProfileCreationData.FOOD_QUANTITY, "");
        breakfast = profileDetails.getString(ProfileCreationData.BREAKFAST, "");
        lunch = profileDetails.getString(ProfileCreationData.LUNCH, "");
        dinner = profileDetails.getString(ProfileCreationData.DINNER, "");
        cigarettesUnits = profileDetails.getString(ProfileCreationData.CIGARETTES_UNIT, "0");
        alcoholUnits = profileDetails.getString(ProfileCreationData.ALCOHOL_UNIT, "0");
        exerciseInMinutes = profileDetails.getString(ProfileCreationData.EXERCISE_IN_MINUTES, "");

        foodCategories = profileDetails.getString(ProfileCreationData.FOOD_CATEGORY, "");
        foodIds = profileDetails.getString(ProfileCreationData.FOOD_IDS, "");
        foodImageLinks = profileDetails.getString(ProfileCreationData.FOOD_IMAGES_LINKS, "");


        /// Habits Data
        userWaterConsumptionDaily = profileDetails.getString(ProfileCreationData.WATER_CONSUMPTION, null);
        userAlcoholConsumption = profileDetails.getString(ProfileCreationData.ALCOHOL, null);
        userSmoking = profileDetails.getString(ProfileCreationData.SMOKING, null);
        userEating = profileDetails.getString(ProfileCreationData.NOV_VEG, null);
        userExercise = profileDetails.getString(ProfileCreationData.EXERCISE, null);
        userSleepingHours = profileDetails.getString(ProfileCreationData.SLEEP_HOURS, null);
        userMentalCondition = profileDetails.getString(ProfileCreationData.MENTAL_CONDITIONS, null);
        userOverallLifeStyleScore = profileDetails.getString(ProfileCreationData.OVERALL_LIFESTYLE_SCORE, "0");


        // Medical Info
        bloodTest = profileDetails.getString(ProfileCreationData.BLOOD_TEST, null);
        fastingBloodSugar = profileDetails.getString(ProfileCreationData.SUGAR_LEVEL, null);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        // Log login-related data
        Log.d("Login Data", "Login ID: " + userLoginId);
        Log.d("Login Data", "Phone Number: " + userPhoneNumber);

        // Log profile creation data
        Log.d("Profile Data", "Profile ID: " + userProfileId);
        Log.d("Profile Data", "Name: " + userProfileName);
        Log.d("Profile Data", "Email Address: " + userEmailAddress);
        Log.d("Profile Data", "Gender: " + userGender);
        Log.d("Profile Data", "Date of Birth: " + userDateOfBirth);
        Log.d("Profile Data", "Age: " + userAge);
        Log.d("Profile Data", "Height: " + userHeight);
        Log.d("Profile Data", "Weight: " + userWeight);
        Log.d("Profile Data", "BMI: " + UserBMI);
        Log.d("Profile Data", "Water Consumption Daily: " + userWaterConsumptionDaily);
        Log.d("Profile Data", "Alcohol Consumption: " + userAlcoholConsumption);
        Log.d("Profile Data", "Smoking: " + userSmoking);
        Log.d("Profile Data", "Non-Veg Eating: " + userEating);
        Log.d("Profile Data", "Exercise: " + userExercise);
        Log.d("Profile Data", "Sleeping Hours: " + userSleepingHours);
        Log.d("Profile Data", "Mental Conditions: " + userMentalCondition);
        Log.d("Profile Data", "Overall Lifestyle Score: " + userOverallLifeStyleScore);

        // Log medical info data
        Log.d("Medical Info", "Blood Test: " + bloodTest);
        Log.d("Medical Info", "Fasting Blood Sugar Level: " + fastingBloodSugar);



    }

    private void initVars(){
        progressDialog =  new ProgressDialog(Conditions.this);

        // dots
        LinearLayout linearLayout =findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(),linearLayout, 4,2);

        // checkboxes
        chkNone=findViewById(R.id.chk_none);
        chkDia=findViewById(R.id.chk_dia);
        chkLipid=findViewById(R.id.chk_lungs);
        chkKidney=findViewById(R.id.chk_lipid);
        chkLiver=findViewById(R.id.chk_kidney);
        chkLungs=findViewById(R.id.chk_liver);

        // buttons
        buttonNext=findViewById(R.id.button_next);
    }
    private void checkBoxListener(){

        chkNone.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked){
                chkDia.setChecked(false);
                chkLipid.setChecked(false);
                chkKidney.setChecked(false);
                chkLiver.setChecked(false);
                chkLungs.setChecked(false);

                stringBuilder.setLength(0);
            }else{
                ch1=false;
                ch2=false;
                ch3=false;
                ch4=false;
                ch5=false;
            }

        });


        chkDia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch1=true;
            }else{
                ch1=false;
            }
        });

        chkLipid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch2=true;
            }else{
                ch2=false;
            }
        });
        chkKidney.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch3=true;
            }else{
                ch3=false;
            }
        });
        chkLiver.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch4=true;
            }else{
                ch4=false;
            }
        });
        chkLungs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch5=true;
            }else{
                ch5=false;
            }
        });

    }

    private void onClicks(){
        buttonNext.setOnClickListener(v -> {
            stringBuilder.setLength(0);

            if (ch1){
                stringBuilder.append("Diabetes ,");
            }
            if (ch2){
                stringBuilder.append("Lungs Issue ,");
            }
            if (ch3){
                stringBuilder.append("Lipid Issue ,");
            }

            if (ch4){
                stringBuilder.append("Kidney Issue ,");
            }

            if (ch5){
                stringBuilder.append("Liver Issue");
            }

            if (userLoginId==null || userProfileId==null){
                insertPersonalInformation();
            }else{
                showErrorDialog("Error: Login ID or Profile ID is missing. Please try again.");
            }

        });
    }

    private void insertPersonalInformation(){


        RequestParams params = new RequestParams();
        params.put("login_id", userLoginId);
        params.put("phone", userPhoneNumber);
        params.put("name", userProfileName);
        params.put("email", userEmailAddress);
        params.put("gender", userGender);
        params.put("dob", userDateOfBirth);
        params.put("age", userAge);
        params.put("height", userHeight);
        params.put("weight", userWeight);
        params.put("bmi", UserBMI);

        /// print comment it :: for testing only
        // Log the parameters
        Log.d("Request Parameter", String.valueOf(params));


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.insertNewProfile, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoading();
                setLoadingProgress(30,60);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody).trim();

                if (response.contains("created")) {
                    String[] parts = response.split("\\$");
                    String action = parts[0];
                    userProfileId = parts[1];

                    if (action.equals("created")) {
                        try {

                            SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(ProfileCreationData.PROFILE_ID, String.valueOf(userProfileId));
                            editor.apply();


                            /// personal data inserted successfully
                            insertHabitInformation(userProfileId);
                            insertDailyRoutine(userProfileId);


                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                            dismissLoadingProgress();
                            handleException(e.getMessage());
                        }
                    }
                } else {
                    if (response.contains("name_already_exist")){
                        dismissLoadingProgress();
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main),
                                userProfileName + " is already exist! please try again with different name",
                                Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        dismissLoadingProgress();
                        handleException("Something went wrong. Please try again later."+response);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Check if there's an error message in the response body
                dismissLoadingProgress();
                if (responseBody != null && responseBody.length > 0) {
                    String errorMessage = new String(responseBody).trim();
                    Log.e(TAG, "HTTP request failed with error: " + errorMessage);
                    // Toast.makeText(BMI.this, "HTTP request failed. Please try again later.", Toast.LENGTH_SHORT).show();
                    handleException("HTTP request failed. Please try again later.");
                } else {
                    // Handle other types of errors, e.g., network issues or timeouts
                    Log.e(TAG, "HTTP request failed with an unknown error.");
                    // Toast.makeText(BMI.this, "An unknown error occurred. Please check your network connection.", Toast.LENGTH_SHORT).show();
                    handleException("An unknown error occurred. Please check your network connection.");
                }
            }
            @Override
            public void onRetry(int retryNo) {
                // Called when the request is retried
            }
        });
    }

    private void insertHabitInformation(String profileId) {
        RequestParams params = new RequestParams();
        params.put("login_id", userLoginId);
        params.put("profile_id", profileId);
        params.put("water_consume", userWaterConsumptionDaily);
        params.put("alcoholic", userAlcoholConsumption);
        params.put("smoking", userSmoking);
        params.put("non_veg", userEating);
        params.put("exercise", userExercise);
        params.put("sleep_hours", userSleepingHours);
        params.put("mental_conditions", userMentalCondition);
        params.put("life_style_score", userOverallLifeStyleScore);

        Log.d("Request Parameter", String.valueOf(params));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.addHobbies, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // Perform any necessary setup when the request starts.
                setLoadingProgress(60,90);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody).trim();
                Log.d(TAG, response);
                if (response.equals("inserted")) {
                    insertBloodReportData(profileId);
                } else {
                    dismissLoadingProgress();
                    handleException(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissLoadingProgress();
                if (responseBody != null && responseBody.length > 0) {
                    String errorMessage = new String(responseBody).trim();
                    Log.e(TAG, "HTTP request failed with error: " + errorMessage);
                    // Toast.makeText(BMI.this, "HTTP request failed. Please try again later.", Toast.LENGTH_SHORT).show();
                    handleException("HTTP request failed. Please try again later.");
                } else {
                    // Handle other types of errors, e.g., network issues or timeouts
                    Log.e(TAG, "HTTP request failed with an unknown error.");
                    // Toast.makeText(BMI.this, "An unknown error occurred. Please check your network connection.", Toast.LENGTH_SHORT).show();
                    handleException("An unknown error occurred. Please check your network connection.");
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // This method is called when the request is retried (e.g., due to network issues).
                // You can add custom handling for retry scenarios here.
            }
        });
    }


    private void insertBloodReportData(String profileId) {
        RequestParams params = new RequestParams();
        params.put("login_id", userLoginId);
        params.put("profile_id", profileId);
        params.put("diabetic", "-");
        params.put("diabetic_values", fastingBloodSugar);
        params.put("report_age", bloodTest);
        params.put("conditions", String.valueOf(stringBuilder));

        Log.d("Request Parameter", String.valueOf(params));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.addBloodReport, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                setLoadingProgress(100, 100);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody).trim();
                if (response.equals("inserted")) {
                    updateSharedPreferences();
                    // Handle success case and update preferences.
                    Intent intent = new Intent(getApplicationContext(), ProfileCreationSuccess.class);
                    startActivity(intent);
                    finish();
                } else {
                    handleException(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissLoadingProgress();
                if (responseBody != null && responseBody.length > 0) {
                    String errorMessage = new String(responseBody).trim();
                    Log.e(TAG, "HTTP request failed with error: " + errorMessage);
                    // Toast.makeText(BMI.this, "HTTP request failed. Please try again later.", Toast.LENGTH_SHORT).show();
                    handleException("HTTP request failed. Please try again later.");
                } else {
                    // Handle other types of errors, e.g., network issues or timeouts
                    Log.e(TAG, "HTTP request failed with an unknown error.");
                    // Toast.makeText(BMI.this, "An unknown error occurred. Please check your network connection.", Toast.LENGTH_SHORT).show();
                    handleException("An unknown error occurred. Please check your network connection.");
                }
            }
        });
    }


    private void updateSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences sharedPreferences2 = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putString(LoginUtils.LOGIN_ID, String.valueOf(userLoginId));
        editor2.putString(LoginUtils.PROFILE_ID, String.valueOf(userProfileId));
        editor2.putString(LoginUtils.USER_NAME, String.valueOf(userProfileName));
        editor2.apply();

        SharedPreferences sharedPreferences3 = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
        editor3.putString(ActiveProfile.LOGIN_ID, String.valueOf(userLoginId));
        editor3.putString(ActiveProfile.PROFILE_ID, String.valueOf(userProfileId));
        editor3.putString(ActiveProfile.NAME, String.valueOf(userProfileName));
        editor3.apply();
    }



    private void handleException(String s) {
        showErrorDialog("An error occurred: " + s);
    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);

        // Set a positive button with a click listener
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Perform an action when the "OK" button is clicked
            // For example, you can close the dialog or perform another task.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //startActivity(intent);
            // finish();
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


    private void showLoading(){
        progressDialog.setTheme(ProgressDialog.THEME_LIGHT);
        progressDialog.setMode(ProgressDialog.MODE_DETERMINATE);
        progressDialog.setTitle("");
        progressDialog.setMessage("Creating your profile");
        progressDialog.hideProgressText();
        progressDialog.show();

    }


    private void setLoadingProgress(int progress , int secondaryProgress){
        progressDialog.setProgress(progress);
        progressDialog.setSecondaryProgress(secondaryProgress);
    }


    private void dismissLoadingProgress(){
        progressDialog.dismiss();
    }


    private void insertDailyRoutine(String profileId){


        String skipMeal = String.valueOf(breakfast) + "/" + String.valueOf(lunch) +"/"+ String.valueOf(dinner);


        String url = "https://humorstech.com/humors_app/app_final/insert_daily_routine_data.php?";
        url +=  "login_id="+userLoginId;
        url +=  "&profile_id="+profileId;
        url +=  "&water_consumed="+userWaterConsumptionDaily;
        url +=  "&cigarettes_unit="+cigarettesUnits;
        url +=  "&alcohol_unit="+alcoholUnits;
        url +=  "&exercise_minutes="+exerciseInMinutes;
        url +=  "&sleep_hours="+userSleepingHours;
        url +=  "&food_intake="+foodIntake;
        url +=  "&food_name="+String.valueOf(foodName);
        url +=  "&food_quantity="+String.valueOf(foodQuantity);
        url +=  "&food_cat="+String.valueOf(foodCategories);
        url +=  "&food_id="+String.valueOf(foodIds);
        url +=  "&food_image_link="+String.valueOf(foodImageLinks);
        url +=  "&skip_meal="+skipMeal;




        MyVolleyRequest myVolleyRequest = new MyVolleyRequest(this); // 'this' is your context

        myVolleyRequest.makeGetRequest(url, new MyVolleyRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {


                String responseStr = new String(response).trim();



            }

            @Override
            public void onError(VolleyError error) {
//                Intent intent = new Intent(getApplicationContext(), BloodTest.class);
//                startActivity(intent);
//                Animatoo1.animateSlideLeft(LifeStyleScore.this);
            }

            @Override
            public void onRetry() {
                // Handle retry logic here
                Log.d(TAG, "Retrying the request due to no internet connection.");
                // You can implement retry logic here, e.g., show a message to the user.
                Toast.makeText(getApplicationContext(), "Retrying the request due to no internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}