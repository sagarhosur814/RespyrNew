package com.humorstech.respyr.suggestion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.humorstech.respyr.R;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodSuggestionAdapterBasedOnTime extends RecyclerView.Adapter<FoodSuggestionAdapterBasedOnTime.ViewHolder> {

    private List<BreakFastSuggestionDataModel> foodItems;
    private Context context;
    private Activity activity;

    public FoodSuggestionAdapterBasedOnTime(List<BreakFastSuggestionDataModel> foodItems, Activity activity) {
        this.foodItems = foodItems;
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_suggestion_based_on_time_card, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BreakFastSuggestionDataModel foodItem = foodItems.get(position);


        //// get current result calories and other data
        //// recommended nutrition values

        SharedPreferences sharedPreferences = activity.getSharedPreferences(ActiveResultData.TITLE, Context.MODE_PRIVATE);

        String recommendedCaloriesStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_CALORIES, null);

        double recommendedCalories = Double.parseDouble(recommendedCaloriesStr);
        double foodItemCalories = foodItem.getCalories();


        holder.caloriesTextView.setText(String.valueOf((int)foodItemCalories) + " kcal" + "\t|\t" + String.valueOf((int)recommendedCalories) + " kcal");

        // Bind the data to the ViewHolder's views
        holder.foodNameTextView.setText(foodItem.getFoodName());

    }


    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, caloriesTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.txt_food_name);
            caloriesTextView = itemView.findViewById(R.id.txt_calories_value);
        }
    }
}