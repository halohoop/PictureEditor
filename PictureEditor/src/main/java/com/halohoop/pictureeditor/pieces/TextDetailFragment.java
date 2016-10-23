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
import android.widget.ImageView;

import com.halohoop.pictureeditor.R;
import com.halohoop.pictureeditor.widgets.ColorShowView;

public class TextDetailFragment extends Fragment implements IFragment {

    private ImageView mIvAddText;
    private ColorShowView mColorShowView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_tools_detail_layout, null);
        mIvAddText = (ImageView) view.findViewById(R.id.iv_add_text);
        mColorShowView = (ColorShowView) view.findViewById(R.id.color_show_view_in_text_detail_container);
        return view;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mColorShowView.setOnClickListener(onClickListener);
        mIvAddText.setOnClickListener(onClickListener);
    }

    public void setColor(int color) {
        mColorShowView.setColor(color);
    }

    @Override
    public Fragment getSelf() {
        return this;
    }
}
