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

import com.halohoop.pictureeditor.R;
import com.halohoop.pictureeditor.widgets.ColorPickerView;

public class ColorPickerDetailFragment extends Fragment implements IFragment {

    private ColorPickerView mColorPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_picker_tools_detail_layout, null);
        mColorPicker = (ColorPickerView) view.findViewById(R.id.color_picker);
        return view;
    }

    public void setColorPickListener(ColorPickerView.ColorPickListener colorPickListener) {
        mColorPicker.setColorPickListener(colorPickListener);
    }

    public void setColorsMarginBetween(int marginBetween) {
        mColorPicker.setMarginBetween(marginBetween);
    }

    public void setColors(String... colors) {
        mColorPicker.setColors(colors);
    }

    @Override
    public Fragment getSelf() {
        return this;
    }
}
