/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * BitmapUtils.java
 *
 * Utils that deal with bitmaps;
 *
 * Author huanghaiqi, Created at 2016-10-18
 *
 * Ver 1.0, 2016-10-18, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

public class BitmapUtils {
    /**
     * 将整张图片马赛克
     *
     * @param old
     * @param blockSize
     * @return
     */
    public static Bitmap mosaicIt(Bitmap old, int blockSize) {
        if (old == null || old.isRecycled()) {
            return null;
        }
        int bw = old.getWidth();
        int bh = old.getHeight();
        int[] bitmapPxs = new int[bw * bh];
        // get bitmap pxs
        old.getPixels(bitmapPxs, 0, bw, 0, 0, bw, bh);
        Rect targetRect = new Rect();
        targetRect.set(0, 0, bw, bh);
        //start mosaic it
        int rowCount = (int) Math.ceil((float) bh / blockSize);
        int columnCount = (int) Math.ceil((float) bw / blockSize);
        int maxX = bw;
        int maxY = bh;
        for (int r = 0; r < rowCount; r++) { // row loop
            for (int c = 0; c < columnCount; c++) {// column loop
                int startX = targetRect.left + c * blockSize + 1;
                int startY = targetRect.top + r * blockSize + 1;
                dimBlock(bitmapPxs, startX, startY, blockSize, maxX, maxY);
            }
        }
        return Bitmap.createBitmap(bitmapPxs, bw, bh, Bitmap.Config.ARGB_8888);
    }

    /**
     * 从块内取样，并放大(用取样像素填充整个块)，从而达到马赛克的模糊效果
     */
    private static void dimBlock(int[] pxs, int startX, int startY,
                                 int blockSize, int maxX, int maxY) {
        int stopX = startX + blockSize - 1;
        int stopY = startY + blockSize - 1;
        if (stopX > maxX) {
            stopX = maxX;
        }
        if (stopY > maxY) {
            stopY = maxY;
        }
        //get middle point pixel of the block
        int sampleColorX = startX + blockSize / 2;
        int sampleColorY = startY + blockSize / 2;

        if (sampleColorX > maxX) {
            sampleColorX = maxX;
        }
        if (sampleColorY > maxY) {
            sampleColorY = maxY;
        }

        int colorLinePosition = (sampleColorY - 1) * maxX;
        // 像素从1开始，但是数组从0开始
        int sampleColor = pxs[colorLinePosition + sampleColorX - 1];
        //block filled with sample color
        for (int y = startY; y <= stopY; y++) {
            int p = (y - 1) * maxX;
            for (int x = startX; x <= stopX; x++) {
                // 像素从1开始，但是数组从0开始
                pxs[p + x - 1] = sampleColor;
            }
        }
    }

    /**
     * 获取某一点的颜色
     * @param bitmap
     * @param x
     * @param y
     * @return
     */
    public static int getCertainPointColor(Bitmap bitmap, int x, int y) {
        if (bitmap != null || bitmap.isRecycled()) {
            return Color.GRAY;
        }
        int pixel = bitmap.getPixel(x, y);
        //获取颜色
        int alphaValue = Color.alpha(pixel);
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);
        return Color.argb(alphaValue, redValue, greenValue, blueValue);
    }
}
