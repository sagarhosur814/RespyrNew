package com.humorstech.respyr.dashboard;

import android.text.Spanned;

public class CheckListData {

    private Spanned check_list_name;
    private int checklist_id;
    private int image_id;

    public CheckListData(Spanned check_list_name, int checklist_id, int image_id) {
        this.check_list_name = check_list_name;
        this.checklist_id = checklist_id;
        this.image_id = image_id;
    }

    // Getter methods for the fields
    public Spanned getCheckListName() {
        return check_list_name;
    }

    public int getChecklistId() {
        return checklist_id;
    }

    public int getImageId() {
        return image_id;
    }
}
