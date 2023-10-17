package com.humorstech.respyr.trends;

public class TrendDataModel {

    private String scoreName;

    public TrendDataModel(String scoreName) {
        this.scoreName = scoreName;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }
}
