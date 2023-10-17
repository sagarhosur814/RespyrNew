package com.humorstech.respyr.results;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.humorstech.respyr.R;
import com.humorstech.respyr.SmoothScrollSnapHelper;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.reading.BlowScoreAnalysis;
import com.humorstech.respyr.reading.DiabeticAnalysis;
import com.humorstech.respyr.reading.LifeStyleAnalysis;
import com.humorstech.respyr.reading.VitalAnalysis;
import com.humorstech.respyr.results.activity.ActivityScore;
import com.humorstech.respyr.suggestion.BreakFastSuggestionDataModel;
import com.humorstech.respyr.suggestion.FoodSuggestionAdapterBasedOnTime;
import com.humorstech.respyr.suggestion.Suggestion;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class DetailedResultAnalysis {

    private Context context;
    private Activity activity;

    private FragmentManager fragmentManager;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    BottomSheetDialog bottomSheetDialog;
    DetailedResultAnalysis(Activity activity, FragmentManager fragmentManager){
        this.activity = activity;
        this.context = activity;
        this.fragmentManager = fragmentManager;
    }

    @SuppressLint("InflateParams")
    public void showCancelBottomSheet(ArrayList<Object> sheetData) {


        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout for the BottomSheetDialog
        View view = inflater.inflate(R.layout.detailed_result_bottom_sheet, null);

        // Create and configure the BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.TransparentDialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setDimAmount(0);

        // Get the BottomSheetBehavior from the inflated view
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        // Disable user interactions with the BottomSheetDialog
        bottomSheetDialog.setCancelable(false);


        // Find the LockableScrollView in the inflated view
        LockableScrollView scrollView = view.findViewById(R.id.scrollview);




        // Set a listener to intercept the back button press
        bottomSheetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

                    Intent intent = new Intent(context, MainActivity.class);
                    activity.startActivity(intent);
                }
                return false; // Let the event be handled normally
            }
        });




        // Set the peek height of the BottomSheet
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        float scale = context.getResources().getDisplayMetrics().density;
        // int headerHeight = 590;
        //float pixels = headerHeight * scale + 0.5f;
        //  bottomSheetBehavior.setPeekHeight(screenHeight - (int) pixels);




        LinearLayout llHeader=bottomSheetDialog.findViewById(R.id.header);

        assert llHeader != null;
        ViewTreeObserver vto = llHeader.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This will be called when the header layout has been measured and laid out
                int llHeaderHeight = llHeader.getHeight();

                // Remove the listener to avoid multiple calls
                llHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Now you can set the peek height of the BottomSheetBehavior
                bottomSheetBehavior.setPeekHeight(llHeaderHeight);
            }
        });


        Button llViewTrend =bottomSheetDialog.findViewById(R.id.button_health_score_view_trend);
        Button buttonViewCompleteAnalysis =bottomSheetDialog.findViewById(R.id.button_view_analysis);

        assert llViewTrend != null;
        assert buttonViewCompleteAnalysis != null;
        llViewTrend.setVisibility(View.GONE);
        buttonViewCompleteAnalysis.setVisibility(View.VISIBLE);


        Animation visibleAnim = AnimationUtils.loadAnimation(context, R.anim.visible_anim);
        Animation hideAnim = AnimationUtils.loadAnimation(context, R.anim.visible_anim);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // This method is called when the state of the bottom sheet changes
                // newState can be one of STATE_COLLAPSED, STATE_EXPANDED, STATE_HIDDEN, etc.
                // You can perform actions based on the new state here


                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Perform actions when the bottom sheet is in the collapsed state
                    //  llViewTrend.setVisibility(View.GONE);
                    llViewTrend.setVisibility(View.GONE);
                    llViewTrend.startAnimation(hideAnim);
//

                    buttonViewCompleteAnalysis.setVisibility(View.VISIBLE);
                    buttonViewCompleteAnalysis.startAnimation(visibleAnim);

                    int llHeaderHeight = llHeader.getHeight();
                    // Now you can set the peek height of the BottomSheetBehavior
                    bottomSheetBehavior.setPeekHeight(llHeaderHeight);


                    scrollView.setScrollingEnabled(false);
                    scrollView.smoothScrollTo(0, 0); // Scrolls to the top


                }else if(newState == BottomSheetBehavior.STATE_EXPANDED){

                    llViewTrend.setVisibility(View.VISIBLE);
                    llViewTrend.startAnimation(visibleAnim);

                    buttonViewCompleteAnalysis.setVisibility(View.GONE);

                    int llHeaderHeight = llHeader.getHeight();
                    // Now you can set the peek height of the BottomSheetBehavior
                    bottomSheetBehavior.setPeekHeight(llHeaderHeight);

                    scrollView.setScrollingEnabled(true);
                    scrollView.smoothScrollTo(0, 0); // Scrolls to the top


                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // This method is called when the bottom sheet is being dragged or settling
                // slideOffset represents the slide offset in the range of 0 to 1
                // You can perform actions based on the slide offset here
            }
        });




        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setSkipCollapsed(false);
        // Show the BottomSheetDialog
        bottomSheetDialog.show();

        String profileName = (String) sheetData.get(0);
        double healthScore = (double) sheetData.get(1);
        double diabeticScore = (double) sheetData.get(2);
        double vitalScore = (double) sheetData.get(3);
        double blowScore = (double) sheetData.get(4);
        double activityScore = (double) sheetData.get(5);
        double nutritionScore = (double) sheetData.get(6);

        // Perform various actions within the BottomSheetDialog
        performHealthScore(profileName, healthScore);
        performDiabeticScore(profileName, diabeticScore);
        performVitalScore(profileName, vitalScore);
        performBlowScore(profileName, blowScore);
        performLifeStyleScore(profileName, activityScore);
        performNutritionScore(profileName, nutritionScore);

        performFoodSuggestionBasedOnTime();


        //////////////////// on clicks
        Button buttonViewSuggestion=bottomSheetDialog.findViewById(R.id.button_suggestion_1);
        assert buttonViewSuggestion != null;
        buttonViewSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Suggestion.class);
                activity.startActivity(intent);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void performHealthScore(String profileName, double healthScore){

        TextView txtHealthScoreProfileName = bottomSheetDialog.findViewById(R.id.txt_profile_name);
        TextView txtHealthScore = bottomSheetDialog.findViewById(R.id.txt_overall_health_score);
        RoundedProgressBar progressHealthScore =bottomSheetDialog.findViewById(R.id.progress_overall_health_score);

        ImageButton imgDiabeticShare = bottomSheetDialog.findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=bottomSheetDialog.findViewById(R.id.button_diabetic_track_life_style);


        double roundedValue = Math.round(healthScore);

        if (healthScore - Math.floor(healthScore) >= 0.5) {
            roundedValue = Math.ceil(healthScore);
        }


        // set progress bar percentage
        int scoreNew = (int) roundedValue;
        assert progressHealthScore != null;
        progressHealthScore.setProgressPercentage(scoreNew, true);


        // set profile name
        assert txtHealthScore != null;
        assert txtHealthScoreProfileName != null;
        txtHealthScore.setText(String.valueOf(scoreNew) + "%");
        txtHealthScoreProfileName.setText(profileName +"," +"\nYour Diabetic Score is");




        /// set color according to health score ranges
        if (healthScore <= 100 && healthScore >= 80) {
            progressHealthScore.setProgressDrawableColor(ContextCompat.getColor(context, R.color.green));
            txtHealthScore.setTextColor(context.getResources().getColor(R.color.green));
        } else if (healthScore >= 71  && healthScore < 80) {
            progressHealthScore.setProgressDrawableColor(ContextCompat.getColor(context, R.color.yellow));
            txtHealthScore.setTextColor(context.getResources().getColor(R.color.yellow));

        } else if (healthScore >= 0 && healthScore <= 70) {
            progressHealthScore.setProgressDrawableColor(ContextCompat.getColor(context, R.color.red));
            txtHealthScore.setTextColor(context.getResources().getColor(R.color.red));
        }


        Button buttonViewAnalysis = bottomSheetDialog.findViewById(R.id.button_view_analysis);
        Button buttonViewTrend = bottomSheetDialog.findViewById(R.id.button_health_score_view_trend);
        buttonViewAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

     //   showBottomSheet("overall_health_score",healthScore);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("overall_health_score",healthScore);
            }
        });


    }

    private void performDiabeticScore(String profileName, double diabeticScore){

        TextView txtDiabeticProfileName = bottomSheetDialog.findViewById(R.id.txt_diabetic_score_profile_name);
        TextView txtDiabeticScore = bottomSheetDialog.findViewById(R.id.txt_diabetic_score);
        TextView txtDiabeticMessage = bottomSheetDialog.findViewById(R.id.txt_diabetic_message);
        TextView txtDiabeticSubMessage = bottomSheetDialog.findViewById(R.id.txt_diabetic_sub_message);
        ImageView txtImgDiabeticStatus = bottomSheetDialog.findViewById(R.id.img_diabetic_warning);

        ImageButton imgDiabeticShare = bottomSheetDialog.findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=bottomSheetDialog.findViewById(R.id.button_diabetic_track_life_style);


        RoundedProgressBar diabeticProgress =bottomSheetDialog.findViewById(R.id.progress_diabetic_score);


        DiabeticAnalysis.setDiabetic(context,
                diabeticScore, // diabetic score
                profileName, // profile name
                diabeticProgress,
                txtDiabeticScore,
                txtDiabeticProfileName,
                txtDiabeticSubMessage,
                txtDiabeticMessage,
                txtImgDiabeticStatus );

        Button buttonViewTrend=bottomSheetDialog.findViewById(R.id.button_view_trend_diabetic);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_diabetic_score", diabeticScore);
            }
        });

    }
    private void performVitalScore(String profileName, double vitalScore){
        TextView txtVitalProfileName = bottomSheetDialog.findViewById(R.id.txt_vital_score_profile_name);
        TextView txtVitalScore = bottomSheetDialog.findViewById(R.id.txt_vital_score);
        TextView txtVitalMessage = bottomSheetDialog.findViewById(R.id.txt_vital_message);
        TextView txtVitalSubMessage = bottomSheetDialog.findViewById(R.id.txt_vital_sub_message);
        ImageView imgVitalStatus = bottomSheetDialog.findViewById(R.id.img_vital_warning);

        RoundedProgressBar vitalProgress =bottomSheetDialog.findViewById(R.id.progress_vital_score);


        ImageButton imgDiabeticShare = bottomSheetDialog.findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=bottomSheetDialog.findViewById(R.id.button_diabetic_track_life_style);

        VitalAnalysis.setVital(
                context,
                vitalScore,
                profileName,
                vitalProgress,
                txtVitalScore,
                txtVitalProfileName,
                txtVitalSubMessage,
                txtVitalMessage,
                imgVitalStatus );


        Button buttonViewTrend=bottomSheetDialog.findViewById(R.id.button_view_trend_vital_score);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_vital_score", vitalScore);
            }
        });
    }
    private void performBlowScore(String profileName, double blowScore){
        TextView txtBlowProfileName = bottomSheetDialog.findViewById(R.id.txt_blow_score_profile_name);
        TextView txtBlowScore = bottomSheetDialog.findViewById(R.id.txt_blow_score);
        TextView txtBlowScoreMessage = bottomSheetDialog.findViewById(R.id.txt_blow_message);
        TextView txtBlowScoreSubMessage = bottomSheetDialog.findViewById(R.id.txt_blow_sub_message);
        ImageView imgBlowStatus = bottomSheetDialog.findViewById(R.id.img_blow_warning);
        RoundedProgressBar blowProgress =bottomSheetDialog.findViewById(R.id.progress_blow_score);

        ImageButton imgDiabeticShare = bottomSheetDialog.findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=bottomSheetDialog.findViewById(R.id.button_diabetic_track_life_style);


        BlowScoreAnalysis.setBlow(context,
                blowScore,
                profileName,
                blowProgress,
                txtBlowScore,
                txtBlowProfileName,
                txtBlowScoreSubMessage,
                txtBlowScoreMessage,
                imgBlowStatus );



        Button buttonViewTrend=bottomSheetDialog.findViewById(R.id.button_view_trend_blow);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_respiratory_score", blowScore);
            }
        });

    }
    private void performLifeStyleScore(String profileName, double lifeStyleScore){
        TextView txtLifeStyleProfileName = bottomSheetDialog.findViewById(R.id.txt_life_style_score_profile_name);
        TextView txtLifeStyleScore = bottomSheetDialog.findViewById(R.id.txt_lifestyle_score);
        TextView txtLifeStyleMessage = bottomSheetDialog.findViewById(R.id.txt_lifestyle_message);
        TextView txtLifeStyleSubMessage = bottomSheetDialog.findViewById(R.id.txt_lifestyle_sub_message);
        ImageView imgLifeStyleStatus = bottomSheetDialog.findViewById(R.id.img_lifestyle_warning);
        RoundedProgressBar blowProgress =bottomSheetDialog.findViewById(R.id.progress_lifestyle_score);

        ImageButton imgDiabeticShare = bottomSheetDialog.findViewById(R.id.button_share_diabetic);
        Button buttonTrackLifeStyle=bottomSheetDialog.findViewById(R.id.button_tract_activity_score);




        LifeStyleAnalysis.setLifeStyle(context,
                lifeStyleScore,
                profileName,
                blowProgress,
                txtLifeStyleScore,
                txtLifeStyleProfileName,
                txtLifeStyleSubMessage,
                txtLifeStyleMessage,
                imgLifeStyleStatus );


        buttonTrackLifeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityScore.class);
                activity.startActivity(intent);
            }
        });

        Button buttonViewTrend=bottomSheetDialog.findViewById(R.id.button_view_trend_lifestyle);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_activity_score", lifeStyleScore);
            }
        });

    }

    private void performNutritionScore(String profileName, double nutritionScore){
        TextView txtNutritionProfileName = bottomSheetDialog.findViewById(R.id.txt_nutrition_score_profile_name);
        TextView txtNutritionScore = bottomSheetDialog.findViewById(R.id.txt_nutrition_score);
        TextView txtNutritionMessage = bottomSheetDialog.findViewById(R.id.txt_nutrition_message);
        TextView txtNutritionSubMessage = bottomSheetDialog.findViewById(R.id.txt_nutrition_sub_message);
        ImageView imgNutritionStatus = bottomSheetDialog.findViewById(R.id.img_lifestyle_warning);
        RoundedProgressBar nutritionProgress =bottomSheetDialog.findViewById(R.id.progress_nutrition_score);

        ImageButton imgDiabeticShare = bottomSheetDialog.findViewById(R.id.button_share_diabetic);
        Button buttonTrackNutrition=bottomSheetDialog.findViewById(R.id.button_track_nutrition);


        LifeStyleAnalysis.setNutrition(context,
                nutritionScore,
                profileName,
                nutritionProgress,
                txtNutritionScore,
                txtNutritionProfileName,
                txtNutritionSubMessage,
                txtNutritionMessage,
                imgNutritionStatus );


        Button buttonViewTrend=bottomSheetDialog.findViewById(R.id.button_view_trend_nutrition);
        assert buttonViewTrend != null;
        buttonViewTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("final_nutrition_score", nutritionScore);
            }
        });

        buttonTrackNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NutritionScore.class);
                activity.startActivity(intent);
            }
        });

    }


    public void showBottomSheet(String dataKey, double currentScore) {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(activity,dataKey,currentScore);
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
    }

    @SuppressLint("SetTextI18n")
    private void performFoodSuggestionBasedOnTime(){

        TextView txtMealTime = bottomSheetDialog.findViewById(R.id.txt_meal_time);
        TextView txtTime1 = bottomSheetDialog.findViewById(R.id.txt_time_1);
        TextView txtTime2 = bottomSheetDialog.findViewById(R.id.txt_time_2);
        TextView txtTime3 = bottomSheetDialog.findViewById(R.id.txt_time_3);

        RecyclerView recyclerView =bottomSheetDialog.findViewById(R.id.food_suggestion_list);
        ScrollingPagerIndicator indicator =bottomSheetDialog.findViewById(R.id.indicator);

        SharedPreferences preferences = context.getSharedPreferences(ActiveResultData.TITLE, MODE_PRIVATE);
        String breakfastSuggestions = preferences.getString(ActiveResultData.BREAKFAST_SUGGESTION, null);
        String lunchSuggestions = preferences.getString(ActiveResultData.LUNCH_SUGGESTION, null);
        String dinnerSuggestions = preferences.getString(ActiveResultData.DINNER_SUGGESTION, null);



        assert txtTime1 != null;
        txtTime1.setText(TimeBasedMeal.getFormattedTime());
        assert txtTime2 != null;
        txtTime2.setText(TimeBasedMeal.getFormattedTime());
        assert txtTime3 != null;
        txtTime3.setText(TimeBasedMeal.getFormattedTime());


        switch (TimeBasedMeal.getMealTime()){
            case  "breakfast" : txtMealTime.setText("It's breakfast time");
                setSuggestionList(recyclerView , breakfastSuggestions, indicator);
                break;
            case  "lunch" : txtMealTime.setText("It's lunch time");
                setSuggestionList(recyclerView , lunchSuggestions, indicator);
                break;
            case  "snack" : txtMealTime.setText("It's snack time");
                setSuggestionList(recyclerView , breakfastSuggestions, indicator);
                break;
            case  "dinner" : txtMealTime.setText("It's dinner time");
                setSuggestionList(recyclerView , dinnerSuggestions, indicator);break;

        }


        Button buttonViewSuggestion =bottomSheetDialog.findViewById(R.id.button_view_all_suggestion);
        assert buttonViewSuggestion != null;
        buttonViewSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(context, Suggestion.class);
                    activity.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void setSuggestionList(RecyclerView recyclerView, String data, ScrollingPagerIndicator recyclerIndicator) {
        List<BreakFastSuggestionDataModel> foodItems = new ArrayList<>();
        String[] lines = data.split("\n");

        try {
            for (int i = 1; i < lines.length; i++) {
                String[] parts = lines[i].split(": ");
                if (parts.length != 2) {
                    // Handle data format error
                    continue; // Skip this line and move to the next one
                }

                String name = parts[0];
                String[] values = parts[1].split(", ");
                if (values.length != 5) {
                    // Handle values format error
                    continue; // Skip this line and move to the next one
                }

                double calories = Double.parseDouble(values[0].replaceAll("[^0-9]", ""));
                double carbohydrates = Double.parseDouble(values[1].replaceAll("[^0-9]", ""));
                double protein = Double.parseDouble(values[2].replaceAll("[^0-9]", ""));
                double fat = Double.parseDouble(values[3].replaceAll("[^0-9.]", ""));
                double fiber = Double.parseDouble(values[4].replaceAll("[^0-9.]", ""));

                BreakFastSuggestionDataModel foodItem = new BreakFastSuggestionDataModel(name, calories, carbohydrates, protein, fat, fiber);
                foodItems.add(foodItem);
            }

            FoodSuggestionAdapterBasedOnTime adapter = new FoodSuggestionAdapterBasedOnTime(foodItems, activity);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);

            // Set dots
            recyclerIndicator.attachToRecyclerView(recyclerView);

            // Attach the custom SnapHelper
            SmoothScrollSnapHelper snapHelper = new SmoothScrollSnapHelper(adapter);
            snapHelper.attachToRecyclerView(recyclerView);

        } catch (NumberFormatException e) {
            // Handle number parsing error
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }








}