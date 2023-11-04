package com.humorstech.task2_uphar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.anton46.stepsview.StepsView;
import com.humorstech.respyr.R;
import com.humorstech.task1_uphar.GaugeView;

public class activity_task2 extends AppCompatActivity {

    private StepsView stepsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
//        stepsView=findViewById(R.id.stepsView);
//        String[] stepsArray = {"Personal Info", "BMI", "Lifestyle", "Medical History"};
//
//
//
//        stepsView.setLabels(stepsArray)
//                .setBarColorIndicator(Color.GRAY )
//                .setProgressColorIndicator(Color.rgb(63, 175, 88))
//                .setLabelColorIndicator(Color.BLACK)
//                .setCompletedPosition(2)
//                .drawView();
        GaugeView gaugeView =findViewById(R.id.gaugeView);
        gaugeView.setAdapter(new GaugeView.Adapter4Test());
        gaugeView.setLabel("Shubham, \n" +
                "Your Vital score is");
        gaugeView.animateToValue(20f);
        gaugeView.setCurrent(20);
    }
}