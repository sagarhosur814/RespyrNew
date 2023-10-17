package com.humorstech.respyr.results;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.reading.Stuffs;
import com.humorstech.respyr.utills.BloodPressureResults;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class GeneratingResults extends AppCompatActivity {

    private String profileName,waterIntake, smokingUnits, isTakenAlcohol, sleepHours, exerciseInMinutes, foodName, foodQuantity, foodCount, isHadBreakfast, isHadLunch, isHadDinner, gender, age, height, weight, activityLevel , foodType;
    private String SP, DP, Beats, Breath, spo2, bpm1, bpm2;
    private String responseFromRawValues;

    //////////////////////////////////////////////////// xml views//////////////////////////////
    private CircularProgressIndicator circularProgressIndicator1;
    private CircularProgressIndicator circularProgressIndicator2;
    private CircularProgressIndicator circularProgressIndicator3;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private View view1;
    private View view2;

    private TextView txtLog;
    private LinearLayout llResultPage;
    private LinearLayout llErrorLayout;



    //////////////////////// Main Scores
    double finalOverallHealthScore=0;
    double finalDiabeticScore=0;
    double finalVitalScore=0;
    double finalRespiratoryScore=0;
    double finalActivityScore=0;
    double finalNutritionScore=0;
    double finalLiverScore=0;

    private boolean isDataFetched=false;


    private String profileId, loginId;

    double ethanol, h2;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating_results);
        StatusBarColor statusBarColor= new StatusBarColor(GeneratingResults.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        SharedPreferences preferences = getSharedPreferences("CALIBRATION_TIME_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("CALIBRATION_TIME");
        editor.apply();


        init();

    }


    private void stepper(){
        circularProgressIndicator1 =findViewById(R.id.analyse_progress1);
        circularProgressIndicator2 =findViewById(R.id.analyse_progress2);
        circularProgressIndicator3 =findViewById(R.id.analyse_progress3);

        imageView1 =findViewById(R.id.analyse_image1);
        imageView2 =findViewById(R.id.analyse_image2);
        imageView3 =findViewById(R.id.analyse_image3);

        view1=findViewById(R.id.analyse_view1);
        view2=findViewById(R.id.analyse_view2);

        setAnalysingProgress(2);

        new CountDownTimer(9000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long taskDuration = 9 / 3; // Duration of each task (in seconds)
                int currentTask = (int)((9 - seconds) / taskDuration + 1); // Current task number



                // Perform actions based on the current task
                switch (currentTask) {
                    case 1:
                        setAnalysingProgress(2);
                        break;
                    case 2:
                        setAnalysingProgress(2);
                        break;
                    case 3:
                        setAnalysingProgress(3);
                        break;
                }
            }

            public void onFinish() {
                cancel();
                if(isDataFetched){
                    Intent intent = new Intent(getApplicationContext(), FinalResult.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }

    private void setAnalysingProgress(int i){
        // 1. Calculating your results
        // 2. Analysing your results
        // 3. Generating your report
        int active = R.drawable.uncheck_progress1;
        int inactive = R.drawable.uncheck_progress;
        int completed = R.drawable.check_progress;

        int colorCompleted = getResources().getColor(R.color.green);
        int colorInComplete = getResources().getColor(R.color.calories_progress_bg_active);


        switch (i){
            case 1 :
                circularProgressIndicator1.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);

                imageView1.setImageResource(active);
                imageView2.setImageResource(inactive);
                imageView3.setImageResource(inactive);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;

            case 2 :
                circularProgressIndicator2.setVisibility(View.VISIBLE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(active);
                imageView3.setImageResource(inactive);


                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;
            case 3 :
                circularProgressIndicator3.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(active);



                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));

                break;
            case 4 :
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(completed);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));

                break;
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        initVars();
        stepper();

        try {
            getUserData();
            getBloodPressureData();
            getIntentData();
        } catch (Exception e) {
            handleException(e);
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
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    private void initVars(){
        txtLog=findViewById(R.id.txt_log);
        txtLog.setMovementMethod(new ScrollingMovementMethod());

        llErrorLayout=findViewById(R.id.layout_error);
        llResultPage=findViewById(R.id.layout_result_page);
    }

    private void getUserData(){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(ReadingParameters.TITLE, Context.MODE_PRIVATE);
            profileId = sharedPreferences.getString(ReadingParameters.PROFILE_ID, null);
            loginId = sharedPreferences.getString(ReadingParameters.LOGIN_ID, null);

            profileName = sharedPreferences.getString(ReadingParameters.NAME, null);

            waterIntake = sharedPreferences.getString(ReadingParameters.WATER_INTAKE, null);
            waterIntake = sharedPreferences.getString(ReadingParameters.WATER_INTAKE, null);
            smokingUnits = sharedPreferences.getString(ReadingParameters.SMOKING_UNITS, null);
            isTakenAlcohol = sharedPreferences.getString(ReadingParameters.IS_TAKEN_ALCOHOL, null);
            sleepHours = sharedPreferences.getString(ReadingParameters.SLEEP_HOURS, null);
            exerciseInMinutes = sharedPreferences.getString(ReadingParameters.EXERCISE_IN_MINUTES, null);
            foodName = sharedPreferences.getString(ReadingParameters.FOOD_NAME, null);
            foodQuantity = sharedPreferences.getString(ReadingParameters.FOOD_QUANTITY, null);
            foodCount = sharedPreferences.getString(ReadingParameters.FOOD_COUNT, null);
            isHadBreakfast = sharedPreferences.getString(ReadingParameters.IS_HAD_BREAKFAST, null);
            isHadLunch = sharedPreferences.getString(ReadingParameters.IS_HAD_LUNCH, null);
            isHadDinner = sharedPreferences.getString(ReadingParameters.IS_HAD_DINNER,null);
            gender = sharedPreferences.getString(ReadingParameters.GENDER, null);
            age = sharedPreferences.getString(ReadingParameters.AGE, "");
            height = sharedPreferences.getString(ReadingParameters.HEIGHT, "");
            weight = sharedPreferences.getString(ReadingParameters.WEIGHT, "");
            activityLevel =  UserDataManager.getActivityLevel(getApplicationContext());
            foodType = UserDataManager.getUserFoodType(getApplicationContext());


            System.out.println("Profile ID: " + profileId);
            System.out.println("Login ID: " + loginId);
            System.out.println("Profile Name: " + profileName);
            System.out.println("Water Intake: " + waterIntake);
            System.out.println("Smoking Units: " + smokingUnits);
            System.out.println("Alcohol Consumption: " + isTakenAlcohol);
            System.out.println("Sleep Hours: " + sleepHours);
            System.out.println("Exercise Minutes: " + exerciseInMinutes);
            System.out.println("Food Name: " + foodName);
            System.out.println("Food Quantity: " + foodQuantity);
            System.out.println("Food Count: " + foodCount);
            System.out.println("Had Breakfast: " + isHadBreakfast);
            System.out.println("Had Lunch: " + isHadLunch);
            System.out.println("Had Dinner: " + isHadDinner);
            System.out.println("Gender: " + gender);
            System.out.println("Age: " + age);
            System.out.println("Height: " + height);
            System.out.println("Weight: " + weight);
            System.out.println("Activity Level: " + activityLevel);
            System.out.println("Food Type: " + foodType);
            System.out.println("Current Day Name" + UserDataManager.getCurrentDayName());

        } catch (Exception e) {
            handleException(e);
        }

    }

    private void getBloodPressureData(){
        try {

            SharedPreferences sharedPreferences2 = getSharedPreferences(BloodPressureResults.TITLE, Context.MODE_PRIVATE);
            SP = sharedPreferences2.getString(BloodPressureResults.SYSTOLIC, "");
            DP = sharedPreferences2.getString(BloodPressureResults.DIASTOLIC, "");
            Beats = sharedPreferences2.getString(BloodPressureResults.FINAL_BPM, "");
            Breath = sharedPreferences2.getString(BloodPressureResults.BREATH_RATE, "");
            spo2 = sharedPreferences2.getString(BloodPressureResults.SPO2, "");
            bpm1 = sharedPreferences2.getString(BloodPressureResults.BPM1, "");
            bpm2 = sharedPreferences2.getString(BloodPressureResults.BPM2, "");


            txtLog.append("Blood Pressure Values-----------" + "\n");
            txtLog.append("SP: " + SP + "\n");
            txtLog.append("DP: " + DP + "\n");
            txtLog.append("Beats: " + Beats + "\n");
            txtLog.append("Breath: " + Breath + "\n");
            txtLog.append("SPO2: " + spo2 + "\n");
            txtLog.append("BPM1: " + bpm1 + "\n");
            txtLog.append("BPM2: " + bpm2 + "\n");


        } catch (Exception e) {
            handleException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getIntentData(){

        try {

            Intent intent = getIntent();
            if (intent!=null){

                /////////////// response from the production table
                responseFromRawValues = intent.getStringExtra("response");
            }

            /// dummy data //// this line is for testing purpose only
            // // comment this line for actual work
          // responseFromRawValues="{\"data\":[{\"status\":1,\"ace\":\"0.17162892\",\"pp_press\":\"73\",\"etholpp\":\"10\",\"battry\":null,\"finaltemp\":\"31\",\"duration\":\"90\",\"rawmic\":null,\"bmhumid\":null,\"besthumid\":0,\"bpm\":null,\"valpress\":null,\"peak_press\":null,\"nopp\":null,\"cap\":\"0\",\"val1820\":\"180\",\"valFinal1820\":\"181\",\"MVacetone\":3.912}]}";

            txtLog.append("\n");
            txtLog.append("responseFromRawValues: " + responseFromRawValues + "\n");


            decodeRawValueResponse(responseFromRawValues);

        } catch (Exception e) {
            handleException(e);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decodeRawValueResponse(String jsonData){


        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONObject dataObject = dataArray.getJSONObject(0);
            ethanol = dataObject.getDouble("etholpp");
            double MV_acetone = dataObject.getDouble("MVacetone");
            double blow = dataObject.getDouble("cap");
             h2 = dataObject.getDouble("h2");

            ///////////////////// Values::::: Raw values Response ///////////////
            System.out.println("///////////////////// Values::::: Raw values Response ///////////////");
            System.out.println("Ethanol: " + ethanol);
            System.out.println("MV Acetone: " + MV_acetone);
            System.out.println("Blow: " + blow);
            System.out.println("----------------------------------------------------------------------");
            txtLog.append("-----------------------------------------------");
            txtLog.append("Ethanol: " + ethanol + "\n");
            txtLog.append("MV Acetone: " + MV_acetone + "\n");
            txtLog.append("Blow: " + blow + "\n");
            txtLog.append("-----------------------------------------------");


            if (isNetworkConnected()){
                fetchDiabeticScore(SP, DP,Beats,spo2, String.valueOf(MV_acetone),String.valueOf(ethanol),age,String.valueOf(blow));
            }else{
                handleException("Please check your internet connection");
            }

        }catch (Exception e){
            handleException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchDiabeticScore(String systolicPressure,
                                    String diastolicPressure,
                                    String finalHeartRate,
                                    String finalSpo2,
                                    String acetoneValue,
                                    String ethanolValue,
                                    String userAge,
                                    String blow ){



        try {
            RequestParams params = new RequestParams();
            params.put("acetone",acetoneValue);
            params.put("ethnol",ethanolValue);
            params.put("spo2",finalSpo2);
            params.put("heart_rate",finalHeartRate);
            params.put("diastolic",diastolicPressure);
            params.put("systolic",systolicPressure);
            params.put("blow",blow);
            params.put("age",userAge);
            params.put("login", loginId);
            params.put("profile", profileId);
            params.put("h2","0.2");

            txtLog.append("-------------------------------------------------------------"+"\n");
            txtLog.append("Params for DB_VITAL" + "\n");
            txtLog.append(String.valueOf(HTTP_URLS.GET_DB_VITAL+params) + "\n");
            System.out.println("Params for DB_VITAL");
            System.out.println(HTTP_URLS.GET_DB_VITAL+params);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get( HTTP_URLS.GET_DB_VITAL,params,

                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            txtLog.append("-------------------------------------------------------------"+"\n");
                            txtLog.append("Params for DB_VITAL" + "\n");
                            txtLog.append(String.valueOf(HTTP_URLS.GET_DB_VITAL+params) + "\n");
                            txtLog.append("-------------------------------------------------------------"+"\n");
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                            String response2 = new String(responseBody);
                            txtLog.append("-------------------------------------------------------------"+"\n");
                            txtLog.append("Response from DB_VITAL" + "\n");
                            txtLog.append(response2 + "\n");

                            try {

                                JSONObject jsonObject = new JSONObject(response2);
                                String diabeticPrediction = jsonObject.getString("Dibetic_Prediction");
                             //   double bloodPressureScore = jsonObject.getDouble("Blood_Pressure_Score");
                               // double diabeticValue = jsonObject.getDouble("Dibetic_value");
                                double heartRateScore = jsonObject.getDouble("Heart_Rate_Score");
                              //  double spo2Score = jsonObject.getDouble("SPO2_Score");
                                double diabeticScore = jsonObject.getDouble("Dibetic_Score");
                                double overallVitalScore = jsonObject.getDouble("Overall_Vital_Score");


                                ////////////////// store final blow score
                                finalRespiratoryScore = jsonObject.getDouble("Blow_Score");

                                System.out.println("diabeticScore-------->"+diabeticScore);
                                System.out.println("overallVitalScore-------->"+overallVitalScore);
                                System.out.println("Blow score-------->"+finalRespiratoryScore);
                                txtLog.append("--------------------------------------------------"+"\n");


                                if(isNetworkConnected()){
                                    //////////////////////////////////// call final method to fetch result from the server
                                    fetchFinalResultData(overallVitalScore, diabeticScore);
                                }else{
                                   handleException("No internet connection. Please check your network.");
                                }


                            }catch (JSONException e){
                                System.out.println(e.getMessage());
                               // handleException(e);
                                txtLog.append(e.getMessage());
                                txtLog.append("------------------------------------------------- "+"\n");
                            }

                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            // Handle HTTP request failure here
                            String errorMsg;

                            // Check the status code to determine the specific error
                            if (statusCode == 0) {
                                errorMsg = "No internet connection. Please check your network.";
                            } else {
                                errorMsg = "HTTP request failed with status code: " + statusCode;
                            }

                            handleException(errorMsg);
                        }

                    });
        }catch (Exception e){
            handleException(e);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchFinalResultData(double vitalScore, double diabeticScore) {

        try {
            txtLog.append("fetchFinalResultData" + "_____________started" + "\n");

            ////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////// all parameters required to generate final result pages
            RequestParams params = new RequestParams();
            params.put("activity_level", activityLevel);
            params.put("DB_Score", diabeticScore);
            params.put("breakfast", getMealValue(Boolean.parseBoolean(isHadBreakfast)));
            params.put("lunch", getMealValue(Boolean.parseBoolean(isHadLunch)));
            params.put("dinner", getMealValue(Boolean.parseBoolean(isHadDinner)));
            params.put("VT_Score", vitalScore);
            params.put("height", height);
            params.put("age", age);
            params.put("weight", weight);
            params.put("gender", gender);
            params.put("food_intake", foodCount);
            params.put("food_name", foodName);
            params.put("food_quantity", foodQuantity);
            params.put("exercise_hours", exerciseInMinutes);
            params.put("sleep_hours", sleepHours);
            params.put("water_consumption", waterIntake);
            params.put("alcohol_consumption", isTakenAlcohol);
            params.put("smoking_units", smokingUnits);
            params.put("day", UserDataManager.getCurrentDayName());
            params.put("type", foodType);
            params.put("login", loginId);
            params.put("profile", profileId);
            params.put("blow_score", finalRespiratoryScore);
            params.put("ethnol", ethanol);
            params.put("h2", h2);
            ////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(HTTP_URLS.GENERATE_OVERALL_SCORE, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    txtLog.append("Params-----------------------------------" + "\n");
                    txtLog.append(String.valueOf(params) + "\n");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        String response3 = new String(response);
                        txtLog.append("Final Response-----------------------------------" + "\n");
                        txtLog.append(response3 + "\n");

                        System.out.println("___________________________RESPONSE_________________________________________");
                        System.out.println(response3);
                        System.out.println("------------------------------------------------------------------------");

                        //////////////////////////////////// Decode final json here
                        // this method decode final json
                        // and display all required values in result pager
                        decodeResultData(response3);
                        /////////////////////////////////////////////////////////////
                    } catch (Exception e) {
                        // Handle any unexpected exceptions during response processing
                        e.printStackTrace();
                        handleException(e);
                        // Log or show an error message to the user
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        // Handle HTTP request failure here
                        String errorMsg = "HTTP request failed with status code: " + statusCode;
                        txtLog.append(errorMsg + "\n");
                        handleException(errorMsg);
                        // Log or show an error message to the user
                    } catch (Exception e) {
                        // Handle any unexpected exceptions during failure handling
                        e.printStackTrace();
                        handleException(e);
                        // Log or show an error message to the user
                    }
                }
            });
        }catch (Exception e){
            handleException(e);
        }
    }


    private void decodeResultData(String jsonData) {
        System.out.println("___________________________JSON_________________________________________");
        System.out.println(jsonData);
        System.out.println("------------------------------------------------------------------------");

        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            JsonArray dataArray = jsonObject.getAsJsonArray("data");

            for (int i = 0; i < dataArray.size(); i++) {
                JsonObject item = dataArray.get(i).getAsJsonObject();

                finalOverallHealthScore = item.get("healthScore").getAsDouble();

                if (finalOverallHealthScore <= 0) {
                    performErrorLayout("RESPRROR004");
                    llErrorLayout.setVisibility(View.VISIBLE);
                    llResultPage.setVisibility(View.GONE);
                } else {

                    /////////////////////////////// display  result page

                    llErrorLayout.setVisibility(View.GONE);  //// hide error page
                    llResultPage.setVisibility(View.VISIBLE); //// make visible result page


                    //// scores
                    finalDiabeticScore = item.get("diabeticScore").getAsDouble();
                    finalVitalScore = item.get("vitalScore").getAsDouble();
                    finalActivityScore = item.get("OverallLifeStyleScore").getAsDouble();
                    finalNutritionScore= item.get("OverallNutrientScore").getAsDouble();
                    finalLiverScore= item.get("score_liver").getAsDouble();


                    // workout
                    JsonArray exerciseDetails = item.get("exercisesDetails").getAsJsonArray();





                    //// nutrition values-------------------------------------------------------
                    /// current values
                    double curr_cal = item.get("curr_cal").getAsDouble();
                    double curr_car = item.get("curr_car").getAsDouble();
                    double curr_pro = item.get("curr_pro").getAsDouble();
                    double curr_fat = item.get("curr_fat").getAsDouble();
                    double curr_fib = item.get("curr_fib").getAsDouble();

                    // recommended
                    double reco_cal = item.get("reco_cal").getAsDouble();
                    double reco_car = item.get("reco_car").getAsDouble();
                    double reco_pro = item.get("reco_pro").getAsDouble();
                    double reco_fat = item.get("reco_fat").getAsDouble();
                    double reco_fib = item.get("reco_fib").getAsDouble();
                    ////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////


                    //////////////////// Suggestions////////////////////////////////////////////

                    ///////// food suggestions /////////////////////////////////////////////////
                    String breakfastSuggestions= item.get("breakfast_sugs").getAsString();
                    String lunchSuggestions= item.get("lunch_sug").getAsString();
                    String dinnerSuggestions= item.get("dinner_sug").getAsString();
                    ////////////////////////////////////////////////////////////////////////////
                    // life style suggestions
                    JsonArray lifeStyleSuggestions =item.get("LifeStyleSuggestions").getAsJsonArray();


                    System.out.println("Suggestions");
                    System.out.println(breakfastSuggestions);
                    System.out.println("--------------------------------------------------------");

                    System.out.println(SuggestionJsonConversion.convertToJSON(breakfastSuggestions));
                    System.out.println(SuggestionJsonConversion.convertToJSON(lunchSuggestions));
                    System.out.println(SuggestionJsonConversion.convertToJSON(dinnerSuggestions));



                    //// save result data
                    SharedPreferences preferences = getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    // scores
                    /// food suggestions
                    editor.putString(ActiveResultData.PROFILE_NAME, profileName);
                    editor.putString(ActiveResultData.PROFILE_ID, profileId);
                    editor.putString(ActiveResultData.LOGIN_ID, loginId);
                    editor.putString(ActiveResultData.OVERALL_HEALTH_SCORE, String.valueOf(finalOverallHealthScore));
                    editor.putString(ActiveResultData.DIABETIC_SCORE, String.valueOf(finalDiabeticScore));
                    editor.putString(ActiveResultData.VITAL_SCORE, String.valueOf(finalVitalScore));
                    editor.putString(ActiveResultData.RESPIRATORY_SCORE, String.valueOf(finalRespiratoryScore));
                    editor.putString(ActiveResultData.ACTIVITY_SCORE, String.valueOf(finalActivityScore));
                    editor.putString(ActiveResultData.NUTRITION_SCORE, String.valueOf(finalNutritionScore));
                    editor.putString(ActiveResultData.LIVER_SCORE, String.valueOf(finalLiverScore));

                    /// food suggestions
                    editor.putString(ActiveResultData.BREAKFAST_SUGGESTION, breakfastSuggestions);
                    editor.putString(ActiveResultData.LUNCH_SUGGESTION, lunchSuggestions);
                    editor.putString(ActiveResultData.DINNER_SUGGESTION, dinnerSuggestions);

                    ///
                    editor.putString(ActiveResultData.RECOMMENDED_CALORIES, String.valueOf(reco_cal));
                    editor.putString(ActiveResultData.RECOMMENDED_CARBOHYDRATE, String.valueOf(reco_car));
                    editor.putString(ActiveResultData.RECOMMENDED_PROTEIN, String.valueOf(reco_pro));
                    editor.putString(ActiveResultData.RECOMMENDED_FATS, String.valueOf(reco_fat));
                    editor.putString(ActiveResultData.RECOMMENDED_FIBER, String.valueOf(reco_fib));

                    editor.putString(ActiveResultData.ACTUAL_CALORIES, String.valueOf(curr_cal));
                    editor.putString(ActiveResultData.ACTUAL_CARBOHYDRATE, String.valueOf(curr_car));
                    editor.putString(ActiveResultData.ACTUAL_PROTEIN, String.valueOf(curr_pro));
                    editor.putString(ActiveResultData.ACTUAL_FATS, String.valueOf(curr_fat));
                    editor.putString(ActiveResultData.ACTUAL_FIBER, String.valueOf(curr_fib));

                    /// workout suggestions
                    editor.putString(ActiveResultData.WORKOUT_SUGGESTIONS, String.valueOf(exerciseDetails));

                    /// lifestyle Suggestions
                    editor.putString(ActiveResultData.LIFESTYLE_SUGGESTIONS, String.valueOf(lifeStyleSuggestions));

                    editor.putBoolean(ActiveResultData.IS_FRESH_READING, true);

                    editor.putString(ActiveResultData.DATE, Stuffs.getCurrentDate());
                    editor.putString(ActiveResultData.TIME, Stuffs.getCurrentTime());




                    // Print the values
                    System.out.println("Profile Name: " + profileName);
                    System.out.println("Profile ID: " + profileId);
                    System.out.println("Login ID: " + loginId);
                    System.out.println("Overall Health Score: " + finalOverallHealthScore);
                    System.out.println("Diabetic Score: " + finalDiabeticScore);
                    System.out.println("Vital Score: " + finalVitalScore);
                    System.out.println("Respiratory Score: " + finalRespiratoryScore);
                    System.out.println("Activity Score: " + finalActivityScore);
                    System.out.println("Nutrition Score: " + finalNutritionScore);
                    System.out.println("Breakfast Suggestion: " + breakfastSuggestions);
                    System.out.println("Lunch Suggestion: " + lunchSuggestions);
                    System.out.println("Dinner Suggestion: " + dinnerSuggestions);
                    System.out.println("Recommended Calories: " + reco_cal);
                    System.out.println("Recommended Carbohydrate: " + reco_cal);
                    System.out.println("Recommended Protein: " + reco_pro);
                    System.out.println("Recommended Fats: " + reco_fat);
                    System.out.println("Recommended Fiber: " + reco_fib);
                    System.out.println("Actual Calories: " + curr_cal);
                    System.out.println("Actual Carbohydrate: " + curr_car);
                    System.out.println("Actual Protein: " + curr_pro);
                    System.out.println("Actual Fats: " + curr_fat);
                    System.out.println("Actual Fiber: " + curr_fib);
                    System.out.println("Workout Suggestions: " + String.valueOf(exerciseDetails));
                    System.out.println("Lifestyle Suggestions: " + String.valueOf(lifeStyleSuggestions));



                    isDataFetched=true;
                    editor.apply();




                }



            }
        } catch (JsonSyntaxException e) {

            // Handle JSON syntax errors
            ///// ERROR CODE : RESPRROR001

            performErrorLayout("RESPRROR001");
            llErrorLayout.setVisibility(View.VISIBLE);
            llResultPage.setVisibility(View.GONE);
            e.printStackTrace();

            handleException(e);
        } catch (JsonIOException e) {

            // Handle JSON IO errors (file not found, etc.)
            ///// ERROR CODE : RESPRROR002
            performErrorLayout("RESPRROR002");
            llErrorLayout.setVisibility(View.VISIBLE);
            llResultPage.setVisibility(View.GONE);
            e.printStackTrace();

            handleException(e);
        } catch (Exception e) {
            // Handle other general exceptions
            ///// ERROR CODE : RESPRROR003
            performErrorLayout("RESPRROR003");
            llErrorLayout.setVisibility(View.VISIBLE);
            llResultPage.setVisibility(View.GONE);
            e.printStackTrace();
            handleException(e);
        }
    }


    private String getMealValue(boolean isTakenMeal){
        //// true = taken
        //// false = not taken
        if(isTakenMeal)
            return  "yes";
        else
            return "no";
    }



    private void performErrorLayout(String errorCode){
        TextView txtNeedSupport = findViewById(R.id.txt_need_support);
        TextView txtHelpCenter = findViewById(R.id.txt_help_center);

        SpannableString content = new SpannableString("Need Support?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtNeedSupport.setText(content);


        String fullTextHelpCenter = "Try again after sometime,\nif this error keeps occurring contact our help center";
        // Find the position of "help center" in the full text
        int startIndex = fullTextHelpCenter.indexOf("help center");
        int endIndex = startIndex + "help center".length();

        // Create a SpannableString for "help center" with underline and custom text color
        SpannableString helpCenterSpan = new SpannableString("help center");
        helpCenterSpan.setSpan(new UnderlineSpan(), 0, helpCenterSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helpCenterSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), 0, helpCenterSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        // Create a SpannableStringBuilder for the full text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullTextHelpCenter);
        spannableStringBuilder.replace(startIndex, endIndex, helpCenterSpan);

        // Set the formatted SpannableStringBuilder to the TextView
        txtHelpCenter.setText(spannableStringBuilder);



        Button buttonTryLater =findViewById(R.id.button_try_later);
        ImageButton buttonCopyErrorCode =findViewById(R.id.button_copy_error_code);
        TextView txtErrorCode =findViewById(R.id.txt_error_code);
        txtErrorCode.setText(errorCode);

        buttonCopyErrorCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text you want to copy
                String textToCopy = txtErrorCode.getText().toString();

                // Get the ClipboardManager
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData with the text
                ClipData clipData = ClipData.newPlainText("label", textToCopy);

                // Set the ClipData to the clipboard
                clipboardManager.setPrimaryClip(clipData);

                // Show a toast message
                Toast.makeText(GeneratingResults.this, "Error code copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        buttonTryLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}