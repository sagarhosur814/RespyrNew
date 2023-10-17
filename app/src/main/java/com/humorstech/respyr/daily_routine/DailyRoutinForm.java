package com.humorstech.respyr.daily_routine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.MyVolleyRequest;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.daily_routine.added.AddedFoodAdapter2;
import com.humorstech.respyr.daily_routine.added.AddedFoodData;
import com.humorstech.respyr.daily_routine.search.FoodDataBase;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.dashboard.Methods;
import com.humorstech.respyr.profile.Profile;
import com.humorstech.respyr.reading.BeforeReading;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class DailyRoutinForm extends AppCompatActivity {


    private String name,gender,age,height,weight, exercise;

    private int cigarettesCount=0;
    private int waterCount=0;
    private int exerciseCount=0;

    private boolean isBreakfast=true;
    private boolean isLunch=true;
    private boolean isDinner=true;

    private int alcoholConsumption=0;
    private int sleepHours=0;

    private final HashMap<String, String> foodItems = new HashMap<>();
    private final HashMap<String, String> foodQuantitiesList = new HashMap<>();
    private final HashMap<String, String> foodCatList = new HashMap<>();
    private final HashMap<String, String> foodNameList = new HashMap<>();
    private final HashMap<String, String> foodIdList = new HashMap<>();
    private final HashMap<String, String> foodImageList = new HashMap<>();


    private final HashMap<String, String> foodQuantities = new HashMap<>();
    private final HashMap<String, String> foodCategories = new HashMap<>();
    private final HashMap<String, String> foodIds = new HashMap<>();
    private final HashMap<String, String> foodImageLinks = new HashMap<>();

    private int foodCount;
    private final StringBuilder foodNameBuffer1 = new StringBuilder();
    private final StringBuilder foodNameBuffer2 = new StringBuilder();
    private final StringBuilder foodQuantityBuffer1 = new StringBuilder();
    private final StringBuilder foodQuantityBuffer2 = new StringBuilder();

    private final StringBuilder foodIdBuffer = new StringBuilder();
    private final StringBuilder foodCatBuffer = new StringBuilder();
    private final StringBuilder foodImagesBuffer = new StringBuilder();

    private static final String TAG = "DailyRoutine";
    private String loginId, profileId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_routin_form);
        StatusBarColor statusBarColor = new StatusBarColor(DailyRoutinForm.this);
        statusBarColor.setDarkColor(getResources().getColor(R.color.carbohydrate));

        // clear food database

        // clear existing data
        FoodDataBase dataBase=new FoodDataBase(getApplicationContext());
        dataBase.clearTable();


        // clear all buffers
        foodNameBuffer1.setLength(0);
        foodNameBuffer2.setLength(0);
        foodQuantityBuffer1.setLength(0);
        foodQuantityBuffer2.setLength(0);
        foodIdBuffer.setLength(0);
        foodCatBuffer.setLength(0);
        foodImagesBuffer.setLength(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initVars();
        onClicks();
        getProfileData();
        activityDate();
        performMeal();
        performSmokeCounter();
        performWaterCounter();
        performExercise();
        performAlcohol();
        performSleepTiming();
        performAddedFood();
    }




    private void getProfileData(){
        /// get required data
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        loginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);
        profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID,null );
        refreshProfileData(loginId, profileId);
    }


    private void initVars(){
        ImageView profileImage = findViewById(R.id.img_toolbar_av);
        Methods.setToolBar(getApplicationContext(), profileId, gender,profileImage);
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
            String email = jsonObject.getString("email");
            String dob = jsonObject.getString("dob");

             name = jsonObject.getString("name");
             gender = jsonObject.getString("gender");
             age = jsonObject.getString("age");
             height = jsonObject.getString("height");
             weight = jsonObject.getString("weight");

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



            fetchDailyRoutineData();



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

            exercise =  String.valueOf(exerciseStr);

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


    private void fetchDailyRoutineData(){


        String url = "https://humorstech.com/humors_app/app_final/fetch_daily_routine_data.php?login_id="+
                loginId+"" +
                "&profile_id="+profileId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = response.trim();
                        if(!response.equals("0")){
                            decodeJson(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void decodeJson(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);


            waterCount = jsonObject.getInt("water_consumed");
            cigarettesCount = jsonObject.getInt("cigarettes_unit");
            alcoholConsumption = jsonObject.getInt("alcohol_unit");
            exerciseCount = jsonObject.getInt("exercise_minutes");
            sleepHours = jsonObject.getInt("sleep_hours");
            foodCount = jsonObject.getInt("food_intake");
            String foodName = jsonObject.getString("food_name");
            String foodQuantity = jsonObject.getString("food_quantity");
            String foodId = jsonObject.getString("food_id");
            String foodCat = jsonObject.getString("food_cat");
            String foodImageLink = jsonObject.getString("food_image_link");
            String skipMeal = jsonObject.getString("skip_meal");




            // Now you can use these values as needed.
            displayOldList(foodName,foodQuantity, foodId,foodCat,foodImageLink );

            skipMeal(skipMeal);
            performSmokeCounter();
            performWaterCounter();
            performExercise();
            performAlcohol();
            performSleepTiming();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void skipMeal(String inputString){
        String[] parts = null;

        try {
            // Check if the inputString is not null
            if (inputString != null) {
                // Split the string by "/"
                parts = inputString.split("/");


            } else {
                // Handle the case where inputString is null
                // For example, set parts to an empty array or display an error message
            }
        } catch (Exception e) {
            // Handle any exceptions that may occur during the split operation
            // For example, log the error or display an error message
            e.printStackTrace();
        }

        // Check if the split was successful and parts is not null
        if (parts != null) {
            // Now, 'parts' contains an array of substrings
            // parts[0] will contain "true"
            // parts[1] will contain "false"
            // parts[2] will contain "false"
            isBreakfast = Boolean.parseBoolean(parts[0]);
            isLunch = Boolean.parseBoolean(parts[1]);
            isDinner = Boolean.parseBoolean(parts[2]);

            performMeal();


        } else {
            // Handle the case where the split operation failed
            // This could be due to a null inputString or another exception
            // For example, display an error message to the user

            isBreakfast = true;
            isLunch = true;
            isDinner = true;

            performMeal();
        }

    }

    private void displayOldList(String foodName, String foodQuantity,String foodId,String foodCat,String foodImageLink  ){


        try {

            JSONArray jsonArray = FoodDataToJson.convertToJSON(foodName, foodQuantity,foodId,foodCat ,foodImageLink );
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                int quantity = jsonObject.getInt("quantity");
                int id = jsonObject.getInt("id");
                String category = jsonObject.getString("category");
                String image = jsonObject.getString("image");


                // Print the values using Log
                Log.d("FoodItem", "Name: " + name);
                Log.d("FoodItem", "Quantity: " + quantity);
                Log.d("FoodItem", "ID: " + id);
                Log.d("FoodItem", "Category: " + category);
                Log.d("FoodItem", "Image: " + image);

                insertDataIntoSQLite(id, name,String.valueOf(quantity),category,image, "Breakfast" );
                // display added foods
                performAddedFood();

            }
        }catch (Exception e){

        }
    }

    private boolean insertDataIntoSQLite(int id, String foodName, String foodAmount, String foodCategory, String imageLink, String foodType) {
        // Insert the data into the SQLite database
        // Replace with your SQLite database insertion logic
        FoodDataBase dbHelper = new FoodDataBase(getApplicationContext());
        boolean isInserted = dbHelper.insertFoodItem(
                id,
                foodName,
                foodCategory,
                foodAmount,
                imageLink,
                "-",
                foodType);

        if (isInserted) {
            return true;
        } else {
            return false;
        }
    }



    private void performSleepTiming(){

        IndicatorSeekBar sleepSeekBar = findViewById(R.id.sleep_hour_bar);
        EditText etSleepHours=findViewById(R.id.et_hours);
        sleepSeekBar.setProgress(sleepHours);
        etSleepHours.setText(String.valueOf(sleepHours));


        sleepSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                etSleepHours.setText(String.valueOf(seekParams.progress));
                sleepHours=seekParams.progress;
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void activityDate(){
        TextView activityDate =findViewById(R.id.txt_routine_date);

        // Get yesterday's date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,0);
        Date yesterday = calendar.getTime();

        // Format the date to your desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(yesterday);

        activityDate.setText("Today, \n" + formattedDate);

    }



    @SuppressLint("SetTextI18n")
    private void onClicks(){
        ImageButton buttonBack=findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());

        Button buttonAddMeal=findViewById(R.id.button_add_meal);
        buttonAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SearchMeal.class);
            startActivity(intent);
        });

        Button button_clear=findViewById(R.id.button_clear);
        button_clear.setOnClickListener(v -> {
            FoodDataBase dataBase=new FoodDataBase(getApplicationContext());
            dataBase.clearTable();
            recreate();
        });

        Button buttonNext=findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> {
            if (foodCount==0){
                Snackbar.make(v, "Please fill out the complete form.", Snackbar.LENGTH_SHORT).show();
            }else{
                moveToBloodPressure();
            }
        });
    }
    private void moveToBloodPressure(){




        SharedPreferences sharedPreferences = getSharedPreferences(ReadingParameters.TITLE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ReadingParameters.WATER_INTAKE, String.valueOf(waterCount));
        editor.putString(ReadingParameters.SMOKING_UNITS, String.valueOf(cigarettesCount));
        editor.putString(ReadingParameters.IS_TAKEN_ALCOHOL,  String.valueOf(alcoholConsumption));
        editor.putString(ReadingParameters.SLEEP_HOURS,  String.valueOf(sleepHours));
        editor.putString(ReadingParameters.EXERCISE_IN_MINUTES, String.valueOf(exerciseCount));
        editor.putString(ReadingParameters.ACTIVITY_LEVEL, String.valueOf(exercise));
        editor.putString(ReadingParameters.FOOD_NAME, String.valueOf(foodNameBuffer1));
        editor.putString(ReadingParameters.FOOD_QUANTITY, String.valueOf(foodQuantityBuffer1));
        editor.putString(ReadingParameters.FOOD_COUNT, String.valueOf(foodCount));
        editor.putString(ReadingParameters.IS_HAD_BREAKFAST,  String.valueOf(isBreakfast));
        editor.putString(ReadingParameters.IS_HAD_LUNCH, String.valueOf(isLunch));
        editor.putString(ReadingParameters.IS_HAD_DINNER, String.valueOf(isDinner));
        editor.putString(ReadingParameters.SMOKING_UNITS, String.valueOf(cigarettesCount));


        editor.putString(ReadingParameters.NAME, name);
        editor.putString(ReadingParameters.GENDER, gender);
        editor.putString(ReadingParameters.AGE, age);
        editor.putString(ReadingParameters.WEIGHT, weight);
        editor.putString(ReadingParameters.HEIGHT, height);
        editor.putString(ReadingParameters.LOGIN_ID, loginId);
        editor.putString(ReadingParameters.PROFILE_ID, profileId);
        editor.apply();

        TextView txtLog =findViewById(R.id.txt_log);

        txtLog.append("Water Intake: " + sharedPreferences.getString(ReadingParameters.WATER_INTAKE, "") + "\n");
        txtLog.append("Cigarettes Count: " + sharedPreferences.getString(ReadingParameters.SMOKING_UNITS, "") + "\n");
        txtLog.append("Alcohol Consumption: " + sharedPreferences.getString(ReadingParameters.IS_TAKEN_ALCOHOL, "") + "\n");
        txtLog.append("Sleep Hours: " + sharedPreferences.getString(ReadingParameters.SLEEP_HOURS, "") + "\n");
        txtLog.append("Exercise Count: " + sharedPreferences.getString(ReadingParameters.EXERCISE_IN_MINUTES, "") + "\n");
        txtLog.append("Food Name: " + sharedPreferences.getString(ReadingParameters.FOOD_NAME, "") + "\n");
        txtLog.append("Food Quantity: " + sharedPreferences.getString(ReadingParameters.FOOD_QUANTITY, "") + "\n");
        txtLog.append("Food Count: " + sharedPreferences.getString(ReadingParameters.FOOD_COUNT, "") + "\n");
        txtLog.append("Had Breakfast: " + sharedPreferences.getString(ReadingParameters.IS_HAD_BREAKFAST, "") + "\n");
        txtLog.append("Had Lunch: " + sharedPreferences.getString(ReadingParameters.IS_HAD_LUNCH, "") + "\n");
        txtLog.append("Had Dinner: " + sharedPreferences.getString(ReadingParameters.IS_HAD_DINNER, "") + "\n");
        txtLog.append("Name: " + sharedPreferences.getString(ReadingParameters.NAME, "") + "\n");
        txtLog.append("Gender: " + sharedPreferences.getString(ReadingParameters.GENDER, "") + "\n");
        txtLog.append("Age: " + sharedPreferences.getString(ReadingParameters.AGE, "") + "\n");
        txtLog.append("Height: " + sharedPreferences.getString(ReadingParameters.HEIGHT, "") + "\n");
        txtLog.append("Weight: " + sharedPreferences.getString(ReadingParameters.WEIGHT, "") + "\n");


        insertDailyRoutine();


    }
    @SuppressLint("SetTextI18n")
    private void performWaterCounter(){
        ImageButton buttonWaterMinus =findViewById(R.id.button_water_minus);
        ImageButton buttonWaterPlus =findViewById(R.id.button_water_plus);
        TextView txtWaterCount =findViewById(R.id.txt_water_glasses);
        txtWaterCount.setText(waterCount +" glass");
        buttonWaterMinus.setOnClickListener(v -> {

            txtWaterCount.setText("");
            if (waterCount>0){
                waterCount--;
            }
            txtWaterCount.setText(waterCount +" glass");
        });

        buttonWaterPlus.setOnClickListener(v -> {
            txtWaterCount.setText("");
            waterCount++;
            txtWaterCount.setText(waterCount +" glass");
        });

    }

    private void performAlcohol(){

        RadioButton rYes = findViewById(R.id.radio_button_alcohol_yes);
        RadioButton rNo = findViewById(R.id.radio_button_alcohol_no);

        if (alcoholConsumption<=0){
            rYes.setChecked(false);
            rNo.setChecked(true);
        }else{
            rYes.setChecked(true);
            rNo.setChecked(false);
        }


        RadioGroup radioGroup = findViewById(R.id.radio_group_alcohol_taken);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button_alcohol_yes) {
                alcoholConsumption=1;

            } else if (checkedId == R.id.radio_button_alcohol_no) {
                alcoholConsumption=0;
            }
        });
    }
    private void performSmokeCounter(){
        ImageButton buttonSmokeMinus =findViewById(R.id.image_button_smoke_minus);
        ImageButton buttonSmokePlus =findViewById(R.id.image_button_smoke_plus);
        TextView txtCigarettes =findViewById(R.id.txt_cigarettes);

        txtCigarettes.setText(String.valueOf(cigarettesCount));

        buttonSmokeMinus.setOnClickListener(v -> {
            txtCigarettes.setText("");
            if (cigarettesCount>0){
                cigarettesCount--;
            }
            txtCigarettes.setText(String.valueOf(cigarettesCount));
        });

        buttonSmokePlus.setOnClickListener(v -> {
            txtCigarettes.setText("");
            cigarettesCount++;
            txtCigarettes.setText(String.valueOf(cigarettesCount));
        });

    }

    @SuppressLint("SetTextI18n")
    private void performExercise(){
        Button buttonExerciseMinus =findViewById(R.id.button_exc_minus);
        Button buttonExercisePlus =findViewById(R.id.button_exc_add);
        TextView txtExerciseCount =findViewById(R.id.txt_exercise);
        txtExerciseCount.setText(exerciseCount +" minutes");

        buttonExerciseMinus.setOnClickListener(v -> {

            txtExerciseCount.setText("");
            if (exerciseCount>0){
                exerciseCount= exerciseCount-15;
            }
            txtExerciseCount.setText(exerciseCount +" minutes");
        });

        buttonExercisePlus.setOnClickListener(v -> {
            txtExerciseCount.setText("");
            exerciseCount=exerciseCount+15 ;
            txtExerciseCount.setText(exerciseCount +" minutes");
        });

    }
    private void performMeal(){

        CheckBox chkBreakfast=findViewById(R.id.chk_breakfast);
        CheckBox chkLunch=findViewById(R.id.chk_lunch);
        CheckBox chkDinner=findViewById(R.id.chk_dinner);

        if (!isBreakfast){
            chkBreakfast.setChecked(true);
        }
        if (!isLunch){
            chkLunch.setChecked(true);
        }
        if (!isDinner){
            chkDinner.setChecked(true);
        }


        chkBreakfast.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Checkbox is checked
            // Checkbox is unchecked
            isBreakfast= !isChecked;
        });

        chkLunch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Checkbox is checked
            // Checkbox is unchecked
            isLunch= !isChecked;
        });

        chkDinner.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Checkbox is checked
            // Checkbox is unchecked
            isDinner= !isChecked;
        });

    }

    private void performAddedFood(){


        foodNameBuffer1.setLength(0);
        foodNameBuffer2.setLength(0);
        foodQuantityBuffer1.setLength(0);
        foodQuantityBuffer2.setLength(0);
        foodIdBuffer.setLength(0);
        foodCatBuffer.setLength(0);
        foodImagesBuffer.setLength(0);


        RecyclerView recyclerView4 = findViewById(R.id.list_added_food_items);
        recyclerView4.setLayoutManager(new LinearLayoutManager(this));

        AddedFoodAdapter2 addedFoodAdapter;
        List<AddedFoodData> foodList;
        FoodDataBase database;

        // Initialize the FoodDataBase
        database = new FoodDataBase(getApplicationContext());

        foodList = database.getAllFood();



        // Create and set the adapter
        addedFoodAdapter = new AddedFoodAdapter2(foodList, getApplicationContext());
        recyclerView4.setAdapter(addedFoodAdapter);


        LinearLayout emptyListMessage =findViewById(R.id.empty_list_message);
        int dataCount = database.getDataCount();





        for (AddedFoodData foodData : foodList) {


            String foodName = foodData.getFoodName();
            String foodQuantity = foodData.getFoodQuantity();
            String cat = foodData.getFoodCategory();
            int id = foodData.getId();
            String images = foodData.getImageLink1();

            foodItems.put(foodName, foodQuantity);

            foodNameList.put(foodName, foodName);

            foodQuantitiesList.put(foodName, foodQuantity);
            foodCatList.put(foodName, cat);
            foodIdList.put(foodName, String.valueOf(id));
            foodImageList.put(foodName, images);
        }

        int index=0;
        for (Map.Entry<String, String> entry : foodItems.entrySet()) {
            String foodName = entry.getKey().trim();
            String quantity = entry.getValue().trim();
            foodNameBuffer1.append("food_name_").append(index).append("=").append(foodName).append("&");
            foodNameBuffer2.append("food_name_").append(index).append("=").append(foodName).append("!");
            foodQuantityBuffer1.append("food_quantity_").append(index).append("=").append(quantity).append("&");
            foodQuantityBuffer2.append("food_quantity_").append(index).append("=").append(quantity).append("!");
            index++;
        }

        foodCount = foodItems.size();


        int index2 =0;
        for (Map.Entry<String, String> entry : foodIdList.entrySet()) {
            String id = entry.getValue().trim();
            foodIdBuffer.append("food_id_").append(index2).append("=").append(id).append("!");
            index2++;
        }

        int index3 =0;
        for (Map.Entry<String, String> entry : foodCatList.entrySet()) {
            String foodCat = entry.getValue().trim();
            foodCatBuffer.append("food_cat_").append(index3).append("=").append(foodCat).append("!");
            index3++;
        }

        int index4 =0;
        for (Map.Entry<String, String> entry : foodImageList.entrySet()) {
            String foodImage = entry.getValue().trim();
            foodImagesBuffer.append("food_image_").append(index4).append("=").append(foodImage).append("!");
            index4++;
        }




        if (dataCount == 0){
            emptyListMessage.setVisibility(View.VISIBLE);
            recyclerView4.setVisibility(View.GONE);
        }else{
            emptyListMessage.setVisibility(View.GONE);
            recyclerView4.setVisibility(View.VISIBLE);
        }


    }



    private void insertDailyRoutine(){


        String skipMeal = String.valueOf(isBreakfast) + "/" + String.valueOf(isLunch) +"/"+ String.valueOf(isDinner);


        String url = "https://humorstech.com/humors_app/app_final/insert_daily_routine_data.php?";
        url +=  "login_id="+loginId;
        url +=  "&profile_id="+profileId;
        url +=  "&water_consumed="+waterCount;
        url +=  "&cigarettes_unit="+cigarettesCount;
        url +=  "&alcohol_unit="+alcoholConsumption;
        url +=  "&exercise_minutes="+exerciseCount;
        url +=  "&sleep_hours="+sleepHours;
        url +=  "&food_intake="+foodCount;
        url +=  "&food_name="+String.valueOf(foodNameBuffer2);
        url +=  "&food_quantity="+String.valueOf(foodQuantityBuffer2);
        url +=  "&food_cat="+String.valueOf(foodCatBuffer);
        url +=  "&food_id="+String.valueOf(foodIdBuffer);
        url +=  "&food_image_link="+String.valueOf(foodImagesBuffer);
        url +=  "&skip_meal="+skipMeal;




        MyVolleyRequest myVolleyRequest = new MyVolleyRequest(this); // 'this' is your context

        myVolleyRequest.makeGetRequest(url, new MyVolleyRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {


                Intent intent = new Intent(getApplicationContext(), BeforeReading.class);
                startActivity(intent);


            }

            @Override
            public void onError(VolleyError error) {

                Intent intent = new Intent(getApplicationContext(), BeforeReading.class);
                startActivity(intent);
                finish();
                // Handle errors here
                if (error.networkResponse != null) {
                    Log.e(TAG, "Error Response Code: " + error.networkResponse.statusCode);
                }
                if (error.getMessage() != null) {
                    Log.e(TAG, "Error Message: " + error.getMessage());
                }
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




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Animatoo1.animateSlideRight(DailyRoutinForm.this);
    }
}