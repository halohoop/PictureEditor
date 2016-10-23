/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ColorShowView.java
 *
 * Single Color show
 *
 * Author huanghaiqi, Created at 2016-10-02
 *
 * Ver 1.0, 2016-10-02, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class ColorShowView extends View {

    private float mMeasuredHeight;
    private float mMeasuredWidth;
    private float mInnerRadius;
    private float mPaddingBetweenInnerAndOuter;
    private float mOuterStrokeWidth;
    private PointF mMiddlePoint;
    private Paint mPaint;
    private int mColor;

    public ColorShowView(Context context) {
        this(context, null);
    }

    public ColorShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int defaultColor = Color.parseColor("#FF2968");
        mPaint.setColor(defaultColor);
        mColor = defaultColor;
        mInnerRadius = 32;
        mOuterStrokeWidth = 3;
        mPaddingBetweenInnerAndOuter = 5;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        mMiddlePoint = new PointF(mMeasuredWidth / 2.0f, mMeasuredHeight / 2.0f);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        mColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mMiddlePoint.x, mMiddlePoint.y, mInnerRadius, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mOuterStrokeWidth);
        float outerRadius = mInnerRadius + mPaddingBetweenInnerAndOuter;
        if (mPaint.getColor() == Color.BLACK) {
            mPaint.setColor(Color.WHITE);
        }
        canvas.drawCircle(mMiddlePoint.x, mMiddlePoint.y, outerRadius, mPaint);
    }
}