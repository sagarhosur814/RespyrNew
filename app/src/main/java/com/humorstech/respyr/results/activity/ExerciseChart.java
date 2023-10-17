package com.humorstech.respyr.results.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.JsonArray;
import com.humorstech.respyr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseChart {

    public static Context context;

    public static void drawChart(Activity activity, BarChart barChart, JSONArray jsonArray) {

        try {
            Context context = activity.getApplicationContext();

            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>(); // Dates for X-axis labels

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dayData = jsonArray.getJSONObject(i).getJSONObject("data");
                float waterIntake = (float) dayData.optDouble("exercise_time", 0.0);

                if (waterIntake == -1){
                    entries.add(new BarEntry(i, 0)); // Using index i as X-axis value
                }else{
                    entries.add(new BarEntry(i, waterIntake)); // Using index i as X-axis value
                }


            }


            labels.add("Sun");
            labels.add("Mon");
            labels.add("Tue");
            labels.add("Wed");
            labels.add("Thr");
            labels.add("Fri");
            labels.add("Sat");




            Typeface customFont = ResourcesCompat.getFont(activity, R.font.roboto);



            // Set X-axis labels
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setTextSize(8f);
            xAxis.setTextColor(context.getResources().getColor(R.color.black));
            xAxis.setDrawGridLines(false);
            xAxis.setTypeface(customFont);


            //////////////////////////Y axis
            YAxis leftYAxis = barChart.getAxisLeft();
            leftYAxis.setDrawAxisLine(false);   // Hide the Y-axis line
            leftYAxis.setDrawLabels(true);     // Show Y-axis labels
            leftYAxis.enableGridDashedLine(10f, 10f, 0f);      // Dash length, space length, phase
            leftYAxis.setGridColor(activity.getResources().getColor(R.color.black));             // Grid line color
            leftYAxis.setGridLineWidth(0.1f);                                          // Grid line thickness
            leftYAxis.setTypeface(customFont);
            leftYAxis.setTextColor(activity.getResources().getColor(R.color.black));      // Label color
            leftYAxis.setTextSize(8f);
            YAxis rightYAxis = barChart.getAxisRight();
            rightYAxis.setEnabled(false);


            leftYAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return (int) value + " mins";
                }
            });

            Description description = new Description();
            description.setEnabled(false);
            barChart.setDescription(description);
            Legend legend = barChart.getLegend();
            legend.setEnabled(false);





            BarDataSet barDataSet = new BarDataSet(entries, "Water Intake");
            BarData barData = new BarData(barDataSet);
            barDataSet.setColor(context.getResources().getColor(R.color.primary));
            barData.setBarWidth(.70f);


            // column top values
            barData.setDrawValues(true);
            barData.setValueTextColor(context.getResources().getColor(R.color.black));
            barData.setValueTextSize(8f);
            barData.setValueTypeface(customFont);

            barChart.setData(barData);
            barChart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
