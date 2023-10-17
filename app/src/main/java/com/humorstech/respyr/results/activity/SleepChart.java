package com.humorstech.respyr.results.activity;

import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;
import com.humorstech.respyr.R;

import java.util.ArrayList;

public class SleepChart {

    public static void  drawChart(ChartProgressBar mChart, ArrayList<BarData> dataList){
        mChart.setMaxValue(24.0f);
        mChart.setDataList(dataList);
        mChart.build();
    }
}
