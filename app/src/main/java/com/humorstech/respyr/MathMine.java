package com.humorstech.respyr;

import java.text.DecimalFormat;

public class MathMine {

    public static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("##.#");
        return Double.valueOf(twoDForm.format(d));
    }

    public static  double findBMI(double weight, double height){
        return  roundTwoDecimals(weight / ((height/100) * (height/100)));
    }

    public static double feetToCM(double feet){
        return roundTwoDecimals(feet * 30.48);
    }

}
