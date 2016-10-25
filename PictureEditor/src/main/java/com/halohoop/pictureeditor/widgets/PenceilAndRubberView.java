/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * PenceilAndRubberView.java
 *
 * 铅笔和橡皮擦自定义view
 *
 * Author huanghaiqi, Created at 2016-09-30
 *
 * Ver 1.0, 2016-09-30, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;

import com.halohoop.pictureeditor.R;

public class PenceilAndRubberView extends View implements AnimationEndMark {

    private Drawable mDrawableRubberOn;
    private Drawable mDrawablePenceilOn;
    private Drawable mDrawableRubberOff;
    private Drawable mDrawablePenceilOff;
    private MODE mMode = MODE.PENCEILON;
    private int mWidth;
    private int mHeight;
    private int mDeltaX;
    private int mHalfHeight;
    private int mTwoPicturePadding;
    private Rect mTopRect;
    private Rect mBottomRect;
    private int mPositionOffset;
    private int mAnimationOffset;
    private int mScreenWidth;
    private int ANIMATION_DURATION = 200;

    public PenceilAndRubberView(Context context) {
        this(context, null);
    }

    public PenceilAndRubberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PenceilAndRubberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.PenceilAndRubberView);
        mDrawablePenceilOn = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResPenceilOn);
        mDrawableRubberOn = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResRubberOn);
        mDrawablePenceilOff = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResPenceilOff);
        mDrawableRubberOff = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResRubberOff);
        mDeltaX = Math.abs(mDrawablePenceilOn.getIntrinsicWidth()
                - mDrawableRubberOff.getIntrinsicWidth());
        mMode = MODE.PENCEILON;
        mTwoPicturePadding = 0;
        mPositionOffset = 40;
        mAnimationOffset = 30;
        Point screenSize = getScreenSize(context);
        mScreenWidth = screenSize.x;
    }

    @SuppressLint("NewApi")//getSize(方法)需要 api13才能使用
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        //Build.VERSION_CODES.HONEYCOMB_MR2 → 13
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(out);
        } else {
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        return out;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int intrinsicWidthPenceilOn = mDrawablePenceilOn.getIntrinsicWidth();
        int intrinsicHeightPenceilOn = mDrawablePenceilOn.getIntrinsicHeight();
        int intrinsicWidthPenceilOff = mDrawablePenceilOff.getIntrinsicWidth();
        int intrinsicHeightPenceilOff = mDrawablePenceilOff.getIntrinsicHeight();
        int intrinsicWidthRubberOn = mDrawablePenceilOn.getIntrinsicWidth();
        int intrinsicHeightRubberOn = mDrawablePenceilOn.getIntrinsicHeight();
        int intrinsicWidthRubberOff = mDrawableRubberOff.getIntrinsicWidth();
        int intrinsicHeightRubberOff = mDrawableRubberOff.getIntrinsicHeight();
        mWidth = getTheMaxOne(intrinsicWidthPenceilOn, intrinsicWidthPenceilOff,
                intrinsicWidthRubberOn, intrinsicWidthRubberOff);
        mHeight = getTheMaxOne(intrinsicHeightPenceilOn, intrinsicHeightPenceilOff,
                intrinsicHeightRubberOn, intrinsicHeightRubberOff) * 2 + mTwoPicturePadding;
        mHalfHeight = mHeight / 2;
        setMeasuredDimension(mWidth, mHeight);
        initRects();
    }

    private void initRects() {
        if (mMode == MODE.PENCEILON) {
            mTopRect = new Rect(mPositionOffset, 0, mWidth + mPositionOffset,
                    mHalfHeight - mTwoPicturePadding / 2);
            mBottomRect = new Rect(mPositionOffset + mAnimationOffset, mHalfHeight + mTwoPicturePadding / 2,
                    mWidth + mPositionOffset + mAnimationOffset, mHeight);
        } else if (mMode == MODE.RUBBERON) {
            mTopRect = new Rect(mPositionOffset + mAnimationOffset, 0,
                    mWidth + mPositionOffset + mAnimationOffset,
                    mHalfHeight - mTwoPicturePadding / 2);
            mBottomRect = new Rect(mPositionOffset, mHalfHeight + mTwoPicturePadding / 2,
                    mWidth + mPositionOffset, mHeight);
        }
    }

    public int getTheMaxOne(int... widths) {
        int max = widths[0];
        for (int i = 1; i < widths.length; i++) {
            max = Math.max(max, widths[i]);
        }
        return max;
    }

    @Override
    public boolean isAnimationEnd() {
        return mIsAnimationEnd;
    }

    public enum MODE {
        PENCEILON, RUBBERON
    }

    public MODE getMode() {
        return this.mMode;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mCanvasShouldTranX, 0);
        if (mMode == MODE.PENCEILON) {
            mDrawablePenceilOn.setBounds(mTopRect);
            mDrawableRubberOff.setBounds(mBottomRect);
            mDrawablePenceilOn.draw(canvas);
            mDrawableRubberOff.draw(canvas);
        } else if (mMode == MODE.RUBBERON) {
            mDrawablePenceilOff.setBounds(mTopRect);
            mDrawableRubberOn.setBounds(mBottomRect);
            mDrawablePenceilOff.draw(canvas);
            mDrawableRubberOn.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (mTopRect.contains(x, y)) {
                    //start animation
                    if (mMode != MODE.PENCEILON && mIsAnimationEnd) {
                        changeMode(MODE.PENCEILON);
                    }
                } else if (mBottomRect.contains(x, y)) {
                    //start animation
                    if (mMode != MODE.RUBBERON && mIsAnimationEnd) {
                        changeMode(MODE.RUBBERON);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public interface PenceilOrRubberModeCallBack {
        void onModeSelected(MODE mode);
    }

    private PenceilOrRubberModeCallBack mPenceilOrRubberModeCallBack;

    public void setPenceilOrRubberModeCallBack(
            PenceilOrRubberModeCallBack penceilOrRubberModeCallBack) {
        this.mPenceilOrRubberModeCallBack = penceilOrRubberModeCallBack;
    }

    private boolean mIsAnimationEnd = true;

    private void changeMode(MODE modeToChangeTo) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,
                mAnimationOffset)
                .setDuration(ANIMATION_DURATION);
        //mark current value
        final int currentTopLeft = mTopRect.left;
        final int currentTopRight = mTopRect.right;
        final int currentBottomLeft = mBottomRect.left;
        final int currentBottomRight = mBottomRect.right;
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        if (modeToChangeTo == MODE.PENCEILON) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    mTopRect.set(currentTopLeft - animatedValue, mTopRect.top,
                            currentTopRight - animatedValue, mTopRect.bottom);
                    mBottomRect.set(currentBottomLeft + animatedValue, mBottomRect.top,
                            currentBottomRight + animatedValue, mBottomRect.bottom);
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsAnimationEnd = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimationEnd = true;
                    mMode = MODE.PENCEILON;
                    invalidate();//final state update
                    if (mPenceilOrRubberModeCallBack != null) {
                        mPenceilOrRubberModeCallBack.onModeSelected(mMode);
                    }
                }
            });
        } else if (modeToChangeTo == MODE.RUBBERON) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    mTopRect.set(currentTopLeft + animatedValue, mTopRect.top,
                            currentTopRight + animatedValue, mTopRect.bottom);
                    mBottomRect.set(currentBottomLeft - animatedValue, mBottomRect.top,
                            currentBottomRight - animatedValue, mBottomRect.bottom);
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsAnimationEnd = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimationEnd = true;
                    mMode = MODE.RUBBERON;
                    invalidate();//final state update
                    if (mPenceilOrRubberModeCallBack != null) {
                        mPenceilOrRubberModeCallBack.onModeSelected(mMode);
                    }
                }
            });
        }
        valueAnimator.start();
    }


    @Override
    public void setVisibility(final int visibility) {
        if (visibility != View.VISIBLE && getVisibility() == View.VISIBLE) {
            post(new Runnable() {
                @Override
                public void run() {
                    //to do animator to hide
                    doAnimatorToHide(visibility);//maybe invisible or gone
                }

            });
        } else if (visibility == View.VISIBLE && getVisibility() != View.VISIBLE) {
            post(new Runnable() {
                @Override
                public void run() {
                    //to do animator to show
                    doAnimatorToShow();//just visible
                }
            });
        }
    }


    private int mCanvasShouldTranX = 0;

    private void doAnimatorToShow() {
        AnimatorSet finalAs = new AnimatorSet();
        AnimatorSet as = new AnimatorSet();

        ValueAnimator canvasTransXAnimator = ValueAnimator.ofInt(mWidth, 0).setDuration(ANIMATION_DURATION);
        canvasTransXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mCanvasShouldTranX = animatedValue;
            }
        });

        int leftAndRightTopDistance = Math.abs(mTopRect.right - mTopRect.left);
        final int lastTopLeft = mTopRect.left;
        final int lastTopRight = mTopRect.right;
        ValueAnimator valueAnimatorTop
                = ValueAnimator.ofInt(0, leftAndRightTopDistance).setDuration(ANIMATION_DURATION);
        valueAnimatorTop.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimatorTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mTopRect.left = lastTopLeft - animatedValue;
                mTopRect.right = lastTopRight - animatedValue;
                invalidate();
            }
        });

        int leftAndRightBottomDistance = Math.abs(mBottomRect.right - mBottomRect.left);
        final int lastBottomLeft = mBottomRect.left;
        final int lastBottomRight = mBottomRect.right;
        ValueAnimator valueAnimatorBottom
                = ValueAnimator.ofInt(0, leftAndRightBottomDistance).setDuration(ANIMATION_DURATION);
        valueAnimatorBottom.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimatorBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mBottomRect.left = lastBottomLeft - animatedValue;
                mBottomRect.right = lastBottomRight - animatedValue;
                mTopRect.left = lastTopLeft;
                mTopRect.right = lastTopRight;
                invalidate();
            }
        });
        as.playSequentially(valueAnimatorBottom, valueAnimatorTop);
        finalAs.playTogether(canvasTransXAnimator, as);
        finalAs.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimationEnd = false;
                PenceilAndRubberView.super.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimationEnd = true;
                invalidate();
            }
        });
        finalAs.start();
    }

    private void doAnimatorToHide(final int visibility) {
        AnimatorSet as = new AnimatorSet();
        int leftAndRightTopDistance = Math.abs(mTopRect.right - mTopRect.left);
        final int lastTopLeft = mTopRect.left;
        final int lastTopRight = mTopRect.right;
        ValueAnimator valueAnimatorTop
                = ValueAnimator.ofInt(0, leftAndRightTopDistance).setDuration(ANIMATION_DURATION);
        valueAnimatorTop.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimatorTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mTopRect.left = lastTopLeft + animatedValue;
                mTopRect.right = lastTopRight + animatedValue;
                invalidate();
            }
        });

        int leftAndRightBottomDistance = Math.abs(mBottomRect.right - mBottomRect.left);
        final int lastBottomLeft = mBottomRect.left;
        final int lastBottomRight = mBottomRect.right;
        ValueAnimator valueAnimatorBottom
                = ValueAnimator.ofInt(0, leftAndRightBottomDistance).setDuration(ANIMATION_DURATION);
        valueAnimatorBottom.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimatorBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mBottomRect.left = lastBottomLeft + animatedValue;
                mBottomRect.right = lastBottomRight + animatedValue;
                invalidate();
            }
        });
        as.playSequentially(valueAnimatorTop, valueAnimatorBottom);
        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimationEnd = true;
                PenceilAndRubberView.super.setVisibility(visibility);
                invalidate();
            }
        });
        as.start();
    }
}