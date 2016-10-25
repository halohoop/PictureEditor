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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.halohoop.pictureeditor.utils.LogUtils;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MarkableImageView extends PhotoView {
    private Bitmap mMainBitmap;
    private Bitmap mMosaicBitmap;
    private float mDownX;
    private float mDownY;
    private Paint mDrawPaint;
    private Paint mMosaicPaint;
    private Paint mRubberPaint;
    private Bitmap mMutableBitmap;
    private Canvas mDrawCanvas;
    private float mRealScaleRatio = 1;

    public MarkableImageView(Context context) {
        this(context, null);
    }

    public MarkableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMarkableView();
    }

    private void initFreePaint() {
        mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawPaint.setDither(true);
        mDrawPaint.setColor(Color.BLACK);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeWidth(1);
    }

    private void initRubberPaint() {
        mRubberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRubberPaint.setDither(true);
        mRubberPaint.setStyle(Paint.Style.STROKE);
        mRubberPaint.setStrokeCap(Paint.Cap.ROUND);
        mRubberPaint.setStrokeJoin(Paint.Join.ROUND);
        mRubberPaint.setStrokeWidth(25);
    }

    private void initMosaicPaint() {
        mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMosaicPaint.setDither(true);
        mMosaicPaint.setStyle(Paint.Style.STROKE);
        mMosaicPaint.setStrokeCap(Paint.Cap.ROUND);
        mMosaicPaint.setStrokeJoin(Paint.Join.ROUND);
        mMosaicPaint.setStrokeWidth(25);
    }

    public void setDefaultState() {

    }

    private void initMarkableView() {
        initFreePaint();
        initMosaicPaint();
        initRubberPaint();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.mMainBitmap = bm;
        mMutableBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config
                .ARGB_8888);
        mDrawCanvas = new Canvas(mMutableBitmap);
        BitmapShader rubberShader = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode
                .REPEAT);
        mRubberPaint.setShader(rubberShader);
        getRealScaleRatio();
    }

    private float mFirstScale = 1;

    private void getRealScaleRatio() {
        RectF displayRect = getDisplayRect();
        //图片长宽有一个大于控件长宽
        if (this.mMainBitmap.getWidth() > getMeasuredWidth()
                || this.mMainBitmap.getHeight() > getMeasuredHeight()) {
            //横图还是竖图
            if (this.mMainBitmap.getWidth() >= this.mMainBitmap.getHeight()) {
                //横图
                mRealScaleRatio = ((float) getMeasuredWidth()) / ((float) this.mMainBitmap
                        .getWidth());
                mFirstScale = mRealScaleRatio;
                LogUtils.i("halohoop11:" + mFirstScale);
            } else {//竖图
                mRealScaleRatio = ((float) getMeasuredHeight()) / ((float) this.mMainBitmap
                        .getHeight());
                mFirstScale = mRealScaleRatio;
            }
        } else {//图片长宽都不大于控件长宽
            //横图还是竖图
            if (this.mMainBitmap.getWidth() >= this.mMainBitmap.getHeight()) {
                //横图
                mRealScaleRatio = ((float) getMeasuredWidth()) / ((float) this.mMainBitmap
                        .getWidth());
            } else {//竖图
                mRealScaleRatio = ((float) getMeasuredHeight()) / ((float) this.mMainBitmap
                        .getHeight());
            }
        }
    }

    public void setMosaicBitmap(Bitmap mosaicBitmap) {
        if (!mosaicBitmap.isMutable()) {
            this.mMosaicBitmap = Bitmap.createBitmap(mosaicBitmap.getWidth(), mosaicBitmap
                    .getHeight(), Bitmap.Config
                    .ARGB_8888);
            Canvas canvas = new Canvas(mMosaicBitmap);
            canvas.drawBitmap(mosaicBitmap, 0, 0, null);
            mosaicBitmap.recycle();
        } else {
            this.mMosaicBitmap = mosaicBitmap;
        }
        BitmapShader bitmapShader = new BitmapShader(this.mMosaicBitmap, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        mMosaicPaint.setShader(bitmapShader);
    }

    enum MODE {
        ZOOM, EDIT
    }

    public enum EDIT_MODE {
        PEN, RUBBER, TEXT, SHAPE, MOSAIC
    }

    private MODE mMode = MODE.EDIT;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX(0);
                mDownY = event.getY(0);
                super.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mMode = MODE.ZOOM;
                super.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE.ZOOM) {
                    super.dispatchTouchEvent(event);
                } else if (mMode == MODE.EDIT) {
                    float x = event.getX();
                    float y = event.getY();
                    RectF displayRect = getDisplayRect();
                    float realDrawPosX = (Math.abs(displayRect.left) + x) / mRealScaleRatio;
                    float realDrawPosY = (y - displayRect.top) / mRealScaleRatio;
                    mDrawCanvas.drawCircle(realDrawPosX, realDrawPosY, 20, mRubberPaint);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 手指放开事件
                if (event.getPointerCount() <= 1) {
                    mMode = MODE.EDIT;
                }
                super.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                mMode = MODE.EDIT;
                super.dispatchTouchEvent(event);
                break;
        }
        updateRealScaleRatio();
        return true;
    }

    private void updateRealScaleRatio() {
        RectF displayRect = getDisplayRect();
        float scale = displayRect.width() / mMainBitmap.getWidth();
        if (scale < mFirstScale) {
            mRealScaleRatio = mFirstScale;
        } else {
            mRealScaleRatio = scale;
        }
        LogUtils.i("mRealScaleRatio:" + mRealScaleRatio);
        LogUtils.i("displayRect:" + displayRect);
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
        super.onDraw(canvas);
        if (mMutableBitmap != null) {
            canvas.drawBitmap(mMutableBitmap, getImageMatrix(), null);
        }
    }

}
