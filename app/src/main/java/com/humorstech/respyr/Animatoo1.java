package com.humorstech.respyr;


import android.app.Activity;
import android.content.Context;

public class Animatoo1 {


    public static void animateInAndOut(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_in_out_enter,
                R.anim.animate_in_out_exit
        );
    }



    public static void animateFade(Context context) {
        ((Activity) context).overridePendingTransition(


                R.anim.animate_fade_enter,
                R.anim.animate_fade_exit

        );
    }



    public static void animateSlideDown(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_slide_down_enter,
                R.anim.animate_slide_down_exit
        );
    }

    public static void animateSlideLeft(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_slide_left_enter,
                R.anim.animate_slide_left_exit
        );
    }

    public static void animateSwipeRight(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_swipe_right_enter,
                R.anim.animate_swipe_right_exit
        );
    }

    public static void animateSlideUp(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_slide_up_enter,
                R.anim.animate_slide_up_exit
        );
    }



    public static void animateSwipeLeft(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_swipe_left_enter,
                R.anim.animate_swipe_left_exit
        );
    }

    public static void animateSlideRight(Context context) {
        ((Activity) context).overridePendingTransition(
                R.anim.animate_slide_in_left,
                R.anim.animate_slide_out_right
        );
    }


}
