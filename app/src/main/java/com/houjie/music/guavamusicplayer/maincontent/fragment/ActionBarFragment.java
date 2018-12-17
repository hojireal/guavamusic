package com.houjie.music.guavamusicplayer.maincontent.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.houjie.music.guavamusicplayer.R;
import com.houjie.music.guavamusicplayer.utils.Log;

public abstract class ActionBarFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = Log.makeLogTag(ActionBarFragment.class);

    protected View mBaseView;
    protected Toolbar mToolbar;
    protected boolean mToolbarInitialized;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mToolbarInitialized) {
            throw new IllegalStateException("You must run super.initializeToolbar at " +
                    "the end of your onCreateView method");
        }
    }

    protected void initializeToolbar() {
        mToolbar = mBaseView.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(),
                getOverflowIconResId()));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        FloatingActionButton fab = mBaseView.findViewById(R.id.fab);
        fab.setImageResource(getFloatingActionButtonIconResId());
        fab.setOnClickListener(this);

        DrawerLayout drawer = mBaseView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = mBaseView.findViewById(R.id.nav_view);
        navigationView.inflateMenu(getNavigationViewMenuResId());
        navigationView.inflateHeaderView(getNavigationViewHeaderViewResId());
        navigationView.setNavigationItemSelectedListener(this);

        mToolbarInitialized = true;
    }

    @MenuRes
    protected abstract int getNavigationViewMenuResId();

    @LayoutRes
    protected abstract int getNavigationViewHeaderViewResId();

    @DrawableRes
    protected abstract int getOverflowIconResId();

    @DrawableRes
    protected abstract int getFloatingActionButtonIconResId();
}
