package com.humorstech.respyr.trends;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.humorstech.respyr.R;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent); // Reference to a TextView in your custom layout
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        // Customize the content of the marker
        tvContent.setText("Value: " + entry.getY());

        super.refreshContent(entry, highlight);
    }

    // Optional: You can also customize the marker's positioning if needed
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 10f);
        // Adjust offset for positioning
    }
}
