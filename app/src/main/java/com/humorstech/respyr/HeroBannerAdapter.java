package com.humorstech.respyr;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.humorstech.respyr.daily_routine.DailyRoutinForm;
import com.humorstech.respyr.profile.user.AddNewProfile;

import java.util.ArrayList;
import java.util.List;

public class HeroBannerAdapter extends PagerAdapter {
    private List<HeroBannerImageModel> items = new ArrayList<>();
    private Activity activity;

    public HeroBannerAdapter(Activity activity, List<HeroBannerImageModel> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(activity);
        HeroBannerImageModel item = items.get(position);
        imageView.setImageResource(item.getImageResId());

        imageView.setScaleType(ImageView.ScaleType.FIT_XY); // Scale the image to fill the width and height of the ImageView
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        container.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (position == 0) {
                    intent = new Intent(activity, DailyRoutinForm.class);
                } else {
                    intent = new Intent(activity, AddNewProfile.class);
                }
                moveToActivity(intent);
            }
        });

        return imageView;
    }

    private void moveToActivity(Intent intent) {
        activity.startActivity(intent);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}

