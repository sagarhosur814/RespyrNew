package com.humorstech.respyr.trends;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.humorstech.respyr.MPCharts;
import com.humorstech.respyr.R;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ResultPageTrendWeekly extends Fragment {


    private String dataKey ;
    private String jsonData ;


    public ResultPageTrendWeekly(String dataKey){
        this.dataKey=dataKey;
    }


    private View view;
    private Calendar calendar;

    private TextView txtCurrentWeek;


    private ImageButton buttonWeekMinus, buttonWeekPlus;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_result_page_trend_weekly, container, false);
        // Initialize your fragment's views and functionality here


        progressBar=view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);


        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Set to start of the current week


        txtCurrentWeek=view.findViewById(R.id.txt_current_week);
        updateDateLabel();


        onClick();

        return view;
    }



    ////////////////////////////////// display chart operation
    @SuppressLint("SetJavaScriptEnabled")
    private void performMainChart(String jsonData){
        // Find the WebView by ID
        WebView webView = view.findViewById(R.id.chartWebView);

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


        // CreaWebViewChartste and load the Highcharts chart in the WebView
        mainHealthScoreChart2(jsonData,dataKey,webView);

        buttonWeekMinus.setEnabled(true);
        buttonWeekPlus.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }



    private void onClick(){

         buttonWeekMinus = view.findViewById(R.id.button_week_minus);
         buttonWeekPlus = view.findViewById(R.id.button_week_plus);

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
        txtCurrentWeek.setText(formattedStartDate + " - " + formattedEndDate);

        SimpleDateFormat startDateFormatted = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startDateFormattedStr = startDateFormatted.format(startOfWeek.getTime());
        String endDateFormattedStr = startDateFormatted.format(endOfWeek.getTime());
        fetchData(startDateFormattedStr,endDateFormattedStr);

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


        client.get("https://humorstech.com/humors_app/app_final/trends/test2.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                buttonWeekMinus.setEnabled(false);
                buttonWeekPlus.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                MPCharts mpCharts = new MPCharts(getContext());
                //LineChart lineChart=view.findViewById(R.id.lineChart);


                String response = new String(responseBody);
                System.out.println("-----------------------------------------------");
                System.out.println(params);
                System.out.println(response);
                System.out.println("-----------------------------------------------");
                performMainChart(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
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


    public static String mainHealthScoreChart(String jsonData, String dataKey, WebView webView) {
        // Load the provided HTML content as a string
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Highcharts Example</title>\n" +
                "    <!-- Include Highcharts library -->\n" +
                "    <script src=\"https://code.highcharts.com/highcharts.js\"></script>\n" +
                "    <script src=\"https://code.highcharts.com/modules/accessibility.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <!-- The container for the chart -->\n" +
                "    <div id=\"container\" style=\"width: 100%; height: 230px;\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "        // Parse JSON data passed as a parameter\n" +
                "        var jsonData = " + jsonData + ";\n" +
                "\n" +
                "        // Extract data for the chart\n" +
                "        var categories = [];\n" +
                "        var seriesData = [];\n" +
                "        for (var i = 0; i < jsonData.length; i++) {\n" +
                "            var dayData = jsonData[i].data;\n" +
                "            var healthScore = parseFloat(dayData." + dataKey + ");\n" +
                "            var timeLabel = dayData.dayname;\n" +
                "            // Check if the health score is not equal to 1 (display only when not 1)\n" +
                "            if (healthScore !== 1) {\n" +
                "                seriesData.push(healthScore);\n" +
                "                categories.push(timeLabel);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        // Determine the last value in the seriesData array\n" +
                "        var lastValue = seriesData[seriesData.length - 1];\n" +
                "\n" +
                "        // Define color thresholds\n" +
                "        var greenThreshold = 80;\n" +
                "        var yellowThreshold = 61;\n" +
                "\n" +
                "        // Determine the line color and marker enabled status based on the last value\n" +
                "        var lineColor;\n" +
                "        var lineColor1;\n" +
                "        var markerEnabled;\n" +
                "        if (lastValue >= 80 && lastValue <= 100) {\n" +
                "            lineColor = '#3FAF58';\n" +
                "            lineColor1 = '#ABDCB5';\n" +
                "            markerEnabled = true; // Show marker for last value\n" +
                "        } else if (lastValue >= 71 && lastValue < 80) {\n" +
                "            lineColor = '#FFC412';\n" +
                "            lineColor1 = '#FFF2CC';\n" +
                "            markerEnabled = true; // Show marker for last value\n" +
                "        } else if (lastValue <= 7 && lastValue < 80) {\n" +
                "            lineColor = '#EA5455';\n" +
                "            lineColor1 = '#FDE8E9';\n" +
                "            markerEnabled = true; // Show marker for last value\n" +
                "        }\n" +
                "\n" +
                "        // Initialize the chart with data points and labels\n" +
                "        Highcharts.chart('container', {\n" +
                "            license: null,\n" +
                "            chart: {\n" +
                "                type: 'area',\n" +
                "                events: { load: function() { var lastPoint = this.series[0].points[this.series[0].points.length - 1]; this.tooltip.refresh(lastPoint); lastPoint.update({ marker: { enabled: true, symbol: 'circle' } }); } },\n" +
                "            },\n" +
                "            title: {\n" +
                "                text: ''\n" +
                "            },\n" +
                "            xAxis: {\n" +
                "                categories: categories,\n" +
                "                labels: {\n" +
                "                    style: {\n" +
                "                        fontSize: '8px',\n" +
                "                        fontFamily: 'Roboto',\n" +
                "                        color: '#A1A1A1'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "tooltip: { formatter: function() { return '"+getScoreName(dataKey)+ " : '+ this.y.toFixed(2);}},"+
                "            yAxis: {\n" +
                "                title: '',\n" +
                "                max: 100,\n" +
                "                min: 0,\n" +
                "                labels: {\n" +
                "                    style: {\n" +
                "                        fontSize: '8px',\n" +
                "                        fontFamily: 'Roboto',\n" +
                "                        color: '#A1A1A1'\n" +
                "                    }\n" +
                "                },\n" +
                "                tickAmount: 5,\n" +
                "                gridLineDashStyle: 'dash',\n" +
                "            },\n" +
                "            legend: {\n" +
                "                enabled: false\n" +
                "            },\n" +
                "            plotOptions: {\n" +
                "                area: {\n" +
                "                    lineWidth: 2,\n" +
                "                    marker: {\n" +
                "                        enabled: true,\n" +
                "                        radius: 5,\n" +
                "                        lineWidth: 2,\n" +
                "                        lineColor: 'white',\n" +
                "                        fillColor: lineColor,\n" +
                "                        symbol: 'circle',\n" +
                "                    },\n" +
                "                    states: {\n" +
                "                        hover: {\n" +
                "                            lineWidthPlus: 0\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            series: [{\n" +
                "                name: '" + dataKey + "',\n" +
                "                data: seriesData,\n" +
                "                lineColor: lineColor,\n" +
                "                fillColor: {\n" +
                "                    linearGradient: {\n" +
                "                        x1: 0,\n" +
                "                        y1: 0,\n" +
                "                        x2: 0,\n" +
                "                        y2: 1\n" +
                "                    },\n" +
                "                    stops: [\n" +
                "                        [0, lineColor1],\n" +
                "                        [1, 'rgba(255, 255, 255, 0.00) 100%']\n" +
                "                    ]\n" +
                "                }\n" +
                "            }]\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>\n";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        return htmlContent;
    }


    public  String mainHealthScoreChart2(String jsonData, String dataKey, WebView webView) {
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
                "                                return ' " + getScoreName(dataKey) +  " : ' + yValue;\n" +
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


}