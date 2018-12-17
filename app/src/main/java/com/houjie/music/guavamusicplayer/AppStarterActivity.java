package com.houjie.music.guavamusicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.houjie.music.guavamusicplayer.maincontent.fragment.OnSplashFragmentClosedCallback;
import com.houjie.music.guavamusicplayer.utils.Log;

public abstract class AppStarterActivity extends AppCompatActivity
        implements OnSplashFragmentClosedCallback {
    private static final String TAG = Log.makeLogTag(AppStarterActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_app_starter);
        setSplashFragment();
    }

    private void setSplashFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.starter_fragment_container);
        if (null == fragment) {
            fragment = createSplashFragment();
            fm.beginTransaction().add(R.id.starter_fragment_container, fragment).commit();
        }
    }

    protected abstract Fragment createSplashFragment();

    @Override
    public void onSplashFragmentClosed() {
        Log.d(TAG, "onSplashFragmentClosed");
        setMainFragment();
    }

    private void setMainFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.starter_fragment_container);
        if (null != fragment) {
            fm.beginTransaction().remove(fragment).commit();
        }

        fragment = createMainFragment();
        fm.beginTransaction().add(R.id.starter_fragment_container, fragment).commit();
    }

    protected abstract Fragment createMainFragment();
}
