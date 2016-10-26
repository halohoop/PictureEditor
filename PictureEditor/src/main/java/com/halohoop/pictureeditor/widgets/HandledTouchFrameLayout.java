/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * HandledTouchFrameLayout.java
 *
 * 截获触摸事件的framelyout
 *
 * Author huanghaiqi, Created at 2016-10-26
 *
 * Ver 1.0, 2016-10-26, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class HandledTouchFrameLayout extends FrameLayout{
    public HandledTouchFrameLayout(Context context) {
        super(context);
    }

    public HandledTouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HandledTouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
