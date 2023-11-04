package com.humorstech.task1_uphar;





import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import java.text.DecimalFormat;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.humorstech.respyr.R;

public class GaugeView extends View {
    public static final int NO_POSITION = -1;


    private static final String[] SCALE_TITLES = {
            "0", "60", "80", "100"
    };

    private ValueAnimator valueAnimator;
    private float targetValue;
    private long animationDuration = 1000; // Animation duration in milliseconds


    private static final float ASPECT_WIDTH_HEIGHT_RATIO = 2f;

    private static final float ANGLE_START = 180f;
    private static final float ANGLE_SWEEP = 180f;

    private static final int SCALE_COUNT = 50;
    private static final int SCALE_COUNT_EACH_GROUP = 5;
    private static final float ANGLE_SCALE_STEP = ((float) ANGLE_SWEEP / SCALE_COUNT);

    @ColorInt
    private static final int DEFAULT_COLOR = Color.parseColor("#41000000");

    private static final String DEFAULT_FORMAT = "0.##";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);

    private int gravity = Gravity.CENTER;
    // todo
    private boolean useGradient;

    private Adapter adapter;
    private float current;
    private CharSequence currentStr = DECIMAL_FORMAT.format(0f);
    private CharSequence label;

    private float innerRadius;
    private float thickness;

    private PaintFlagsDrawFilter paintFlagsDrawFilter;

    private Paint arcPaint;
    private Paint arcTrackPaint;

    private final RectF arcRect = new RectF();
    private final RectF drawRect = new RectF();
    //private final PointF centerPoint = new PointF();

    private TextPaint gradeLabelPaint;
    private float gradeLabelPadding;

    private Paint scalePaint;
    private float scaleStrokeWidth;
    private float scaleStrokeLength;
    private float scalePadding;

    private Paint cursorPaint;
    private Drawable cursorDrawable;
    private float cursorPadding;

    private TextPaint valuePaint;
    private final Rect valueRect = new Rect();

    private TextPaint labelPaint;
    private float labelPadding;

    public GaugeView(Context context) {
        this(context, null);
    }

    public GaugeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GaugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public GaugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                     int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (isInEditMode()) {
            setAdapter(new Adapter4Test());
            setCurrent(20f);
            setLabel("BMI");
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GradeGaugeView, defStyleAttr, defStyleRes);

        int gravity = a.getInt(R.styleable.GradeGaugeView_android_gravity, Gravity.CENTER);
        setGravity(gravity);

        boolean useGradient = a.getBoolean(R.styleable.GradeGaugeView_useGradient, false);
        setUseGradient(useGradient);

        a.recycle();

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(
                0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        thickness = dp2px(35);

        arcTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcTrackPaint.setStyle(Paint.Style.STROKE);
        arcTrackPaint.setColor(DEFAULT_COLOR);
        arcTrackPaint.setStrokeWidth(thickness);



        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(thickness);

        gradeLabelPadding = dp2px(20);

        gradeLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        gradeLabelPaint.setTextAlign(Align.CENTER);

        // Set the Poppins font for grade labels
        gradeLabelPaint.setTypeface(Typeface.create("Poppins", Typeface.BOLD));

        // Set the text color for grade labels
        gradeLabelPaint.setColor(Color.parseColor("#252525"));

        gradeLabelPaint.setTextSize(dp2px(12));

        scaleStrokeWidth = dp2px(1);
        scaleStrokeLength = dp2px(8);
        scalePadding = dp2px(4);

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStyle(Paint.Style.STROKE);

        cursorDrawable = ContextCompat.getDrawable(context, R.drawable.upward);
        cursorPadding = dp2px(16);
        cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorPaint.setTextAlign(Align.CENTER);

        labelPadding = dp2px(16);

        valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        valuePaint.setTextAlign(Align.CENTER);
        valuePaint.setFakeBoldText(true);

        // Set the Poppins font for the value text
        valuePaint.setTypeface(Typeface.create("Poppins", Typeface.BOLD));

        // Set the text color for the value text
        valuePaint.setColor(Color.parseColor("#252525"));

        valuePaint.setTextSize(dp2px(36));

        labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        labelPaint.setTextAlign(Align.CENTER);
        labelPaint.setColor(DEFAULT_COLOR);

        // Set the Poppins font for the label text
        labelPaint.setTypeface(Typeface.create("Poppins", Typeface.NORMAL));

        // Set the text color for the label text
        labelPaint.setColor(Color.parseColor("#252525"));

        labelPaint.setTextSize(dp2px(16));
    }



    public void setGravity(int gravity) {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.CENTER_HORIZONTAL;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.CENTER_VERTICAL;
        }

        if (gravity != this.gravity) {
            invalidate();
        }

        this.gravity = gravity;
    }

    public void setUseGradient(boolean useGradient) {
        if (useGradient != this.useGradient) {
            invalidate();
        }

        this.useGradient = useGradient;
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter != adapter) {
            this.adapter = adapter;
            invalidate();
        }
    }

    public void setCurrent(float current) {
        if (this.current != current) {
            this.current = current;
            this.currentStr = DECIMAL_FORMAT.format(current);
            invalidate();
        }
    }

    public void setLabel(CharSequence valueLabel) {
        if (!TextUtils.equals(valueLabel, this.label)) {
            this.label = valueLabel;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingHorizontal = getPaddingHorizontal();
        int widthSize = getSuggestedMinimumWidth() + getPaddingHorizontal();
        int widthSizeAndState = resolveSizeAndState(widthSize, widthMeasureSpec, 0);
        widthSize = widthSizeAndState & MEASURED_SIZE_MASK;

        int heightSizeNoPadding = (int) ((widthSize - paddingHorizontal) / ASPECT_WIDTH_HEIGHT_RATIO);
        int heightSize = heightSizeNoPadding + getPaddingVertical();
        int heightSizeAndState = resolveSizeAndState(heightSize, heightMeasureSpec, 0);
        heightSize = heightSizeAndState & MEASURED_SIZE_MASK;

        setMeasuredDimension(widthSize, heightSize);
    }

    private int getPaddingHorizontal() {
        return getPaddingLeft() + getPaddingRight();
    }

    private int getPaddingVertical() {
        return getPaddingTop() + getPaddingBottom();
    }

    @Override
    public int getPaddingBottom() {
        return super.getPaddingBottom() + (int) (scaleStrokeWidth * 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float gradeTextSize = getGradeLabelTextSize();
        float gradeTextUsedSize = gradeTextSize + gradeLabelPadding;

        float widthNoPadding = w - getPaddingHorizontal();
        float radiusMeasuredByWidth = (widthNoPadding - gradeTextUsedSize * 2) / ASPECT_WIDTH_HEIGHT_RATIO;

        float heightNoPadding = h - getPaddingVertical();
        float radiusMeasuredByHeight = heightNoPadding - gradeTextUsedSize;

        float radius = Math.min(radiusMeasuredByWidth, radiusMeasuredByHeight);
        innerRadius = radius - thickness;
        arcRect.set(-radius, -radius, radius, radius);

        drawRect.set(0, 0, radius * 2, radius);

        float drawRectDx = getPaddingLeft() + gradeTextUsedSize;
        switch ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK)) {
            case Gravity.LEFT:
                drawRectDx += 0;
                break;

            case Gravity.RIGHT:
                drawRectDx += radiusMeasuredByWidth * 2 - drawRect.width();
                break;

            default:
                drawRectDx += (radiusMeasuredByWidth * 2 - drawRect.width()) / 2;
                break;
        }

        float drawRectDy = getPaddingTop() + gradeTextUsedSize;
        switch ((gravity & Gravity.VERTICAL_GRAVITY_MASK)) {
            case Gravity.TOP:
                drawRectDy += 0;
                break;

            case Gravity.BOTTOM:
                drawRectDy += radiusMeasuredByHeight - drawRect.height();
                break;

            default:
                drawRectDy += (radiusMeasuredByHeight - drawRect.height()) / 2;
                break;
        }

        drawRect.offset(drawRectDx, drawRectDy);
    }

    private float getGradeLabelTextSize() {
        FontMetrics fontMetrics = gradeLabelPaint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;
    }

    private float getCenterPointXForDraw() {
        return drawRect.centerX();
    }

    private float getCenterPointYForDraw() {
        return drawRect.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(paintFlagsDrawFilter);

        canvas.save();
        canvas.translate(getCenterPointXForDraw(), getCenterPointYForDraw());
        canvas.rotate(ANGLE_START);

        drawArc(canvas,innerRadius+scaleStrokeWidth);
        drawScales(canvas);
        drawCursor(canvas);
        drawArcline(canvas);
        drawLabelAndValue(canvas);
        drawShadow(canvas,innerRadius*1.45f);

        canvas.restore();
    }

    private void drawShadow(Canvas canvas, float radius) {
        RectF shadowRectF = new RectF();
        Paint narcTrackPaint= new Paint();
        narcTrackPaint.setStyle(Paint.Style.STROKE);
        narcTrackPaint.setColor(Color.TRANSPARENT);
        narcTrackPaint.setStrokeWidth(7f);
        narcTrackPaint.setShadowLayer(15f, 0f, 0f, Color.argb(127, 0, 0, 0));
        shadowRectF.set(-radius, -radius , radius, radius);

        // Use the same paint for the shadow
//        arcTrackPaint.setShadowLayer(10f, 0f, 10f, Color.argb(127, 0, 0, 0));

        // Draw the shadow using the same paint
        canvas.drawArc(shadowRectF, 0, 180, false, narcTrackPaint);
    }


    private void drawArc(Canvas canvas, Float radius) {

        canvas.drawArc(arcRect, 0, ANGLE_SWEEP, false, arcTrackPaint);
        if (adapter != null && adapter.getCount() > 0) {
            float minValue = adapter.getMinValue();
            float maxValue = adapter.getMaxValue();
            float values = maxValue - minValue;
            if (values != 0) {
                float ratio = ANGLE_SWEEP / values;
                for (int i = 0; i < adapter.getCount(); i++) {
                    float gradeMinScale = adapter.getMinValue(i);
                    float gradeMaxScale = adapter.getMaxValue(i);

                    float startAngle = (gradeMinScale - minValue) * ratio;
                    float sweepAngle = (gradeMaxScale - gradeMinScale) * ratio;

                    arcPaint.setColor(adapter.getColor(i));
                    canvas.drawArc(arcRect, startAngle, sweepAngle, false, arcPaint);

                    CharSequence gradeTitle = adapter.getTitle(i);
                    if (!TextUtils.isEmpty(gradeTitle)) {
                        float centerAngle = startAngle + sweepAngle / 2f;
                        drawArcText(canvas, centerAngle, gradeTitle);
                    }
                }
            }
        }
    }

    private void drawArcline(Canvas canvas) {


        float newInnerRadius = innerRadius * 1.6f; // Adjust the inner radius
        RectF arcRect = new RectF();
        arcRect.set(-newInnerRadius, -newInnerRadius, newInnerRadius, newInnerRadius);

        float minValue = 0;
        float maxValue = 100;
        float angleStep = ANGLE_SWEEP / (maxValue - minValue);

        // Define the arc value ranges and colors
        int[] COLORS = {
                Color.parseColor("#EA5455"),  // Color for 0-60
                Color.TRANSPARENT,
                Color.parseColor("#EA5455"),
                Color.TRANSPARENT,
                Color.parseColor("#FFC412"),  // Color for 60-80
                Color.TRANSPARENT,
                Color.parseColor("#FFC412"),
                Color.TRANSPARENT,
                Color.parseColor("#3FAF58")  , // Color for 80-100
                Color.TRANSPARENT,
                Color.parseColor("#3FAF58")
        };
        int[] valueRanges = {2,27,33,58,62,67,73,78,82,86,93, 96};

        for (int i = 0; i < valueRanges.length - 1; i++) {
            float startValue = valueRanges[i];
            float endValue = valueRanges[i + 1];
            int color = COLORS[i];

            float startAngle = (startValue - minValue) * angleStep;
            float sweepAngle = (endValue - startValue) * angleStep;

            arcTrackPaint.setColor(color);
            arcTrackPaint.setStrokeWidth(4f); // Set the arc thickness

            canvas.drawArc(arcRect, startAngle, sweepAngle, false, arcTrackPaint);

        }
    }



    private void drawArcText(Canvas canvas, float angle, CharSequence text) {
        canvas.save();

        final float angleOffset = 90;
        canvas.rotate(angle + angleOffset);

        int textLength = text.length();
        float y = -(innerRadius + thickness + gradeLabelPadding);
        canvas.drawText(text, 0, textLength, 0, y-10, gradeLabelPaint);

        canvas.restore();
    }

    private void drawScales(Canvas canvas) {
        final float angleOffset = 90;

        for (int i = 0; i < SCALE_TITLES.length; i++) {
            canvas.save();

            CharSequence text = SCALE_TITLES[i];
            float v = 100f;
            Float intValue = Float.parseFloat(SCALE_TITLES[i]);
            float angle = (intValue * 180f) / (100f); // Calculate the angle evenly

            gradeLabelPaint.setColor(Color.parseColor("#535359"));
            gradeLabelPaint.setTextSize(dp2px(10));

            // Set the Poppins font for scale titles
            gradeLabelPaint.setTypeface(Typeface.create("Roboto", Typeface.NORMAL));

            canvas.rotate(angle + angleOffset);
            if(i==SCALE_TITLES.length-1)
            {
               canvas.rotate(-angleOffset+87);
            }

            int textLength = text.length();
            float y = -(innerRadius + thickness + gradeLabelPadding);
            canvas.drawText(text, 0, textLength, 0, y-10, gradeLabelPaint);

            canvas.restore();
        }
    }




    private void drawScaleTitle(Canvas canvas, String title, float y) {
        canvas.save();

        gradeLabelPaint.setColor(Color.parseColor("#252525"));
        gradeLabelPaint.setTextSize(dp2px(12));

        // Set the Poppins font for scale titles
        gradeLabelPaint.setTypeface(Typeface.create("Poppins", Typeface.NORMAL));

        int textLength = title.length();
        canvas.drawText(title, 0, textLength, 0, y, gradeLabelPaint);

        canvas.restore();
    }


    private int getColorByValue(float value) {
        if (adapter == null || adapter.getCount() == 0) {
            return DEFAULT_COLOR;
        }

        if (value >= adapter.getMaxValue()) {
            return adapter.getColor(adapter.getCount() - 1);
        }

        int position = adapter.getPositionByValue(value);
        if (position == NO_POSITION) {
            return DEFAULT_COLOR;
        }

        return adapter.getColor(position);
    }

    private void drawCursor(Canvas canvas) {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }

        canvas.save();

        final float angleOffset = 90;
        float minValue = adapter.getMinValue();
        float maxValue = adapter.getMaxValue();
        float values = maxValue - minValue;
        if (values != 0) {
            float ratio = ANGLE_SWEEP / values;

            // Adjust the marker position to be 2.6 units ahead
            float adjustedValue = current - 2.6f;

            float angle = (adjustedValue - minValue) * ratio;
            canvas.rotate(angle + angleOffset);

            float dy = -innerRadius + scaleStrokeLength * 1.5f + cursorPadding;
            canvas.translate((float) 0, (float) (-innerRadius * 1.4));

            int gradeColor = getColorByValue(Color.BLACK);
            cursorPaint.setColor(gradeColor);

            int width = cursorDrawable.getIntrinsicWidth();
            int height = cursorDrawable.getIntrinsicHeight();
            cursorDrawable.setBounds(0, 0, width, height);
            cursorDrawable.setColorFilter(gradeColor, PorterDuff.Mode.SRC_ATOP);

            cursorDrawable.draw(canvas);
        }

        canvas.restore();
    }


    public void animateToValue(float targetValue) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        this.targetValue = targetValue;
        valueAnimator = ValueAnimator.ofFloat(current, targetValue);
        valueAnimator.setDuration(animationDuration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setCurrent((Float) animation.getAnimatedValue());
            }
        });

        valueAnimator.start();
    }


    private void drawLabelAndValue(Canvas canvas) {
        canvas.save();
        canvas.rotate(-ANGLE_START);

        int textColor = getColorByValue(current);
        valuePaint.setColor(textColor);

        if (!TextUtils.isEmpty(label)) {
            int textLength = currentStr.length();
            canvas.drawText(currentStr, 0, textLength, 0, 0, valuePaint);

            valuePaint.getTextBounds(currentStr.toString(), 0, textLength, valueRect);

            int labelLength = label.length();
            float labelY = -(valueRect.height() + labelPadding);
            canvas.drawText(label, 0, labelLength, 0, labelY, labelPaint);
        }

        canvas.restore();

    }

    public int dp2px(int values) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    public static abstract class Adapter {
        public abstract int getCount();

        public abstract CharSequence getTitle(int position);

        public abstract float getMinValue(int position);

        public abstract float getMaxValue(int position);

        @ColorInt
        public abstract int getColor(int position);

        public float getMinValue() {
            return getMinValue(0);
        }

        public float getMaxValue() {
            return getMaxValue(getCount() - 1);
        }

        public int getPositionByValue(float value) {
            for (int i = 0; i < getCount(); i++) {
                float min = getMinValue(i);
                float max = getMaxValue(i);
                if (value >= min && value < max) {
                    return i;
                }
            }
            return GaugeView.NO_POSITION;
        }
    }

    public static class Adapter4Test extends Adapter {
        private static final String[] TITLES = {"Poor", "Fair", "Good"};
        private static final float[][] SCALES = {
                {0, 60f}, {60f, 80f}, {80f, 100f}
        };


        private static final int[] COLORS = {
                Color.parseColor("#EA5455"),
                Color.parseColor("#FFC412"),
                Color.parseColor("#3FAF58"),
        };

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getTitle(int position) {
            return TITLES[position];
        }

        @Override
        public float getMinValue(int position) {
            return SCALES[position][0];
        }

        @Override
        public float getMaxValue(int position) {
            return SCALES[position][1];
        }

        @Override
        public int getColor(int position) {
            return COLORS[position];
        }
    }
}
