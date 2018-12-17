package com.houjie.music.guavamusicplayer.maincontent.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.houjie.music.guavamusicplayer.R;
import com.houjie.music.guavamusicplayer.utils.Log;

public class SplashFragment extends Fragment {
    private static final String TAG = Log.makeLogTag(SplashFragment.class);
    private static final int CLOSE_DELAY_MILLS = 1000;

    private OnSplashFragmentClosedCallback mOnSplashFragmentClosedCallback;
    private Handler mHandler = new Handler();
    private Runnable mCloseRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mOnSplashFragmentClosedCallback) {
                mOnSplashFragmentClosedCallback.onSplashFragmentClosed();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnSplashFragmentClosedCallback = (OnSplashFragmentClosedCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnSplashFragmentClosedCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        mHandler.removeCallbacks(mCloseRunnable);
        mHandler.postDelayed(mCloseRunnable, CLOSE_DELAY_MILLS);
    }
}
