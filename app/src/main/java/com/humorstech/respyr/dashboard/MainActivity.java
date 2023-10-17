package com.humorstech.respyr.dashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.viewpagerdots.DotsIndicator;
import com.aries.ui.view.tab.CommonTabLayout;
import com.aries.ui.view.tab.listener.CustomTabEntity;
import com.aries.ui.view.tab.listener.OnTabSelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.Dialogs;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.HeroBannerAdapter;
import com.humorstech.respyr.HeroBannerImageModel;
import com.humorstech.respyr.R;
import com.humorstech.respyr.SmoothScrollSnapHelper;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.TabEntity;
import com.humorstech.respyr.UserProfileManager;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.daily_routine.ConfirmReading;
import com.humorstech.respyr.daily_routine.DailyRoutinForm;
import com.humorstech.respyr.notification.Main;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.profile.Profile;
import com.humorstech.respyr.results.TimeBasedMeal;
import com.humorstech.respyr.suggestion.BreakFastSuggestionDataModel;
import com.humorstech.respyr.suggestion.FoodSuggestionAdapterBasedOnTime;
import com.humorstech.respyr.suggestion.Suggestion;
import com.humorstech.respyr.trends.Trends;
import com.humorstech.respyr.trends.ViewScores;
import com.humorstech.respyr.trends.WebViewCharts;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.BottomBarUtils;
import com.humorstech.respyr.utills.DashboardData;
import com.humorstech.respyr.utills.LoginUtils;
import com.humorstech.respyr.utills.Stuffs;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MainActivity extends AppCompatActivity implements UserProfileManager.UserUpdateCallback {



    private String activeProfileId, activeLoginId, activeProfileGender;
    private TimeLineAdapter adapter;

    private Button openDatePickerButton;

    // Counter for back presses
    private int backPressCount = 0;


    ///////////////// XML References
    private TextView txtGrowth, txtGrowthMessage;
    private  ViewPager bannerViewPages;
    private DotsIndicator bannerIndicator;
    private LinearLayout llTakeReading, llAllContent;

    private LinearLayout llSuggestionLayout;
    private LinearLayout llMainLayout;



    private String DATA_DATE;
    private ImageView profileImage;


    Calendar selectedDate;

    @Override
    public void onUserProfileDataFetchSuccess() {

        
    }

    @Override
    public void onUserProfileDataFetchFailure() {

    }


    @Override
    public void onUserProfileDataFetchStarted() {
        showLoading();
    }

    @Override
    public void handleProfileException(String s) {
        dismissLoading();
        showRetryDialog(s);
    }


    @Override
    public void onUserProfileDataFetchCompleted() {
        dismissLoading();
        init();
    }
    
    private void showLoading(){
        llMainLayout.setVisibility(View.GONE);
        Dialogs.showCustomDialog(MainActivity.this);
    }
    private void dismissLoading(){
        llMainLayout.setVisibility(View.VISIBLE);
        Dialogs.dismissDialog();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarColor statusBarColor= new StatusBarColor(  MainActivity.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        initVars();

        performCheckListForTakeReading();

        setupDashboard();
        refreshProfileData();


    }

    private void init(){

        setCurrentDate();
        onClicks();

    }




    private void initVars(){
        profileImage = findViewById(R.id.img_toolbar_av);

        llMainLayout = findViewById(R.id.main_content);

        llTakeReading=findViewById(R.id.layout_take_reading);
        llAllContent=findViewById(R.id.layout_all_content);

        txtGrowth=findViewById(R.id.txt_growth);
        txtGrowthMessage=findViewById(R.id.txt_growth_in_text);
        openDatePickerButton = findViewById(R.id.button_open_date_picker);

        bannerViewPages = findViewById(R.id.view_pages_banners);
        bannerIndicator = findViewById(R.id.dots);
    }

    private void refreshProfileData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        activeLoginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);
        activeProfileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, null);
        activeProfileGender = sharedPreferences.getString(ActiveProfile.GENDER, null);
        if (activeLoginId == null || activeProfileId == null) {
            handleException("Something went wrong! Please login again");
        }else {
            UserProfileManager userProfileManager = new UserProfileManager(this, this);
            userProfileManager.fetchUpdateUserData(activeLoginId, activeProfileId);
            /// active profile Icon
            Methods.setToolBar(getApplicationContext(), activeProfileId, activeProfileGender,profileImage);
        }
    }


    private void setupDashboard(){



        //////////////// Main Dashboard Banner
        com.humorstech.respyr.dashboard.Stuffs.mainDashboardHeadingBanner(
                MainActivity.this,
                bannerViewPages,
                bannerIndicator);

        performFoodSuggestionBasedOnTime();


        whatWillHelpBanner();

        /// bottom navigation bar
        bottomNavigation();

    }




    private void setCurrentDate(){

        selectedDate = com.humorstech.respyr.dashboard.Stuffs.getCurrentDate();


        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());


        if (selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                selectedDate.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                String formattedDate = " Today, "+dateFormat.format(selectedDate.getTime());
                openDatePickerButton.setText(formattedDate);

        } else {
                String formattedDate = dateFormat.format(selectedDate.getTime());
                openDatePickerButton.setText(formattedDate);
        }



        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        DATA_DATE = dateFormat2.format(selectedDate.getTime());
        fetchToDaysData(DATA_DATE,1);

    }



    private void showDatePickerDialog() {
        int mYear, mMonth, mDay, mHour, mMinute;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                R.style.DatePickerDialogTheme, // Set the custom style here
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Get the selected date as a Calendar object
                        selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);

                        // Get the current date as a Calendar object
                        Calendar currentDate = Calendar.getInstance();

                        // Compare the selected date with the current date
                        if (selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                                selectedDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                                selectedDate.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                            // If the selected date is the current date, set "Today" as the text
                            String formattedDate = dateFormat.format(selectedDate.getTime());

                            openDatePickerButton.setText("Today, " + formattedDate);
                        } else {
                            // Format and set the selected date as the text for openDatePickerButton
                            String formattedDate = dateFormat.format(selectedDate.getTime());
                            openDatePickerButton.setText(formattedDate);
                        }


                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                        DATA_DATE = dateFormat2.format(selectedDate.getTime());

                        fetchToDaysData(DATA_DATE,2);
                    }
                },
                mYear,
                mMonth,
                mDay
        );
        // Disable future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


        datePickerDialog.show();

    }




    private List<TimelineItem> parseJsonData(String jsonData) {
        List<TimelineItem> dataList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = jsonObject.getInt("id");
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");
                String overallHealthScore = jsonObject.getString("overall_health_score");
                String finalDiabeticScore = jsonObject.getString("final_diabetic_score");
                String finalVitalScore = jsonObject.getString("final_vital_score");
                String finalRespiratoryScore = jsonObject.getString("final_respiratory_score");
                String finalActivityScore = jsonObject.getString("final_activity_score");
                String finalNutritionScore = jsonObject.getString("final_nutrition_score");
                String finalLiverScore = jsonObject.getString("final_liver_score");

                TimelineItem data = new TimelineItem(id,date, time, overallHealthScore, finalDiabeticScore, finalVitalScore, finalRespiratoryScore, finalActivityScore, finalNutritionScore, finalLiverScore);
                dataList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }


    private void performCheckListForTakeReading(){
        RecyclerView recyclerView = findViewById(R.id.checklist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        // Create ChecklistItems from your JSON data
        List<CheckListData> checklistItems = new ArrayList<>();
        checklistItems.add(new CheckListData(Stuffs.checkListNamesForDashboard[0], 1, R.drawable.chk1));
        checklistItems.add(new CheckListData(Stuffs.checkListNamesForDashboard[1], 2,  R.drawable.chk1));
        checklistItems.add(new CheckListData(Stuffs.checkListNamesForDashboard[2], 3,  R.drawable.chk3));
        checklistItems.add(new CheckListData(Stuffs.checkListNamesForDashboard[3], 4,  R.drawable.chk4));
        checklistItems.add(new CheckListData(Stuffs.checkListNamesForDashboard[4], 5,  R.drawable.chk5));
        checklistItems.add(new CheckListData(Stuffs.checkListNamesForDashboard[5], 6,  R.drawable.chk6));


        CheckListAdapter adapter = new CheckListAdapter(checklistItems,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.indicator);
        recyclerIndicator.attachToRecyclerView(recyclerView);

        // Attach the custom SnapHelper
        SmoothScrollSnapHelper snapHelper = new SmoothScrollSnapHelper(adapter);
        snapHelper.attachToRecyclerView(recyclerView);


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
                com.humorstech.respyr.dashboard.Stuffs.performLogout( MainActivity.this);
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void showRetryDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Data Fetching failed");
        builder.setMessage(errorMessage);
        // Set a positive button with a click listener
        builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                refreshProfileData();
            }
        });
        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }











    private void onClicks() {
        RelativeLayout buttonProfile=findViewById(R.id.button_profile);buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });
        RelativeLayout buttonNotification=findViewById(R.id.button_notification);
        buttonNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
            Animatoo1.animateSlideLeft(MainActivity.this);
        });

        Button buttonStartTest1=findViewById(R.id.button_start_test_1);
        buttonStartTest1.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ConfirmReading.class);
            startActivity(intent);
            finish();
            Animatoo1.animateSlideLeft(MainActivity.this);
        });


        openDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        LinearLayout llCardDiabetic =findViewById(R.id.ll_card_diabetic);
        llCardDiabetic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewScores.class);
                intent.putExtra("dataKey", "final_diabetic_score");
                intent.putExtra("date", DATA_DATE);
                startActivity(intent);
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });

        LinearLayout llCardBlow=findViewById(R.id.ll_card_blow);
        llCardBlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewScores.class);
                intent.putExtra("dataKey", "final_respiratory_score");
                intent.putExtra("date", DATA_DATE);
                startActivity(intent);
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });


        LinearLayout llCardVital=findViewById(R.id.ll_card_vital);
        llCardVital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewScores.class);
                intent.putExtra("dataKey", "final_vital_score");
                intent.putExtra("date", DATA_DATE);
                startActivity(intent);
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });


        LinearLayout llCardLiver=findViewById(R.id.ll_card_liver);
        llCardLiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewScores.class);
                intent.putExtra("dataKey", "final_liver_score");
                intent.putExtra("date", DATA_DATE);
                startActivity(intent);
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });


        LinearLayout llCardActivity=findViewById(R.id.ll_card_activity);
        llCardActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewScores.class);
                intent.putExtra("dataKey", "final_activity_score");
                intent.putExtra("date", DATA_DATE);
                startActivity(intent);
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });



        LinearLayout llCardNutrition=findViewById(R.id.ll_card_nutrition);
        llCardNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewScores.class);
                intent.putExtra("dataKey", "final_nutrition_score");
                intent.putExtra("date", DATA_DATE);
                startActivity(intent);
                Animatoo1.animateSlideLeft(MainActivity.this);
            }
        });

    }



    private void bottomNavigation(){

        BottomBarUtils bottomBarUtils = new BottomBarUtils();
        int[] mIconSelectIds = bottomBarUtils.bottomSelectedIcons;
        int[] mIconUnSelectIds = bottomBarUtils.bottomUnSelectedIcons;
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        String[] mTitles = bottomBarUtils.bottomTitles;

        CommonTabLayout tableLayoutHome;
        tableLayoutHome = findViewById(R.id.table_layout_home);
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        tableLayoutHome.setTabData(mTabEntities);
        tableLayoutHome.setCurrentTab(0);

        tableLayoutHome.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {


                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, ConfirmReading.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, Trends.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }



    @SuppressLint("SetTextI18n")
    private void performFoodSuggestionBasedOnTime(){

        llSuggestionLayout =findViewById(R.id.suggestion_layout);

        TextView txtMealTime = findViewById(R.id.txt_meal_time);
        TextView txtTime1 = findViewById(R.id.txt_time_1);
        TextView txtTime2 = findViewById(R.id.txt_time_2);
        TextView txtTime3 = findViewById(R.id.txt_time_3);

        RecyclerView recyclerView =findViewById(R.id.food_suggestion_list);
        ScrollingPagerIndicator indicator =findViewById(R.id.indicator);

        SharedPreferences preferences = getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
        String breakfastSuggestions = preferences.getString(ActiveResultData.BREAKFAST_SUGGESTION, null);
        String lunchSuggestions = preferences.getString(ActiveResultData.LUNCH_SUGGESTION, null);
        String dinnerSuggestions = preferences.getString(ActiveResultData.DINNER_SUGGESTION, null);




        assert txtTime1 != null;
        txtTime1.setText(TimeBasedMeal.getFormattedTime());
        assert txtTime2 != null;
        txtTime2.setText(TimeBasedMeal.getFormattedTime());
        assert txtTime3 != null;
        txtTime3.setText(TimeBasedMeal.getFormattedTime());


        switch (TimeBasedMeal.getMealTime()){
            case  "breakfast" : txtMealTime.setText("It's breakfast time");
                setSuggestionList(recyclerView , breakfastSuggestions, indicator);
                break;
            case  "lunch" : txtMealTime.setText("It's lunch time");
                setSuggestionList(recyclerView , lunchSuggestions, indicator);
                break;
            case  "snack" : txtMealTime.setText("It's snack time");
                setSuggestionList(recyclerView , breakfastSuggestions, indicator);
                break;
            case  "dinner" : txtMealTime.setText("It's dinner time");
                setSuggestionList(recyclerView , dinnerSuggestions, indicator);break;

        }


        Button buttonViewSuggestion =findViewById(R.id.button_view_all_suggestion);
        assert buttonViewSuggestion != null;
        buttonViewSuggestion.setOnClickListener(v -> {
            try{
                Intent intent = new Intent(getApplicationContext(), Suggestion.class);
                startActivity(intent);
            }catch (Exception e){
                llSuggestionLayout.setVisibility(View.GONE);
                e.printStackTrace();
            }
        });

    }
    private void setSuggestionList(RecyclerView recyclerView, String data, ScrollingPagerIndicator recyclerIndicator) {
        try {
            List<BreakFastSuggestionDataModel> foodItems = new ArrayList<>();
            String[] lines = data.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String[] parts = lines[i].split(": ");
                if (parts.length != 2) {
                    // Handle data format error
                    continue; // Skip this line and move to the next one
                }

                String name = parts[0];
                String[] values = parts[1].split(", ");
                if (values.length != 5) {
                    // Handle values format error
                    continue; // Skip this line and move to the next one
                }

                double calories = Double.parseDouble(values[0].replaceAll("[^0-9]", ""));
                double carbohydrates = Double.parseDouble(values[1].replaceAll("[^0-9]", ""));
                double protein = Double.parseDouble(values[2].replaceAll("[^0-9]", ""));
                double fat = Double.parseDouble(values[3].replaceAll("[^0-9.]", ""));
                double fiber = Double.parseDouble(values[4].replaceAll("[^0-9.]", ""));

                BreakFastSuggestionDataModel foodItem = new BreakFastSuggestionDataModel(name, calories, carbohydrates, protein, fat, fiber);
                foodItems.add(foodItem);
            }

            FoodSuggestionAdapterBasedOnTime adapter = new FoodSuggestionAdapterBasedOnTime(foodItems, MainActivity.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);

            // Set dots
            recyclerIndicator.attachToRecyclerView(recyclerView);

            // Attach the custom SnapHelper
            SmoothScrollSnapHelper snapHelper = new SmoothScrollSnapHelper(adapter);
            snapHelper.attachToRecyclerView(recyclerView);

        } catch (NumberFormatException e) {
            // Handle number parsing error
            llSuggestionLayout.setVisibility(View.GONE);
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions
            llSuggestionLayout.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    private void performTakeReadingLayout(){
        llTakeReading.setVisibility(View.VISIBLE);
        llAllContent.setVisibility(View.GONE);

    }

    private void performDashboardContentLayout(){
        llAllContent.setVisibility(View.VISIBLE);
        llTakeReading.setVisibility(View.GONE);
        fetchCurrentDayTrendData(DATA_DATE);
    }




    ///////////////////////////////////// FETCH DATA ///////////////////////////////////////////////
    //////////////////////////////////// DASHBOARD DATA ////////////////////////////////////////////
    private void fetchToDaysData(String date, int dataType){
        try {
            RequestParams params = new RequestParams();
            params.put("login_id",activeLoginId);
            params.put("profile_id",activeProfileId);
            params.put("date",date);


            AsyncHttpClient client = new AsyncHttpClient();
            client.get( HTTP_URLS.GET_DASHBOARD_DATA_BY_DATE,params,

                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            String response = new String(responseBody).trim();

                            System.out.println("response---------->"+response);

                            if (response.equals("0")){
                               if (dataType==1){
                                   performTakeReadingLayout();
                               }else if(dataType==2){
                                   Toast.makeText(MainActivity.this, "0 records founds", Toast.LENGTH_SHORT).show();
                                   setCurrentDate();
                               }
                            }else{
                                performDashboardContentLayout();
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

                        }

                    });
        }catch (Exception e){
        }
    }

    /////////////////////////////////////////////////////// TREND DATA /////////////////////////////////////////////////////////////////

    private void fetchCurrentDayTrendData(String date){
        try {
            RequestParams params = new RequestParams();
            params.put("login_id",activeLoginId);
            params.put("profile_id",activeProfileId);
            params.put("date",date);


            AsyncHttpClient client = new AsyncHttpClient();
            client.get(HTTP_URLS.GET_DASHBOARD_TREND_DATA_BY_DATE,params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String response = new String(responseBody).trim();
                            //// generate chart
                            performMainChart(response);
                            performAllScores(response);
                            performTestTimeLine(response);

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


                        }

                    });
        }catch (Exception e){

        }
    }


    private void performAllScores(String jsonData){


        try {
            // Parse the JSON data into a JSONArray
            JSONArray jsonArray = new JSONArray(jsonData);

            // Initialize variables to store the sum of scores and the count of entries
            double healthScoreSum = 0;
            double diabeticScoreSum = 0;
            double vitalScoreSum = 0;
            double nutrientScoreSum = 0;
            double respiratoryScoreSum = 0;
            double activityScoreSum = 0;
            double liverScoreSum = 0;
            int entryCount = jsonArray.length();

            double previousHealthScore=0;
            double latestHealthScore=0;

            // Iterate through the JSON array
            for (int i = 0; i < entryCount; i++) {
                JSONObject entry = jsonArray.getJSONObject(i);
                // Extract scores from each entry
                double healthScore = Double.parseDouble(entry.getString("overall_health_score"));
                double diabeticScore = Double.parseDouble(entry.getString("final_diabetic_score"));
                double vitalScore = Double.parseDouble(entry.getString("final_vital_score"));
                double respiratoryScore = Double.parseDouble(entry.getString("final_respiratory_score"));
                double activityScore = Double.parseDouble(entry.getString("final_activity_score"));
                double nutrientScore = Double.parseDouble(entry.getString("final_nutrition_score"));
                double liverScore = Double.parseDouble(entry.getString("final_liver_score"));

                // Add the scores to their respective sums
                healthScoreSum += healthScore;
                diabeticScoreSum += diabeticScore;
                vitalScoreSum += vitalScore;
                nutrientScoreSum += nutrientScore;
                respiratoryScoreSum += respiratoryScore;
                activityScoreSum += activityScore;
                liverScoreSum += liverScore;


                if (entryCount>1){
                    if (i == entryCount-1){
                        latestHealthScore = Double.parseDouble(entry.getString("overall_health_score"));
                    }
                    if (i == entryCount-2){
                        previousHealthScore = Double.parseDouble(entry.getString("overall_health_score"));
                    }
                }


            }

            // Calculate the average scores
            double averageHealthScore = healthScoreSum / entryCount;
            double averageDiabeticScore = diabeticScoreSum / entryCount;
            double averageVitalScore = vitalScoreSum / entryCount;
            double averageRespiratoryScore = respiratoryScoreSum / entryCount;
            double averageActivityScore = activityScoreSum / entryCount;
            double averageNutrientScore = nutrientScoreSum / entryCount;
            double averageLiverScore = liverScoreSum / entryCount;


            com.humorstech.respyr.dashboard.Stuffs.performGrowth(
                    (float)previousHealthScore,
                    (float)latestHealthScore,
                    (float)averageHealthScore,
                    txtGrowth,
                    txtGrowthMessage);


            // Print the average scores
            System.out.println("Average Health Score: " + averageHealthScore);
            System.out.println("Average Diabetic Score: " + averageDiabeticScore);
            System.out.println("Average Vital Score: " + averageVitalScore);
            System.out.println("Average Nutrient Score: " + averageNutrientScore);
            setScore(1,averageDiabeticScore);
            setScore(2,averageVitalScore);
            setScore(3,averageRespiratoryScore);
            setScore(4,averageLiverScore);
            setScore(5,averageActivityScore);
            setScore(6,averageNutrientScore);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setScore(int i , double svgScore){

        TextView txtDiabeticScoreAvg =findViewById(R.id.txt_diabetic_score_avg);
        TextView txtDiabeticScoreStatus =findViewById(R.id.txt_diabetic_score_status);

        TextView txtVitalScoreAvg =findViewById(R.id.txt_vital_score_avg);
        TextView txtVitalScoreStatus =findViewById(R.id.txt_vital_score_status);

        TextView txtRespiratoryScoreAvg =findViewById(R.id.txt_respiratory_score_avg);
        TextView txtRespiratoryScoreStatus =findViewById(R.id.txt_respiratory_score_status);

        TextView txtActivityScoreAvg =findViewById(R.id.txt_activity_score_avg);
        TextView txtActivityScoreStatus =findViewById(R.id.txt_activity_score_status);


        TextView txtNutritionScoreAvg =findViewById(R.id.txt_nutrition_score_avg);
        TextView txtNutritionScoreStatus =findViewById(R.id.txt_nutrition_score_status);

        TextView txtLiverScoreAvg =findViewById(R.id.txt_liver_score_avg);
        TextView txtLiverScoreStatus =findViewById(R.id.txt_liver_score_status);

        switch (i){
            case 1 : roundOffScore(svgScore, txtDiabeticScoreAvg, txtDiabeticScoreStatus);break;
            case 2 : roundOffScore(svgScore, txtVitalScoreAvg, txtVitalScoreStatus);break;
            case 3 : roundOffScore(svgScore, txtRespiratoryScoreAvg, txtRespiratoryScoreStatus);break;
            case 4 : roundOffScore(svgScore, txtLiverScoreAvg, txtLiverScoreStatus);break;
            case 5 : roundOffScore(svgScore, txtActivityScoreAvg, txtActivityScoreStatus);break;
            case 6 : roundOffScore(svgScore, txtNutritionScoreAvg, txtNutritionScoreStatus);break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void roundOffScore(double score, TextView txtScore, TextView txtScoreStatus){
        double roundedValue = Math.round(score);

        if (score - Math.floor(score) >= 0.5) {
            roundedValue = Math.ceil(score);
        }

        int scoreNew = (int) roundedValue;

        txtScore.setText(scoreNew + "%");

        if (score <= 100 && score >= 80) {
            txtScoreStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
            txtScoreStatus.setText("Good");
        } else if (score >= 71  && score < 80) {
            txtScoreStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));
            txtScoreStatus.setText("Fair");
        } else if (score >= 0 && score <= 70) {
            txtScoreStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            txtScoreStatus.setText("Poor");
        }
    }




    @SuppressLint("SetJavaScriptEnabled")
    private void performMainChart(String jsonData){
        // Find the WebView by ID
        WebView webView = findViewById(R.id.chartWebView);

        // Configure the WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation within the WebView
        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebView Console", consoleMessage.message());
                return true;
            }
        });


        // Create and load the Highcharts chart in the WebView
        WebViewCharts.generateHealthScoreChartJs(jsonData,webView,"overall_health_score");

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void whatWillHelpBanner(){
        RecyclerView bannerListRecyclerView = findViewById(R.id.what_will_help_list);
        bannerListRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));



        // Create a list of ImageModel objects, providing image resources or file paths

        List<BannerModel> imageList = new ArrayList<>();
        imageList.add(new BannerModel(R.drawable.bannercard1));
        imageList.add(new BannerModel(R.drawable.bannercard2));
        imageList.add(new BannerModel(R.drawable.bannercard3));
        imageList.add(new BannerModel(R.drawable.bannercard4));
        imageList.add(new BannerModel(R.drawable.bannercard5));
        imageList.add(new BannerModel(R.drawable.bannercard6));

        BannerAdapter bannerAdapter = new BannerAdapter(imageList);
        bannerListRecyclerView.setAdapter(bannerAdapter);



        //Attach the custom SnapHelper
        //SmoothScrollSnapHelper snapHelper = new SmoothScrollSnapHelper(adapter);
        //snapHelper.attachToRecyclerView(bannerListRecyclerView);
        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.what_will_help_indicator);
        recyclerIndicator.attachToRecyclerView(bannerListRecyclerView);

        // Set up the SwipeRefreshLayout
        // Handle the refresh action here (e.g., fetch new data)

    }

    private void performTestTimeLine(String jsonData){

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load JSON data (replace with your JSON data loading mechanism)
        // String jsonData = parseJsonData(jsonData1); // Implement your data loading logic

        // Parse JSON data into a list of HealthData objects
        List<TimelineItem> healthDataList = parseJsonData(jsonData);

        // Initialize and set the adapter
        adapter = new TimeLineAdapter(healthDataList, MainActivity.this, "overall_health_score");
        recyclerView.setAdapter(adapter);
    }









    @Override
    public void onBackPressed() {
        if (backPressCount == 1) {
            finish();
            Animatoo1.animateSlideRight(MainActivity.this);
        } else {
            // Show a toast message indicating the need to press again to exit
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressCount++;
        }
    }
}