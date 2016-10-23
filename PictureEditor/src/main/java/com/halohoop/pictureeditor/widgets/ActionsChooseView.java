/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ActionsChooseView.java
 *
 * actions chooser view
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

public class ActionsChooseView extends FrameLayout implements View.OnClickListener {

    private ImageView mIvMosaic;
    private ImageView mIvShape;
    private ImageView mIvWord;
    private ImageView mIvPen;
    private int mIndex = 0;

    public ActionsChooseView(Context context) {
        this(context, null);
    }

    public ActionsChooseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionsChooseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.actions_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mIvPen = (ImageView) findViewById(R.id.iv_pen);
        mIvWord = (ImageView) findViewById(R.id.iv_word);
        mIvShape = (ImageView) findViewById(R.id.iv_shape);
        mIvMosaic = (ImageView) findViewById(R.id.iv_mosaic);
        mIvPen.setOnClickListener(this);
        mIvWord.setOnClickListener(this);
        mIvShape.setOnClickListener(this);
        mIvMosaic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int lastIndex = mIndex;
        switch (v.getId()) {
            case R.id.iv_pen:
                mIndex = 0;
                break;
            case R.id.iv_word:
                mIndex = 1;
                break;
            case R.id.iv_shape:
                mIndex = 2;
                break;
            case R.id.iv_mosaic:
                mIndex = 3;
                break;
        }
        if (lastIndex != mIndex) {
            mIvPen.setImageResource(mIndex == 0 ? R.drawable.pen_on : R.drawable.pen_off);
            mIvWord.setImageResource(mIndex == 1 ? R.drawable.word_on : R.drawable.word_off);
            mIvShape.setImageResource(mIndex == 2 ? R.drawable.shape_on : R.drawable.shape_off);
            mIvMosaic.setImageResource(mIndex == 3 ? R.drawable.mosaic_on : R.drawable.mosaic_off);
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onActionSelected(mIndex);
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
        void onActionSelected(int index);
    }
}