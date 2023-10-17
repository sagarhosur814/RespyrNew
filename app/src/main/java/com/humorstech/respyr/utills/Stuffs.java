package com.humorstech.respyr.utills;

import android.text.Html;
import android.text.Spanned;

import com.humorstech.respyr.R;

public class Stuffs {


    public static String[] checkListNames= {
            "I'm fasting since 4 hours minimum.",
            "I haven’t consumed water or any other fluid in the last one hour.",
            "I haven't consumed any alcohol in last 24 hours. ",
            "I haven't smoked or consuming any type of tobacco in last four hours. ",
            "I haven’t brushed my teeth in last one hour.",
            "I am sitting relaxed to take the test"
    };

    public static String checklist1 = "Fast for<font color=\"#308BF9\">" + " at least 4 hours" + "</font> ";
    public static String checklist2 = "Avoid drinking water or any type fluid for <font color=\"#308BF9\"> at least 1 hour.</font>";
    public static String checklist3 = "Avoid drinking Alcohol for <font color=\"#308BF9\">" + " at least 24 hours" + "</font>";
    public static String checklist4 = "Avoid  smoking of any type for at least <font color=\"#308BF9\">" + " last 4 hours. " + "</font>";
    public static String checklist5 = "Avoid Taking test within 1 hour<font color=\"#308BF9\">" + " of brushing teeth" + "</font>";
    public static String checklist6 = "Sit & relax before taking test";









    public static Spanned newCheckList1 = Html.fromHtml(checklist1);
    public static Spanned newCheckList2 = Html.fromHtml(checklist2);
    public static Spanned newCheckList3 = Html.fromHtml(checklist3);
    public static Spanned newCheckList4 = Html.fromHtml(checklist4);
    public static Spanned newCheckList5 = Html.fromHtml(checklist5);
    public static Spanned newCheckList6 = Html.fromHtml(checklist6);


    public static Spanned[] checkListNamesForDashboard= {
            newCheckList1,
            newCheckList2,
            newCheckList3,
            newCheckList4,
            newCheckList5,
            newCheckList6,
    };



    public static int[]  checkListImages = {
            R.drawable.chk1,R.drawable.chk2,R.drawable.chk3,R.drawable.chk4,R.drawable.chk5,R.drawable.chk6
    };
}
