package com.humorstech.respyr.results;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.VolleyHelperJson;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.reading.CaloriesAnalysis;
import com.humorstech.respyr.reading.LifeStyleAnalysis;
import com.humorstech.respyr.reading.food_breakup.FoodBreakUP;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class NutritionScore extends AppCompatActivity {


    private BarChart barChart;

    private String responseJson;

    private  JSONObject jsonObject;

//    private String responseJson= "{\n" +
//            "    \"data\": [\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-17\",\n" +
//            "            \"calories\": 2000,\n" +
//            "            \"carbohydrate\": 250,\n" +
//            "            \"proteins\": 120,\n" +
//            "            \"fats\": 80,\n" +
//            "            \"fibre\": 30\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-18\",\n" +
//            "            \"calories\": 1800,\n" +
//            "            \"carbohydrate\": 220,\n" +
//            "            \"proteins\": 110,\n" +
//            "            \"fats\": 70,\n" +
//            "            \"fibre\": 25\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-19\",\n" +
//            "            \"calories\": 2200,\n" +
//            "            \"carbohydrate\": 280,\n" +
//            "            \"proteins\": 130,\n" +
//            "            \"fats\": 90,\n" +
//            "            \"fibre\": 35\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-20\",\n" +
//            "            \"calories\": 1900,\n" +
//            "            \"carbohydrate\": 240,\n" +
//            "            \"proteins\": 115,\n" +
//            "            \"fats\": 75,\n" +
//            "            \"fibre\": 28\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-21\",\n" +
//            "            \"calories\": 2100,\n" +
//            "            \"carbohydrate\": 260,\n" +
//            "            \"proteins\": 125,\n" +
//            "            \"fats\": 85,\n" +
//            "            \"fibre\": 32\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-22\",\n" +
//            "            \"calories\": 2300,\n" +
//            "            \"carbohydrate\": 300,\n" +
//            "            \"proteins\": 140,\n" +
//            "            \"fats\": 95,\n" +
//            "            \"fibre\": 40\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"date\": \"2023-08-23\",\n" +
//            "            \"calories\": 1750,\n" +
//            "            \"carbohydrate\": 210,\n" +
//            "            \"proteins\": 105,\n" +
//            "            \"fats\": 65,\n" +
//            "            \"fibre\": 20\n" +
//            "        }\n" +
//            "    ]\n" +
//            "}\n";

    private TextView txtTotalNutritionValue;
    private TextView txtAverageNutritionValue;
    private Button buttonViewTrend;
    private StatusBarColor statusBarColor;
    private boolean isDataFetched=false;

    private String name;

    SharedPreferences preferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_score);

        //////////////////////////////////////////////////////////////////////////////////
        statusBarColor= new StatusBarColor(NutritionScore.this);
        statusBarColor.setColor(getResources().getColor(R.color.light_secondary));
        preferences = getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
        //////////////////////////////////////////////////////////////////////////////////

        //********************************************//
        //                Nutrition Score             //
        //********************************************//


        fetchData();
        init();



    }
    private void init(){
        initVars();
        performSpinner();
        performNutritionScore();
        performCalories();
        onClicks();
    }
    private void initVars(){
        barChart = findViewById(R.id.barChart);
        txtTotalNutritionValue=findViewById(R.id.txt_total_nutri);
        txtAverageNutritionValue=findViewById(R.id.txt_avg_nutri);
        buttonViewTrend=findViewById(R.id.button_view_trend);
    }
    private void onClicks(){
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTrend();
            }
        });

        ImageButton buttonBack =findViewById(R.id.image_button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void viewTrend(){
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(NutritionScore.this,"nutritionscore",95.0);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    private void fetchData(){
        VolleyHelperJson.fetchJsonData(this, "https://humorstech.com/humors_app/app_final/trends/nutrition_trend.php?",
                new VolleyHelperJson.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response1) throws JSONException {
                        // Process the JSON data here

                        jsonObject = response1;
                        isDataFetched=true;
                        initializeChart("calories", jsonObject);
                        upDateNutritionValues("calories", jsonObject);

                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Handle the error message appropriately, e.g., show a toast, display a message, etc.

                        System.out.println(errorMessage);
                    }
                });
    }

    private void performNutritionScore(){
        TextView txtNutritionProfileName = findViewById(R.id.txt_nutrition_score_profile_name);
        TextView txtNutritionScore = findViewById(R.id.txt_nutrition_score);
        TextView txtNutritionMessage = findViewById(R.id.txt_nutrition_message);
        TextView txtNutritionSubMessage = findViewById(R.id.txt_nutrition_sub_message);
        ImageView imgNutritionStatus = findViewById(R.id.img_nutrition_warning);
        RoundedProgressBar nutritionProgress =findViewById(R.id.progress_nutrition_score);


        String profileName = preferences.getString(ActiveResultData.PROFILE_NAME, "Alex");
        String nutritionScoreStr = preferences.getString(ActiveResultData.NUTRITION_SCORE, "90.6");


        double nutritionScore = Double.parseDouble(nutritionScoreStr);

        LifeStyleAnalysis.setNutrition(getApplicationContext(),
                nutritionScore,
                profileName,
                nutritionProgress,
                txtNutritionScore,
                txtNutritionProfileName,
                txtNutritionSubMessage,
                txtNutritionMessage,
                imgNutritionStatus );

    }
    private void performSpinner(){
        String[] items = {"Calories", "Carbohydrate", "Proteins", "Fats", "Fibre"};
        Spinner spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.trend_drop_down, items);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set item selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDataFetched){
                    updateChartBasedOnSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        PowerSpinnerView powerSpinnerView =findViewById(R.id.power_spinner_view);
        powerSpinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                if (isDataFetched){
                    updateChartBasedOnSelection(newIndex);
                }
            }
        });
    }



    private void updateChartBasedOnSelection(int selectedItemPosition) {
        String selectedDataKey = "calories"; // Replace with the actual key for selected data

        switch (selectedItemPosition) {
            case 0:  selectedDataKey = "calories";break;
            case 1: selectedDataKey = "carbohydrate"; break;
            case 2: selectedDataKey = "proteins";break;
            case 3: selectedDataKey = "fats";break;
            case 4: selectedDataKey = "fibre";break;
        }

        initializeChart(selectedDataKey , jsonObject);
        upDateNutritionValues(selectedDataKey, jsonObject);
    }






    private void initializeChart(String dataKey, JSONObject jsonObject) {
        try {
            JSONArray dataArray = jsonObject.getJSONArray("data");
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>(); // Dates for X-axis labels
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM\nEEE", Locale.getDefault());

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dayData = dataArray.getJSONObject(i);
                float value = (float) dayData.optDouble(dataKey, 0.0);
                entries.add(new BarEntry(i + 1, value));

                // Change the date format
                String dateString = dayData.optString("date", "");
                if (!dateString.isEmpty()) {
                    try {
                        Date date = inputFormat.parse(dateString);
                        String formattedDate = outputFormat.format(date);
                        labels.add(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle parsing exception
                    }
                }
            }

            // Set X-axis labels
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value - 1; // Subtract 1 to match array indices
                    if (index >= 0 && index < labels.size()) {
                        return labels.get(index);
                    }
                    return "";
                }
            });
            // Set X-axis labels formatter
            barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(), barChart.getTransformer(YAxis.AxisDependency.LEFT)));


            BarDataSet barDataSet = new BarDataSet(entries, dataKey);
            BarData barData = new BarData(barDataSet);


            //////////////////////////////*****************************************************design//////////////////////////

            Typeface customFont = ResourcesCompat.getFont(this, R.font.roboto);


            // Hide the description
            Description description = new Description();
            description.setEnabled(false);
            barChart.setDescription(description);

            // Hide the legend
            Legend legend = barChart.getLegend();
            legend.setEnabled(false);


            // set bar color

            barDataSet.setColor(getResources().getColor(R.color.black));
            // set column width
            barData.setBarWidth(.70f);


            // column top values
            barData.setDrawValues(true);
            barData.setValueTextColor(getResources().getColor(R.color.black));
            barData.setValueTextSize(8f);
            barData.setValueTypeface(customFont);



            //////////////////////////X axis
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // Set X-axis position to bottom
            xAxis.setGranularity(1f);
            xAxis.setTextSize(8f);
            xAxis.setTextColor(getResources().getColor(R.color.black));


            barChart.setExtraOffsets(0, 0, 0, 30);


            // Hide the X-axis grid lines
            xAxis.setDrawGridLines(false);

            // font family
            xAxis.setTypeface(customFont);

            //////////////////////////Y axis
            YAxis leftYAxis = barChart.getAxisLeft();
            leftYAxis.setDrawAxisLine(false);   // Hide the Y-axis line
            leftYAxis.setDrawLabels(true);     // Show Y-axis labels

            leftYAxis.setAxisMinimum(barDataSet.getYMin());
            leftYAxis.setAxisMaximum(barDataSet.getYMax());

            // Customize Y-axis grid lines
            leftYAxis.enableGridDashedLine(10f, 10f, 0f);      // Dash length, space length, phase
            leftYAxis.setGridColor(getResources().getColor(R.color.black));             // Grid line color
            leftYAxis.setGridLineWidth(0.1f);                                          // Grid line thickness

            // font family
            leftYAxis.setTypeface(customFont);


            // Customize Y-axis label color, text size, and typeface
            leftYAxis.setTextColor(getResources().getColor(R.color.black));      // Label color
            leftYAxis.setTextSize(8f);




            YAxis rightYAxis = barChart.getAxisRight();
            rightYAxis.setEnabled(false);




            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            barChart.setData(barData);
            barChart.invalidate();


        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing exception
        }
    }



    @SuppressLint("SetTextI18n")
    private void upDateNutritionValues(String dataKey, JSONObject jsonObject){

        int totalValue = 0;

        String nutritionType = "g";
        if (Objects.equals(dataKey, "calories")){
            nutritionType="cal";
        }

        try {
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dayData = dataArray.getJSONObject(i);
                float value = (float) dayData.getDouble(dataKey);
                totalValue += value;
            }

            int averageValue = (int)totalValue / dataArray.length();

            txtAverageNutritionValue.setText(averageValue +" "+ nutritionType);
            txtTotalNutritionValue.setText(totalValue +" "+ nutritionType);



        }catch (Exception e){

        }
    }


    private void performCalories(){


        String curr_calStr = preferences.getString(ActiveResultData.ACTUAL_CALORIES, "715");
        String curr_carStr = preferences.getString(ActiveResultData.ACTUAL_CARBOHYDRATE, "715");
        String curr_proStr = preferences.getString(ActiveResultData.ACTUAL_PROTEIN, "715");
        String curr_fatStr = preferences.getString(ActiveResultData.ACTUAL_FATS, "715");
        String curr_fibStr = preferences.getString(ActiveResultData.ACTUAL_FIBER, "715");
        String reco_calStr = preferences.getString(ActiveResultData.RECOMMENDED_CALORIES, "715");
        String reco_carStr = preferences.getString(ActiveResultData.RECOMMENDED_CARBOHYDRATE, "715");
        String reco_proStr = preferences.getString(ActiveResultData.RECOMMENDED_PROTEIN, "715");
        String reco_fatStr = preferences.getString(ActiveResultData.RECOMMENDED_FATS, "715");
        String reco_fibStr = preferences.getString(ActiveResultData.RECOMMENDED_FIBER, "715");


        /// calories
        TextView txtCaloriesScore = findViewById(R.id.txt_calories_score);
        TextView txtActualCalories = findViewById(R.id.txt_calories_actual);
        TextView txtRecommendedCalories = findViewById(R.id.txt_calories_recomoneded);
        TextView txtCaloriesDifference = findViewById(R.id.txt_calories_difference);


        double curr_cal = Double.parseDouble(curr_calStr);
        double curr_car = Double.parseDouble(curr_carStr);
        double curr_pro = Double.parseDouble(curr_proStr);
        double curr_fat = Double.parseDouble(curr_fatStr);
        double curr_fib = Double.parseDouble(curr_fibStr);
        double reco_cal = Double.parseDouble(reco_calStr);
        double reco_car = Double.parseDouble(reco_carStr);
        double reco_pro = Double.parseDouble(reco_proStr);
        double reco_fat = Double.parseDouble(reco_fatStr);
        double reco_fib = Double.parseDouble(reco_fibStr);



        double actualCalories = curr_cal;
        double recommendedCalories  = reco_cal;
        double caloriesScore = (actualCalories / recommendedCalories) * 100;

        CaloriesAnalysis.performTexts(getApplicationContext(),
                caloriesScore,
                name,
                actualCalories,
                recommendedCalories,
                txtCaloriesScore,
                txtActualCalories,
                txtRecommendedCalories,
                txtCaloriesDifference,
                1
        );

        // carbo hydrates
        TextView txtCarboHydratesScore = findViewById(R.id.txt_carbo_hydrate_score);
        TextView txtActualCarboHydrates = findViewById(R.id.txt_carbo_hydrate_actual);
        TextView txtRecommendedCarboHydrates = findViewById(R.id.txt_carbo_hydrate_recommended);
        TextView txtCarboHydratesDifference = findViewById(R.id.txt_carbo_hydrate_difference);



        double actualCarboHydrates = curr_car;
        double recommendedCarboHydrates  = reco_car;
        double caloriesCarboHydrates = (actualCarboHydrates / recommendedCarboHydrates) * 100;

        CaloriesAnalysis.performTexts(getApplicationContext(),
                caloriesCarboHydrates,
                name,
                actualCarboHydrates,
                recommendedCarboHydrates,
                txtCarboHydratesScore,
                txtActualCarboHydrates,
                txtRecommendedCarboHydrates,
                txtCarboHydratesDifference,
                2
        );

        // protein
        TextView txtProteinScore = findViewById(R.id.txt_protein_score);
        TextView txtActualProtein = findViewById(R.id.txt_actual_protein);
        TextView txtRecommendedProtein = findViewById(R.id.txt_recommended_protein);
        TextView txtDifferenceProtein = findViewById(R.id.txt_difference_protein);


        double actualProtein = curr_pro;
        double recommendedProtein  = reco_pro;
        double scoreProtein = (actualProtein / recommendedProtein) * 100;

        CaloriesAnalysis.performTexts(getApplicationContext(),
                scoreProtein,
                name,
                actualProtein,
                recommendedProtein,
                txtProteinScore,
                txtActualProtein,
                txtRecommendedProtein,
                txtDifferenceProtein,
                3
        );

        // fats
        TextView txtFatsScore = findViewById(R.id.txt_fats_score);
        TextView txtActualFats = findViewById(R.id.txt_fats_actual);
        TextView txtRecommendedFats= findViewById(R.id.txt_fats_recommended);
        TextView txtDifferenceFats = findViewById(R.id.txt_fats_difference);


        double actualFats= curr_fat;
        double recommendedFats  = reco_fat;
        double scoreFats = (actualFats / recommendedFats) * 100;

        CaloriesAnalysis.performTexts(getApplicationContext(),
                scoreFats,
                name,
                actualFats,
                recommendedFats,
                txtFatsScore,
                txtActualFats,
                txtRecommendedFats,
                txtDifferenceFats,
                4
        );

        // fibre
        TextView txtFibreScore = findViewById(R.id.txt_fibre_score);
        TextView txtActualFibre = findViewById(R.id.txt_fibre_actual);
        TextView txtRecommendedFibre= findViewById(R.id.txt_fibre_recommended);
        TextView txtDifferenceFibre= findViewById(R.id.txt_fibre_difference);


        double actualFibre= curr_fib;
        double recommendedFibre = reco_fib;
        double scoreFibre = (actualFibre / recommendedFibre) * 100;

        CaloriesAnalysis.performTexts(getApplicationContext(),
                scoreFibre,
                name,
                actualFibre,
                recommendedFibre,
                txtFibreScore,
                txtActualFibre,
                txtRecommendedFibre,
                txtDifferenceFibre,
                5
        );



        /// perform progress
        //Buttons
        TextView btnCalories, btnCarbohydrate, btnProtein, btnFats, btnFiber;
        btnCalories=findViewById(R.id.button_calories);
        btnCarbohydrate=findViewById(R.id.button_carbohydrate);
        btnProtein=findViewById(R.id.button_proteins);
        btnFats=findViewById(R.id.button_fats);
        btnFiber=findViewById(R.id.button_fibre);

        // Layouts
        RelativeLayout layoutCalories =findViewById(R.id.layout_calories_card);
        RelativeLayout layoutCarboHydrate =findViewById(R.id.layout_carbohydrate_card);
        RelativeLayout layoutProtein=findViewById(R.id.layout_protein_card);
        RelativeLayout layoutFats=findViewById(R.id.layout_fats_card);
        RelativeLayout layoutFiber=findViewById(R.id.layout_fiber_card);


        // Circular Progress
        CircularProgressIndicator p1=findViewById(R.id.p1);
        CircularProgressIndicator p2=findViewById(R.id.p2);
        CircularProgressIndicator p3=findViewById(R.id.p3);
        CircularProgressIndicator p4=findViewById(R.id.p4);
        CircularProgressIndicator p5=findViewById(R.id.p5);
        CircularProgressIndicator p6=findViewById(R.id.p6);
        CircularProgressIndicator p7=findViewById(R.id.p7);
        CircularProgressIndicator p8=findViewById(R.id.p8);
        CircularProgressIndicator p9=findViewById(R.id.p9);
        CircularProgressIndicator p10=findViewById(R.id.p10);
        CircularProgressIndicator p11=findViewById(R.id.p11);
        CircularProgressIndicator p12=findViewById(R.id.p12);
        CircularProgressIndicator p13=findViewById(R.id.p13);
        CircularProgressIndicator p14=findViewById(R.id.p14);
        CircularProgressIndicator p15=findViewById(R.id.p15);
        CircularProgressIndicator p16=findViewById(R.id.p16);
        CircularProgressIndicator p17=findViewById(R.id.p17);
        CircularProgressIndicator p18=findViewById(R.id.p18);
        CircularProgressIndicator p19=findViewById(R.id.p19);
        CircularProgressIndicator p20=findViewById(R.id.p20);
        CircularProgressIndicator p21=findViewById(R.id.p21);
        CircularProgressIndicator p22=findViewById(R.id.p22);
        CircularProgressIndicator p23=findViewById(R.id.p23);
        CircularProgressIndicator p24=findViewById(R.id.p24);
        CircularProgressIndicator p25=findViewById(R.id.p25);


        // default
        CaloriesAnalysis.performButtons(getApplicationContext(),1,btnCalories,btnCarbohydrate,btnProtein,btnFats,btnFiber);
        CaloriesAnalysis.performLayouts(getApplicationContext(),1,layoutCalories,layoutCarboHydrate,layoutProtein,layoutFats,layoutFiber);


        CaloriesAnalysis.setProgress(getApplicationContext(),1,(int)caloriesScore,(int)caloriesCarboHydrates,(int)scoreProtein,(int)scoreFats,(int)scoreFibre,p1,p2,p3,p4,p5);

        btnCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CaloriesAnalysis.performButtons(getApplicationContext(),1,btnCalories,btnCarbohydrate,btnProtein,btnFats,btnFiber);
                CaloriesAnalysis.performLayouts(getApplicationContext(),1,layoutCalories,layoutCarboHydrate,layoutProtein,layoutFats,layoutFiber);
                CaloriesAnalysis.setProgress(getApplicationContext(),1,(int)caloriesScore,(int)caloriesCarboHydrates,(int)scoreProtein,(int)scoreFats,(int)scoreFibre,p1,p2,p3,p4,p5);

            }
        });
        btnCarbohydrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaloriesAnalysis.performButtons(getApplicationContext(),2,btnCalories,btnCarbohydrate,btnProtein,btnFats,btnFiber);
                CaloriesAnalysis.performLayouts(getApplicationContext(),2,layoutCalories,layoutCarboHydrate,layoutProtein,layoutFats,layoutFiber);
                CaloriesAnalysis.setProgress(getApplicationContext(),2,(int)caloriesScore,(int)caloriesCarboHydrates,(int)scoreProtein,(int)scoreFats,(int)scoreFibre,p6,p7,p8,p9,p10);

            }
        });
        btnProtein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaloriesAnalysis.performButtons(getApplicationContext(),3,btnCalories,btnCarbohydrate,btnProtein,btnFats,btnFiber);
                CaloriesAnalysis.performLayouts(getApplicationContext(),3,layoutCalories,layoutCarboHydrate,layoutProtein,layoutFats,layoutFiber);
                CaloriesAnalysis.setProgress(getApplicationContext(),3,(int)caloriesScore,(int)caloriesCarboHydrates,(int)scoreProtein,(int)scoreFats,(int)scoreFibre,p11,p12,p13,p14,p15);
            }
        });
        btnFats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaloriesAnalysis.performButtons(getApplicationContext(),4,btnCalories,btnCarbohydrate,btnProtein,btnFats,btnFiber);
                CaloriesAnalysis.performLayouts(getApplicationContext(),4,layoutCalories,layoutCarboHydrate,layoutProtein,layoutFats,layoutFiber);
                CaloriesAnalysis.setProgress(getApplicationContext(),4,(int)caloriesScore,(int)caloriesCarboHydrates,(int)scoreProtein,(int)scoreFats,(int)scoreFibre,p16,p17,p18,p19,p20);
            }
        });
        btnFiber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaloriesAnalysis.performButtons(getApplicationContext(),5,btnCalories,btnCarbohydrate,btnProtein,btnFats,btnFiber);
                CaloriesAnalysis.performLayouts(getApplicationContext(),5,layoutCalories,layoutCarboHydrate,layoutProtein,layoutFats,layoutFiber);
                CaloriesAnalysis.setProgress(getApplicationContext(),5,(int)caloriesScore,(int)caloriesCarboHydrates,(int)scoreProtein,(int)scoreFats,(int)scoreFibre,p21,p22,p23,p24,p25);
            }
        });

        Button buttonViewFoodBreakup=findViewById(R.id.button_view_food_breakup);
        buttonViewFoodBreakup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FoodBreakUP.class);
                startActivity(intent);
            }
        });

    }
}


class CustomXAxisRenderer extends XAxisRenderer {
    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y,
                             MPPointF anchor, float angleDegrees) {
        String line[] = formattedLabel.split("\n");
        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
        for (int i = 1; i < line.length; i++) { // we've already processed 1st line
            Utils.drawXAxisValue(c, line[i], x, y + mAxisLabelPaint.getTextSize() * i,
                    mAxisLabelPaint, anchor, angleDegrees);
        }
    }
}