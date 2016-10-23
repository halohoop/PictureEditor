/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * PenDrawView.java
 *
 * Pen draw view
 *
 * Author huanghaiqi, Created at 2016-10-23
 *
 * Ver 1.0, 2016-10-23, huanghaiqi, Create file.
 */

package com.halohoop.penrubberpart.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PenDrawView extends View {
    public static final int PEN_MODE = 0;
    public static final int RUBBER_MODE = 1;
    private Bitmap mBitmap;
    private int mMode = PEN_MODE;
    private List<PathBean> mPathBeans;
    private Bitmap mDrawBitmap;
    private Canvas mDrawCanvas;
    private Paint mPenPaint;
    private Paint mRubberPaint;
    private float mLastDownX;
    private float mLastDownY;

    public PenDrawView(Context context) {
        this(context, null);
    }

    public PenDrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PenDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPathBeans = new ArrayList<>();
        mPenPaint = createPaint(PEN_MODE);
        mRubberPaint = createPaint(RUBBER_MODE);
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    private Paint createPaint(int mode) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        if (mode == PEN_MODE) {
            paint.setStrokeWidth(50);
        } else if (mode == RUBBER_MODE) {
            paint.setStrokeWidth(50);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
        return paint;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        mDrawBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mDrawBitmap);

        BitmapShader bitmapShader
                = new BitmapShader(this.mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mRubberPaint.setShader(bitmapShader);
        invalidate();
    }

    class PathBean {
        Path path;
        int mode;
        boolean isAvailable = false;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PathBean pathBean = new PathBean();
                if (mMode == PEN_MODE) {
                    pathBean.mode = PEN_MODE;
                } else if (mMode == RUBBER_MODE) {
                    pathBean.mode = RUBBER_MODE;
                }
                pathBean.path = new Path();
                pathBean.path.reset();
                mLastDownX = event.getX();
                mLastDownY = event.getY();
                pathBean.path.moveTo(mLastDownX, mLastDownY);
                mPathBeans.add(pathBean);
                break;
            case MotionEvent.ACTION_MOVE:
                PathBean pathBean1 = mPathBeans.get(mPathBeans.size() - 1);
                pathBean1.isAvailable = true;
                float moveX = event.getX();
                float moveY = event.getY();
                pathBean1.path.quadTo(mLastDownX, mLastDownY,
                        (moveX+mLastDownX)/2, (moveY+mLastDownY)/2);
                if (pathBean1.mode == PEN_MODE) {
                    mDrawCanvas.drawPath(pathBean1.path, mPenPaint);
                } else if (pathBean1.mode == RUBBER_MODE) {
                    mDrawCanvas.drawPath(pathBean1.path, mRubberPaint);
                }
                updateDrawBitmap();
                mLastDownX = moveX;
                mLastDownY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                PathBean pathBean2 = mPathBeans.get(mPathBeans.size() - 1);
                if (!pathBean2.isAvailable) {
                    mPathBeans.remove(mPathBeans.size() - 1);
                }
                updateDrawBitmap();
                break;
        }
        invalidate();
        return true;
    }

    private void updateDrawBitmap() {
        for (int i = 0; i < mPathBeans.size(); i++) {
            PathBean pathBean = mPathBeans.get(i);
            if (pathBean.mode == PEN_MODE) {
                mDrawCanvas.drawPath(pathBean.path, mPenPaint);
            } else if (pathBean.mode == RUBBER_MODE) {
                mDrawCanvas.drawPath(pathBean.path, mRubberPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.drawBitmap(mDrawBitmap, 0, 0, null);
        }
    }

    public void undo() {

    }

    public void redo() {

    }
}
