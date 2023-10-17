package com.humorstech.respyr.reading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.humorstech.respyr.R;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class LifeStyleAnalysis {
    public static  int unHealthy = 60;
    public static  int moderate = 80;
    public static  int healthy = 100;


    public static String[] titles = {"Good","Fair","Poor"};


    public static String[] titles1 = {"Keep it up","Step up a notch" ,"Attention Required"};
    public static String[] subTitles = {"Your doing a fair job. Step up your game to see better results.",
                                            "Your doing a fair job. Step up your game to see better results." ,
                                            "Take a step towards better health. You will not regret it. Make amends and track your progress to see the change."};


    public static String[] subTitles1 = {"Your nutrition needs attention. Make amends and track your progress to see the change.",
            "Your doing a fair job. Start tracking your nutrition to see better results." ,
            "Your nutrition level is exceedingly well. Don’t stop now. Keep at it to enhance your health score."};


    public static String[] status = {"Poor", "Step up a notch", "Fair"};



    @SuppressLint("SetTextI18n")
    public static void setLifeStyle(Context context, double score, String profileName, RoundedProgressBar roundedProgressBar,
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
        txtProfileName.setText(profileName +"," +"\nYour Activity Score is");

        if (score <= 100 && score >= 80) {


            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.green));
            txtDiabeticMessage.setText(titles1[0]);
            txtDiabeticSuggestion.setText(subTitles[0]);
            imageView.setImageResource(R.drawable.diabetic_badge);
            txtScore.setTextColor(ContextCompat.getColor(context, R.color.green));

        } else if (score >= 71  && score < 80) {

            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.yellow));
            txtDiabeticMessage.setText(titles1[1]);
            txtDiabeticSuggestion.setText(subTitles[1]);
            imageView.setImageResource(R.drawable.diabetic_badge);

            txtScore.setTextColor(ContextCompat.getColor(context, R.color.yellow));

        } else if (score >= 0 && score <= 70) {



            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.red));
            txtDiabeticMessage.setText(titles1[2]);
            txtDiabeticSuggestion.setText(subTitles[2]);
            imageView.setImageResource(R.drawable.badge_poor);

            txtScore.setTextColor(ContextCompat.getColor(context, R.color.red));
        }


    }

  @SuppressLint("SetTextI18n")
    public static void setNutrition(Context context, double score, String profileName, RoundedProgressBar roundedProgressBar,
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
        txtProfileName.setText(profileName +"," +"\nYour Nutrition Score is");

        if (score <= 100 && score >= 80) {


            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.green));
            txtDiabeticMessage.setText(titles1[0]);
            txtDiabeticSuggestion.setText(subTitles1[0]);
            imageView.setImageResource(R.drawable.diabetic_badge);

            txtScore.setTextColor(ContextCompat.getColor(context, R.color.green));

        } else if (score >= 71  && score < 80) {

            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.yellow));
            txtDiabeticMessage.setText(titles1[1]);
            txtDiabeticSuggestion.setText(subTitles1[1]);
            imageView.setImageResource(R.drawable.diabetic_badge);
            txtScore.setTextColor(ContextCompat.getColor(context, R.color.yellow));

        } else if (score >= 0 && score <= 70) {

            roundedProgressBar.setProgressDrawableColor(ContextCompat.getColor(context, R.color.red));
            txtDiabeticMessage.setText(titles1[2]);
            txtDiabeticSuggestion.setText(subTitles1[2]);
            imageView.setImageResource(R.drawable.badge_poor);
            txtScore.setTextColor(ContextCompat.getColor(context, R.color.red));


        }


    }







}
