/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * EditorActivity.java
 *
 * Main editor activity
 *
 * Author huanghaiqi, Created at 2016-10-01
 *
 * Ver 1.0, 2016-10-01, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.halohoop.pictureeditor.controllers.ToolDetailsPagerAdapter;
import com.halohoop.pictureeditor.pieces.ColorPickerDetailFragment;
import com.halohoop.pictureeditor.pieces.IFragment;
import com.halohoop.pictureeditor.pieces.MosaicDetailFragment;
import com.halohoop.pictureeditor.pieces.PenRubberDetailFragment;
import com.halohoop.pictureeditor.pieces.ShapeDetailFragment;
import com.halohoop.pictureeditor.pieces.TextDetailFragment;
import com.halohoop.pictureeditor.utils.BitmapUtils;
import com.halohoop.pictureeditor.utils.LogUtils;
import com.halohoop.pictureeditor.widgets.ActionsChooseView;
import com.halohoop.pictureeditor.widgets.ColorPickerView;
import com.halohoop.pictureeditor.widgets.MarkableImageView;
import com.halohoop.pictureeditor.widgets.PenceilAndRubberView;
import com.halohoop.pictureeditor.widgets.ShapesChooseView;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity
        implements ActionsChooseView.OnSelectedListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener,
        ColorPickerView.ColorPickListener,
        ShapesChooseView.OnSelectedListener,PenceilAndRubberView.PenceilOrRubberModeCallBack {

    private ActionsChooseView mActionsChooseView;
    private ViewPager mNoScrollVp;
    private List<IFragment> mIFragments;
    private PenRubberDetailFragment mPenRubberDetailFragment;
    private ColorPickerDetailFragment mColorPickerDetailFragment;
    private ShapeDetailFragment mShapeDetailFragment;
    private TextDetailFragment mTextDetailFragment;
    private MosaicDetailFragment mMosaicDetailFragment;
    private MarkableImageView mMarkableImageView;
    private View mProgressContainer;
    private PenceilAndRubberView mPenceilAndRubberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mProgressContainer = findViewById(R.id.progress_container);
        mNoScrollVp = (ViewPager) findViewById(R.id.no_scroll_vp);
        mPenceilAndRubberView = (PenceilAndRubberView) findViewById(R.id.penceil_and_rubber_view);
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
        mMarkableImageView = (MarkableImageView) findViewById(R.id.markableview);
        mMarkableImageView.setMaximumScale(6);
        mNoScrollVp.setOffscreenPageLimit(7);//important
        mActionsChooseView = (ActionsChooseView) findViewById(R.id.actions_choose_view);
        mActionsChooseView.setOnSelectedListener(this);
        mIFragments = createFragments();
        mNoScrollVp.setAdapter(new ToolDetailsPagerAdapter(getSupportFragmentManager(),
                mIFragments));
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(250);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_pic1);
                Bitmap mosaicBitmap = BitmapUtils.mosaicIt(bitmap, 10);
                mMarkableImageView.setMosaicBitmap(mosaicBitmap);
                Message message = new Message();
                message.obj = bitmap;
                message.what = MOSAIC_BITMAP_DONE;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private static final int MOSAIC_BITMAP_DONE = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MOSAIC_BITMAP_DONE) {
                mProgressContainer.setVisibility(View.GONE);
                Bitmap bitmap = (Bitmap) msg.obj;
                mMarkableImageView.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mPenRubberDetailFragment.setOnSeekBarChangeListenerOnAlphaSeekBar(this);
        mPenRubberDetailFragment.setOnSeekBarChangeListenerOnThicknessSeekBar(this);
        mPenRubberDetailFragment.setOnClickListener(this);
        mTextDetailFragment.setOnClickListener(this);
        mShapeDetailFragment.setOnClickListener(this);
        mShapeDetailFragment.setListener(this);
        mMosaicDetailFragment.setOnSeekBarChangeListenerOnThicknessSeekBar(this);
        mColorPickerDetailFragment.setColorPickListener(this);
    }

    private final static int FRAGMENT_PEN_RUBBER = 0;
    private final static int FRAGMENT_TEXT = 1;
    private final static int FRAGMENT_SHAPE = 2;
    private final static int FRAGMENT_MOSAIC = 3;
    private final static int FRAGMENT_COLOR_PICKER = 4;

    private List<IFragment> createFragments() {
        List<IFragment> iFragments = new ArrayList<>();
        mPenRubberDetailFragment = new PenRubberDetailFragment();
        mTextDetailFragment = new TextDetailFragment();
        mShapeDetailFragment = new ShapeDetailFragment();
        mMosaicDetailFragment = new MosaicDetailFragment();
        mColorPickerDetailFragment = new ColorPickerDetailFragment();
        iFragments.add(mPenRubberDetailFragment);
        iFragments.add(mTextDetailFragment);
        iFragments.add(mShapeDetailFragment);
        iFragments.add(mMosaicDetailFragment);
        iFragments.add(mColorPickerDetailFragment);
        //iFragments.add more here
        return iFragments;
    }

    private int mCurrentIndex = 0;

    @Override
    public void onActionSelected(int index) {
        if (index == FRAGMENT_PEN_RUBBER) {
            mNoScrollVp.setCurrentItem(FRAGMENT_PEN_RUBBER, true);
        } else if (index == FRAGMENT_TEXT) {
            mNoScrollVp.setCurrentItem(FRAGMENT_TEXT, true);
        } else if (index == FRAGMENT_SHAPE) {
            mNoScrollVp.setCurrentItem(FRAGMENT_SHAPE, true);
        } else if (index == FRAGMENT_MOSAIC) {
            mNoScrollVp.setCurrentItem(FRAGMENT_MOSAIC, true);
        } else if (index == FRAGMENT_COLOR_PICKER) {
            mNoScrollVp.setCurrentItem(FRAGMENT_COLOR_PICKER, true);
        }
        mCurrentIndex = index;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.alpha_seek_bar) {
        } else if (seekBar.getId() == R.id.thickness_seek_bar) {
        } else if (seekBar.getId() == R.id.mosaic_thickness_seek_bar) {
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.color_show_view_in_pen_and_rubber
                || v.getId() == R.id.color_show_view_in_shapes_group
                || v.getId() == R.id.color_show_view_in_text_detail_container) {
            mNoScrollVp.setCurrentItem(FRAGMENT_COLOR_PICKER, true);
        } else if (v.getId() == R.id.iv_add_text) {
            //text add click
        }
    }

    @Override
    public void onColorPicked(int color) {
        mPenRubberDetailFragment.setColor(color);
        mShapeDetailFragment.setColor(color);
        mTextDetailFragment.setColor(color);
    }

    @Override
    public void onColorPickedDone() {
        mNoScrollVp.setCurrentItem(mCurrentIndex, true);
    }

    @Override
    public void onShapeSelected(int index) {

    }

    @Override
    public void onModeSelected(PenceilAndRubberView.MODE mode) {

    }
}
