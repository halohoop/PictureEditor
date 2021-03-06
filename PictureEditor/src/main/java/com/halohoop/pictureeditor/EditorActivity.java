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

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.halohoop.pictureeditor.controllers.ToolDetailsPagerAdapter;
import com.halohoop.pictureeditor.pieces.ColorPickerDetailFragment;
import com.halohoop.pictureeditor.pieces.IFragment;
import com.halohoop.pictureeditor.pieces.MosaicDetailFragment;
import com.halohoop.pictureeditor.pieces.PenRubberDetailFragment;
import com.halohoop.pictureeditor.pieces.RubberDetailFragment;
import com.halohoop.pictureeditor.pieces.ShapeDetailFragment;
import com.halohoop.pictureeditor.pieces.SimpleCustomDialog;
import com.halohoop.pictureeditor.pieces.TextDetailFragment;
import com.halohoop.pictureeditor.utils.LogUtils;
import com.halohoop.pictureeditor.widgets.ActionsChooseView;
import com.halohoop.pictureeditor.widgets.ColorPickerView;
import com.halohoop.pictureeditor.widgets.MarkableImageView;
import com.halohoop.pictureeditor.widgets.PenceilAndRubberView;
import com.halohoop.pictureeditor.widgets.ShapesChooseView;
import com.halohoop.pictureeditor.widgets.beans.Shape;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity
        implements ActionsChooseView.OnSelectedListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener,
        ColorPickerView.ColorPickListener,
        ShapesChooseView.OnSelectedListener,
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        DialogInterface.OnClickListener,
        MarkableImageView.OnSaveCompleteListener, MarkableImageView.OnButtonStateListener {

    private ActionsChooseView mActionsChooseView;
    private ViewPager mNoScrollVp;
    private List<IFragment> mIFragments;
    private PenRubberDetailFragment mPenRubberDetailFragment;
    private ColorPickerDetailFragment mColorPickerDetailFragment;
    private ShapeDetailFragment mShapeDetailFragment;
    private TextDetailFragment mTextDetailFragment;
    private MosaicDetailFragment mMosaicDetailFragment;
    private RubberDetailFragment mRubberDetailFragment;
    private MarkableImageView mMarkableImageView;
    private View mProgressContainer;
    private PenceilAndRubberView mPenceilAndRubberView;
    private ImageView mIvStepbackward;
    private ImageView mIvStepforward;
    private ImageView mIvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mProgressContainer = findViewById(R.id.progress_container);
        findViewById(R.id.iv_cancel).setOnClickListener(this);
        mIvSave = (ImageView) findViewById(R.id.iv_save);
        mIvStepforward = (ImageView) findViewById(R.id.iv_stepforward);
        mIvStepbackward = (ImageView) findViewById(R.id.iv_stepbackward);
        mIvStepforward.setOnClickListener(this);
        mIvStepbackward.setOnClickListener(this);
        mIvSave.setOnClickListener(this);
        mIvStepforward.setEnabled(false);
        mIvStepbackward.setEnabled(false);
        mIvSave.setEnabled(false);
        mNoScrollVp = (ViewPager) findViewById(R.id.no_scroll_vp);
        mPenceilAndRubberView = (PenceilAndRubberView) findViewById(R.id.penceil_and_rubber_view);
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
        mMarkableImageView = (MarkableImageView) findViewById(R.id.markableview);
        mMarkableImageView.setMaximumScale(6);
        mMarkableImageView.setOnSaveCompleteListener(this);
        mMarkableImageView.setUndoButtonResId(R.id.iv_stepbackward);
        mMarkableImageView.setRedoButtonResId(R.id.iv_stepforward);
        mMarkableImageView.setSaveButtonResId(R.id.iv_save);
        mMarkableImageView.setOnButtonStateListener(this);
        mNoScrollVp.setOffscreenPageLimit(7);//important
        mActionsChooseView = (ActionsChooseView) findViewById(R.id.actions_choose_view);
        mActionsChooseView.setOnSelectedListener(this);
        mActionsChooseView.setAnimationEndMark(mPenceilAndRubberView);
        mIFragments = createFragments();
        mNoScrollVp.setAdapter(new ToolDetailsPagerAdapter(getSupportFragmentManager(),
                mIFragments));
        mNoScrollVp.setCurrentItem(ActionsChooseView.FRAGMENT_PEN, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(250);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_pic4);
//                Bitmap mosaicBitmap = BitmapUtils.mosaicIt(bitmap, 10);
                Bitmap mosaicRect = BitmapFactory.decodeResource(getResources(), R.mipmap
                        .mosaic_rect);
                mMarkableImageView.setMosaicBitmap(mosaicRect);
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
                mProgressContainer.setVisibility(View.INVISIBLE);
                Bitmap bitmap = (Bitmap) msg.obj;
                mMarkableImageView.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mPenRubberDetailFragment.setOnSeekBarChangeListenerOnAlphaSeekBar(this);
            mPenRubberDetailFragment.setOnSeekBarChangeListenerOnThicknessSeekBar(this);
            mPenRubberDetailFragment.setOnClickListener(this);
            mTextDetailFragment.setOnClickListener(this);
            mShapeDetailFragment.setOnClickListener(this);
            mShapeDetailFragment.setListener(this);
            mMosaicDetailFragment.setOnSeekBarChangeListenerOnThicknessSeekBar(this);
            mRubberDetailFragment.setOnSeekBarChangeListenerOnThicknessSeekBar(this);
            mColorPickerDetailFragment.setColorPickListener(this);
        }
    }

    private List<IFragment> createFragments() {
        List<IFragment> iFragments = new ArrayList<>();
        mPenRubberDetailFragment = new PenRubberDetailFragment();
        mTextDetailFragment = new TextDetailFragment();
        mShapeDetailFragment = new ShapeDetailFragment();
        mMosaicDetailFragment = new MosaicDetailFragment();
        mRubberDetailFragment = new RubberDetailFragment();
        mColorPickerDetailFragment = new ColorPickerDetailFragment();
        iFragments.add(mRubberDetailFragment);
        iFragments.add(mPenRubberDetailFragment);
        iFragments.add(mTextDetailFragment);
        iFragments.add(mShapeDetailFragment);
        iFragments.add(mMosaicDetailFragment);
        iFragments.add(mColorPickerDetailFragment);
        //iFragments.add more here
        return iFragments;
    }

    private int mCurrentIndex = ActionsChooseView.FRAGMENT_PEN;

    @Override
    public void onActionSelected(int index) {
        //show or hide pen and rubber
        if (index != ActionsChooseView.FRAGMENT_PEN) {
            mPenceilAndRubberView.setVisibility(View.INVISIBLE);//此处不能使用gone，导致焦点变化的bug
        } else {
            mPenceilAndRubberView.setVisibility(View.VISIBLE);
        }
        //four mode to show
        if (index == ActionsChooseView.FRAGMENT_PEN) {
            PenceilAndRubberView.MODE mode = mPenceilAndRubberView.getMode();
            if (mode == PenceilAndRubberView.MODE.PENCEILON) {
                mMarkableImageView.setEditMode(MarkableImageView.EDIT_MODE.PEN);
                mNoScrollVp.setCurrentItem(ActionsChooseView.FRAGMENT_PEN, false);
            } else if (mode == PenceilAndRubberView.MODE.RUBBERON) {
                mMarkableImageView.setEditMode(MarkableImageView.EDIT_MODE.RUBBER);
                mNoScrollVp.setCurrentItem(ActionsChooseView.FRAGMENT_RUBBER, false);
            }
        } else {
            mNoScrollVp.setCurrentItem(index, false);
        }
        if (index == ActionsChooseView.FRAGMENT_MOSAIC) {
            mMarkableImageView.setEditMode(MarkableImageView.EDIT_MODE.MOSAIC);
        }
        if (index == ActionsChooseView.FRAGMENT_SHAPE) {
            mMarkableImageView.setEditMode(MarkableImageView.EDIT_MODE.SHAPE);
        }
        mCurrentIndex = index;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress == 0) {
            progress = 1;
        }
        if (seekBar.getId() == R.id.alpha_seek_bar) {
            mMarkableImageView.updateDrawPaintAlpha(progress);
            LogUtils.i("" + progress);
        } else if (seekBar.getId() == R.id.thickness_seek_bar) {
            mMarkableImageView.updateDrawPaintStrokeWidth(progress);
            LogUtils.i("" + progress);
        } else if (seekBar.getId() == R.id.mosaic_thickness_seek_bar) {
            mMarkableImageView.updateMosaicPaintStrokeWidth(progress);
            LogUtils.i("" + progress);
        } else if (seekBar.getId() == R.id.rubber_thickness_seek_bar) {
            mMarkableImageView.updateRubberPaintStrokeWidth(progress);
            LogUtils.i("" + progress);
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
            mNoScrollVp.setCurrentItem(ActionsChooseView.FRAGMENT_COLOR_PICKER, false);
        } else if (v.getId() == R.id.iv_add_text) {
        } else if (v.getId() == R.id.iv_cancel) {
            if (mMarkableImageView.isEdited()) {
                alertDialog();
            } else {
                finish();
            }
        } else if (v.getId() == R.id.iv_save) {
            mMarkableImageView.save();
            mIsSaving = true;
        } else if (v.getId() == R.id.iv_stepbackward) {
            mMarkableImageView.undo();
        } else if (v.getId() == R.id.iv_stepforward) {
            mMarkableImageView.redo();
        }
    }

    private boolean mIsSaving = false;

    @Override
    public void onBackPressed() {
        if (mIsSaving) {
            return;
        }
        if (mMarkableImageView.isEdited()) {
            alertDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onColorPicked(int color) {
        mPenRubberDetailFragment.setColor(color);
        mShapeDetailFragment.setColor(color);
        mTextDetailFragment.setColor(color);
        //set main draw view color
        mMarkableImageView.setColor(color);
    }

    @Override
    public void onColorPickedDone() {
        mNoScrollVp.setCurrentItem(mCurrentIndex, false);
    }

    @Override
    public void onShapeSelected(int index) {
        if (index == 0) {//line
            mMarkableImageView.setShapeType(Shape.SHAPE_TYPE.LINE);
        } else if (index == 1) {//arrow
            mMarkableImageView.setShapeType(Shape.SHAPE_TYPE.ARROW);
        } else if (index == 2) {//rect
            mMarkableImageView.setShapeType(Shape.SHAPE_TYPE.RECT);
        } else if (index == 3) {//circle
            mMarkableImageView.setShapeType(Shape.SHAPE_TYPE.CIRCLE);
        } else if (index == 4) {//roundrect
            mMarkableImageView.setShapeType(Shape.SHAPE_TYPE.ROUNDRECT);
        }
    }

    @Override
    public void onModeSelected(PenceilAndRubberView.MODE mode) {
        if (mode == PenceilAndRubberView.MODE.PENCEILON) {
            mNoScrollVp.setCurrentItem(ActionsChooseView.FRAGMENT_PEN, false);
            mCurrentIndex = ActionsChooseView.FRAGMENT_PEN;
            mMarkableImageView.setEditMode(MarkableImageView.EDIT_MODE.PEN);
        } else if (mode == PenceilAndRubberView.MODE.RUBBERON) {
            mNoScrollVp.setCurrentItem(ActionsChooseView.FRAGMENT_RUBBER, false);
            mCurrentIndex = ActionsChooseView.FRAGMENT_RUBBER;
            mMarkableImageView.setEditMode(MarkableImageView.EDIT_MODE.RUBBER);
        }
    }

    private void alertDialog() {
        SimpleCustomDialog.Builder dialog = new SimpleCustomDialog.Builder(this);
        dialog.setNegativeButtonClickListener(this)
                .setPositiveButtonClickListener(this)
                .create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            finish();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            //eat it
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMarkableImageView.destroyEveryThing();
    }

    @Override
    public void onSaveStart(String path, String fileName) {
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveComplete(Boolean isSaveSucc, String path, String fileName) {
        if (isSaveSucc) {
            //Notification
            Toast.makeText(this, "fileName:" + fileName + "--" + isSaveSucc, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "fileName:" + fileName + "--" + isSaveSucc, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveFail(String path, String fileName) {
        Toast.makeText(this, "fileName:" + fileName + "--fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateRedoUndoState(int id, boolean enable) {
        if (id == R.id.iv_stepforward) {//redo
            if (enable) {
                mIvStepforward.setEnabled(true);
                mIvStepforward.setImageDrawable(getResources().getDrawable(R.drawable
                        .stepforward));
            } else {
                mIvStepforward.setEnabled(false);
                mIvStepforward.setImageDrawable(getResources().getDrawable(R.drawable
                        .stepforward_off));
            }
        } else if (id == R.id.iv_stepbackward) {//undo
            if (enable) {
                mIvStepbackward.setEnabled(true);
                mIvStepbackward.setImageDrawable(getResources().getDrawable(R.drawable.stepback));
            } else {
                mIvStepbackward.setEnabled(false);
                mIvStepbackward.setImageDrawable(getResources().getDrawable(R.drawable
                        .stepback_off));
            }
        }
    }

    @Override
    public void onUpdateSavetate(int id, boolean enable) {
        if (id == R.id.iv_save) {//save
            if (enable) {
                mIvSave.setEnabled(true);
                mIvSave.setImageDrawable(getResources().getDrawable(R.drawable.save));
            } else {
                mIvSave.setEnabled(false);
                mIvSave.setImageDrawable(getResources().getDrawable(R.drawable
                        .save_off));
            }
        }
    }
}
