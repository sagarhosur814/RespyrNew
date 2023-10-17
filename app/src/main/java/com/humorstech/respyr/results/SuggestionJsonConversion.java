package com.humorstech.respyr.results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SuggestionJsonConversion {


    public static String convertToJSON(String breakfastSuggestions) {
        JSONArray foodArray = new JSONArray();

        String[] lines = breakfastSuggestions.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty() || line.contains("Randomly selected foods for Breakfast:")) {
                continue;
            }

            String[] parts = line.split(": ");
            if (parts.length < 2) {
                continue;
            }

            String foodName = parts[0];
            String[] nutrients = parts[1].split(", ");
            JSONObject foodObject = new JSONObject();

            try {
                foodObject.put("Name", foodName);
                for (String nutrient : nutrients) {
                    String[] nutrientParts = nutrient.split("\\(");
                    String nutrientName = nutrientParts[0].trim();
                    String nutrientValue = nutrientParts[1].replaceAll("[)]", "");
                    foodObject.put(nutrientName, nutrientValue);
                }
                foodArray.put(foodObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BreakfastSuggestions", foodArray);
            return jsonObject.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
