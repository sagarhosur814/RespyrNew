//package com.humorstech.task2_uphar;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import com.anton46.stepsview.StepsViewIndicator;
//import com.humorstech.respyr.R;
//
//import java.util.List;
//
//public class CustomStepsView extends LinearLayout implements StepsViewIndicator.OnDrawListener {
//
//    private StepsViewIndicator mStepsViewIndicator;
//    private FrameLayout mLabelsLayout;
//    private String[] mLabels;
//    private int mProgressColorIndicator = Color.YELLOW;
//    private int mLabelColorIndicator = Color.BLACK;
//    private int mBarColorIndicator = Color.BLACK;
//    private int mCompletedPosition = 0;
//
//    public CustomStepsView(Context context) {
//        this(context, null);
//    }
//
//    public CustomStepsView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public CustomStepsView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void init() {
//        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_steps_view, this);
//        mStepsViewIndicator = (StepsViewIndicator) rootView.findViewById(R.id.steps_indicator_view);
//        mStepsViewIndicator.setDrawListener(this);
//        mLabelsLayout = (FrameLayout) rootView.findViewById(R.id.labels_container);
//    }
//
//    public String[] getLabels() {
//        return mLabels;
//    }
//
//    public CustomStepsView setLabels(String[] labels) {
//        mLabels = labels;
//        mStepsViewIndicator.setStepSize(labels.length);
//        return this;
//    }
//
//    public int getProgressColorIndicator() {
//        return mProgressColorIndicator;
//    }
//
//    public CustomStepsView setProgressColorIndicator(int progressColorIndicator) {
//        mProgressColorIndicator = progressColorIndicator;
//        mStepsViewIndicator.setProgressColor(mProgressColorIndicator);
//        return this;
//    }
//
//    public int getLabelColorIndicator() {
//        return mLabelColorIndicator;
//    }
//
//    public CustomStepsView setLabelColorIndicator(int labelColorIndicator) {
//        mLabelColorIndicator = labelColorIndicator;
//        return this;
//    }
//
//    public int getBarColorIndicator() {
//        return mBarColorIndicator;
//    }
//
//    public CustomStepsView setBarColorIndicator(int barColorIndicator) {
//        mBarColorIndicator = barColorIndicator;
//        mStepsViewIndicator.setBarColor(mBarColorIndicator);
//        return this;
//    }
//
//    public int getCompletedPosition() {
//        return mCompletedPosition;
//    }
//
//    public CustomStepsView setCompletedPosition(int completedPosition) {
//        mCompletedPosition = completedPosition;
//        mStepsViewIndicator.setCompletedPosition(mCompletedPosition);
//        return this;
//    }
//
//    public void drawView() {
//        if (mLabels == null) {
//            throw new IllegalArgumentException("labels must not be null.");
//        }
//
//        if (mCompletedPosition < 0 || mCompletedPosition > mLabels.length - 1) {
//            throw new IndexOutOfBoundsException(String.format("Index : %s, Size : %s", mCompletedPosition, mLabels.length));
//        }
//
//        mStepsViewIndicator.invalidate();
//    }
//
//    @Override
//    public void onReady() {
//        drawLabels();
//    }
//
//    private void drawLabels() {
//        List<Float> indicatorPosition = mStepsViewIndicator.getThumbContainerXPosition();
//
//        if (mLabels != null) {
//            for (int i = 0; i < mLabels.length; i++) {
//                View labelLayout = LayoutInflater.from(getContext()).inflate(R.layout.label_layout, null);
//                TextView textView = labelLayout.findViewById(R.id.stepLabel);
//                textView.setText(mLabels[i]);
//                textView.setTextColor(mLabelColorIndicator);
//                labelLayout.setX(indicatorPosition.get(i));
//                labelLayout.setLayoutParams(
//                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//                if (i <= mCompletedPosition) {
//                    textView.setTypeface(null, Typeface.BOLD);
//                }
//
//                mLabelsLayout.addView(labelLayout);
//            }
//        }
//    }
//}