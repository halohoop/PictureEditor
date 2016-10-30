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
import com.halohoop.pictureeditor.widgets.ColorShowView;
import com.halohoop.pictureeditor.widgets.ShapesChooseView;

public class ShapeDetailFragment extends Fragment implements IFragment {

    private ShapesChooseView mShapeChooseView;
    private ColorShowView mColorShowView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shape_tools_detail_layout, null);
        mShapeChooseView = (ShapesChooseView) view.findViewById(R.id.shapes_choose_view);
        mColorShowView = (ColorShowView) view.findViewById(R.id.color_show_view_in_shapes_group);
        return view;
    }

    public void setListener(ShapesChooseView.OnSelectedListener onSelectedListener) {
        if (mShapeChooseView != null) {
            mShapeChooseView.setOnSelectedListener(onSelectedListener);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (mColorShowView != null) {
            mColorShowView.setOnClickListener(onClickListener);
        }
    }

    public void setColor(int color) {
        mColorShowView.setColor(color);
    }

    @Override
    public Fragment getSelf() {
        return this;
    }
}
