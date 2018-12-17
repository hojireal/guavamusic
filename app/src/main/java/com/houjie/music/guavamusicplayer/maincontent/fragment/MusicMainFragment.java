package com.houjie.music.guavamusicplayer.maincontent.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.houjie.music.guavamusicplayer.R;
import com.houjie.music.guavamusicplayer.discovery.fragment.DiscoveryFragment;
import com.houjie.music.guavamusicplayer.musichall.fragment.MusicHallFragment;
import com.houjie.music.guavamusicplayer.my.fragment.MyFragment;
import com.houjie.music.guavamusicplayer.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class MusicMainFragment extends TabInActionBarFragment {
    private static final String TAG = Log.makeLogTag(MusicMainFragment.class);

    private List<ContentFragmentItem> mContentFragmentItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_music_main, container, false);
        initializeToolbar();
        initializeTab();
        return mBaseView;
    }

    @Override
    protected int getOverflowIconResId() {
        return R.drawable.ic_fragment_music_main_overflow_icon;
    }

    @Override
    protected int getFloatingActionButtonIconResId() {
        return android.R.drawable.ic_dialog_email;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    protected int getNavigationViewMenuResId() {
        return R.menu.fragment_music_main_drawer;
    }

    @Override
    protected int getNavigationViewHeaderViewResId() {
        return R.layout.fragment_music_main_nav_header;
    }

    @Override
    protected FragmentPagerAdapter createViewPagerAdapter() {
        mContentFragmentItems = createContentFragmentItems();
        return new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                ContentFragmentItem item = mContentFragmentItems.get(i);
                Fragment f = null;
                try {
                    f = (Fragment) item.mFragmentClass.newInstance();
                } catch (Exception e) {
                    Log.e(TAG, e.getCause());
                }
                return f;
            }

            @Override
            public int getCount() {
                return mContentFragmentItems.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return getString(mContentFragmentItems.get(position).mTitleResId);
            }
        };
    }

    private List<ContentFragmentItem> createContentFragmentItems() {
        final List<ContentFragmentItem> items = new ArrayList<>();
        items.add(new ContentFragmentItem(R.string.fragment_content_my, MyFragment.class));
        items.add(new ContentFragmentItem(R.string.fragment_content_music_hall, MusicHallFragment.class));
        items.add(new ContentFragmentItem(R.string.fragment_content_discovery, DiscoveryFragment.class));
        return items;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
    }

    private static class ContentFragmentItem {
        @StringRes
        int mTitleResId;
        Class mFragmentClass;

        ContentFragmentItem(int resId, Class fragmentClass) {
            mTitleResId = resId;
            mFragmentClass = fragmentClass;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_music_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
