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
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.halohoop.pictureeditor.utils.LogUtils;
import com.halohoop.pictureeditor.widgets.beans.Shape;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class MarkableImageView extends PhotoView {
    private Bitmap mMainBitmap;
    private Bitmap mMosaicBitmap;
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
    private Bitmap mMutableBitmap2;
    private Canvas mDrawCanvas;
    private float mRealScaleRatio = 1;
    private float mRealDownPosX;
    private float mRealDownPosY;
    private int mColor;
    private int mAlpha;
    private PointF mMidPoint;
    //圆角矩形角半径
    private float radiusCornor = 5.0f;

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

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.mMainBitmap = bm;
        mMutableBitmap1 = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config
                .ARGB_8888);
        mMutableBitmap2 = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config
                .ARGB_8888);
        mMutableBitmap = mMutableBitmap1;
        mDrawCanvas = new Canvas(mMutableBitmap);
        BitmapShader rubberShader = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode
                .REPEAT);
        mRubberPaint.setShader(rubberShader);
        getRealScaleRatio();
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
            if (!mMutableBitmap1.isRecycled()) {
                mMutableBitmap1.recycle();
                mMutableBitmap1 = null;
            }
            if (!mMutableBitmap2.isRecycled()) {
                mMutableBitmap2.recycle();
                mMutableBitmap2 = null;
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
            mEveryMoves.add(everyMove);
        } else if (mEditMode == EDIT_MODE.RUBBER) {
            if (mEveryMoves.size() > 0) {
                EveryMove everyMove = new EveryMove();
                everyMove.mEditMode = EDIT_MODE.RUBBER;
                everyMove.mStrokeWidth = mRubberPaint.getStrokeWidth();
                everyMove.mPath = new Path();
                everyMove.mPath.reset();
                everyMove.mPath.moveTo(realDownPosX, realDownPosY);
                mEveryMoves.add(everyMove);
            }
        } else if (mEditMode == EDIT_MODE.MOSAIC) {
            EveryMove everyMove = new EveryMove();
            everyMove.mEditMode = EDIT_MODE.MOSAIC;
            everyMove.mStrokeWidth = mMosaicPaint.getStrokeWidth();
            everyMove.mPath = new Path();
            everyMove.mPath.reset();
            everyMove.mPath.moveTo(realDownPosX, realDownPosY);
            mEveryMoves.add(everyMove);
        } else if (mEditMode == EDIT_MODE.SHAPE) {
            EveryMove everyMove = new EveryMove();
            everyMove.mEditMode = EDIT_MODE.SHAPE;
            everyMove.mColor = mColor;
            everyMove.mShape = createNewShape(realDownPosX, realDownPosY);
            mEveryMoves.add(everyMove);
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
            everyMove.mPath.quadTo(mRealDownPosX, mRealDownPosY,
                    (mRealDownPosX + realMovePosX) / 2, (mRealDownPosY + realMovePosY) / 2);
            mRealDownPosX = realMovePosX;
            mRealDownPosY = realMovePosY;
        } else if (everyMove.mEditMode == EDIT_MODE.RUBBER) {
            everyMove.mPath.quadTo(mRealDownPosX, mRealDownPosY,
                    (mRealDownPosX + realMovePosX) / 2, (mRealDownPosY + realMovePosY) / 2);
            mRealDownPosX = realMovePosX;
            mRealDownPosY = realMovePosY;
        } else if (everyMove.mEditMode == EDIT_MODE.MOSAIC) {
            everyMove.mPath.quadTo(mRealDownPosX, mRealDownPosY,
                    (mRealDownPosX + realMovePosX) / 2, (mRealDownPosY + realMovePosY) / 2);
            mRealDownPosX = realMovePosX;
            mRealDownPosY = realMovePosY;
        } else if (everyMove.mEditMode == EDIT_MODE.SHAPE) {
            updateShapeState(realMovePosX, realMovePosY);
        }
        invalidate();
    }

    private void updateShapeState(float realMovePosX, float realMovePosY) {
        Shape shape = mEveryMoves.get(mEveryMoves.size() - 1).mShape;
        switch (shape.getShapeType()) {
            case LINE:
                PointF[] circlePointFs = shape.getPoints();
                circlePointFs[1].x = realMovePosX;
                circlePointFs[1].y = realMovePosY;
                break;
//            case ARROW:
//                // angle of the arrow
//                updateAngle();
//                updateTrianglePointFs();
//                break;
//            case RECT:
//                updateRectanglePointFs();
//                break;
//            case CIRCLE:
//                updateCirclePointFsAndRadius();
//                break;
//            case ROUNDRECT:
//                updateRectanglePointFs();
//                break;
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
            updateNewMoveInOnDraw(canvas);
            LogUtils.i("rubber debug");
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
        Shape mShape;
        String mText;
        float mTextSize;
    }

}
