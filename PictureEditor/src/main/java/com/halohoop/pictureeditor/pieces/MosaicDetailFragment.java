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
import com.halohoop.pictureeditor.widgets.ThicknessSeekBar;

public class MosaicDetailFragment extends Fragment implements IFragment {

    private ThicknessSeekBar mThicknessSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mosaic_tools_detail_layout, null);
        mThicknessSeekBar = (ThicknessSeekBar) view.findViewById(R.id.mosaic_thickness_seek_bar);
        return view;
    }

    public void setOnSeekBarChangeListenerOnThicknessSeekBar(SeekBar.OnSeekBarChangeListener l) {
        mThicknessSeekBar.setOnSeekBarChangeListener(l);
    }

    @Override
    public Fragment getSelf() {
        return this;
    }
}
