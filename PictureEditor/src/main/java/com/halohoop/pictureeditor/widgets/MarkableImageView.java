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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.halohoop.pictureeditor.R;
import com.halohoop.pictureeditor.controllers.DeleteScreenshot;
import com.halohoop.pictureeditor.utils.LogUtils;
import com.halohoop.pictureeditor.widgets.beans.Shape;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.senab.photoview.PhotoView;

public class MarkableImageView extends PhotoView {
    private Bitmap mMainBitmap;
    private Bitmap mMosaicBitmap;
    private Bitmap mCacheMutableBitmap;
    private float mDownX;
    private float mDownY;
    private Paint mDrawPaint;
    private float mDrawPaintStrokeWidth = 1;
    private Paint mMosaicPaint;
    private float mMosaicPaintStrokeWidth = 25;
    private Paint mRubberPaint;
    private float mRubberPaintStrokeWidth = 25;
    private Paint mShapePaint;
    private float mShapePaintStrokeWidth = 3;
    private Bitmap mMutableBitmap;
    private Bitmap mMutableBitmap1;
    private Canvas mDrawCanvas;
    private float mRealScaleRatio = 1;
    private float mRealDownPosX;
    private float mRealDownPosY;
    private int mColor;
    private int mAlpha;
    private PointF mMidPoint;
    //圆角矩形角半径
    private float radiusCornor = 5.0f;
    private Canvas mDrawCanvas2;
    private Notification.Builder mNotificationBuilder;
    private Notification.Builder mPublicNotificationBuilder;
    private WindowManager mWindowManager;
    private NotificationManager mNotificationManager;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private int mNotificationIconSize;
    private float mBgPadding;
    private float mBgPaddingScale;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private Notification.BigPictureStyle mNotificationStyle;

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

    private Paint initShapePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mShapePaintStrokeWidth);
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
        mAlpha = 255;
        mDrawPaint.setColor(mColor);
    }

    private void resetAllPaintToMatchOutside() {
        mDrawPaint.setColor(mColor);
        mDrawPaint.setAlpha(mAlpha);
        mDrawPaint.setStrokeWidth(mDrawPaintStrokeWidth);
        mRubberPaint.setStrokeWidth(mRubberPaintStrokeWidth);
        mMosaicPaint.setStrokeWidth(mMosaicPaintStrokeWidth);
    }

    private int parseColor(String colorHex) {
        try {
            return Color.parseColor(colorHex);
        } catch (IllegalArgumentException iae) {
            return Color.BLACK;
        }
    }

    public void updateDrawPaintStrokeWidth(float strokeWidth) {
        this.mDrawPaintStrokeWidth = strokeWidth;
        mDrawPaint.setStrokeWidth(strokeWidth);
    }

    public void updateDrawPaintAlpha(int alpha) {
        this.mAlpha = alpha;
        mDrawPaint.setAlpha(alpha);
    }

    public void updateRubberPaintStrokeWidth(float strokeWidth) {
        this.mRubberPaintStrokeWidth = strokeWidth;
        mRubberPaint.setStrokeWidth(strokeWidth);
    }

    public void updateMosaicPaintStrokeWidth(float strokeWidth) {
        this.mMosaicPaintStrokeWidth = strokeWidth;
        mMosaicPaint.setStrokeWidth(strokeWidth);
    }


    private void initMarkableView() {
        mDrawPaint = initDrawPaint();
        mRubberPaint = initRubberPaint();
        mShapePaint = initShapePaint();
        mMosaicPaint = initMosaicPaint();
        setDefaultState();
    }

    private int mIsSetImageBitmapFirstTimeRunMark = 0;

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.mMainBitmap = bm;
        if (mIsSetImageBitmapFirstTimeRunMark == 0) {
            mIsSetImageBitmapFirstTimeRunMark++;
            mMutableBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config
                    .ARGB_8888);
            mDrawCanvas = new Canvas(mMutableBitmap);
            BitmapShader rubberShader = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode
                    .REPEAT);
            mRubberPaint.setShader(rubberShader);
            getRealScaleRatio();
        }
        invalidate();
    }

    public boolean isEdited() {
        return mEveryMoves.size() > 0;
    }

    //是否正在保存当中
    private boolean mIsSaving = false;

    /**
     * called by EditorActivity(Main Thread) or itself(new Thread)
     */
    public synchronized void destroyEveryThing() {
        if (!mIsSaving) {
            if (mMutableBitmap != null && !mMutableBitmap.isRecycled()) {
                mMutableBitmap.recycle();
                mMutableBitmap = null;
            }
            mEveryMoves.clear();
            mEveryMoves = null;
        }
    }

    //横图和正方形图 或者是 竖图
    enum VERTICAL_HORIZONTAL {
        VERTICAL, HORIZONTAL
    }

    private float mFirstScale = 1;
    //图片 横图和正方形图 或者是 竖图
    private VERTICAL_HORIZONTAL mVerticalHorizontal = VERTICAL_HORIZONTAL.VERTICAL;

    private void getRealScaleRatio() {
        RectF displayRect = getDisplayRect();
        //图片长宽有一个大于控件长宽
        if (this.mMainBitmap.getWidth() > getMeasuredWidth()
                || this.mMainBitmap.getHeight() > getMeasuredHeight()) {
            //横图还是竖图
            if (this.mMainBitmap.getWidth() >= this.mMainBitmap.getHeight()) {
                //横图或者正方形
                mRealScaleRatio = ((float) getMeasuredWidth()) / ((float) this.mMainBitmap
                        .getWidth());
                mFirstScale = mRealScaleRatio;
                mVerticalHorizontal = VERTICAL_HORIZONTAL.HORIZONTAL;
            } else {//竖图
                mRealScaleRatio = ((float) getMeasuredHeight()) / ((float) this.mMainBitmap
                        .getHeight());
                mFirstScale = mRealScaleRatio;
                mVerticalHorizontal = VERTICAL_HORIZONTAL.VERTICAL;
            }
        } else {//图片长宽都不大于控件长宽
            //横图还是竖图
            if (this.mMainBitmap.getWidth() >= this.mMainBitmap.getHeight()) {
                //横图或者正方形
                mRealScaleRatio = ((float) getMeasuredWidth()) / ((float) this.mMainBitmap
                        .getWidth());
                mFirstScale = mRealScaleRatio;
                mVerticalHorizontal = VERTICAL_HORIZONTAL.HORIZONTAL;
            } else {//竖图
                mRealScaleRatio = ((float) getMeasuredHeight()) / ((float) this.mMainBitmap
                        .getHeight());
                mFirstScale = mRealScaleRatio;
                mVerticalHorizontal = VERTICAL_HORIZONTAL.VERTICAL;
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
        if (mEditMode == EDIT_MODE.PEN) {
            EveryMove everyMove = new EveryMove();
            everyMove.mEditMode = EDIT_MODE.PEN;
            everyMove.mColor = mColor;
            everyMove.mAlpha = mDrawPaint.getAlpha();
            everyMove.mStrokeWidth = mDrawPaint.getStrokeWidth();
            everyMove.mPath = new Path();
            everyMove.mPath.reset();
            everyMove.mPath.moveTo(realDownPosX, realDownPosY);
            pushIntoMoves(everyMove);
        } else if (mEditMode == EDIT_MODE.RUBBER) {
            if (mEveryMoves.size() > 0) {//没有其他东西的时候禁止使用橡皮擦
                EveryMove everyMove = new EveryMove();
                everyMove.mEditMode = EDIT_MODE.RUBBER;
                everyMove.mStrokeWidth = mRubberPaint.getStrokeWidth();
                everyMove.mPath = new Path();
                everyMove.mPath.reset();
                everyMove.mPath.moveTo(realDownPosX, realDownPosY);
                pushIntoMoves(everyMove);
            }
        } else if (mEditMode == EDIT_MODE.MOSAIC) {
            EveryMove everyMove = new EveryMove();
            everyMove.mEditMode = EDIT_MODE.MOSAIC;
            everyMove.mStrokeWidth = mMosaicPaint.getStrokeWidth();
            everyMove.mPath = new Path();
            everyMove.mPath.reset();
            everyMove.mPath.moveTo(realDownPosX, realDownPosY);
            pushIntoMoves(everyMove);
        } else if (mEditMode == EDIT_MODE.SHAPE) {
            EveryMove everyMove = new EveryMove();
            everyMove.mEditMode = EDIT_MODE.SHAPE;
            everyMove.mColor = mColor;
            everyMove.mShape = createNewShape(realDownPosX, realDownPosY);
            pushIntoMoves(everyMove);
        }
    }

    private void pushIntoMoves(EveryMove everyMove) {
        mEveryMoves.add(everyMove);
        if (mEveryMoves.size() > 10) {
            //将前5个固定到最终的图片上
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Bitmap cacheMutableBitmap = Bitmap.createBitmap(
                            mMainBitmap.getWidth(),
                            mMainBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    mDrawCanvas2 = new Canvas(cacheMutableBitmap);
                    if (mCacheMutableBitmap != null && !mCacheMutableBitmap.isRecycled()) {
                        mDrawCanvas2.drawBitmap(mCacheMutableBitmap, 0, 0, null);
                    }
                    final Bitmap oldBitmap = mCacheMutableBitmap;
                    mCacheMutableBitmap = cacheMutableBitmap;
                    for (int i = 0; i < 5; i++) {
                        EveryMove move = mEveryMoves.remove(0);
                        fixMovesToBitmap(mDrawCanvas2, move);
                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                            if (oldBitmap != null && !oldBitmap.isRecycled()) {
                                oldBitmap.recycle();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private Shape.SHAPE_TYPE mShapeType = Shape.SHAPE_TYPE.LINE;

    public void setShapeType(Shape.SHAPE_TYPE shapeType) {
        this.mShapeType = shapeType;
    }

    private Shape createNewShape(float realDownPosX, float realDownPosY) {
        Shape shape = null;
        switch (mShapeType) {
            case LINE:
                Shape line = new Shape(Shape.SHAPE_TYPE.LINE);
                shape = line;
                PointF[] linePoints = line.getPoints();
                linePoints[0].x = realDownPosX;
                linePoints[0].y = realDownPosY;
                break;
            case ARROW:
                Shape triangle = new Shape(Shape.SHAPE_TYPE.ARROW);
                shape = triangle;
                PointF[] arrowPoints = triangle.getPoints();
                arrowPoints[4].x = realDownPosX;
                arrowPoints[4].y = realDownPosY;
                break;
            case RECT:
                Shape rectangle = new Shape(Shape.SHAPE_TYPE.RECT);
                shape = rectangle;
                PointF[] rectanglePoints = rectangle.getPoints();
                rectanglePoints[0].x = realDownPosX;
                rectanglePoints[0].y = realDownPosY;
                break;
            case CIRCLE:
                Shape circle = new Shape(Shape.SHAPE_TYPE.CIRCLE);
                shape = circle;
                PointF[] circlePoints = circle.getPoints();
                circlePoints[0].x = realDownPosX;
                circlePoints[0].y = realDownPosY;
                break;
            case ROUNDRECT:
                Shape roundRectangle = new Shape(Shape.SHAPE_TYPE.ROUNDRECT);
                shape = roundRectangle;
                PointF[] roundRectanglePoints = roundRectangle.getPoints();
                roundRectanglePoints[0].x = realDownPosX;
                roundRectanglePoints[0].y = realDownPosY;
                break;
        }
        return shape;
    }

    private void updateNewMove(float realMovePosX, float realMovePosY) {
        if (mEveryMoves.size() <= 0) {
            return;
        }
        EveryMove everyMove = mEveryMoves.get(mEveryMoves.size() - 1);
        if (everyMove.mEditMode == EDIT_MODE.PEN) {
            handleActionMovePenRubberAndMosaic(realMovePosX, realMovePosY, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.RUBBER) {
            handleActionMovePenRubberAndMosaic(realMovePosX, realMovePosY, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.MOSAIC) {
            handleActionMovePenRubberAndMosaic(realMovePosX, realMovePosY, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.SHAPE) {
            updateShapeState(realMovePosX, realMovePosY);
        }
        invalidate();
    }

    private void handleActionMovePenRubberAndMosaic(float realMovePosX, float realMovePosY,
                                                    EveryMove everyMove) {
        everyMove.mPath.quadTo(mRealDownPosX, mRealDownPosY,
                (mRealDownPosX + realMovePosX) / 2, (mRealDownPosY + realMovePosY) / 2);
        mRealDownPosX = realMovePosX;
        mRealDownPosY = realMovePosY;
    }

    private void updateShapeState(float realMovePosX, float realMovePosY) {
        Shape shape = mEveryMoves.get(mEveryMoves.size() - 1).mShape;
        switch (shape.getShapeType()) {
            case LINE:
                PointF[] circlePointFs = shape.getPoints();
                circlePointFs[1].x = realMovePosX;
                circlePointFs[1].y = realMovePosY;
                break;
            case ARROW:
                updateDistanceXYAndAngle(realMovePosX, realMovePosY);
                updateTrianglePointFs(shape, realMovePosX, realMovePosY);
                break;
            case RECT:
                updateDistanceXY(realMovePosX, realMovePosY);
                updateRectanglePointFs(shape, realMovePosX, realMovePosY);
                break;
            case CIRCLE:
                updateDistanceXY(realMovePosX, realMovePosY);
                updateCirclePointFsAndRadius(shape, realMovePosX, realMovePosY);
                break;
            case ROUNDRECT:
                updateDistanceXY(realMovePosX, realMovePosY);
                updateRectanglePointFs(shape, realMovePosX, realMovePosY);
                break;
        }
    }

    private void updateCirclePointFsAndRadius(Shape shape, float realMovePosX, float realMovePosY) {
        PointF[] circlePointFs = shape.getPoints();
        circlePointFs[1].x = realMovePosX;
        circlePointFs[1].y = realMovePosY;
        float radius = (float) Math.sqrt(mDisX * mDisX + mDisY * mDisY);
        shape.setRadius(radius);
    }

    private void updateRectanglePointFs(Shape shape, float realMovePosX, float realMovePosY) {
        PointF[] rectanglePointFs = shape.getPoints();
        //----------------------
        //left top to right bottom
        if (mRealDownPosX <= realMovePosX && mRealDownPosY <= realMovePosY) {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y + Math.abs(mDisY);
            rectanglePointFs[2].x = realMovePosX;
            rectanglePointFs[2].y = realMovePosY;
            rectanglePointFs[3].x = realMovePosX;
            rectanglePointFs[3].y = realMovePosY - Math.abs(mDisY);
        }
        //right bottom to left top
        else if (mRealDownPosX >= realMovePosX && mRealDownPosY >= realMovePosY) {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y - Math.abs(mDisY);
            rectanglePointFs[2].x = realMovePosX;
            rectanglePointFs[2].y = realMovePosY;
            rectanglePointFs[3].x = realMovePosX;
            rectanglePointFs[3].y = realMovePosY + Math.abs(mDisY);
        }
        //left bottom to right top
        else if (mRealDownPosX < realMovePosX && mRealDownPosY > realMovePosY) {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y - Math.abs(mDisY);
            rectanglePointFs[2].x = realMovePosX;
            rectanglePointFs[2].y = realMovePosY;
            rectanglePointFs[3].x = realMovePosX;
            rectanglePointFs[3].y = realMovePosY + Math.abs(mDisY);
        }
        //right top to left bottom
        else {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y + Math.abs(mDisY);
            rectanglePointFs[2].x = realMovePosX;
            rectanglePointFs[2].y = realMovePosY;
            rectanglePointFs[3].x = realMovePosX;
            rectanglePointFs[3].y = realMovePosY - Math.abs(mDisY);
        }
    }

    /**
     * the angle of arrow to rotate
     */
    private double mAngle;
    private float heightOfArrow = 40.0f;
    private float widthOfArrow = 10.0f;
    private float mDisX;
    private float mDisY;

    private void updateDistanceXYAndAngle(float realMovePosX, float realMovePosY) {
        updateDistanceXY(realMovePosX, realMovePosY);
        mAngle = Math.atan(mDisY / mDisX);
    }

    private void updateDistanceXY(float realMovePosX, float realMovePosY) {
        mDisX = realMovePosX - mRealDownPosX;
        mDisY = realMovePosY - mRealDownPosY;
    }

    public void updateTrianglePointFs(Shape shape, float realMovePosX, float realMovePosY) {
        PointF[] trianglePointFs = shape.getPoints();

        trianglePointFs[0].x = realMovePosX;
        trianglePointFs[0].y = realMovePosY;

        double sH = Math.abs(Math.sin(mAngle) * heightOfArrow);
        double cH = Math.abs(Math.cos(mAngle) * heightOfArrow);

        double sW = Math.abs(Math.sin(mAngle) * widthOfArrow);
        double cW = Math.abs(Math.cos(mAngle) * widthOfArrow);


        //left top to right bottom
        if (mRealDownPosX <= realMovePosX && mRealDownPosY <= realMovePosY) {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y - sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x - cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x + sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x - sW);
        }
        //right bottom to left top
        else if (mRealDownPosX >= realMovePosX && mRealDownPosY >= realMovePosY) {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y + sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x + cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x - sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x + sW);
        }
        //left bottom to right top
        else if (mRealDownPosX < realMovePosX && mRealDownPosY > realMovePosY) {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y + sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x - cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x - sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x + sW);
        }
        //right top to left bottom
        else {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y - sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x + cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x + sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x - sW);
        }
    }

    public void updateNewMoveInOnDraw(Canvas ondrawCanvas) {
        if (mEveryMoves.size() <= 0) {
            return;
        }
        for (int i = 0; i < mEveryMoves.size(); i++) {
            EveryMove everyMove = mEveryMoves.get(i);
            if (everyMove.mEditMode == EDIT_MODE.PEN) {
                drawPen(ondrawCanvas, everyMove);
            } else if (everyMove.mEditMode == EDIT_MODE.RUBBER) {
                drawRubber(ondrawCanvas, everyMove);
            } else if (everyMove.mEditMode == EDIT_MODE.MOSAIC) {
                drawMosaic(ondrawCanvas, everyMove);
            } else if (everyMove.mEditMode == EDIT_MODE.SHAPE) {
                drawShape(ondrawCanvas, everyMove);
            }
        }
        resetAllPaintToMatchOutside();
    }

    private void drawMosaic(Canvas canvas, EveryMove everyMove) {
        mMosaicPaint.setStrokeWidth(everyMove.mStrokeWidth);
        canvas.drawPath(everyMove.mPath, mMosaicPaint);
    }

    private void drawRubber(Canvas canvas, EveryMove everyMove) {
        mRubberPaint.setStrokeWidth(everyMove.mStrokeWidth);
        canvas.drawPath(everyMove.mPath, mRubberPaint);
    }

    private void drawPen(Canvas canvas, EveryMove everyMove) {
        mDrawPaint.setColor(everyMove.mColor);
        mDrawPaint.setStrokeWidth(everyMove.mStrokeWidth);
        mDrawPaint.setAlpha(everyMove.mAlpha);
        canvas.drawPath(everyMove.mPath, mDrawPaint);
    }

    private void finalDrawOnBitmap() {
        if (mEveryMoves.size() <= 0) {
            return;
        }
        EveryMove everyMove = mEveryMoves.get(mEveryMoves.size() - 1);
        if (everyMove.mEditMode == EDIT_MODE.PEN) {
            drawPen(mDrawCanvas, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.RUBBER) {
            drawRubber(mDrawCanvas, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.MOSAIC) {
            drawMosaic(mDrawCanvas, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.SHAPE) {
            drawShape(mDrawCanvas, everyMove);
        }
        resetAllPaintToMatchOutside();
        invalidate();
    }

    private void fixMovesToBitmap(Canvas canvas, EveryMove everyMove) {
        if (everyMove.mEditMode == EDIT_MODE.PEN) {
            drawPen(canvas, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.RUBBER) {
            drawRubber(canvas, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.MOSAIC) {
            drawMosaic(canvas, everyMove);
        } else if (everyMove.mEditMode == EDIT_MODE.SHAPE) {
            drawShape(canvas, everyMove);
        }
    }

    private void drawShape(Canvas canvas, EveryMove everyMove) {
        try {
            Shape shape = everyMove.mShape;
            mShapePaint.setColor(everyMove.mColor);
            PointF[] pointFs = shape.getPoints();
            switch (shape.getShapeType()) {
                case LINE:
                    canvas.drawLine(pointFs[0].x, pointFs[0].y,
                            pointFs[1].x, pointFs[1].y, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
                case ARROW:
                    //draw arrow
                    //draw triangle
                    Path triangle = new Path();
                    triangle.moveTo(pointFs[0].x, pointFs[0].y);
                    triangle.lineTo(pointFs[2].x, pointFs[2].y);
                    triangle.lineTo(pointFs[3].x, pointFs[3].y);
                    triangle.close();
                    canvas.drawPath(triangle, mShapePaint);

                    canvas.drawLine(pointFs[4].x, pointFs[4].y, pointFs[1].x,
                            pointFs[1].y, mShapePaint);

                    //draw arrow
                    break;
                case RECT:
                    mShapePaint.setStyle(Paint.Style.STROKE);
                    float minLeftTopX = Math.min(pointFs[0].x, pointFs[2].x);
                    float minLeftTopY = Math.min(pointFs[0].y, pointFs[2].y);
                    float maxRightBottomX = Math.max(pointFs[0].x, pointFs[2].x);
                    float maxRightBottomY = Math.max(pointFs[0].y, pointFs[2].y);
                    canvas.drawRect(minLeftTopX, minLeftTopY,
                            maxRightBottomX, maxRightBottomY, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
                case CIRCLE:
                    float radius = shape.getRadius();
                    mShapePaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(pointFs[0].x, pointFs[0].y, radius, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
                case ROUNDRECT:
                    mShapePaint.setStyle(Paint.Style.STROKE);
                    float minRoundLeftTopX = Math.min(pointFs[0].x, pointFs[2].x);
                    float minRoundLeftTopY = Math.min(pointFs[0].y, pointFs[2].y);
                    float maxRoundRightBottomX = Math.max(pointFs[0].x, pointFs[2].x);
                    float maxRoundRightBottomY = Math.max(pointFs[0].y, pointFs[2].y);
                    canvas.drawRoundRect(minRoundLeftTopX, minRoundLeftTopY,
                            maxRoundRightBottomX, maxRoundRightBottomY,
                            radiusCornor, radiusCornor * 2, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
            }
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    private float getRealPosYOnBitmap(float y) {
        RectF displayRect = getDisplayRect();
        if (mVerticalHorizontal == VERTICAL_HORIZONTAL.HORIZONTAL) {
            return (y - displayRect.top) / mRealScaleRatio;
        } else {//mVerticalHorizontal == VERTICAL_HORIZONTAL.VERTICAL
            return (Math.abs(displayRect.top) + y) / mRealScaleRatio;
        }
    }

    private float getRealPosXOnBitmap(float x) {
        RectF displayRect = getDisplayRect();
        if (mVerticalHorizontal == VERTICAL_HORIZONTAL.HORIZONTAL) {
            return (Math.abs(displayRect.left) + x) / mRealScaleRatio;
        } else {//mVerticalHorizontal == VERTICAL_HORIZONTAL.VERTICAL
            return (x - displayRect.left) / mRealScaleRatio;
        }
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

    private Paint mDebugPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsEditing) {
            RectF displayRect = getDisplayRect();
            canvas.clipRect(displayRect);
            canvas.clipRect(displayRect, Region.Op.INTERSECT);//get two rects intersect parts
            canvas.save();
            canvas.setMatrix(getImageMatrix());
            canvas.translate(0, 60/*dp*/ * 2 / mRealScaleRatio);
            if (mCacheMutableBitmap != null && !mCacheMutableBitmap.isRecycled()) {
                canvas.drawBitmap(mCacheMutableBitmap, 0, 0, null);
            }
            updateNewMoveInOnDraw(canvas);
            LogUtils.i("rubber debug");
            canvas.restore();
        } else {
            if (mMutableBitmap != null) {
                canvas.drawBitmap(mMutableBitmap, getImageMatrix(), null);
            }
        }
        mDebugPaint.setTextSize(100);
        mDebugPaint.setColor(Color.RED);
        canvas.drawText("Debug:" + mEveryMoves.size(), 0, 100, mDebugPaint);
    }

    //    private List<EveryMove> mEveryMoves = new ArrayList<>();
    private CopyOnWriteArrayList<EveryMove> mEveryMoves = new CopyOnWriteArrayList<>();

    private class EveryMove {
        EDIT_MODE mEditMode;
        Path mPath;
        int mColor;
        float mStrokeWidth;
        int mAlpha;
        Shape mShape;
        String mText;
        float mTextSize;
    }

    private static boolean mTickerAddSpace;

    public void save() {
        //do save
        String fileName = "";
        String filePath = "";
        Bitmap savedBitmap = null;
        if (mOnSaveCompleteListener != null) {
            mOnSaveCompleteListener.onComplete("filePath", "fileName");
        }

        Resources r = getResources();
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mNotificationManager = (NotificationManager) getContext().getSystemService(Context
                .NOTIFICATION_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);

        // Get the various target sizes
        mNotificationIconSize = r.getDimensionPixelSize(android.R.dimen
                .notification_large_icon_height);
        int iconSize = mNotificationIconSize;
        // Scale has to account for both sides of the bg
        mBgPadding = (float) r.getDimensionPixelSize(R.dimen.global_screenshot_bg_padding);
        mBgPaddingScale = mBgPadding / mDisplayMetrics.widthPixels;

        // determine the optimal preview size
        int panelWidth = 0;
        try {
            panelWidth = r.getDimensionPixelSize(R.dimen.notification_panel_width);
        } catch (Resources.NotFoundException e) {
        }
        if (panelWidth <= 0) {
            // includes notification_panel_width==match_parent (-1)
            panelWidth = mDisplayMetrics.widthPixels;
        }
        mPreviewWidth = panelWidth;
        mPreviewHeight = r.getDimensionPixelSize(R.dimen.notification_max_height);

        Bitmap preview = Bitmap.createBitmap(mPreviewWidth, mPreviewHeight, mMainBitmap.getConfig
                ());
        Canvas c = new Canvas(preview);
        Paint paint = new Paint();
        ColorMatrix desat = new ColorMatrix();
        desat.setSaturation(0.25f);
        paint.setColorFilter(new ColorMatrixColorFilter(desat));
        Matrix matrix = new Matrix();
        matrix.postTranslate((mPreviewWidth - mMainBitmap.getWidth()) / 2,
                (mPreviewHeight - mMainBitmap.getHeight()) / 2);
        c.drawBitmap(savedBitmap, matrix, paint);
        c.drawColor(0x40FFFFFF);
        c.setBitmap(null);
        Bitmap croppedIcon = Bitmap.createScaledBitmap(preview, iconSize, iconSize, true);

        final long now = System.currentTimeMillis();
        mTickerAddSpace = !mTickerAddSpace;
        mNotificationBuilder = new Notification.Builder(getContext())
                .setTicker(r.getString(R.string.screenshot_saving_ticker)
                        + (mTickerAddSpace ? " " : ""))
                .setContentTitle(r.getString(R.string.screenshot_saving_title))
                .setContentText(r.getString(R.string.screenshot_saving_text))
                .setSmallIcon(R.mipmap.stat_notify_image)
                .setWhen(now)
                .setColor(r.getColor(com.android.internal.R.color
                        .system_notification_accent_color));
        mNotificationStyle = new Notification.BigPictureStyle()
                .bigPicture(preview);
        mNotificationBuilder.setStyle(mNotificationStyle);

        // For "public" situations we want to show all the same info but
        // omit the actual screenshot image.
        mPublicNotificationBuilder = new Notification.Builder(getContext())
                .setContentTitle(r.getString(R.string.screenshot_saving_title))
                .setContentText(r.getString(R.string.screenshot_saving_text))
                .setSmallIcon(R.mipmap.stat_notify_image)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setWhen(now)
                .setColor(r.getColor(
                        com.android.internal.R.color.system_notification_accent_color));

        //mNotificationBuilder.setPublicVersion(mPublicNotificationBuilder.build());

        Notification n = mNotificationBuilder.build();
        //n.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(SCREENSHOT_NOTIFICATION_ID, n);

        // On the tablet, the large icon makes the notification appear as if it is clickable (and
        // on small devices, the large icon is not shown) so defer showing the large icon until
        // we compose the final post-save notification below.
        mNotificationBuilder.setLargeIcon(croppedIcon);
        // But we still don't set it for the expanded view, allowing the smallIcon to show here.
        mNotificationStyle.bigLargeIcon((Bitmap) null);
    }

    private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s.png";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";
    protected static final int SCREENSHOT_NOTIFICATION_ID = 789;

    private void notification(String imageFilePath,
                              String imageFileName,
                              long imageTime,
                              long dateSeconds) {
        // Save the screenshot to the MediaStore
        ContentValues values = new ContentValues();
        ContentResolver resolver = getContext().getContentResolver();
        values.put(MediaStore.Images.ImageColumns.DATA, imageFilePath);
        values.put(MediaStore.Images.ImageColumns.TITLE, imageFileName);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.ImageColumns.WIDTH, mMainBitmap.getWidth());
        values.put(MediaStore.Images.ImageColumns.HEIGHT, mMainBitmap.getHeight());
        values.put(MediaStore.Images.ImageColumns.SIZE, new File(imageFilePath).length());
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        String subjectDate = DateFormat.getDateTimeInstance().format(new Date(imageTime));
        String subject = String.format(Locale.ENGLISH, SCREENSHOT_SHARE_SUBJECT_TEMPLATE,
                subjectDate);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        Intent chooserIntent = Intent.createChooser(sharingIntent, null);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        mNotificationBuilder.addAction(R.mipmap.ic_menu_share,
                getContext().getResources().getString(com.android.internal.R.string.share),
                PendingIntent.getActivity(getContext(), 0, chooserIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT));

        Intent deleteIntent = new Intent();
        deleteIntent.setClass(getContext(), DeleteScreenshot.class);
        deleteIntent.putExtra(DeleteScreenshot.SCREENSHOT_URI, uri.toString());

        mNotificationBuilder.addAction(R.mipmap.ic_menu_delete,
                getContext().getResources().getString(com.android.internal.R.string.delete),
                PendingIntent.getBroadcast(getContext(), 0, deleteIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT));
    }

    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //new a notification
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private OnSaveCompleteListener mOnSaveCompleteListener;

    public void setOnSaveCompleteListener(OnSaveCompleteListener onSaveCompleteListener) {
        this.mOnSaveCompleteListener = onSaveCompleteListener;
    }

    public interface OnSaveCompleteListener {
        void onComplete(String path, String fileName);
    }

}
