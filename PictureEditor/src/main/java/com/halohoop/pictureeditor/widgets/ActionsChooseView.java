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

    private AnimationEndMark mAnimationEndMark;

    public void setAnimationEndMark(AnimationEndMark animationEndMark) {
        this.mAnimationEndMark = animationEndMark;
    }
    public final static int FRAGMENT_RUBBER = 0;
    public final static int FRAGMENT_PEN = 1;
    public final static int FRAGMENT_TEXT = 2;
    public final static int FRAGMENT_SHAPE = 3;
    public final static int FRAGMENT_MOSAIC = 4;
    public final static int FRAGMENT_COLOR_PICKER = 5;
    @Override
    public void onClick(View v) {
        if (mAnimationEndMark == null) {
            throw new RuntimeException("please call setAnimationEndMarkHelper after"
                    + "inflate a ActionsChooseView");
        }
        if (mAnimationEndMark.isAnimationEnd()) {
            int lastIndex = mIndex;
            switch (v.getId()) {
                case R.id.iv_pen:
                    mIndex = FRAGMENT_PEN;
                    break;
                case R.id.iv_word:
                    mIndex = FRAGMENT_TEXT;
                    break;
                case R.id.iv_shape:
                    mIndex = FRAGMENT_SHAPE;
                    break;
                case R.id.iv_mosaic:
                    mIndex = FRAGMENT_MOSAIC;
                    break;
            }
            if (lastIndex != mIndex) {
                mIvPen.setImageResource(mIndex == FRAGMENT_PEN ? R.drawable.pen_on : R.drawable.pen_off);
                mIvWord.setImageResource(mIndex == FRAGMENT_TEXT ? R.drawable.word_on : R.drawable.word_off);
                mIvShape.setImageResource(mIndex == FRAGMENT_SHAPE ? R.drawable.shape_on : R.drawable.shape_off);
                mIvMosaic.setImageResource(mIndex == FRAGMENT_MOSAIC ? R.drawable.mosaic_on : R.drawable
                        .mosaic_off);
                if (mOnSelectedListener != null) {
                    mOnSelectedListener.onActionSelected(mIndex);
                }
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