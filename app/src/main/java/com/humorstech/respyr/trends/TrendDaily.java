package com.humorstech.respyr.trends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.JsonFetcher;
import com.humorstech.respyr.R;
import com.humorstech.respyr.dashboard.Methods;
import com.humorstech.respyr.notification.Main;
import com.humorstech.respyr.utills.ActiveProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrendDaily extends Fragment {


    private View view;
    private ImageButton buttonWeekMinus, buttonWeekPlus;
    private TextView txtCurrentWeek;
    private Calendar calendar;

    private String activeProfileId, activeLoginId;

    private double currentScore, lastData;

    private TextView txtScoreGrowth, txtScoreAverage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trend_daily, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        activeLoginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);
        activeProfileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, null);
        String gender = sharedPreferences.getString(ActiveProfile.GENDER, null);



        init();

        return view;
    }

    private void init(){
        initVars();
        performWeekChart();
    }

    private void initVars(){
        buttonWeekMinus = view.findViewById(R.id.button_week_minus);
        buttonWeekPlus = view.findViewById(R.id.button_week_plus);
        txtCurrentWeek = view.findViewById(R.id.txt_current_week);

        txtScoreGrowth = view.findViewById(R.id.txt_score_status_in_percentage);
        txtScoreAverage = view.findViewById(R.id.txt_current_week);


    }



    private void performTrendsList(String jsonData){
        RecyclerView recyclerView = view.findViewById(R.id.daily_trend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setNestedScrollingEnabled(false);

        // Create sample data
        List<TrendDataModel> items = new ArrayList<>();
        items.add(new TrendDataModel("overall_health_score"));
        items.add(new TrendDataModel("final_diabetic_score"));
        items.add(new TrendDataModel("final_vital_score"));
        items.add(new TrendDataModel("final_respiratory_score"));
        items.add(new TrendDataModel("final_liver_score"));
        items.add(new TrendDataModel("final_activity_score"));
        items.add(new TrendDataModel("final_nutrition_score"));

        // Add more items as needed

        // Create and set the adapter
        TrendDailyAdapter adapter = new TrendDailyAdapter(items, getContext(), jsonData);
        recyclerView.setAdapter(adapter);
        buttonWeekPlus.setEnabled(true);
        buttonWeekMinus.setEnabled(true);
    }





    ////////////////////////////////////////////////////////// all methods for weekdays button ////////////////////

    private void performWeekChart(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        updateDateLabel();
        weekButtons();
    }


    //////////////// week button clicks
    private void weekButtons() {
        buttonWeekMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractWeeks(1);
            }
        });

        buttonWeekPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeeks(1);
            }
        });
    }

    private void subtractWeeks(int weeks) {
        if (calendar != null) {
            calendar.add(Calendar.WEEK_OF_YEAR, -weeks);
            updateDateLabel();
        }
    }

    private void addWeeks(int weeks) {
        if (calendar != null) {
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.WEEK_OF_YEAR, weeks);

            // Check if the new date is not after the current date
            if (newCalendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
                calendar = newCalendar;
                updateDateLabel();
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());

        // Calculate the starting date of the current week
        Calendar startOfWeek = (Calendar) calendar.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());

        // Calculate the ending date of the current week
        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_MONTH, 6);

        // Format the starting and ending dates
        String formattedStartDate = sdf.format(startOfWeek.getTime());
        String formattedEndDate = sdf.format(endOfWeek.getTime());

        // Display the date range
        txtCurrentWeek.setText(formattedStartDate + " - " + formattedEndDate);


        /// format
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        // Format the starting and ending dates
        String requiredStartDate = requiredDateFormat.format(startOfWeek.getTime());
        String requiredEndDate = requiredDateFormat.format(endOfWeek.getTime());
        fetchData(requiredStartDate,requiredEndDate);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    ///////////////////////////////////////// fetch chart data from the server
    private void fetchData(String startDate1, String endDate1){


        // Create an instance of AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Define the URL and optional parameters
        String apiUrl = HTTP_URLS.DAILY_TREND;
        RequestParams params = new RequestParams();
        params.put("login_id", activeLoginId);
        params.put("profile_id", activeProfileId);
        params.put("start_date", startDate1);
        params.put("end_date", endDate1);


        client.get(apiUrl, params, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                buttonWeekPlus.setEnabled(false);
                buttonWeekMinus.setEnabled(false);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // Successfully received a response
                String responseString = new String(responseBody);
                performTrendsList(responseString);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // Request failed

                // Check for common error conditions
                if (statusCode == 401) {
                    // Unauthorized: Handle authentication error
                } else if (statusCode == 404) {
                    // Resource not found
                } else if (statusCode == 500) {
                    // Internal server error
                } else if (error.getCause() instanceof java.net.UnknownHostException) {
                    // Network is not available
                } else {
                    // Handle other errors
                }

                // Log the error for debugging
                error.printStackTrace();

            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
                fetchData(startDate1,endDate1);
            }
        });



    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        activeProfileId=null;
        activeLoginId=null;
    }







}