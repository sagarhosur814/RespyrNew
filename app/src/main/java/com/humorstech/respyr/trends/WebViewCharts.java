package com.humorstech.respyr.trends;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.humorstech.respyr.R;

import org.json.JSONArray;
import org.json.JSONException;

public class WebViewCharts {


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
                "                type: 'line',\n" +
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
                "                areaspline: {\n" +
                "                    lineWidth: 2,\n" +
                "                    marker: {\n" +
                "                        enabled: false,\n" +
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



    public static String performApexChart(WebView webView,String dataKey, String jsonData) {
        // JSON data is passed as a string, so you need to parse it
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Health Score Line Chart</title>\n" +
                    "    <!-- Include ApexCharts from a CDN -->\n" +
                    "    <script src=\"https://cdn.jsdelivr.net/npm/apexcharts\"></script>\n" +
                    "    <style>\n" +
                    "        /* Remove all padding and margins */\n" +
                    "        body, html {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <!-- Create a div element to render the chart -->\n" +
                    "    <div id=\"healthScoreChart\" style=\" height: 209px;\"></div>\n" +
                    "    <script>\n" +
                    "        // JavaScript code for ApexCharts chart\n" +
                    "        var jsonData = " + jsonArray.toString() + ";\n" +
                    "        var labels = jsonData.map(function (entry) {\n" +
                    "            return entry.time;\n" +
                    "        });\n" +
                    "        var data = jsonData.map(function (entry) {\n" +
                    "            return parseFloat(entry. "+ dataKey +" );\n" +
                    "        });\n" +
                    "        var lastValue = data[data.length - 1];\n" +
                    "        var lineColor, circleColor;\n" +
                    "        if (lastValue >= 80 && lastValue <= 100) {\n" +
                    "            lineColor = circleColor = '#3FAF58';\n" +
                    "        } else if (lastValue >= 71 && lastValue <= 79) {\n" +
                    "            lineColor = circleColor = '#FFC412';\n" +
                    "        } else {\n" +
                    "            lineColor = circleColor = '#EA5455';\n" +
                    "        }\n" +
                    "        var options = {\n" +
                    "            chart: {\n" +
                    "                type: 'line',\n" +
                    "                fontFamily: 'Roboto',\n" +
                    "                animations: {\n" +
                    "                    enabled: false\n" +
                    "                },\n" +
                    "                toolbar: {\n" +
                    "                    show: false\n" +
                    "                },\n" +
                    "                sparkline: {\n" +
                    "                    enabled: false\n" +
                    "                }\n" +
                    "            },\n" +
                    "            xaxis: {\n" +
                    "                categories: labels,\n" +
                    "                labels: {\n" +
                    "                    style: {\n" +
                    "                        fontSize: '8px'\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                tickAmount: 5\n" +
                    "            },\n" +
                    "            yaxis: {\n" +
                    "                labels: {\n" +
                    "                    style: {\n" +
                    "                        fontSize: '8px'\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                tickAmount: 5\n" +
                    "            },\n" +
                    "            legend: {\n" +
                    "                show: false\n" +
                    "            },\n" +
                    "            stroke: {\n" +
                    "                width: 2,\n" +
                    "                curve: 'straight',\n" +
                    "                colors: [lineColor]\n" +
                    "            },\n" +
                    "            markers: {\n" +
                    "                size: 4, // Set a constant marker size for all data points\n" +
                    "                colors: [circleColor],\n" +
                    "                strokeColors: 'rgba(0, 0, 0, 0)',\n" +
                    "                strokeWidth: 2\n" +
                    "            },\n" +
                    "            series: [\n" +
                    "                {\n" +
                    "                    name: 'Overall Health Score',\n" +
                    "                    data: data\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        };\n" +
                    "        var chart = new ApexCharts(document.querySelector('#healthScoreChart'), options);\n" +
                    "        chart.render();\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>\n";

            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

            return htmlContent;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }






    public static String generateMicroChart(String jsonData, String dataKey, WebView webView) {
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
                "        var customLabels = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']; // Add your custom day labels here\n" +
                "        var categories = customLabels; // Use custom labels as X-axis labels\n" +
                "        var seriesData = [];\n" +
                "        for (var i = 0; i < jsonData.length; i++) {\n" +
                "            var dayData = jsonData[i].data;\n" +
                "            var healthScore = parseFloat(dayData." + dataKey + ");\n" +
                "            // Check if the health score is not equal to 1 (display only when not 1)\n" +
                "            if (healthScore !== 1) {\n" +
                "                seriesData.push(healthScore);\n" +
                "            } else {\n" +
                "                seriesData.push(null);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        // Determine the last value in the seriesData array\n" +
                "        var lastValue = seriesData[seriesData.length - 1];\n" +
                "\n" +
                "        // Define color thresholds\n" +
                "        var greenThreshold = 80;\n" +
                "        var yellowThreshold = 61;\n" + // Adjust the threshold as needed
                "\n" +
                "        // Determine the line color and marker enabled status based on the last value\n" +
                "        var lineColor;\n" +
                "        var lineColor1;\n" +
                "        var markerEnabled;\n" +
                "        if (lastValue >= greenThreshold) {\n" +
                "            lineColor = '#3FAF58';\n" +
                "            lineColor1 = '#ABDCB5';\n" +
                "            markerEnabled = true; // Show marker for the last value\n" +
                "        } else if (lastValue >= yellowThreshold) {\n" +
                "            lineColor = '#FFC412';\n" +
                "            lineColor1 = '#FFF2CC';\n" +
                "            markerEnabled = true; // Show marker for the last value\n" +
                "        } else {\n" +
                "            lineColor = '#EA5455';\n" +
                "            lineColor1 = '#FDE8E9';\n" +
                "            markerEnabled = true; // Show marker for the last value\n" +
                "        }\n" +
                "\n" +
                "        // Initialize the chart with data points and labels\n" +
                "        Highcharts.chart('container', {\n" +
                "            license: null,\n" +
                "            chart: {\n" +
                "                type: 'areaspline',\n" +
                "                // Hide both x-axis and y-axis lines and border\n" +
                "                borderWidth: 0,\n" +
                "            },\n" +
                "            title: {\n" +
                "                text: ''\n" +
                "            },\n" +
                "            xAxis: {\n" +
                "                categories: categories,\n" +
                "                labels: {\n" +
                "                    enabled: false, // Hide x-axis labels\n" +
                "                },\n" +
                "                lineColor: 'transparent', // Hide x-axis line\n" +
                "                tickWidth: 0, // Hide x-axis ticks\n" +
                "            },\n" +
                "            yAxis: {\n" +
                "                title: '',\n" +
                "                max: 100,\n" +
                "                min: 0,\n" +
                "                labels: {\n" +
                "                    enabled: false, // Hide y-axis labels\n" +
                "                },\n" +
                "                gridLineWidth: 0, // Hide y-axis gridlines\n" +
                "                lineColor: 'transparent', // Hide y-axis line\n" +
                "                tickWidth: 0, // Hide y-axis ticks\n" +
                "            },\n" +
                "            legend: {\n" +
                "                enabled: false\n" +
                "            },\n" +
                "            tooltip: {\n" +
                "                backgroundColor: 'white',\n" +
                "                style: {\n" +
                "                    color: 'black',\n" +
                "                    fontSize: '10px',\n" +
                "                    padding: '5px',\n" +
                "                    fontFamily: 'Roboto',\n" +
                "                    boxShadow: '0px 0px 6px 0px rgba(48, 139, 249, 0.25);'\n" +
                "                },\n" +
                "                shared: true,\n" +
                "                valueDecimals: 2,\n" +
                "                formatter: function () {\n" +
                "                    return '<b>" + getScoreName(dataKey) + ":</b> ' + Highcharts.numberFormat(this.y, this.series.tooltipOptions.valueDecimals);\n" +
                "                }\n" +
                "            },\n" +
                "            series: [{\n" +
                "                name: '" + dataKey + "',\n" +
                "                data: seriesData,\n" +
                "                lineWidth: 2,\n" +
                "                lineColor: lineColor,\n" +
                "                states: {\n" +
                "                    hover: {\n" +
                "                        lineWidthPlus: 0\n" +
                "                    }\n" +
                "                },\n" +
                "                marker: {\n" +
                "                    enabled: false,\n" +
                "                    radius: 5,\n" +
                "                    lineWidth: 2,\n" +
                "                    lineColor: 'white',\n" +
                "                    fillColor: lineColor,\n" +
                "                    symbol: 'circle',\n" +
                "                },\n" +
                "                events: {\n" +
                "                    afterAnimate: function () {\n" +
                "                        if (markerEnabled) {\n" +
                "                            this.points[this.points.length - 1].update({\n" +
                "                                marker: {\n" +
                "                                    enabled: true\n" +
                "                                }\n" +
                "                            });\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
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





    public static String generateHealthScoreChartJs(String jsonData, WebView webView, String dataKey) {
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Health Score Chart</title>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "    <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Roboto\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <canvas id=\"chartCanvas\"></canvas>\n" +
                "    <script>\n" +
                "        var jsonData = " + jsonData + ";\n" +
                "        var categories = [];\n" +
                "        var healthScores = [];\n" +
                "        for (var i = 0; i < jsonData.length; i++) {\n" +
                "            var dateTime = jsonData[i].time;\n" +
                "            categories.push(dateTime);\n" +
                "            healthScores.push(parseFloat(jsonData[i]. "+ dataKey + " ));\n" +
                "        }\n" +
                "        var lastValue = healthScores[healthScores.length - 1];\n" +
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
                "        var ctx = document.getElementById('chartCanvas').getContext('2d');\n" +
                "        var chart = new Chart(ctx, {\n" +
                "            type: 'line',\n" +
                "            data: {\n" +
                "                labels: categories,\n" +
                "                datasets: [{\n" +
                "                    label: 'Health Score',\n" +
                "                    data: healthScores,\n" +
                "                    borderColor: lineColor,\n" +
                "                    backgroundColor: fillColor,\n" + // Added fill color
                "                    borderWidth: 2,\n" +
                "                    lineTension: 0.1,\n" +
                "                    pointBackgroundColor: pointBackgroundColor,\n" +
                "                    pointRadius: 3,\n" +
                "                }],\n" +
                "            },\n" +
                "            options: {\n" +
                "   animation: {duration: 0},"+
                "                plugins: {\n" +
                "                    legend: {\n" +
                "                        display: false,\n" +
                "                    },\n" +
                "                    tooltip: {\n" +
                "                        displayColors: false,\n" +
                "                        mode: 'nearest',\n" +
                "                        intersect: false,\n" +
                "                    },\n" +
                "                },\n" +
                "                scales: {\n" +
                "                    x: {\n" +
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
                "                            autoSkip: true,\n" +
                "                            maxTicksLimit: 5,\n" +
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
                "                             font: { size: 8 },\n" +
                "                            autoSkip: true,\n" +
                "                            maxTicksLimit: 7,\n" +
                "                        },\n" +
                "                        border: { display :false ,dash: [4,4], },\n" +
                "                        beginAtZero: false,\n" +
                "                    },\n" +
                "                },\n" +
                "                responsive: true,\n" +
                "                elements: {\n" +
                "                    area: {\n" +
                "                        backgroundColor: 'rgba(0, 0, 0, 0.1)', // Set chart area background color\n" +
                "                    },\n" +
                "                },\n" +
                "            },\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        return htmlContent;
    }







    public static String generateHealthScoreChart(String jsonData, WebView webView) {
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Health Score Chart</title>\n" +
                "    <script src=\"https://code.highcharts.com/highcharts.js\"></script>\n" +
                "    <script src=\"https://code.highcharts.com/modules/accessibility.js\"></script>\n" +
                "    <style>\n" +
                "        body, html, #container {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            width: 100%;\n" +
                "            height: 209px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"container\" ></div>\n" +
                "    <script>\n" +
                "        var jsonData = " + jsonData + ";\n" +
                "        var categories = [];\n" +
                "        var healthScores = [];\n" +
                "        for (var i = 0; i < jsonData.length; i++) {\n" +
                "            var dateTime = jsonData[i].time;\n" +
                "            categories.push(dateTime);\n" +
                "            healthScores.push(parseFloat(jsonData[i].overall_health_score));\n" +
                "        }\n" +
                "        var lastValue = healthScores[healthScores.length - 1];\n" +
                "        var lineColor = '#FF0000';\n" +
                "        var areaGradient = {\n" +
                "            linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 },\n" +

                "            stops: []\n" +
                "        };\n" +
                "        if (lastValue >= 80) {\n" +
                "            lineColor = '#3FAF58';\n" +
                "            areaGradient.stops.push([0, '#ABDCB5']);\n" +
                "            areaGradient.stops.push([1, 'rgba(171, 220, 181, 0.00) 100%']);\n" +
                "        } else if (lastValue >= 61) {\n" +
                "            lineColor = '#FFC412';\n" +
                "            areaGradient.stops.push([0, '#FFF2CC ']);\n" +
                "            areaGradient.stops.push([1, 'rgba(255, 242, 204, 0.00) 100%']);\n" +
                "        }\n" +
                "         else{\n" +
                "            lineColor = '#EA5455';\n" +
                "            areaGradient.stops.push([0,  '#FDE8E9' ]);\n" +
                "            areaGradient.stops.push([1, 'rgba(253, 232, 233, 0.00) 100%)']);\n" +
                "        }\n" +
                "        Highcharts.chart('container', {\n" +
                "  accessibility: {enabled: true },"+
                "            chart: {\n" +
                "                type: 'line',\n" +
                "events: { load: function() { var lastPoint = this.series[0].points[this.series[0].points.length - 1]; this.tooltip.refresh(lastPoint); lastPoint.update({ marker: { enabled: true, symbol: 'circle' } }); } },\n"+
                "            },\n" +
                "            title: {\n" +
                "                text: null\n" +
                "            },\n" +
                "            xAxis: {\n" +
                "                categories: categories,\n" +
                "                title: {\n" +
                "                    text: null\n" +
                "                },\n" +
                "                labels: {\n" +
                "                    style: {\n" +
                "                        fontSize: '8px',\n" +
                "                        color: '#535359',\n" +
                "                        fontFamily: 'Roboto'\n" +
                "                    },\n" +
                "                       step: Math.ceil(categories.length / 5),"+
                "                },\n" +
                "                lineWidth: 0, // Set x-axis line width to 0 to hide it\n" +
                "            },\n" +
                "            yAxis: {\n" +
                "                title: {\n" +
                "                    text: null\n" +
                "                },\n" +
                "                    tickPositions: [0, 20, 40, 60, 80, 100],\n" +
                "gridLineWidth: 1,gridLineDashStyle: 'Dash',"+
                "                min: 0,\n" +
                "                max: 100,\n" +
                "                labels: {\n" +
                "                    style: {\n" +
                "                        fontSize: '8px',\n" +
                "                        color: '#535359',\n" +
                "                        fontFamily: 'Roboto'\n" +

                "                    },\n" +
                "                },\n" +
                "            },\n" +
                "            series: [{\n" +
                "                name: 'Health Score',\n" +
                "                data: healthScores,\n" +
                "                color: lineColor,\n" +
                "                fillColor: {\n" +
                "                    linearGradient: areaGradient.linearGradient,\n" +
                "                    stops: areaGradient.stops\n" +
                "                }\n" +
                "            }],\n" +
                "            legend: {\n" +
                "                enabled: false\n" +
                "            },\n" +
                "            plotOptions: {\n" +
                "series: { states: { hover: { lineWidthPlus: 0 }, select: { lineWidthPlus: 0 } } },\n"+
                "                areaspline: {\n" +
                "                    lineWidth: 2,\n" +
                "                    marker: {\n" +
                "                        enabled: false\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        return htmlContent;
    }


    private static String getScoreName(String dataKey){
        switch (dataKey){
            case "overall_health_score" : return "Health Score";
            case "final_diabetic_score" : return "Diabetic Score";
            case "final_vital_score" : return "Vital Score";
            case "final_respiratory_score" : return "Respiratory Score";
            case "final_activity_score" : return "Activity Score";
            case "final_nutrition_score" : return "Nutrition Score";
            case "final_liver__score" : return "Liver Score";
            default:return "";
        }
    }
}



