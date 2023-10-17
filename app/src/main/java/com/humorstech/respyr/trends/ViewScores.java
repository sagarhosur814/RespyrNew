package com.humorstech.respyr.trends;

import static com.humorstech.respyr.trends.Methods.parseJsonData;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.Dialogs;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.UserProfileManager;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.trends.Methods;
import com.humorstech.respyr.dashboard.TimeLineAdapter;
import com.humorstech.respyr.dashboard.TimelineItem;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.DashboardData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ViewScores extends AppCompatActivity {

    private Calendar selectedDate;
    private String DATA_DATE;
    private String activeProfileId, activeLoginId;

    private Button openDatePickerButton;

    private String dataKey="overall_health_score";

    private LinearLayout llMainLayout;

    private TextView txtScoreName;


    private String  dateStr1;
    private String  dateStr2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scores);
        StatusBarColor statusBarColor= new StatusBarColor(  ViewScores.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
        initVars();

        getIntentData();

    }

    private void getIntentData(){
        Intent intent = getIntent();
        dataKey = intent.getStringExtra("dataKey");
        dateStr1 = intent.getStringExtra("date");
        if (dataKey!=null){

            txtScoreName.setText(getScoreName(dataKey));

            refreshProfileData();
            onClicks();
            setCurrentDate();
        }
    }

    private static String getScoreName(String dataKey){
        switch (dataKey){
            case "overall_health_score" : return "Health Score";
            case "final_diabetic_score" : return "Diabetic Score";
            case "final_vital_score" : return "Vital Score";
            case "final_respiratory_score" : return "Respiratory Score";
            case "final_activity_score" : return "Activity Score";
            case "final_nutrition_score" : return "Nutrition Score";
            case "final_liver_score" : return "Liver Score";
            default:return "";
        }
    }

    private void initVars(){
        openDatePickerButton = findViewById(R.id.button_open_date_picker);
        llMainLayout = findViewById(R.id.main_layout);

        txtScoreName = findViewById(R.id.txt_score_name);

    }

    private void onClicks(){
        openDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void refreshProfileData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        activeLoginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);
        activeProfileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, null);
    }


    private void setCurrentDate(){

     try {
         SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
         SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
         Date date = inputFormat.parse(dateStr1);
         dateStr2 = outputFormat.format(date);


         openDatePickerButton.setText(dateStr2);
         // Pass formattedDataDate to the fetchCurrentDayTrendData method
         fetchCurrentDayTrendData(dateStr1,1);


     }catch (Exception e){
         e.getMessage();
     }


    }

    private void showLoading(){
        llMainLayout.setVisibility(View.GONE);
    }
    private void dismissLoading(){
        llMainLayout.setVisibility(View.VISIBLE);
    }


    private void fetchCurrentDayTrendData(String date, int datCount){
        SharedPreferences preferences = getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
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
                            showLoading();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            dismissLoading();
                            String response = new String(responseBody).trim();

                            if (response.equals("0")){
                                if (datCount==1){
                                    //// generate chart
                                    performMainChart(response);
                                    performTestTimeLine(response);

                                }else if(datCount==2){
                                    Toast.makeText(ViewScores.this, "0 records founds", Toast.LENGTH_SHORT).show();
                                    setCurrentDate();
                                }
                            }else{
                                performMainChart(response);
                                performTestTimeLine(response);
                            }



                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            // Handle HTTP request failure here
                            dismissLoading();
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
        WebViewCharts.generateHealthScoreChartJs(jsonData,webView, dataKey);

    }


    private void showDatePickerDialog() {
        int mYear, mMonth, mDay, mHour, mMinute;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ViewScores.this,
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

                        fetchCurrentDayTrendData(DATA_DATE,2);
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

    private void performTestTimeLine(String jsonData){

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load JSON data (replace with your JSON data loading mechanism)
        // String jsonData = parseJsonData(jsonData1); // Implement your data loading logic

        // Parse JSON data into a list of HealthData objects
        List<TimelineItem> healthDataList = parseJsonData(jsonData);

        // Initialize and set the adapter
        TimeLineAdapter  adapter = new TimeLineAdapter(healthDataList, ViewScores.this, dataKey);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(ViewScores.this);
    }
}