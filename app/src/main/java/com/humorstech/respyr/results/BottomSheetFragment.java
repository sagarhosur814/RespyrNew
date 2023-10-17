package com.humorstech.respyr.results;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.humorstech.respyr.R;
import com.humorstech.respyr.trends.MyMarkerView;
import com.humorstech.respyr.trends.ResultPageTrendWeekly;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Context context;
    private Activity activity;
    private String dataKey;
    private double currentScore, lastData;

    private TextView txtScoreStatus, txtScoreStatusMessage, txtScoreRange;



    public BottomSheetFragment(Activity activity, String dataKey, double currentScore){
        this.activity = activity;
        this.context = activity;
        this.dataKey = dataKey;
        this.currentScore = currentScore;
    }

    private Calendar calendar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = new BottomSheetDialog(activity, R.style.TransparentDialog);
        dialog.setContentView(R.layout.health_score_trend_sheet);


        ///-----------------------------------------------------------------------------------------
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Set to start of the current week
        updateDateLabel();

        ///////////////////////////////// trend tabs weekly, monthly and all time implementation
        ////////////////////////////////////////////////////////////////////////////////////////////
        ViewPager viewPager = dialog.findViewById(R.id.viewPager);
        TabLayout tabLayout = dialog.findViewById(R.id.tabLayout);
        ImageButton imgButtonClose = dialog.findViewById(R.id.button_close);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        /// data key is ::: health score type
        /// json data is ::: api of scores fetched from server
        adapter.addFragment(new ResultPageTrendWeekly(dataKey), "Weekly");


        /// set adapter to view pages
        assert viewPager != null;
        assert tabLayout != null;
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////// sheet close button implementation//////////////
        assert imgButtonClose != null;
        imgButtonClose.setOnClickListener(v -> dialog.dismiss());


        //////////////////// set chart header///////////////////////////
        ////////////////////////////////////////////////////////////////

        TextView textViewChartTitle = dialog.findViewById(R.id.txt_data_name);
        assert textViewChartTitle != null;
        textViewChartTitle.setText(getCharTitle());


        /// perform Header
        txtScoreStatus = dialog.findViewById(R.id.score_status_in_percentage);
        txtScoreStatusMessage = dialog.findViewById(R.id.score_status_in_text);
        txtScoreRange = dialog.findViewById(R.id.txt_score_range);






        //////////////////////////////////////set height of the bottom sheet////////////////////////
        LinearLayout llHeader=dialog.findViewById(R.id.ll_trend_sheet_main);

        assert llHeader != null;
        ViewTreeObserver vto = llHeader.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This will be called when the header layout has been measured and laid out
                int llHeaderHeight = llHeader.getHeight();

                // Remove the listener to avoid multiple calls
                llHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Now you can set the peek height of the BottomSheetBehavior
                dialog.getBehavior().setPeekHeight(llHeaderHeight);
            }
        });




        return dialog;
    }


    private String getCharTitle() {
        switch (dataKey) {
            case "overall_health_score":
                return "Health Score";
            case "final_diabetic_score":
                return "Diabetes Score";
            case "final_vital_score":
                return "Vital Score";
            case "final_respiratory_score":
                return "Respiratory Score";
            case "final_activity_score":
                return "Activity Score";
            case "final_nutrition_score":
                return "Nutrition Score";
            case "final_liver_score":
                return "Liver Score";
            default:
                return "undefined";
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

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

        SimpleDateFormat startDateFormatted = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startDateFormattedStr = startDateFormatted.format(startOfWeek.getTime());
        String endDateFormattedStr = startDateFormatted.format(endOfWeek.getTime());
        fetchData(startDateFormattedStr,endDateFormattedStr);
    }





    private void fetchData(String startDate, String endDate){



        SharedPreferences sharedPreferences = getContext().getSharedPreferences(ReadingParameters.TITLE, Context.MODE_PRIVATE);
        String profileId = sharedPreferences.getString(ReadingParameters.PROFILE_ID, null);
        String loginId = sharedPreferences.getString(ReadingParameters.LOGIN_ID, null);


        // Create an instance of AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("profile_id", profileId);
        params.put("login_id", loginId);
        params.put("start_date", startDate);
        params.put("end_date", endDate);


        client.get("https://humorstech.com/humors_app/app_final/trends/test4.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                parseJsonData(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }



    @SuppressLint("SetTextI18n")
    public  void parseJsonData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String day = jsonObject.getString("day");
                JSONObject data = jsonObject.getJSONObject("data");
                String time = data.getString("time");
                String healthscore = data.getString("overall_health_score");
                String diabeticscore = data.getString("final_diabetic_score");
                String vitalscore = data.getString("final_vital_score");
                String blowscore = data.getString("final_respiratory_score");
                String activityscore = data.getString("final_activity_score");
                String nutritionscore = data.getString("final_nutrition_score");
                String liverScore = data.getString("final_liver_score");


                if (i < jsonArray.length()-1){

                }

                System.out.println("--------------------------------------------------");
                System.out.println("Day: " + day);
                System.out.println("Time: " + time);
                System.out.println("Health Score: " + healthscore);
                System.out.println("Diabetic Score: " + diabeticscore);
                System.out.println("Vital Score: " + vitalscore);
                System.out.println("Blow Score: " + blowscore);
                System.out.println("Activity Score: " + activityscore);
                System.out.println("Nutrition Score: " + nutritionscore);
                System.out.println("----------------------------------------------------");



                // Get the current date and time
                Date currentDate = new Date();
                // Define a date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                // Format the current date as a string
                String currentDateStr = dateFormat.format(currentDate);


                // Create a Calendar instance and set it to the current date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                // Get the date for yesterday
                Date yesterdayDate = calendar.getTime();

                // Define a date format

                // Format the date for yesterday as a string
                String yesterdayDateStr = dateFormat.format(yesterdayDate);


                if (day.equals(yesterdayDateStr)){
                    lastData = data.getDouble(dataKey);

                }


                if (day.equals(currentDateStr)){
                    currentScore  = data.getDouble(dataKey);
                    double  scorePerformance = calculatePercentageIncrease(currentScore, lastData);
                    performHeader(scorePerformance);
                }

                ////////////////////////////////////////////////////////////////////////////////////
                if (day.equals(currentDateStr)){
                    currentScore  = data.getDouble(dataKey);
                    if (currentScore >= 80 && currentScore <=100){
                        txtScoreRange.setText("your score is in good range");
                    }else if(currentScore >=71 && currentScore <=79){
                        txtScoreRange.setText("your score is in fair range");
                    }else{
                        txtScoreRange.setText("your score is in poor range");
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////////



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void performHeader(double scorePerformance){

       Drawable iconUp = getResources().getDrawable(R.drawable.data_up);
       Drawable iconDown = getResources().getDrawable(R.drawable.data_down);


        int green = getResources().getColor(R.color.green);
        int red = getResources().getColor(R.color.red);

        if (scorePerformance<=100){
            if ((int)scorePerformance < 0){
                scorePerformance = Math.abs(scorePerformance);
                txtScoreStatus.setText(String.valueOf((int)scorePerformance) + "%");


                // Tint the drawable
                Drawable tintedDrawable = DrawableCompat.wrap(iconDown);
                DrawableCompat.setTint(tintedDrawable, red);
                txtScoreStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, tintedDrawable, null);

                txtScoreStatusMessage.setText("lower than yesterday");

            }else if((int)scorePerformance==0){
                txtScoreStatus.setText("");
                txtScoreStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                txtScoreStatusMessage.setText("same as yesterday");
            }else{


                txtScoreStatus.setText(String.valueOf((int)scorePerformance) + "%");
                // Tint the drawable
                Drawable tintedDrawable = DrawableCompat.wrap(iconUp);
                DrawableCompat.setTint(tintedDrawable, green);
                txtScoreStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, tintedDrawable, null);

                txtScoreStatusMessage.setText("better than yesterday");
            }
        }else{
            txtScoreStatus.setText("0%");
            txtScoreStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        }

    }

    public static double calculatePercentageIncrease(double currentScore, double previousScore) {
        return ((currentScore - previousScore) / previousScore) * 100;
    }


}
