package com.humorstech.respyr.suggestion;

public class BreakFastSuggestionDataModel {

    private String foodName;
    private double calories;
    private double carbohydrates;
    private double protein;
    private double fat;
    private double fiber;

    public BreakFastSuggestionDataModel(String foodName, double calories, double carbohydrates, double protein, double fat, double fiber) {
        this.foodName = foodName;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.fiber = fiber;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(int carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getFiber() {
        return fiber;
    }

    public void  setFiber(double fiber) {
        this.fiber = fiber;
    }
}
