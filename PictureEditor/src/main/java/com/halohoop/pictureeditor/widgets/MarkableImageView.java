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
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.halohoop.pictureeditor.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

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
    private float mRealDownPosX;
    private float mRealDownPosY;
    private int mColor;
    private PointF mMidPoint;

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

    private Paint initDrawPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(1);
        return paint;
    }

    private Paint initRubberPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(25);
        return paint;
    }

    private Paint initMosaicPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(25);
        return paint;
    }

    public void setDefaultState() {
        mColor = parseColor("#FF2968");
        mDrawPaint.setColor(mColor);
    }

    private int parseColor(String colorHex) {
        try {
            return Color.parseColor(colorHex);
        } catch (IllegalArgumentException iae) {
            return Color.BLACK;
        }
    }

    public void updateDrawPaintStrokeWidth(float strokeWidth) {
        mDrawPaint.setStrokeWidth(strokeWidth);
    }

    public void updateDrawPaintAlpha(int alpha) {
        mDrawPaint.setAlpha(alpha);
    }

    public void updateRubberPaintStrokeWidth(float strokeWidth) {
        mRubberPaint.setStrokeWidth(strokeWidth);
    }

    public void updateMosaicPaintStrokeWidth(float strokeWidth) {
        mMosaicPaint.setStrokeWidth(strokeWidth);
    }


    private void initMarkableView() {
        mDrawPaint = initDrawPaint();
        mMosaicPaint = initMosaicPaint();
        mRubberPaint = initRubberPaint();
        setDefaultState();
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
                mFirstScale = mRealScaleRatio;
            } else {//竖图
                mRealScaleRatio = ((float) getMeasuredHeight()) / ((float) this.mMainBitmap
                        .getHeight());
                mFirstScale = mRealScaleRatio;
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
    private EDIT_MODE mEditMode = EDIT_MODE.PEN;
    //是否正处在单指画的过程中,down + move,no up
    private boolean mIsEditing = false;

    public void setEditMode(EDIT_MODE editMode) {
        this.mEditMode = editMode;
    }


    public EDIT_MODE getEditMode() {
        return this.mEditMode;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsEditing = true;
                mDownX = event.getX(0);
                mDownY = event.getY(0);
                mRealDownPosX = getRealPosXOnBitmap(mDownX);
                mRealDownPosY = getRealPosYOnBitmap(mDownY);
                createNewMove(mRealDownPosX, mRealDownPosY);
                super.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mIsEditing = false;
                mMode = MODE.ZOOM;
                if (mEveryMoves.size() > 0) {
                    mEveryMoves.remove(mEveryMoves.size() - 1);
                }
                super.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE.ZOOM) {
                    super.dispatchTouchEvent(event);
                } else if (mMode == MODE.EDIT) {
                    float moveX = event.getX(0);
                    float moveY = event.getY(0);
                    float realMovePosX = getRealPosXOnBitmap(moveX);
                    float realMovePosY = getRealPosYOnBitmap(moveY);
                    updateNewMove(realMovePosX, realMovePosY);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 手指放开事件
                if (event.getPointerCount() <= 1) {
                    mMode = MODE.EDIT;
                    mIsEditing = false;
                }
                if (mMode == MODE.ZOOM) {
                    super.dispatchTouchEvent(event);
                } else if (mMode == MODE.EDIT) {
                    float moveX = event.getX(0);
                    float moveY = event.getY(0);
                    float realMovePosX = getRealPosXOnBitmap(moveX);
                    float realMovePosY = getRealPosYOnBitmap(moveY);
                    updateNewMove(realMovePosX, realMovePosY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mMode = MODE.EDIT;
                mIsEditing = false;
                finalDrawOnBitmap();
                super.dispatchTouchEvent(event);
                break;
        }
        updateRealScaleRatio();
        return true;
    }

    private void createNewMove(float realDownPosX, float realDownPosY) {
        //create a new move
        EveryMove everyMove = new EveryMove();
        if (mEditMode == EDIT_MODE.PEN) {
            everyMove.mEditMode = EDIT_MODE.PEN;
            everyMove.mColor = mColor;
            everyMove.mAlpha = mDrawPaint.getAlpha();
            everyMove.mStrokeWidth = mDrawPaint.getStrokeWidth();
            everyMove.mPath = new Path();
            everyMove.mPath.reset();
            everyMove.mPath.moveTo(realDownPosX, realDownPosY);
        } else if (mEditMode == EDIT_MODE.RUBBER) {
            everyMove.mEditMode = EDIT_MODE.RUBBER;
            everyMove.mStrokeWidth = mRubberPaint.getStrokeWidth();
            everyMove.mPath = new Path();
            everyMove.mPath.reset();
            everyMove.mPath.moveTo(realDownPosX, realDownPosY);
        }
        mEveryMoves.add(everyMove);
    }

    private void updateNewMove(float realMovePosX, float realMovePosY) {
        if (mEveryMoves.size() <= 0) {
            return;
        }
        EveryMove everyMove = mEveryMoves.get(mEveryMoves.size() - 1);
        if (mEditMode == EDIT_MODE.PEN) {
            everyMove.mPath.quadTo(mRealDownPosX, mRealDownPosY,
                    (mRealDownPosX + realMovePosX) / 2, (mRealDownPosY + realMovePosY) / 2);
            mRealDownPosX = realMovePosX;
            mRealDownPosY = realMovePosY;
        } else if (mEditMode == EDIT_MODE.RUBBER) {
        }
        invalidate();
    }

    public void updateNewMoveInOnDraw(Canvas canvas) {
        if (mEveryMoves.size() <= 0) {
            return;
        }
        for (int i = 0; i < mEveryMoves.size(); i++) {
            EveryMove everyMove = mEveryMoves.get(i);
            if (mEditMode == EDIT_MODE.PEN) {
                mDrawPaint.setColor(everyMove.mColor);
                mDrawPaint.setStrokeWidth(everyMove.mStrokeWidth);
                mDrawPaint.setAlpha(everyMove.mAlpha);
                canvas.drawPath(everyMove.mPath, mDrawPaint);
            } else if (mEditMode == EDIT_MODE.RUBBER) {
            }
        }
    }

    private void finalDrawOnBitmap() {
        if (mEveryMoves.size() <= 0) {
            return;
        }
        EveryMove everyMove = mEveryMoves.get(mEveryMoves.size() - 1);
        if (mEditMode == EDIT_MODE.PEN) {
            mDrawPaint.setStrokeWidth(everyMove.mStrokeWidth);
            mDrawPaint.setColor(everyMove.mColor);
            mDrawPaint.setAlpha(everyMove.mAlpha);
            mDrawCanvas.drawPath(everyMove.mPath, mDrawPaint);
        } else if (mEditMode == EDIT_MODE.RUBBER) {
        }
        invalidate();
    }

    private float getRealPosYOnBitmap(float y) {
        RectF displayRect = getDisplayRect();
        return (y - displayRect.top) / mRealScaleRatio;
    }

    private float getRealPosXOnBitmap(float x) {
        RectF displayRect = getDisplayRect();
        return (Math.abs(displayRect.left) + x) / mRealScaleRatio;
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        mMidPoint = new PointF();
        mMidPoint.x = measuredWidth >> 1;
        mMidPoint.y = measuredHeight >> 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsEditing) {
            canvas.save();
            canvas.setMatrix(getImageMatrix());
            updateNewMoveInOnDraw(canvas);
            canvas.restore();
        } else {
            if (mMutableBitmap != null) {
                canvas.drawBitmap(mMutableBitmap, getImageMatrix(), null);
            }
        }
    }

    private List<EveryMove> mEveryMoves = new ArrayList<>();

    private class EveryMove {
        EDIT_MODE mEditMode;
        Path mPath;
        int mColor;
        float mStrokeWidth;
        int mAlpha;
    }

}
