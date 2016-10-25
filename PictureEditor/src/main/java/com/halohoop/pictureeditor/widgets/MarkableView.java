/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MarkableView.java
 *
 * Edit view
 *
 * Author huanghaiqi, Created at 2016-10-24
 *
 * Ver 1.0, 2016-10-24, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class MarkableView extends View {
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private float mScaleRatio = 1;
    private float mBeginScaleRatio;
    private float mMaxScaleRatio = 3;
    private float mMinScaleRatio = 1;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mDownX0;
    private float mDownY0;
    private float mDownX1;
    private float mDownY1;
    private float mDistance;
    private PointF mMidPointF;

    public MarkableView(Context context) {
        this(context, null);
    }

    public MarkableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
        mMatrix.reset();
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new RuntimeException("please pass in a not null and not recycled bitmap");
        }
        this.mBitmap = bitmap;
        int bitmapWidth = this.mBitmap.getWidth();
        int bitmapHeight = this.mBitmap.getHeight();
        if (bitmapWidth <= bitmapHeight) {//竖图和正方形图，或者是横图
            verticalBitmapInit(bitmapWidth, bitmapHeight);
        } else {
            horizontalBitmapInit(bitmapWidth, bitmapHeight);
        }
        invalidate();
    }

    private void verticalBitmapInit(int bitmapWidth, int bitmapHeight) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (bitmapHeight >= measuredHeight) {
            mMinScaleRatio = ((float) measuredHeight) / ((float) bitmapHeight);
        } else {
            mMinScaleRatio = ((float) bitmapHeight) / ((float) measuredHeight);
        }
        if (bitmapWidth >= measuredWidth) {
            mMaxScaleRatio = ((float) measuredWidth) / ((float) bitmapWidth) * 5;
        } else {
            mMaxScaleRatio = bitmapWidth / measuredWidth * 5;
        }
        mScaleRatio = mMinScaleRatio;
        mMatrix.setScale(mScaleRatio, mScaleRatio);
    }

    private void horizontalBitmapInit(int bitmapWidth, int bitmapHeight) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (bitmapHeight >= measuredHeight) {
            mMaxScaleRatio = ((float) measuredHeight) / ((float) bitmapHeight) * 5;
        } else {
            mMaxScaleRatio = ((float) bitmapHeight) / ((float) measuredHeight) * 5;
        }
        if (bitmapWidth >= measuredWidth) {
            mMinScaleRatio = ((float) measuredWidth) / ((float) bitmapWidth);
        } else {
            mMinScaleRatio = bitmapWidth / measuredWidth;
        }
        mScaleRatio = mMinScaleRatio;
        mMatrix.setScale(mScaleRatio, mScaleRatio);
    }

    enum MODE {
        NORMAL, ZOOM, EDIT
    }

    private MODE mMode = MODE.NORMAL;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mScaleGestureDetector.onTouchEvent(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX0 = event.getX(0);
                mDownY0 = event.getY(0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mDownX1 = event.getX(1);
                mDownY1 = event.getY(1);
                mDistance = getDistance(mDownX0, mDownY0, mDownX1, mDownY1);
                mMidPointF = getMidPointF(mDownX0, mDownY0, mDownX1, mDownY1);
                mMode = MODE.ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE.ZOOM) {
                    float moveX0 = event.getX(0);
                    float moveY0 = event.getY(0);
                    float moveX1 = event.getX(1);
                    float moveY1 = event.getY(1);
                    float distance = getDistance(moveX0, moveY0, moveX1, moveY1);
                    PointF midPointF = getMidPointF(moveX0, moveY0, moveX1, moveY1);
                    mScaleRatio = distance / mDistance - 1;
                    if (mScaleRatio > mMaxScaleRatio) {
                        mScaleRatio = mMaxScaleRatio;
                    } else if (mScaleRatio < mMinScaleRatio) {
                        mScaleRatio = mMinScaleRatio;
                    }
                    mMatrix.setScale(mScaleRatio, mScaleRatio, midPointF.x, midPointF.y);
                    mMatrix.preTranslate(midPointF.x - mMidPointF.x, midPointF.y - mMidPointF.y);
                    invalidate();
                } else {

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 手指放开事件
                mMode = MODE.NORMAL;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private float getDistance(float downX0, float downY0, float downX1, float downY1) {
        float disX = Math.abs(downX0 - downX1);
        float disY = Math.abs(downY0 - downY1);
        double distance = Math.sqrt(disX * disX + disY * disY);
        return (float) distance;
    }

    private PointF getMidPointF(float downX0, float downY0, float downX1, float downY1) {
        float leftDownX = Math.min(downX0, downX1);
        float leftDownY = Math.min(downY0, downY1);
        float disX = Math.abs(downX0 - downX1);
        float disY = Math.abs(downY0 - downY1);
        PointF pointF = new PointF();
        pointF.x = leftDownX + disX / 2;
        pointF.y = leftDownY + disY / 2;
        return pointF;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }

    class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleRatio = detector.getScaleFactor() / 10;
            float deltaScaleRatio = scaleRatio - mBeginScaleRatio;
            mScaleRatio += deltaScaleRatio;
            if (mScaleRatio > mMaxScaleRatio) {
                mScaleRatio = mMaxScaleRatio;
            } else if (mScaleRatio < mMinScaleRatio) {
                mScaleRatio = mMinScaleRatio;
            }
            mMatrix.setScale(mScaleRatio, mScaleRatio);
            invalidate();
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mBeginScaleRatio = detector.getScaleFactor() / 10;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}
