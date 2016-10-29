/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * LogUtils.java
 *
 * LogUtils for debug log
 *
 * Author huanghaiqi, Created at 2016-09-20
 *
 * Ver 1.0, 2016-09-20, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "huanghaiqi";
    private static final boolean DEBUG = true;

    public static void i(String s) {
        if (DEBUG) {
            Log.i(TAG, TAG + "--" + s);
        }
    }

    public static void e(String s) {
        if (DEBUG) {
            Log.e(TAG, TAG + "--" + s);
        }
    }
}
