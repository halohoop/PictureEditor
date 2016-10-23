/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ShapesChooseView.java
 *
 * Shapes chooser view
 *
 * Author huanghaiqi, Created at 2016-10-01
 *
 * Ver 1.0, 2016-10-01, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.halohoop.pictureeditor.R;

public class ShapesChooseView extends FrameLayout implements View.OnClickListener {

    private int mIndex = 0;
    private ImageView mIvShapeLine;
    private ImageView mIvShapeArrow;
    private ImageView mIvShapeRect;
    private ImageView mIvShapeCircle;
    private ImageView mIvShapeRoundrect;

    public ShapesChooseView(Context context) {
        this(context, null);
    }

    public ShapesChooseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapesChooseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.shapes_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIvShapeLine = (ImageView) findViewById(R.id.iv_shape_line);
        mIvShapeArrow = (ImageView) findViewById(R.id.iv_shape_arrow);
        mIvShapeRect = (ImageView) findViewById(R.id.iv_shape_rect);
        mIvShapeCircle = (ImageView) findViewById(R.id.iv_shape_circle);
        mIvShapeRoundrect = (ImageView) findViewById(R.id.iv_shape_roundrect);
        mIvShapeLine.setOnClickListener(this);
        mIvShapeArrow.setOnClickListener(this);
        mIvShapeRect.setOnClickListener(this);
        mIvShapeCircle.setOnClickListener(this);
        mIvShapeRoundrect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int lastIndex = mIndex;
        switch (v.getId()) {
            case R.id.iv_shape_line:
                mIndex = 0;
                break;
            case R.id.iv_shape_arrow:
                mIndex = 1;
                break;
            case R.id.iv_shape_rect:
                mIndex = 2;
                break;
            case R.id.iv_shape_circle:
                mIndex = 3;
                break;
            case R.id.iv_shape_roundrect:
                mIndex = 4;
                break;
        }
        if (lastIndex != mIndex) {
            mIvShapeLine = (ImageView) findViewById(R.id.iv_shape_line);
            mIvShapeArrow = (ImageView) findViewById(R.id.iv_shape_arrow);
            mIvShapeRect = (ImageView) findViewById(R.id.iv_shape_rect);
            mIvShapeCircle = (ImageView) findViewById(R.id.iv_shape_circle);
            mIvShapeRoundrect = (ImageView) findViewById(R.id.iv_shape_roundrect);

            mIvShapeLine.setImageResource(
                    mIndex == 0 ? R.drawable.shape_line_on : R.drawable.shape_line_off);
            mIvShapeArrow.setImageResource(
                    mIndex == 1 ? R.drawable.shape_arrow_on : R.drawable.shape_arrow_off);
            mIvShapeRect.setImageResource(
                    mIndex == 2 ? R.drawable.shape_rect_on : R.drawable.shape_rect_off);
            mIvShapeCircle.setImageResource(
                    mIndex == 3 ? R.drawable.shape_circle_on : R.drawable.shape_circle_off);
            mIvShapeRoundrect.setImageResource(
                    mIndex == 4 ? R.drawable.shape_roundrect_on : R.drawable.shape_roundrect_off);
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onShapeSelected(mIndex);
            }
        }
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.mOnSelectedListener = onSelectedListener;
    }

    public int getSelectedIndex() {
        return mIndex;
    }

    public interface OnSelectedListener {
        void onShapeSelected(int index);
    }
}