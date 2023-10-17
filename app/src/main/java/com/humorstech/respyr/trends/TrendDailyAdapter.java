package com.humorstech.respyr.trends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.humorstech.respyr.R;

import java.util.List;

public class TrendDailyAdapter extends RecyclerView.Adapter<TrendDailyAdapter.ViewHolder> {

    private List<TrendDataModel> items;
    private Context context;
    private String jsonData;

    public TrendDailyAdapter(List<TrendDataModel> items, Context context, String  jsonData) {
        this.items = items;
        this.context = context;
        this.jsonData = jsonData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trend_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrendDataModel item = items.get(position);
        holder.txtScoreName.setText(setCardTitle(item.getScoreName()));
        performMainChart(jsonData, holder.chartWebView, item.getScoreName());

        // Add an OnTouchListener to the WebView
        holder.chartWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int[] webViewLocation = new int[2];
                    holder.chartWebView.getLocationOnScreen(webViewLocation);
                    int webViewX = webViewLocation[0];
                    @SuppressLint("ClickableViewAccessibility") int webViewY = webViewLocation[1];
                    int touchX = (int) event.getRawX();
                    int touchY = (int) event.getRawY();

                    if (touchX < webViewX || touchX > webViewX + holder.chartWebView.getWidth() ||
                            touchY < webViewY || touchY > webViewY + holder.chartWebView.getHeight()) {
                        // Touch is outside the WebView, hide the tooltip here
                        holder.chartWebView.loadUrl("javascript:hideTooltip()"); // Call a JavaScript function to hide the tooltip
                    }
                }
                return false;
            }
        });



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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtScoreName;
        WebView chartWebView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtScoreName = itemView.findViewById(R.id.txt_score_name);
            chartWebView = itemView.findViewById(R.id.chartWebView);

        }
    }

    // Display chart operation
    @SuppressLint("SetJavaScriptEnabled")
    private void performMainChart(String jsonData, WebView webView, String dataKey) {
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
        try {
            mainHealthScoreChart(jsonData, dataKey, webView);
        } catch (Exception e) {
            Log.e("WebView Error", "Error loading chart: " + e.getMessage());
        }
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

}
