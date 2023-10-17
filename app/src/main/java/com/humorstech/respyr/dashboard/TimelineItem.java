package com.humorstech.respyr.dashboard;

public class TimelineItem {
    private int id;
    private String date;

    private String time;
    private String overallHealthScore;
    private String finalDiabeticScore;
    private String finalVitalScore;
    private String finalRespiratoryScore;
    private String finalActivityScore;
    private String finalNutritionScore;
    private String finalLiverScore;


    public TimelineItem(int id, String date, String time, String overallHealthScore, String finalDiabeticScore, String finalVitalScore, String finalRespiratoryScore, String finalActivityScore, String finalNutritionScore, String finalLiverScore) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.overallHealthScore = overallHealthScore;
        this.finalDiabeticScore = finalDiabeticScore;
        this.finalVitalScore = finalVitalScore;
        this.finalRespiratoryScore = finalRespiratoryScore;
        this.finalActivityScore = finalActivityScore;
        this.finalNutritionScore = finalNutritionScore;
        this.finalLiverScore = finalLiverScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getOverallHealthScore() {
        return overallHealthScore;
    }

    public void setOverallHealthScore(String overallHealthScore) {
        this.overallHealthScore = overallHealthScore;
    }

    public String getFinalDiabeticScore() {
        return finalDiabeticScore;
    }

    public void setFinalDiabeticScore(String finalDiabeticScore) {
        this.finalDiabeticScore = finalDiabeticScore;
    }

    public String getFinalVitalScore() {
        return finalVitalScore;
    }

    public void setFinalVitalScore(String finalVitalScore) {
        this.finalVitalScore = finalVitalScore;
    }

    public String getFinalRespiratoryScore() {
        return finalRespiratoryScore;
    }

    public void setFinalRespiratoryScore(String finalRespiratoryScore) {
        this.finalRespiratoryScore = finalRespiratoryScore;
    }

    public String getFinalActivityScore() {
        return finalActivityScore;
    }

    public void setFinalActivityScore(String finalActivityScore) {
        this.finalActivityScore = finalActivityScore;
    }

    public String getFinalNutritionScore() {
        return finalNutritionScore;
    }

    public void setFinalNutritionScore(String finalNutritionScore) {
        this.finalNutritionScore = finalNutritionScore;
    }


    public String getFinalLiverScore() {
        return finalLiverScore;
    }

    public void setFinalLiverScore(String finalLiverScore) {
        this.finalLiverScore = finalLiverScore;
    }



    public String getDataType(String dataKey){
        switch (dataKey){
            case "overall_health_score" : return getOverallHealthScore();
            case "final_diabetic_score" : return  getFinalDiabeticScore();
            case "final_vital_score" : return getFinalVitalScore();
            case "final_respiratory_score" : return getFinalRespiratoryScore();
            case "final_activity_score" : return  getFinalActivityScore();
            case "final_nutrition_score" : return getFinalNutritionScore();
            case "final_liver_score" : return getFinalLiverScore();
            default:return "";
        }
    }

}
