package com.humorstech.respyr.reading.food_breakup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.humorstech.respyr.R;
import com.humorstech.respyr.preferences.ActiveResultData;

import java.util.List;


public class FoodBreakupFats extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    List<FoodBreakupData> foodItemList;
    public FoodBreakupFats(List<FoodBreakupData> foodItemList) {
        // Required empty public constructor
        this.foodItemList=foodItemList;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private FoodBreakupAdapter adapter;

    private static final String TAG = "FoodBreakupCalories";
    private TextView txtFoodBreakupActualCalories, txtFoodBreakupRecommendedCalories, txtFoodBreakupDifference;
    private ImageView imgFoodBreakupCat;

    private double curr_fat;
    private double reco_fat;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_breakup_fats, container, false);

        retrieveData();
        recyclerView =view.findViewById(R.id.food_breakup_list);

        adapter = new FoodBreakupAdapter(foodItemList, getContext(), 4);

        // Set the layout manager and adapter for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        recyclerView = view.findViewById(R.id.food_breakup_list);
        txtFoodBreakupActualCalories = view.findViewById(R.id.txt_food_breakup_actual);
        txtFoodBreakupRecommendedCalories = view.findViewById(R.id.txt_food_breakup_recommended);
        txtFoodBreakupDifference = view.findViewById(R.id.txt_food_breakup_difference);
        imgFoodBreakupCat = view.findViewById(R.id.img_food_breakup_cat);

        caloriesAnalysis();


        return view;
    }

    @SuppressLint("SetTextI18n")
    private void caloriesAnalysis(){

        double actualCalories =curr_fat;
        double recommendedCalories =reco_fat;
        double differenceCalories =recommendedCalories-actualCalories;



        String differenceMessage;

        if (differenceCalories<0){
            int positiveI = (int)Math.abs(differenceCalories);

            String htmlString = "<b>"+positiveI+" g</b>";
            Spanned spannedText = Html.fromHtml(htmlString);
            differenceMessage= name+","+"\n,Your food intake exceeds "+spannedText;


        }else if(differenceCalories==0){
            String htmlString = (int)recommendedCalories+" <b> g</b>";
            Spanned spannedText = Html.fromHtml(htmlString);
            differenceMessage= name+","+"\nYour food intake matches the recommended calorie intake of "+spannedText;
        }
        else{
            String htmlString = (int)differenceCalories+" <b> g</b>";
            Spanned spannedText = Html.fromHtml(htmlString);
            differenceMessage= name+","+"\nYour food intake lacks "+spannedText;
        }

        imgFoodBreakupCat.setImageResource(R.drawable.avocado_dark);
        txtFoodBreakupActualCalories.setText(String.valueOf((int)actualCalories)+" g");
        txtFoodBreakupRecommendedCalories.setText("/"+String.valueOf((int)recommendedCalories)+" g");
        txtFoodBreakupDifference.setText(differenceMessage);
    }

    private void retrieveData(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(ActiveResultData.TITLE, Context.MODE_PRIVATE);
        String curr_calStr = sharedPreferences.getString(ActiveResultData.ACTUAL_CALORIES, "");
        String curr_carStr = sharedPreferences.getString(ActiveResultData.ACTUAL_CARBOHYDRATE, "");
        String curr_proStr = sharedPreferences.getString(ActiveResultData.ACTUAL_PROTEIN, "");
        String curr_fatStr = sharedPreferences.getString(ActiveResultData.ACTUAL_FATS, "");
        String curr_fibStr = sharedPreferences.getString(ActiveResultData.ACTUAL_FIBER, "");

        String reco_calStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_CALORIES, "");
        String reco_carStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_CARBOHYDRATE, "");
        String reco_proStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_PROTEIN, "");
        String reco_fatStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_FATS, "");
        String reco_fibStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_FIBER, "");
        name = sharedPreferences.getString(ActiveResultData.PROFILE_NAME, "");


        double curr_cal;
        try {
            curr_cal = Double.parseDouble(curr_calStr);
        } catch (NumberFormatException e) {
            // Handle the case where "curr_calStr" cannot be parsed to a double
            curr_cal = 0.0; // Assign a default value or handle the error as needed
        }

        double curr_car;
        try {
            curr_car = Double.parseDouble(curr_carStr);
        } catch (NumberFormatException e) {
            curr_car = 0.0;
        }

        double curr_pro;
        try {
            curr_pro = Double.parseDouble(curr_proStr);
        } catch (NumberFormatException e) {
            curr_pro = 0.0;
        }

        try {
            curr_fat = Double.parseDouble(curr_fatStr);
        } catch (NumberFormatException e) {
            curr_fat = 0.0;
        }

        double curr_fib;
        try {
            curr_fib = Double.parseDouble(curr_fibStr);
        } catch (NumberFormatException e) {
            curr_fib = 0.0;
        }

        double reco_cal;
        try {
            reco_cal = Double.parseDouble(reco_calStr);
        } catch (NumberFormatException e) {
            reco_cal = 0.0;
        }

        double reco_car;
        try {
            reco_car = Double.parseDouble(reco_carStr);
        } catch (NumberFormatException e) {
            reco_car = 0.0;
        }

        double reco_pro;
        try {
            reco_pro = Double.parseDouble(reco_proStr);
        } catch (NumberFormatException e) {
            reco_pro = 0.0;
        }

        try {
            reco_fat = Double.parseDouble(reco_fatStr);
        } catch (NumberFormatException e) {
            reco_fat = 0.0;
        }

        double reco_fib;
        try {
            reco_fib = Double.parseDouble(reco_fibStr);
        } catch (NumberFormatException e) {
            reco_fib = 0.0;
        }



    }

}