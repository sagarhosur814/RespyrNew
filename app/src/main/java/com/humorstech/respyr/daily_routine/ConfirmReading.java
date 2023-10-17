package com.humorstech.respyr.daily_routine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.daily_routine.added.AddedFoodAdapter2;
import com.humorstech.respyr.daily_routine.added.AddedFoodData;
import com.humorstech.respyr.daily_routine.search.FoodDataBase;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.reading.BeforeReading;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmReading extends AppCompatActivity {

    private String activeProfileId, activeProfileLoginId;
    private String name,gender,age,height,weight, exercise, foodType;

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


    private final StringBuilder foodNameBuffer1 = new StringBuilder();
    private final StringBuilder foodNameBuffer2 = new StringBuilder();
    private final StringBuilder foodQuantityBuffer1 = new StringBuilder();
    private final StringBuilder foodQuantityBuffer2 = new StringBuilder();


    private final StringBuilder foodIdBuffer = new StringBuilder();
    private final StringBuilder foodCatBuffer = new StringBuilder();
    private final StringBuilder foodImagesBuffer = new StringBuilder();


    private int foodCount;

    private TextView txtLog;

    private Button buttonTrackLater, buttonContinue;




    private boolean isNewReading = true;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reading);
        StatusBarColor statusBarColor= new StatusBarColor(  ConfirmReading.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        // clear existing data
        FoodDataBase dataBase=new FoodDataBase(getApplicationContext());
        dataBase.clearTable();

        init();
    }
    private void init(){
        initVars();
        getActiveProfileData();
        onClicks();
    }

    private void onClicks(){
        buttonTrackLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToBloodPressure();
            }
        });
    }
    private void getActiveProfileData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        activeProfileLoginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);
        activeProfileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID,null );
        name = sharedPreferences.getString(ActiveProfile.NAME,null );
        age = sharedPreferences.getString(ActiveProfile.AGE,null );
        gender = sharedPreferences.getString(ActiveProfile.GENDER,null );
        height = sharedPreferences.getString(ActiveProfile.HEIGHT,null );
        weight = sharedPreferences.getString(ActiveProfile.WEIGHT,null );
        exercise = sharedPreferences.getString(ActiveProfile.EXERCISE,null );
        foodType = sharedPreferences.getString(ActiveProfile.NOV_VEG,null );


        System.out.println("Active Profile Login ID: " + activeProfileLoginId);
        System.out.println("Active Profile ID: " + activeProfileId);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Height: " + height);
        System.out.println("Weight: " + weight);
        System.out.println("Exercise: " + exercise);
        System.out.println("Food Type: " + foodType);

        fetchDailyRoutineData(activeProfileLoginId, activeProfileId);

    }

    private void initVars(){
        buttonTrackLater=findViewById(R.id.button_track_later);
        buttonContinue=findViewById(R.id.button_continue);
        txtLog=findViewById(R.id.txt_log);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Animatoo1.animateSlideRight(ConfirmReading.this);
    }



    private void fetchDailyRoutineData(String loginId, String profileId) {
        String url = "https://humorstech.com/humors_app/app_final/fetch_daily_routine_data.php";

        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                if (responseBody != null) {
                    String response = new String(responseBody).trim();
                    handleResponse(response);
                } else {
                    handleException("Empty response");
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                dismissLoadingProgress();
                handleException("Failed to fetch data. Error: " + error.getMessage());
            }

            @Override
            public void onRetry(int retryNo) {
                // Request is retried
                super.onRetry(retryNo);
            }

            @Override
            public void onCancel() {
                // Request is canceled
                super.onCancel();
            }
        });
    }

    private void handleResponse(String response) {
        if (response.equals("0")) {
            isNewReading = true;
        } else {
            isNewReading = false;
            decodeJson(response);
        }

    }


    private void handleException(String s) {
        showErrorDialog("An error occurred: " + s);
    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        // Set a positive button with a click listener
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               //fetchDailyRoutineData();
            }
        });
        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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



            txtLog.append("waterCount :" + waterCount + "\n");
            txtLog.append("cigarettesCount :" + cigarettesCount + "\n");
            txtLog.append("exerciseCount :" + exerciseCount + "\n");
            txtLog.append("alcoholConsumption :" + alcoholConsumption + "\n");
            txtLog.append("sleepHours :" + sleepHours + "\n");
            txtLog.append("foodCount :" + foodCount + "\n");
            txtLog.append("foodName :" + foodName + "\n");
            txtLog.append("foodQuantity :" + foodQuantity + "\n");
            txtLog.append("skipMeal :" + skipMeal + "\n");


            // Now you can use these values as needed.
            displayOldList(foodName,foodQuantity, foodId,foodCat,foodImageLink );
            skipMeal(skipMeal);
            dismissLoadingProgress();
            moveToBloodPressure();



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



        } else {
            // Handle the case where the split operation failed
            // This could be due to a null inputString or another exception
            // For example, display an error message to the user

            isBreakfast = true;
            isLunch = true;
            isDinner = true;

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

                insertDataIntoSQLite(id, name,String.valueOf(quantity),category,image );
                // display added foods
                performAddedFood();

            }
        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertDataIntoSQLite(int id, String foodName, String foodAmount, String foodCategory, String imageLink) {
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
                "Breakfast");

        if (isInserted) {
            return true;
        } else {
            return false;
        }
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

    private void moveToBloodPressure(){




        SharedPreferences sharedPreferences = getSharedPreferences(ReadingParameters.TITLE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ReadingParameters.NAME, name);
        editor.putString(ReadingParameters.GENDER, gender);
        editor.putString(ReadingParameters.AGE, age);
        editor.putString(ReadingParameters.WEIGHT, weight);
        editor.putString(ReadingParameters.HEIGHT, height);
        editor.putString(ReadingParameters.LOGIN_ID, activeProfileLoginId);
        editor.putString(ReadingParameters.PROFILE_ID, activeProfileId);
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

        if (isNewReading){
            Intent intent = new Intent(getApplicationContext(), DailyRoutinForm.class);
            startActivity(intent);
            Animatoo1.animateSlideLeft(ConfirmReading.this);
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(), BeforeReading.class);
            startActivity(intent);
            Animatoo1.animateSlideLeft(ConfirmReading.this);
            finish();
        }


    }


    private void showLoading(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching your daily routine data...");
        progressDialog.show();
    }


    private void dismissLoadingProgress(){
        progressDialog.dismiss();
    }



}