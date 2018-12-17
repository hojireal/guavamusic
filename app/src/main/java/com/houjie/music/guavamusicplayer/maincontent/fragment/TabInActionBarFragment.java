package com.houjie.music.guavamusicplayer.maincontent.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.houjie.music.guavamusicplayer.R;
import com.houjie.music.guavamusicplayer.utils.Log;

public abstract class TabInActionBarFragment extends ActionBarFragment {
    private static final String TAG = Log.makeLogTag(TabInActionBarFragment.class);

    protected boolean mIsTabInitialized;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mIsTabInitialized) {
            throw new IllegalStateException("You must run super.initializeTab at " +
                    "the end of your onCreateView method");
        }
    }

    protected void initializeTab() {
        mToolbar.setTitle("");

        TabLayout tabLayout = mBaseView.findViewById(R.id.tab_layout);
        ViewPager viewPager = mBaseView.findViewById(R.id.view_pager);
        viewPager.setAdapter(createViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);

        mIsTabInitialized = true;
    }

    protected abstract FragmentPagerAdapter createViewPagerAdapter();
}
