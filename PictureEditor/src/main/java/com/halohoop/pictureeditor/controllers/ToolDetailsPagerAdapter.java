/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ToolDetailsPagerAdapter.java
 *
 * Tools details container adapter
 *
 * Author huanghaiqi, Created at 2016-10-23
 *
 * Ver 1.0, 2016-10-23, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.halohoop.pictureeditor.pieces.IFragment;

import java.util.List;

public class ToolDetailsPagerAdapter extends FragmentStatePagerAdapter {
    private List<IFragment> mIFragments;

    public ToolDetailsPagerAdapter(FragmentManager fm, List<IFragment> iFragments) {
        super(fm);
        this.mIFragments = iFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mIFragments.get(position).getSelf();
    }

    @Override
    public int getCount() {
        return mIFragments.size();
    }
}
