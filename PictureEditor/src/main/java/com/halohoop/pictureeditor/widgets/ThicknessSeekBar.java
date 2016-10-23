/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ThicknessSeekBar.java
 *
 * alpha seek bar
 *
 * Author huanghaiqi, Created at 2016-09-30
 *
 * Ver 1.0, 2016-09-30, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.halohoop.pictureeditor.R;

public class ThicknessSeekBar extends SeekBar {

    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private Path mPath;
    private float mRadius;
    private Paint mPaint;
    private int mProgressColor;

    public ThicknessSeekBar(Context context) {
        this(context, null);
    }

    public ThicknessSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThicknessSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBar);
        //bouncing line stroke width and color
        int defaultColor = Color.parseColor("#FF2968");
        mProgressColor = typedArray.getColor(R.styleable.SeekBar_ProgressColor, defaultColor);
        mPath = new Path();
        mRadius = 0;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(mProgressColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        mRadius = mMeasuredHeight / 2;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        mPaint.setColor(mProgressColor);
        invalidate();
    }

    public void setProgressColor(String progressColorHex) {
        int color = Color.parseColor(progressColorHex);
        setProgressColor(color);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        mPath.reset();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mPaint.setAntiAlias(true);
        mPath.moveTo(0, mMeasuredHeight / 2);
        mPath.lineTo(mMeasuredWidth - mRadius, 0);
        mPath.lineTo(mMeasuredWidth - mRadius, mMeasuredHeight);
        mPath.arcTo(mMeasuredWidth - mRadius * 2, 0, mMeasuredWidth, mMeasuredHeight,
                -90, 180, false);
        mPath.close();
        canvas.clipPath(mPath);
        canvas.clipRect(0, 0, mMeasuredWidth, mMeasuredHeight, Region.Op.INTERSECT);
        drawFinalShape(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }

    private void drawFinalShape(Canvas canvas) {
        for (int i = 0; i < mMeasuredWidth; i++) {
            canvas.drawLine(i, 0, i, mMeasuredHeight, mPaint);
        }
    }


}