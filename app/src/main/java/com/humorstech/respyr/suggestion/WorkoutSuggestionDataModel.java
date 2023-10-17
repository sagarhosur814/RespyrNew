package com.humorstech.respyr.suggestion;

public class WorkoutSuggestionDataModel {


    private String workoutName;
    private String workoutTiming;


    WorkoutSuggestionDataModel(String workoutName, String workoutTiming){
        this.workoutName = workoutName;
        this.workoutTiming = workoutTiming;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getWorkoutTiming() {
        return workoutTiming;
    }

    public void setWorkoutTiming(String workoutTiming) {
        this.workoutTiming = workoutTiming;
    }
}
