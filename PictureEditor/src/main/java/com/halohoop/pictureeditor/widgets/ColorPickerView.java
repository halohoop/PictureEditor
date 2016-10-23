/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ColorPickerView.java
 *
 * color picker
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
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ColorPickerView extends View {

    private Paint mPaint;
    private float mMeasuredWidth;
    private float mMeasuredHeight;
    private float mMarginBetween;
    private float mRectPadding;
    private float paddingBetweenInnerAndOuter;
    private float mSelectStrokeWidth;
    private List<Rect> mRects;
    private List<Integer> mColors;
    private int mColorSelected;
    private int mColorSelectedIndex = 0;
    private float mEveryColorWidth;
    private PointF mMiddlePoint;
    private int defaultColor0;
    private int defaultColor1;
    private int defaultColor2;
    private int defaultColor3;
    private int defaultColor4;
    private int defaultColor5;
    private int defaultColor6;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRects = new ArrayList<>();
        mColors = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarginBetween = 0;
        mRectPadding = 16;
        mSelectStrokeWidth = 3;
        paddingBetweenInnerAndOuter = 5;
        defaultColor0 = parseColor("#FF2968");
        defaultColor1 = parseColor("#CC73E1");
        defaultColor2 = parseColor("#1DADF8");
        defaultColor3 = parseColor("#63DA38");
        defaultColor4 = parseColor("#000000");
        defaultColor5 = parseColor("#FFFFFF");
    }

    public void setColors(String... colorHexs) {
        if (colorHexs == null || colorHexs.length <= 0) {
            mColors.clear();
            mColors.add(defaultColor0);
            mColors.add(defaultColor1);
            mColors.add(defaultColor2);
            mColors.add(defaultColor3);
            mColors.add(defaultColor4);
            mColors.add(defaultColor5);
        } else {
            mColors.clear();
            for (int i = 0; i < colorHexs.length; i++) {
                int color = parseColor(colorHexs[i]);
                mColors.add(color);
            }
        }
        mColorSelected = mColors.get(0);
        mColorSelectedIndex = 0;
        getEveryColorRects();
        invalidate();
    }

    public int getColorSelectedIndex() {
        return mColorSelectedIndex;
    }

    public int getColorSelected() {
        return mColorSelected;
    }

    private int parseColor(String colorHex) {
        try {
            return Color.parseColor(colorHex);
        } catch (IllegalArgumentException iae) {
            return Color.BLACK;
        }
    }

    public void setMarginBetween(int marginBetween) {
        this.mMarginBetween = marginBetween;
        getEveryColorRects();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        mMiddlePoint = new PointF(mMeasuredWidth / 2.0f, mMeasuredHeight / 2.0f);
        setColors();
        Log.i(TAG, "onSizeChanged: " + "onSizeChanged: w:" + w + " h:" + h);
    }

    private void getEveryColorRects() {
        //get every color rect width
        mEveryColorWidth = (mMeasuredWidth - (mColors.size() + 1) * mMarginBetween)
                / mColors.size();
        //get every color rects
        if (mRects.size() != mColors.size()) {
            mRects.clear();
            for (int i = 0; i < mColors.size(); i++) {
                float thisLeft = (mEveryColorWidth + mMarginBetween) * i + (mMarginBetween / 2.0f);
                float thisRight = thisLeft + mEveryColorWidth;
                mRects.add(new Rect((int) thisLeft, 0, (int) thisRight, (int) mMeasuredHeight));
            }
        } else {
            for (int i = 0; i < mColors.size(); i++) {
                float thisLeft = (mEveryColorWidth + mMarginBetween) * i + (mMarginBetween / 2.0f);
                float thisRight = thisLeft + mEveryColorWidth;
                Rect rect = mRects.get(i);
                rect.set((int) thisLeft, 0, (int) thisRight, (int) mMeasuredHeight);
            }
        }
    }

    private ColorPickListener mColorPickListener;

    public void setColorPickListener(ColorPickListener colorPickListener) {
        this.mColorPickListener = colorPickListener;
    }

    public interface ColorPickListener {
        void onColorPicked(int color);

        void onColorPickedDone();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mRects.size(); i++) {
                    Rect rect = mRects.get(i);
                    if (rect.contains(x, y)) {
                        mColorSelected = mColors.get(i);
                        mColorSelectedIndex = i;
                        if (mColorPickListener != null) {
                            mColorPickListener.onColorPicked(mColorSelected);
                        }
                        invalidate();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < mRects.size(); i++) {
                    Rect rect = mRects.get(i);
                    if (rect.contains(x, y)) {
                        mColorSelected = mColors.get(i);
                        mColorSelectedIndex = i;
                        if (mColorPickListener != null) {
                            mColorPickListener.onColorPicked(mColorSelected);
                        }
                        invalidate();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mColorPickListener != null) {
                    mColorPickListener.onColorPickedDone();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mRects.size(); i++) {
            Rect rect = mRects.get(i);
            float halfWidthOfRect = (rect.right - rect.left) / 2.0f;
            float innerDrawRadius = halfWidthOfRect - mRectPadding;
            float outerDrawRadius = innerDrawRadius + paddingBetweenInnerAndOuter;
            Integer color = mColors.get(i);
            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(rect.left + halfWidthOfRect, mMiddlePoint.y, innerDrawRadius, mPaint);
            if (color == mColorSelected) {
                mPaint.setStrokeWidth(mSelectStrokeWidth);
                if (color == Color.BLACK) {
                    mPaint.setColor(Color.WHITE);
                }
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(rect.left + halfWidthOfRect, mMiddlePoint.y, outerDrawRadius,
                        mPaint);
            }
        }
    }
}