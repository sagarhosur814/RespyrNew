package com.humorstech.respyr.reading;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.humorstech.respyr.R;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import java.util.Random;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class DiabeticAnalysis {
    public static  int unHealthy = 60;
    public static  int moderate = 80;
    public static  int healthy = 100;

    public static String[] status = {"Poor", "Step up a notch", "Fair"};
    public static String[] titles = {"Keep it up","Step up a notch" ,"Attention Required"};
    public static String[] subTitles = {
            "Your diabetes risk level is low which is great! Let’s continue to track your lifestyle to maintain it.",
            "Your diabetic risk level is moderate and needs some work. Go the extra mile! Modify your lifestyle to see better results.",
            "Your diabetic risk level is high & might be a cause of concern. Give it your all! Modify your lifestyle to get back on track."
    };

    @SuppressLint("SetTextI18n")
    public static void setDiabetic(Context context, double score, String profileName, RoundedProgressBar roundedProgressBar,
                                   TextView txtScore, TextView txtProfileName, TextView txtDiabeticSuggestion, TextView txtDiabeticMessage, ImageView imageView) {
        double roundedValue = Math.round(score);

        if (score - Math.floor(score) >= 0.5) {
            roundedValue = Math.ceil(score);
        }

        int scoreNew = (int) roundedValue;
        roundedProgressBar.setProgressPercentage(scoreNew, true);


        String good = "which is <b><font color=\"#3FAF58\">" + "good" + "</font></b>";
        String fair = "which is <b><font color=\"#F8B10F\">" + "fair" + "</font></b>";
        String poor = "which is <b><font color=\"#EA5455\">" + "bad" + "</font></b>";

        txtScore.setText(String.valueOf(scoreNew) + "%");
        txtProfileName.setText(profileName +"," +"\nYour Diabetic Score is");


        if (score <= 100 && score >= 80) {


            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.green));
            txtDiabeticMessage.setText(titles[0]);
            txtDiabeticSuggestion.setText(subTitles[0]);
            imageView.setImageResource(R.drawable.diabetic_badge);
            txtScore.setTextColor(ContextCompat.getColor(context, R.color.green));

        } else if (score >= 71  && score < 80) {

            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.yellow));
            txtDiabeticMessage.setText(titles[1]);
            txtDiabeticSuggestion.setText(subTitles[1]);
            imageView.setImageResource(R.drawable.diabetic_badge);
            txtScore.setTextColor(ContextCompat.getColor(context, R.color.yellow));

        } else if (score >= 0 && score <= 70) {



            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.red));
            txtDiabeticMessage.setText(titles[2]);
            txtDiabeticSuggestion.setText(subTitles[2]);
            imageView.setImageResource(R.drawable.badge_poor);
            txtScore.setTextColor(ContextCompat.getColor(context, R.color.red));


        }




    }

    private static int getRandomIndex(int length) {
        Random random = new Random();
        return random.nextInt(length);
    }

    @SuppressLint("SetTextI18n")
    private static void setTitlesAndSubtitles(String title, String subtitle, TextView txtTitle, TextView txtDescription , String profileName) {
        Log.d("DiabeticAnalysis", "Title: " + title );
        Log.d("DiabeticAnalysis", "Subtitle: " + subtitle);
        txtTitle.setText(title+" "+profileName + "!");
        txtDescription.setText(subtitle);
    }
}






