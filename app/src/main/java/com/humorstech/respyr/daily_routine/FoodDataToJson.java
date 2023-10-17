package com.humorstech.respyr.daily_routine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodDataToJson {

    public static JSONArray convertToJSON(String foodNameString, String foodQuantityString, String foodIdString, String foodCatString, String foodImagesString) {
        // Split the dynamic strings into arrays
        String[] foodNameArray = foodNameString.split("!");
        String[] foodQuantityArray = foodQuantityString.split("!");
        String[] foodIdArray = foodIdString.split("!");
        String[] foodCatArray = foodCatString.split("!");
        String[] foodImagesArray = foodImagesString.split("!");

        // Initialize lists to store the data
        List<String> foodNames = new ArrayList<>();
        List<Integer> foodQuantities = new ArrayList<>();
        List<Integer> foodIds = new ArrayList<>();
        List<String> foodCategories = new ArrayList<>();
        List<String> foodImages = new ArrayList<>();

        // Populate the lists with data
        for (String item : foodNameArray) {
            String[] keyValue = item.split("=");
            if (keyValue.length == 2) {
                foodNames.add(keyValue[1]);
            }
        }

        for (String item : foodQuantityArray) {
            String[] keyValue = item.split("=");
            if (keyValue.length == 2) {
                try {
                    foodQuantities.add(Integer.parseInt(keyValue[1]));
                } catch (NumberFormatException e) {
                    // Handle invalid quantity values
                }
            }
        }

        for (String item : foodIdArray) {
            String[] keyValue = item.split("=");
            if (keyValue.length == 2) {
                try {
                    foodIds.add(Integer.parseInt(keyValue[1]));
                } catch (NumberFormatException e) {
                    // Handle invalid ID values
                }
            }
        }

        for (String item : foodCatArray) {
            String[] keyValue = item.split("=");
            if (keyValue.length == 2) {
                foodCategories.add(keyValue[1]);
            }
        }

        for (String item : foodImagesArray) {
            String[] keyValue = item.split("=");
            if (keyValue.length == 2) {
                foodImages.add(keyValue[1]);
            }
        }

        // Create a JSONArray to store all food items
        JSONArray foodItemsArray = new JSONArray();

        // Iterate through the lists and create JSON objects for each food item
        for (int i = 0; i < foodNames.size(); i++) {
            JSONObject foodItem = new JSONObject();
            try {
                foodItem.put("name", foodNames.get(i));
                foodItem.put("quantity", foodQuantities.get(i));
                foodItem.put("id", foodIds.get(i));
                foodItem.put("category", foodCategories.get(i));
                foodItem.put("image", foodImages.get(i));

                // Add the JSON object to the array
                foodItemsArray.put(foodItem);
            } catch (JSONException e) {
                // Handle any JSON-related exceptions here
            }
        }

        // Return the JSON array containing all the food items
        return foodItemsArray;
    }
}
