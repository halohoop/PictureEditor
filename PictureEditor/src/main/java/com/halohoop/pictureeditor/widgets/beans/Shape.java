/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Shape.java
 *
 * Description
 *
 * Author huanghaiqi
 *
 * Ver 1.0, 2016-09-09, huanghaiqi, Create file
 */

package com.halohoop.pictureeditor.widgets.beans;

import android.graphics.PointF;

public class Shape {

    protected float mRadius = 0;//just for circle
    protected PointF[] mPoints;
    protected SHAPE_TYPE mShapeType = SHAPE_TYPE.LINE;

    public Shape(SHAPE_TYPE shapeType) {
        switch (shapeType) {
            case LINE:
                this.mPoints = new PointF[2];
                this.mShapeType = SHAPE_TYPE.LINE;
                break;
            case ARROW:
                this.mPoints = new PointF[5];
                this.mShapeType = SHAPE_TYPE.ARROW;
                break;
            case RECT:
                this.mPoints = new PointF[4];
                this.mShapeType = SHAPE_TYPE.RECT;
                break;
            case CIRCLE:
                this.mPoints = new PointF[2];
                this.mShapeType = SHAPE_TYPE.CIRCLE;
                break;
            case ROUNDRECT:
                this.mPoints = new PointF[4];
                this.mShapeType = SHAPE_TYPE.ROUNDRECT;
                break;
            default:
                throw new IllegalArgumentException("please choose a right enum value from the " +
                        "enum SHAPE_TYPE");
        }
        for (int i = 0; i < mPoints.length; i++) {
            mPoints[i] = new PointF();
        }
    }

    public enum SHAPE_TYPE {
        LINE, ARROW, RECT, CIRCLE, ROUNDRECT
    }

    public SHAPE_TYPE getShapeType() {
        return mShapeType;
    }

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(PointF[] mPoints) {
        this.mPoints = mPoints;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }
}