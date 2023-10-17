package com.humorstech.respyr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MPCharts {


    Context context;
    public MPCharts(Context context){
        this.context = context;
    }

    public void drawGraph(String dataKey, LineChart lineChart,String jsonData){

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> xLabels = new ArrayList<>(); // Store X-axis labels

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dayData = jsonArray.getJSONObject(i);
                String day = dayData.getString("day"); // Extract date from JSON
                JSONObject data = dayData.getJSONObject("data");
                float yValue = Float.parseFloat(data.getString(dataKey));

                // Convert date string to display on X-axis
                //String xLabel = formatDateForChart(day); // Format the date
                xLabels.add(day); // Store the X-axis label

                entries.add(new Entry(i, yValue)); // Note the reversed index
            }

            // Create a linear gradient for the chart background
            int startColor = Color.parseColor("#87CEEB"); // Light Blue
            int endColor = Color.parseColor("#FFFFFF");   // White





            LineDataSet dataSet = new LineDataSet(entries, "Health Score");
            dataSet.setDrawValues(false);
            dataSet.setLineWidth(2f);
            dataSet.setColor(context.getResources().getColor(R.color.green));
            dataSet.setDrawCircles(false);


            dataSet.setDrawFilled(true);
            // Create a gradient drawable
            Drawable fillGradient = ContextCompat.getDrawable(context, R.drawable.chart_gradient);

            dataSet.setFillDrawable(fillGradient);


            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Set curved line mode
            dataSet.setCubicIntensity(0.19f); // Set curve intensity


            dataSet.setDrawHorizontalHighlightIndicator(false);
            dataSet.setDrawVerticalHighlightIndicator(true);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData lineData = new LineData(dataSets);
            lineChart.setData(lineData);


            // Remove padding by setting the content margins to zero
            lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);



            // set mark view

            lineChart.setDrawMarkerViews(false);


            // Customize the chart
            Description description = new Description();
            description.setText("");
            lineChart.setDescription(description);


            // Disable the legend
            Legend legend = lineChart.getLegend();
            legend.setEnabled(false);


            XAxis xAxis = lineChart.getXAxis();
            xAxis.setEnabled(false);


            YAxis yAxisLeft = lineChart.getAxisLeft();
            yAxisLeft.setEnabled(false);


            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);


            lineChart.setTouchEnabled(false);
            lineChart.setHighlightPerTapEnabled(false);

            lineChart.invalidate(); // Refresh the chart


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
