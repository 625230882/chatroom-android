package com.example.rq.test;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by rq on 16/4/24.
 */
import java.util.ArrayList;
import java.util.List;

public class ShowMap extends FragmentActivity{


    private FragmentPagerAdapter _fragmentPageAdater;
    private static List<Fragment> _fragments = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        this._fragments.add(0, new SummaryMapActivity());
        this._fragmentPageAdater = new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return _fragments.get(position);
            }

            @Override
            public int getCount() {
                return _fragments.size();
            }
        };
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(this._fragmentPageAdater);
        pager.setCurrentItem(0);

    }
    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Fragment getItem(int position) {
            return new SummaryMapActivity();
        }
    }


}

