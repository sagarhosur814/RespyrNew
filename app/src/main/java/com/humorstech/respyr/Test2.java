
package com.humorstech.respyr;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;
import com.hadiidbouk.charts.OnBarClickedListener;
import com.humorstech.respyr.reading.PPGPost;
import com.humorstech.respyr.reading.Stuffs;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Test2 extends AppCompatActivity   {

    private ChartProgressBar mChart;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);



        fetchJson();

    }

    private void fetchJson(){
        AsyncHttpClient client = new AsyncHttpClient();
        String apiUrl = "https://humorstech.com/humors_app/app_final/trends/daily_routine_trend_weekly2.php?" +
                "login_id=RESPYR003&profile_id=profile1&start_date=10/01/2023&end_date=10/08/2023";
        RequestParams params = new RequestParams();
        params.put("login_id", "RESPYR003");
        params.put("profile_id", "profile1");
        params.put("profile_id", "profile1");
        params.put("profile_id", "profile1");
        // Perform the GET request
        client.get(apiUrl, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // Successfully got a response
                String response = new String(responseBody);

                Toast.makeText(Test2.this, response, Toast.LENGTH_SHORT).show();
                decodeJson(response);
                // Handle the response here
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // Request failed
                String errorMessage = new String(responseBody);
                // Handle the error here
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void  decodeJson(String jsonData){

        ArrayList<BarData> dataList = new ArrayList<>();
        BarData data = new BarData("Sun", 3.4f, "3.4€");


        try {
            // Parse the JSON array
            JSONArray jsonArray = new JSONArray(jsonData);

            // Loop through the array and parse each object
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract data from the object
                String day = jsonObject.getString("day");
                JSONObject data1 = jsonObject.getJSONObject("data");
                String time = data1.getString("time");
                String dayName = data1.optString("dayname", ""); // Handle optional field
                int sleepHours = data1.optInt("sleep_hours", -1); // Handle optional field with default value
                int exerciseTime = data1.optInt("exercise_time", -1); // Handle optional field with default value
                int waterIntake = data1.optInt("water_intake", -1); // Handle optional field with default value
                int smokeUnits = data1.optInt("smoke_units", -1); // Handle optional field with default value
                int alcoholUnits = data1.optInt("alcohol_units", -1); // Handle optional field with default value


                // Print the extracted data using Log
                Log.d("JSONData", "Day: " + day);
                Log.d("JSONData", "Time: " + time);
                Log.d("JSONData", "Day Name: " + dayName);
                Log.d("JSONData", "Sleep Hours: " + sleepHours);
                Log.d("JSONData", "Exercise Time: " + exerciseTime);
                Log.d("JSONData", "Water Intake: " + waterIntake);
                Log.d("JSONData", "Smoke Units: " + smokeUnits);
                Log.d("JSONData", "Alcohol Units: " + alcoholUnits);

                data = new BarData(Stuffs.dateToDayName(day), (float)sleepHours, "");
                dataList.add(data);
                displayChart(dataList);

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void displayChart(ArrayList<BarData> dataList){


        mChart = (ChartProgressBar) findViewById(R.id.ChartProgressBar);

        mChart.setMaxValue(24.0f);
        mChart.setDataList(dataList);
        mChart.build();

    }


}