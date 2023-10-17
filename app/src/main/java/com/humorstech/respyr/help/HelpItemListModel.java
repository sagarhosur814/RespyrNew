package com.humorstech.respyr.help;

public class HelpItemListModel {

    int id;
    String helpTitle;
    String getHelpTitle;

    public HelpItemListModel(int id, String helpTitle, String getHelpTitle) {
        this.id = id;
        this.helpTitle = helpTitle;
        this.getHelpTitle = getHelpTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHelpTitle() {
        return helpTitle;
    }

    public void setHelpTitle(String helpTitle) {
        this.helpTitle = helpTitle;
    }

    public String getGetHelpTitle() {
        return getHelpTitle;
    }

    public void setGetHelpTitle(String getHelpTitle) {
        this.getHelpTitle = getHelpTitle;
    }
}
