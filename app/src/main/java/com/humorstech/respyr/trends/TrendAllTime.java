package com.humorstech.respyr.trends;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.humorstech.respyr.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendAllTime#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendAllTime extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrendAllTime() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendAllTime.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendAllTime newInstance(String param1, String param2) {
        TrendAllTime fragment = new TrendAllTime();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;

    String jsonData="{\n" +
            "    \"2023-09-29\": {\n" +
            "        \"day\": \"2023-09-29\",\n" +
            "        \"data\": {\n" +
            "            \"time\": \"09\\/29\\/2023 20:02:57\",\n" +
            "            \"dayname\": \"Fri\",\n" +
            "            \"overall_health_score\": \"78.09\",\n" +
            "            \"final_diabetic_score\": \"96.5722\",\n" +
            "            \"final_vital_score\": \"73.3333\",\n" +
            "            \"final_respiratory_score\": \"72.7622\",\n" +
            "            \"final_activity_score\": \"36.319308099555\",\n" +
            "            \"final_nutrition_score\": \"10.26933612338\",\n" +
            "            \"final_liver_score\": \"96.4221\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"2023-10-05\": {\n" +
            "        \"day\": \"2023-10-05\",\n" +
            "        \"data\": {\n" +
            "            \"time\": \"10\\/05\\/2023 19:45:26\",\n" +
            "            \"dayname\": \"Thu\",\n" +
            "            \"overall_health_score\": \"86\",\n" +
            "            \"final_diabetic_score\": \"86.5437\",\n" +
            "            \"final_vital_score\": \"82\",\n" +
            "            \"final_respiratory_score\": \"78.5885\",\n" +
            "            \"final_activity_score\": \"88.378216074204\",\n" +
            "            \"final_nutrition_score\": \"81.930448611697\",\n" +
            "            \"final_liver_score\": \"94.9801\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"2023-10-06\": {\n" +
            "        \"day\": \"2023-10-06\",\n" +
            "        \"data\": {\n" +
            "            \"time\": \"10\\/06\\/2023 13:32:04\",\n" +
            "            \"dayname\": \"Fri\",\n" +
            "            \"overall_health_score\": \"89.72\",\n" +
            "            \"final_diabetic_score\": \"84.885\",\n" +
            "            \"final_vital_score\": \"81.4\",\n" +
            "            \"final_respiratory_score\": \"100.0\",\n" +
            "            \"final_activity_score\": \"86.478351748594\",\n" +
            "            \"final_nutrition_score\": \"77.653793269899\",\n" +
            "            \"final_liver_score\": \"96.2427\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"2023-10-04\": {\n" +
            "        \"day\": \"2023-10-04\",\n" +
            "        \"data\": {\n" +
            "            \"time\": \"10\\/04\\/2023 17:03:13\",\n" +
            "            \"dayname\": \"Wed\",\n" +
            "            \"overall_health_score\": \"82.1\",\n" +
            "            \"final_diabetic_score\": \"82.2\",\n" +
            "            \"final_vital_score\": \"80\",\n" +
            "            \"final_respiratory_score\": \"100.0\",\n" +
            "            \"final_activity_score\": \"88.378216074204\",\n" +
            "            \"final_nutrition_score\": \"81.930448611697\",\n" +
            "            \"final_liver_score\": \"85.2006\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"2023-10-03\": {\n" +
            "        \"day\": \"2023-10-03\",\n" +
            "        \"data\": {\n" +
            "            \"time\": \"10\\/03\\/2023 15:00:13\",\n" +
            "            \"dayname\": \"Tue\",\n" +
            "            \"overall_health_score\": \"84.01\",\n" +
            "            \"final_diabetic_score\": \"82.2\",\n" +
            "            \"final_vital_score\": \"81.9\",\n" +
            "            \"final_respiratory_score\": \"100.0\",\n" +
            "            \"final_activity_score\": \"88.378216074204\",\n" +
            "            \"final_nutrition_score\": \"81.930448611697\",\n" +
            "            \"final_liver_score\": \"85.2006\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"2023-10-02\": {\n" +
            "        \"day\": \"2023-10-02\",\n" +
            "        \"data\": {\n" +
            "            \"time\": \"10\\/02\\/2023 18:30:10\",\n" +
            "            \"dayname\": \"Mon\",\n" +
            "            \"overall_health_score\": \"83.9\",\n" +
            "            \"final_diabetic_score\": \"82.2\",\n" +
            "            \"final_vital_score\": \"81.7\",\n" +
            "            \"final_respiratory_score\": \"100.0\",\n" +
            "            \"final_activity_score\": \"88.378216074204\",\n" +
            "            \"final_nutrition_score\": \"81.930448611697\",\n" +
            "            \"final_liver_score\": \"85.2006\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trend_all_time, container, false);

        WebView webView = view.findViewById(R.id.chartWebView);

        // Create and load the Highcharts chart in the WebView
        try {
            mainHealthScoreChart(jsonData, "overall_health_score", webView);
        } catch (Exception e) {
            Log.e("WebView Error", "Error loading chart: " + e.getMessage());
        }

        return view;
    }


    public  String mainHealthScoreChart(String jsonData, String dataKey, WebView webView) {
        // Load the provided HTML content as a string
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Chart.js Example</title>\n" +
                "    <!-- Include Chart.js library -->\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "    <style>\n" +
                "        /* Ensure the chart canvas takes up the full width and height of the WebView */\n" +
                "        canvas {\n" +
                "            width: 100% !important;\n" +
                "            height: 100% !important;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <!-- The container for the chart -->\n" +
                "    <canvas id=\"chartCanvas\"></canvas>\n" +
                "    <script>\n" +
                "        // Parse JSON data passed as a parameter\n" +
                "        var jsonData = " + jsonData + ";\n" +
                "        var categories = [\"Sun\", \"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\", \"Sat\"];\n" + // Custom day names
                "        var seriesData = [];\n" +
                "        for (var i = 0; i < jsonData.length; i++) {\n" +
                "            var dayData = jsonData[i].data;\n" +
                "            var healthScore = parseFloat(dayData." + dataKey + ");\n" +
                "            var timeLabel = categories[i];\n" + // Use custom day names
                "            // Check if the health score is not equal to 1 (display only when not 1)\n" +
                "            if (healthScore !== 1) {\n" +
                "                seriesData.push(healthScore);\n" +
                "            }\n" +
                "        }\n" +

                "        // Determine the last value in seriesData array\n" +
                "        var lastValue = seriesData[seriesData.length - 1];\n" +

                "        // Initialize lineColor and pointBackgroundColor based on the lastValue\n" +
                "        var lineColor, pointBackgroundColor, fillColor;\n" +
                "        if (lastValue >= 80) {\n" +
                "            lineColor = '#3FAF58';\n" +
                "            pointBackgroundColor = '#3FAF58';\n" +
                "            fillColor = 'rgba(0, 128, 0, 0.2)';\n" +
                "        } else if (lastValue >= 71) {\n" +
                "            lineColor = '#FFC412';\n" +
                "            pointBackgroundColor = '#FFC412';\n" +
                "            fillColor = 'rgba(255, 255, 0, 0.2)';\n" +
                "        } else {\n" +
                "            lineColor = '#EA5455';\n" +
                "            pointBackgroundColor = '#EA5455';\n" +
                "            fillColor = 'rgba(255, 0, 0, 0.2)';\n" +
                "        }\n" +
                "        // Initialize the chart with data points and labels\n" +
                "        var ctx = document.getElementById('chartCanvas').getContext('2d');\n" +
                "        var chart = new Chart(ctx, {\n" +
                "            type: 'line',\n" +
                "            data: {\n" +
                "                labels: categories,\n" + // Use custom day names
                "                datasets: [{\n" +
                "                    label: '" + dataKey + "',\n" +
                "                    data: seriesData,\n" +
                "                    borderColor: lineColor,\n" +
                "                    backgroundColor: fillColor,\n" +
                "                    borderWidth: 2,\n" +
                "                    pointBackgroundColor: pointBackgroundColor,\n" +
                "                    pointRadius: 3,\n" +
                "                }],\n" +
                "            },\n" +
                "            options: {\n" +
                "               layout: {padding: 0},"+
                "                responsive: true,\n" +
                "                animation: false, // Disable animations\n" +
                "                scales: {\n" +
                "                    x: {\n" +
                "                          offset: false,"+
                "                        display: true,\n" +
                "                        title: {\n" +
                "                            display: false,\n" +
                "                            text: '',\n" +
                "                            fontSize: 8,\n" +
                "                            color: '#535359',\n" +
                "                            font: {\n" +
                "                                family: 'Roboto',\n" +
                "                            },\n" +
                "                        },\n" +
                "                        grid: {\n" +
                "                            display: false,\n" +
                "                        },\n" +
                "                        border: { display: false },\n" +
                "                        ticks: {\n" +
                "                            font: { size: 8 },\n" +
                "                            autoSkip: false, // Disable automatic skipping of labels\n" +
                "                            maxRotation: 0,  // Rotate labels to fit if needed\n" +
                "                             padding: 2,"+
                "                        },\n" +
                "                    },\n" +
                "                    y: {\n" +
                "                        display: true,\n" +
                "                        gridLines: { borderDash: [8, 4], color: \"#348632\" },\n" +
                "                        title: {\n" +
                "                            display: false,\n" +
                "                            text: '',\n" +
                "                            fontSize: 8,\n" +
                "                            drawBorder: false,\n" +
                "                            color: '#535359',\n" +
                "                            font: {\n" +
                "                                family: 'Roboto',\n" +
                "                            },\n" +
                "                        },\n" +
                "                        grid: {\n" +
                "                            display: true,\n" +
                "                            color: 'rgba(0, 0, 0, 0.1)',\n" +
                "                        },\n" +
                "                        min: 0,\n" +
                "                        max: 100,\n" +
                "                        ticks: {\n" +
                "                            color: '#535359',\n" +
                "                            font: { size: 8 },\n" +
                "                            autoSkip: true,\n" +
                "                            maxTicksLimit: 7,\n" +
                "                        },\n" +
                "                        border: { display: false, dash: [4, 4] },\n" +
                "                        beginAtZero: false,\n" +
                "                    },\n" +
                "                },\n" +
                "                plugins: {\n" +
                "                    legend: {\n" +
                "                        display: false, // Hide the legend\n" +
                "                    },\n" +
                "                    tooltip: {\n" +
                "                        intersect: false,\n" +
                "                        displayColors: false,\n" +
                "                        mode: 'index',\n" +
                "                        titleFont: {\n" +
                "                            size: 12,\n" +
                "                            weight: 'bold',\n" +
                "                        },\n" +
                "                        callbacks: {\n" +
                "                            title: function (tooltipItems) {\n" +
                "                                var yValue = tooltipItems[0].formattedValue || '';\n" +
                "                                return ' " + setCardTitle(dataKey) +  " : ' + yValue;\n" +
                "                            },\n" +
                "                            label: function (tooltipItem) {\n" +
                "                                return ''; // Empty string to hide the label\n" +
                "                            },\n" +
                "                            afterLabel: function (tooltipItem) {\n" +
                "                                var verticalLine = document.createElement('div');\n" +
                "                                verticalLine.style.borderLeft = '1px solid rgba(0, 0, 0, 1)';\n" +
                "                                verticalLine.style.height = tooltipItem.height + 'px';\n" +
                "                                verticalLine.style.position = 'absolute';\n" +
                "                                verticalLine.style.left = tooltipItem.x + 'px';\n" +
                "                                verticalLine.style.top = tooltipItem.y + 'px';\n" +
                "                                document.body.appendChild(verticalLine);\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +

                "                },\n" +
                "            },\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>\n";

        // Load the HTML content into the WebView
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        return htmlContent;
    }

    private String setCardTitle(String titleName) {
        switch (titleName) {
            case "overall_health_score":
                return "Health Score";
            case "final_diabetic_score":
                return "Diabetic Score";
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
                return " ";
        }
    }
}