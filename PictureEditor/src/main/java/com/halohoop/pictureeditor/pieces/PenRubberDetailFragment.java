/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * PenRubberDetailFragment.java
 *
 * Pen and rubber mode tools detail.
 *
 * Author huanghaiqi, Created at 2016-10-23
 *
 * Ver 1.0, 2016-10-23, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.pieces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.halohoop.pictureeditor.R;
import com.halohoop.pictureeditor.widgets.AlphaSeekBar;
import com.halohoop.pictureeditor.widgets.ColorShowView;
import com.halohoop.pictureeditor.widgets.ThicknessSeekBar;

public class PenRubberDetailFragment extends Fragment implements IFragment {

    private ColorShowView mColorShowView;
    private AlphaSeekBar mAlphaSeekBar;
    private ThicknessSeekBar mThicknessSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pen_rubber_tools_detail_layout, null);
        mColorShowView = (ColorShowView) view.findViewById(R.id.color_show_view_in_pen_and_rubber);
        mThicknessSeekBar = (ThicknessSeekBar) view.findViewById(R.id.thickness_seek_bar);
        mAlphaSeekBar = (AlphaSeekBar) view.findViewById(R.id.alpha_seek_bar);
        return view;
    }

    public void setOnSeekBarChangeListenerOnThicknessSeekBar(SeekBar.OnSeekBarChangeListener l) {
        mThicknessSeekBar.setOnSeekBarChangeListener(l);
    }

    public void setOnSeekBarChangeListenerOnAlphaSeekBar(SeekBar.OnSeekBarChangeListener l) {
        mAlphaSeekBar.setOnSeekBarChangeListener(l);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mColorShowView.setOnClickListener(onClickListener);
    }

    public void setColor(int color) {
        mColorShowView.setColor(color);
        mThicknessSeekBar.setProgressColor(color);
        mAlphaSeekBar.setProgressColor(color);
    }

    @Override
    public Fragment getSelf() {
        return this;
    }
}
