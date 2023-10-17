package com.humorstech.respyr.suggestion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.humorstech.respyr.R;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BreakFastSuggestionAdapter extends RecyclerView.Adapter<BreakFastSuggestionAdapter.ViewHolder> {

    private List<BreakFastSuggestionDataModel> foodItems;
    private Context context;
    private Activity activity;

    public BreakFastSuggestionAdapter(List<BreakFastSuggestionDataModel> foodItems, Activity activity) {
        this.foodItems = foodItems;
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_suggestion_food_item_card, parent, false);
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
        String recommendedCarbohydratesStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_CARBOHYDRATE, null);
        String recommendedProteinStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_PROTEIN, null);
        String recommendedFibreStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_FIBER, null);
        String recommendedFatsStr = sharedPreferences.getString(ActiveResultData.RECOMMENDED_FATS, null);


        double recommendedCalories = Double.parseDouble(recommendedCaloriesStr);
        double recommendedCarbohydrates = Double.parseDouble(recommendedCarbohydratesStr);
        double recommendedProtein =Double.parseDouble(recommendedProteinStr);
        double recommendedFibre = Double.parseDouble(recommendedFibreStr);
        double recommendedFats = Double.parseDouble(recommendedFatsStr);


        double foodItemCalories = foodItem.getCalories();
        double foodItemCarbohydrate = foodItem.getCarbohydrates();
        double foodItemProtein =foodItem.getProtein();
        double foodItemFats = foodItem.getFat();
        double foodItemFibre = foodItem.getFiber();


        holder.caloriesTextView.setText(String.valueOf((int)foodItemCalories) + " kcal");
        holder.recommendedTextView.setText(String.valueOf((int)recommendedCalories) + " kcal");
        holder.carbohydratesTextView.setText(String.valueOf(foodItem.getCarbohydrates()) + "g");
        holder.proteinTextView.setText(String.valueOf(foodItem.getProtein()) + "g");
        holder.fatTextView.setText(String.valueOf(foodItem.getFat()) + "g");
        holder.fiberTextView.setText(String.valueOf(foodItem.getFiber()) + "g");

        System.out.println("Food Item Calories: " + foodItemCalories);
        System.out.println("Food Item Carbohydrates: " + foodItemCarbohydrate);
        System.out.println("Food Item Protein: " + foodItemProtein);
        System.out.println("Food Item Fats: " + foodItemFats);
        System.out.println("Food Item Fibre: " + foodItemFibre);

        System.out.println("Recommended Calories: " + extractNumber(recommendedCaloriesStr));
        System.out.println("Recommended Carbohydrates: " + extractNumber(recommendedCarbohydratesStr));
        System.out.println("Recommended Protein: " + extractNumber(recommendedProteinStr));
        System.out.println("Recommended Fibre: " + extractNumber(recommendedFibreStr));
        System.out.println("Recommended Fats: " + extractNumber(recommendedFatsStr));



        holder.progressCarbohydrate.setProgressPercentage(calculatePercentage(foodItemCarbohydrate, recommendedCarbohydrates), true);
        holder.progressCarbohydrate.setProgressPercentage(calculatePercentage(foodItemCarbohydrate, recommendedCarbohydrates), true);
        holder.progressProtein.setProgressPercentage(calculatePercentage(foodItemProtein, recommendedProtein), true);
        holder.progressFats.setProgressPercentage(calculatePercentage(foodItemFats, recommendedFibre), true);
        holder.progressFats.setProgressPercentage(calculatePercentage(foodItemFibre, recommendedFats), true);

        // Bind the data to the ViewHolder's views
        holder.foodNameTextView.setText(foodItem.getFoodName());

    }

    public static double calculatePercentage(double part, double total) {
        if (total == 0) {
            return 0; // Avoid division by zero
        }

        return (part / total) * 100;
    }

    public static double extractNumber(String input) {
        Pattern pattern = Pattern.compile("\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(input);



        if (matcher.find()) {
            String numberString = matcher.group(1);



            return Double.parseDouble(numberString);
        } else {
            return 0.0; // Return a default value if no match is found
        }
    }


    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, caloriesTextView, carbohydratesTextView, proteinTextView, fatTextView, fiberTextView;

        TextView recommendedTextView;
        RoundedProgressBar progressCarbohydrate, progressProtein, progressFats, progressFibre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            foodNameTextView = itemView.findViewById(R.id.txt_food_name);

            progressCarbohydrate = itemView.findViewById(R.id.progress_carbohydrate);
            progressProtein = itemView.findViewById(R.id.progress_protein);
            progressFats = itemView.findViewById(R.id.progress_fats);
            progressFibre = itemView.findViewById(R.id.progress_fibre);

            caloriesTextView = itemView.findViewById(R.id.txt_calories_value);
            recommendedTextView = itemView.findViewById(R.id.txt_calories_recommended_value);
            carbohydratesTextView = itemView.findViewById(R.id.txt_carbphydrate_value);
            proteinTextView = itemView.findViewById(R.id.txt_protein_value);
            fatTextView = itemView.findViewById(R.id.txt_fats_value);
            fiberTextView = itemView.findViewById(R.id.txt_fibre_value);

//            caloriesTextView = itemView.findViewById(R.id.txt_calories);
//            carbohydratesTextView = itemView.findViewById(R.id.txt_carbohydrates);
//            proteinTextView = itemView.findViewById(R.id.txt_protein);
//            fatTextView = itemView.findViewById(R.id.txt_fat);
//            fiberTextView = itemView.findViewById(R.id.txt_fiber);
        }
    }
}
