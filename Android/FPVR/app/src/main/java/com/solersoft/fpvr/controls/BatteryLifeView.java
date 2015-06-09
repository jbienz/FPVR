package com.solersoft.fpvr.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.solersoft.fpvr.R;

import java.security.InvalidParameterException;

/**
 * A battery life indicator
 */
public class BatteryLifeView extends View
{
    final static private int BarMargin = 8;
    final static private int EventFillColor = Color.YELLOW;
    final static private int EventStrokeColor = Color.BLACK;

    private float barBottom;
    private float barTop;
    private float bottom;
    private Paint criticalPaint;
    private float criticalPercent;
    private float criticalRight;
    private Paint eventPaint;
    private Paint goodPaint;
    private float height;
    private float left;
    private Paint lowPaint;
    private float lowPercent;
    private float lowRight;
    private float midPoint;
    private float remainingPercent;
    private float right;
    private final Rect textBounds = new Rect(); //don't new this up in a draw method
    private Paint textPaint;
    private float top;
    private Paint usagePaint;


    //region Constructors
    public BatteryLifeView(Context context)
    {
        super(context);
        init(null);
    }

    public BatteryLifeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public BatteryLifeView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    //endregion

    private void calculateBars()
    {
        // Create additional top and bottom margins
        barTop = top + BarMargin;
        barBottom = bottom - BarMargin;
        barBottom = Math.max(barBottom, barTop); // Bottom can't be higher than top

        // Calculate segments
        float width = right - left;
        criticalRight = width * criticalPercent;
        lowRight = criticalRight + ((width * lowPercent) - criticalRight);
    }

    private void drawEventPoint(Canvas canvas, float cx, String c)
    {
        // Draw background
        eventPaint.setColor(EventFillColor);
        eventPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, midPoint, midPoint, eventPaint);

        // Outline
        eventPaint.setColor(EventStrokeColor);
        eventPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cx, midPoint, midPoint, eventPaint);

        // Label
        textPaint.getTextBounds(c, 0, c.length(), textBounds);
        canvas.drawText(c, cx - textBounds.exactCenterX(), midPoint - textBounds.exactCenterY(), textPaint);
    }

    private void init(AttributeSet attrs)
    {
        // If passed attributes, load them
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BatteryLifeView);

            criticalPercent = a.getFloat(R.styleable.BatteryLifeView_criticalPercent, 0f);
            criticalPercent = Math.max(criticalPercent, 0f);
            criticalPercent = Math.min(criticalPercent, 1f);

            lowPercent = a.getFloat(R.styleable.BatteryLifeView_lowPercent, 0f);
            lowPercent = Math.max(lowPercent, 0f);
            lowPercent = Math.max(lowPercent, criticalPercent); // Low can't be less than critical

            remainingPercent = a.getFloat(R.styleable.BatteryLifeView_remainingPercent, 0f);
            remainingPercent = Math.max(remainingPercent, 0f);
            remainingPercent = Math.min(remainingPercent, 1f);

            // Bars may have changed
            calculateBars();
        }

        // Create paint variables
        criticalPaint = new Paint();
        criticalPaint.setColor(Color.RED);

        eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eventPaint.setStrokeWidth(2);

        goodPaint = new Paint();
        goodPaint.setColor(Color.GREEN);

        lowPaint = new Paint();
        lowPaint.setColor(Color.YELLOW);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);

        usagePaint = new Paint();
        usagePaint.setColor(Color.BLACK);
        usagePaint.setAlpha(200);
    }

    //region Overrides
    @Override
    protected void onDraw(Canvas canvas)
    {
        if ((right <= 0) || (bottom <= 0))
        {
            super.onDraw(canvas);
        }
        else
        {
            // Draw bars
            if (criticalRight > 0)
            {
                canvas.drawRect(left, barTop, criticalRight, barBottom, criticalPaint);
            }
            if (lowRight > 0)
            {
                canvas.drawRect(criticalRight, barTop, lowRight, barBottom, lowPaint);
            }
            canvas.drawRect(lowRight, barTop, right, barBottom, goodPaint);

            // Dim percent remaining
            if (remainingPercent < 1)
            {
                // Calc width
                float width = right - left;

                // Calculate left edge of percent remaining
                float remainLeft = width * remainingPercent;

                // Dim
                canvas.drawRect(remainLeft, barTop, right, barBottom, usagePaint);
            }

            // Draw event points
            if (criticalRight > 0)
            {
                drawEventPoint(canvas, criticalRight, "C");
            }
            if (lowRight > 0)
            {
                drawEventPoint(canvas, lowRight,"L");
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // The base class implementation of measure defaults to the background size, unless a larger size is allowed by the MeasureSpec.
        // http://developer.android.com/reference/android/view/View.html#onMeasure(int, int)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // Figure out padding
        bottom = h - getPaddingBottom();
        left = getPaddingLeft();
        right = w - getPaddingRight();
        top = getPaddingTop();

        // Calculate mid point
        height = (bottom - top);
        midPoint = top + (height / 2);

        // Calculate bars
        calculateBars();
    }
    //endregion

    //region Public Properties
    public float getCriticalPercent()
    {
        return criticalPercent;
    }

    public void setCriticalPercent(float criticalPercent)
    {
        if (this.criticalPercent != criticalPercent)
        {
            if (criticalPercent < 0) { throw new InvalidParameterException("criticalPercent cannot be less than 0"); }
            this.criticalPercent = criticalPercent;
            lowPercent = Math.max(lowPercent, criticalPercent); // Low can't be less than critical
            calculateBars();
            invalidate();
        }
    }

    public float getLowPercent()
    {
        return lowPercent;
    }

    public void setLowPercent(float lowPercent)
    {
        if (this.lowPercent != lowPercent)
        {
            if (lowPercent < 0) { throw new InvalidParameterException("lowPercent cannot be less than 0"); }
            this.lowPercent = Math.max(lowPercent, criticalPercent); // Low can't be less than critical
            calculateBars();
            invalidate();
        }
    }

    public float getRemainingPercent()
    {
        return remainingPercent;
    }

    public void setRemainingPercent(float remainingPercent)
    {
        if (this.remainingPercent != remainingPercent)
        {
            // Validate
            if ((remainingPercent < 0) || (remainingPercent > 1))
            {
                throw new InvalidParameterException("remainingPercent must be between 0 and 1");
            }
            this.remainingPercent = remainingPercent;
            invalidate();
        }
    }
    //endregion
}
