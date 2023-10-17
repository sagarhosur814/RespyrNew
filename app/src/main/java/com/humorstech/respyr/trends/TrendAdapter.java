package com.humorstech.respyr.trends;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.humorstech.respyr.reading.food_breakup.FoodBreakupCalories;
import com.humorstech.respyr.reading.food_breakup.FoodBreakupCarboHydrate;
import com.humorstech.respyr.reading.food_breakup.FoodBreakupData;
import com.humorstech.respyr.reading.food_breakup.FoodBreakupFats;
import com.humorstech.respyr.reading.food_breakup.FoodBreakupFibre;
import com.humorstech.respyr.reading.food_breakup.FoodBreakupProtein;

import java.util.List;



public class TrendAdapter extends FragmentPagerAdapter {
    private static final int NUM_TABS = 5;
    public TrendAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TrendDaily();
            case 1:
                return new TrendDaily();
            case 2:
                return new TrendAllTime();
            case 3:
                return new TrendDaily();
            case 4:
                return new TrendDaily();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }
}

