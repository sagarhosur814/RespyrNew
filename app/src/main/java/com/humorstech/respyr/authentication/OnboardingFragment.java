package com.humorstech.respyr.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.humorstech.respyr.R;

public class OnboardingFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    public static OnboardingFragment newInstance(int position) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int position = getArguments().getInt(ARG_POSITION);
        int layoutResId = getLayoutResId(position);

        return inflater.inflate(layoutResId, container, false);
    }

    private int getLayoutResId(int position) {
        // Return the layout resource ID for each onboarding screen
        switch (position) {
            case 1:
                return R.layout.fragment_onoarding2;
            case 2:
                return R.layout.fragment_oboarding3;
            default:
                return R.layout.fragment_oboarding1;
        }
    }
}
