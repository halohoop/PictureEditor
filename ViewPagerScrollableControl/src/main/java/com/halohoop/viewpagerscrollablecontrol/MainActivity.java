package com.halohoop.viewpagerscrollablecontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = (ViewPager) findViewById(R.id.vp);
        List<IFragment> mIFragments = createIFragments();
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mIFragments));
    }

    private List<IFragment> createIFragments() {
        IFragment fragment1 = new Fragment1();
        IFragment fragment2 = new Fragment2();
        List<IFragment> iFragments = new ArrayList<>();
        iFragments.add(fragment1);
        iFragments.add(fragment2);
        return iFragments;
    }

    public void scroll(View view) {
        if (vp.getCurrentItem() == 0) {
            vp.setCurrentItem(1,true);
        }else {
            vp.setCurrentItem(0,true);
        }
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        private List<IFragment> mIFragments;

        public MyPagerAdapter(FragmentManager fm, List<IFragment> iFragments) {
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
}
