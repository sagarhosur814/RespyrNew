package com.humorstech.respyr.suggestion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.SmoothScrollSnapHelper;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.utills.ReadingParameters;
import com.yangp.ypwaveview.YPWaveView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class Suggestion extends AppCompatActivity {

    private TextView txtWaterConsumptionSuggestion;


    private String profileName;
    private TextView txtWorkoutTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        StatusBarColor statusBarColor= new StatusBarColor(Suggestion.this);
        statusBarColor.setDarkColor(getResources().getColor(R.color.result_primary));

        init();
    }
    private void init(){
        initVars();
        onClick();
        getAllData();
    }

    private void initVars(){
        txtWaterConsumptionSuggestion=findViewById(R.id.txt_water_consumption_suggestion);
    }
    private void onClick(){
        ImageButton buttonBack=findViewById(R.id.img_button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void getAllData(){
        SharedPreferences preferences = getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
        String breakfastSuggestions = preferences.getString(ActiveResultData.BREAKFAST_SUGGESTION, null);
        String dinnerSuggestions = preferences.getString(ActiveResultData.DINNER_SUGGESTION, null);
        String lunchSuggestions = preferences.getString(ActiveResultData.LUNCH_SUGGESTION, null);
        String workoutSuggestion = preferences.getString(ActiveResultData.WORKOUT_SUGGESTIONS, null);
        profileName = preferences.getString(ActiveResultData.PROFILE_NAME, null);


        /// recyclerview lists
        RecyclerView recyclerView = findViewById(R.id.breakfast_suggestion_list);
        RecyclerView recyclerViewLunch = findViewById(R.id.lunch_suggestion_list);
        RecyclerView recyclerViewDinner = findViewById(R.id.dinner_suggestion_list);


        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.indicator);
        ScrollingPagerIndicator recyclerIndicator2 = findViewById(R.id.lunch_indicator);
        ScrollingPagerIndicator recyclerIndicator3 = findViewById(R.id.dinner_indicator);



        if (breakfastSuggestions!=null){
            setSuggestionList(recyclerView, breakfastSuggestions, recyclerIndicator);
        }
        if (dinnerSuggestions!=null){
            setSuggestionList(recyclerViewLunch, lunchSuggestions, recyclerIndicator2);
        }
        if (lunchSuggestions!=null){
            setSuggestionList(recyclerViewDinner, dinnerSuggestions, recyclerIndicator3);
        }




        /// set up water suggestion
        String lifestyleSuggestions = preferences.getString(ActiveResultData.LIFESTYLE_SUGGESTIONS, "null");
        decodeLifeStyleJson(lifestyleSuggestions);

        // setup workout
        setWorkoutSuggestionList(workoutSuggestion);

        System.out.println("Suggestions");
        System.out.println(breakfastSuggestions);
        System.out.println(lunchSuggestions);
        System.out.println(dinnerSuggestions);
        System.out.println(workoutSuggestion);

        System.out.println(lifestyleSuggestions);


    }

    private void setSuggestionList(RecyclerView recyclerView, String data, ScrollingPagerIndicator recyclerIndicator) {
        List<BreakFastSuggestionDataModel> foodItems = new ArrayList<>();

        // Split the data into lines
        String[] lines = data.split("\n");

        // Check if there is at least one line of data
        if (lines.length > 0) {
            // Process the first line (assuming it contains column headers)
            // If needed, you can add logic to handle headers here

            // Process the remaining lines (starting from index 1)
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];

                // Split the line into parts
                String[] parts = line.split(": ");

                // Check if there are at least two parts
                if (parts.length >= 2) {
                    String name = parts[0];
                    String[] values = parts[1].split(", ");
                    if (values.length == 5) { // Assuming there are 5 values
                        double calories = Double.parseDouble(values[0].replaceAll("[^0-9]", ""));
                        double carbohydrates = Double.parseDouble(values[1].replaceAll("[^0-9]", ""));
                        double protein = Double.parseDouble(values[2].replaceAll("[^0-9]", ""));
                        double fat = Double.parseDouble(values[3].replaceAll("[^0-9.]", ""));
                        double fiber = Double.parseDouble(values[4].replaceAll("[^0-9.]", ""));
                        BreakFastSuggestionDataModel foodItem = new BreakFastSuggestionDataModel(name, calories, carbohydrates, protein, fat, fiber);
                        foodItems.add(foodItem);
                    } else {

                    }
                } else {

                }
            }
        } else {
            // Handle the case where there are no lines of data
            handleException("Suggestion data is not in required format");
        }

        BreakFastSuggestionAdapter adapter = new BreakFastSuggestionAdapter(foodItems, Suggestion.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        // Set dots
        recyclerIndicator.attachToRecyclerView(recyclerView);

        // Attach the custom SnapHelper
        SmoothScrollSnapHelper snapHelper = new SmoothScrollSnapHelper(adapter);
        snapHelper.attachToRecyclerView(recyclerView);


    }


    private void setWorkoutSuggestionList(String jsonString1){
        RecyclerView recyclerView2 = findViewById(R.id.workout_suggestion_list);
        txtWorkoutTime =findViewById(R.id.txt_workout_time);


        ///////////////////// add { for start and } end
        List<WorkoutSuggestionDataModel> workoutList = parseJson(jsonString1);
        WorkoutAdapter adapter = new WorkoutAdapter(workoutList);
        recyclerView2.setAdapter(adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }



    private List<WorkoutSuggestionDataModel> parseJson(String jsonString) {
        List<WorkoutSuggestionDataModel> workoutList = new ArrayList<>();



        if (!jsonString.contains("[\"Maintain a balanced diet\",\"Do a 20-minute physical activity\"]")){
            try {
                // Parse the JSON array
                JSONArray jsonArray = new JSONArray(jsonString);

                // Iterate through the array elements and extract the data
                for (int i = 0; i < jsonArray.length(); i++) {
                    String exerciseData = jsonArray.getString(i);

                    // Extract exercise name and duration from the JSON string
                    String[] parts = exerciseData.split(": ");
                    String exercise = parts[0];
                    String duration = parts[1];

                    // Create a new WorkoutSuggestionDataModel and add it to the workoutList
                    workoutList.add(new WorkoutSuggestionDataModel(exercise, duration));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            workoutList.add(new WorkoutSuggestionDataModel("Maintain a balanced diet", "Do a 20-minute physical activity"));
        }



        return workoutList;
    }







    @SuppressLint("SetTextI18n")
    private void setWaterIntake(double currentWaterConsumptionInML, double extraRecommendedWaterInMl){

        double extraRecommendedWaterInGlasses = calculateWaterGlasses(250, extraRecommendedWaterInMl);;
        double extraRecommendedWaterInLiter = calculateWaterConsumption(250, extraRecommendedWaterInGlasses);;
        double totalWaterRequired = currentWaterConsumptionInML+extraRecommendedWaterInMl;
        double totalWaterRequiredInGlasses = calculateWaterGlasses(250, totalWaterRequired);
        double currentWaterConsumptionInGlasses = calculateWaterGlasses(250, currentWaterConsumptionInML);
        double currentWaterConsumptionInLiter= calculateWaterConsumption(250, currentWaterConsumptionInGlasses);



        double percentageWaterConsumption = (currentWaterConsumptionInGlasses*1000) / totalWaterRequiredInGlasses;

        YPWaveView ypWaveView =findViewById(R.id.progress_water_intake);
        ypWaveView.setProgress((int)percentageWaterConsumption);
        ypWaveView.setMax(1000);
        ypWaveView.setAnimationSpeed(100);
        ypWaveView.startAnimation();


        TextView txtWaterConsumption =findViewById(R.id.txt_water_intake_in_glass);
        TextView txtWaterConsumptionInLitre =findViewById(R.id.txt_water_intake);
        TextView txtRecWaterConsumption =findViewById(R.id.recommended_water_in_take);
        TextView txtRecWaterConsumptionInLitre =findViewById(R.id.recommended_water_in_take_2);


        txtWaterConsumption.setText(String.valueOf((int)currentWaterConsumptionInGlasses) + " glasses" );
        txtWaterConsumptionInLitre.setText("("+String.valueOf(currentWaterConsumptionInLiter) + " liter)" );

        txtRecWaterConsumption.setText("+"+String.valueOf((int)extraRecommendedWaterInGlasses) + " glasses" );
        txtRecWaterConsumptionInLitre.setText("("+String.valueOf(extraRecommendedWaterInLiter) + " liter)" );

    }

    public static double calculateWaterConsumption(double glassVolumeMl, double numberOfGlasses) {
        double totalWaterVolumeMl = glassVolumeMl * numberOfGlasses;
        double totalWaterVolumeLiters = totalWaterVolumeMl / 1000;
        return totalWaterVolumeLiters;
    }

    public static int calculateWaterGlasses(double glassVolumeMl, double consumedWaterMl) {
        return (int) (consumedWaterMl / glassVolumeMl);
    }
    private void decodeLifeStyleJson(String jsonString){

        try {
            ArrayList<String> extractedStrings = extractStrings(jsonString);
            txtWaterConsumptionSuggestion.setText(extractedStrings.get(0));



            /// profile name
            String[] profileNameFirstName = profileName.split(" ");

            String htmlText = profileNameFirstName[0]+" "+extractedStrings.get(0)+ " to maintain good lifestyle." ;
            Spanned spannedText = Html.fromHtml(htmlText);




            SharedPreferences preferences = getSharedPreferences(ReadingParameters.TITLE, MODE_PRIVATE);
            String waterIntakeStr = preferences.getString(ReadingParameters.WATER_INTAKE, null);
            double currentWaterIntake = Double.parseDouble(waterIntakeStr);
            double recommendedWaterConsumption = extractNumber(extractedStrings.get(0));

            System.out.println("Water Intake  : " + calculateWaterConsumption(250,currentWaterIntake));
            System.out.println("Water Intake  : " + recommendedWaterConsumption);


            setWaterIntake((250*currentWaterIntake), recommendedWaterConsumption);
            txtWaterConsumptionSuggestion.setText(spannedText);

        }catch (Exception e){
            handleException(e.getMessage());
            e.printStackTrace();
        }

    }




    public static ArrayList<String> extractStrings(String input) {
        ArrayList<String> result = new ArrayList<>();

        // Define a regular expression pattern to match the desired strings
        String regex = "\"(.*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // Find all matches and add them to the result list
        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }

    public static double extractNumber(String input) {
        // Define a regular expression pattern to match floating-point numbers
        String regex = "-?\\d+\\.\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // Find the first match and parse it as a double
        if (matcher.find()) {
            String matchedNumber = matcher.group();
            return Double.parseDouble(matchedNumber);
        }

        // Return a default value or throw an exception if no number is found
        return 0.0; // Change this to an appropriate default value or error handling
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
            startActivity(intent);
            finish();
            Animatoo1.animateSlideRight(Suggestion.this);
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


}