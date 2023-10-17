package com.humorstech.respyr.trends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.humorstech.respyr.R;

import org.jetbrains.annotations.Nullable;

public class ResultPageTrendMonthly extends Fragment {

    private static final String ARG_POSITION = "position";

    public static ResultPageTrendMonthly newInstance(int position) {
        ResultPageTrendMonthly fragment = new ResultPageTrendMonthly();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_page_trend_monthly, container, false);
        // Initialize your fragment's views and functionality here
        return view;
    }
}
