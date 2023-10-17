package com.humorstech.respyr.trends;

import com.humorstech.respyr.dashboard.TimelineItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Methods {

    public static List<TimelineItem> parseJsonData(String jsonData) {
        List<TimelineItem> dataList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = jsonObject.getInt("id");
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");
                String overallHealthScore = jsonObject.getString("overall_health_score");
                String finalDiabeticScore = jsonObject.getString("final_diabetic_score");
                String finalVitalScore = jsonObject.getString("final_vital_score");
                String finalRespiratoryScore = jsonObject.getString("final_respiratory_score");
                String finalActivityScore = jsonObject.getString("final_activity_score");
                String finalNutritionScore = jsonObject.getString("final_nutrition_score");
                String finalLiverScore = jsonObject.getString("final_liver_score");

                TimelineItem data = new TimelineItem(id,date, time, overallHealthScore, finalDiabeticScore, finalVitalScore, finalRespiratoryScore, finalActivityScore, finalNutritionScore,finalLiverScore);
                dataList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}
