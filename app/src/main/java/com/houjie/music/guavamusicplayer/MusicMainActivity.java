package com.houjie.music.guavamusicplayer;

import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.houjie.music.guavamusicplayer.maincontent.fragment.MusicMainFragment;
import com.houjie.music.guavamusicplayer.maincontent.fragment.SplashFragment;
import com.houjie.music.guavamusicplayer.utils.Log;

public class MusicMainActivity extends AppStarterActivity {
    private static final String TAG = Log.makeLogTag(AppStarterActivity.class);

    @Override
    protected Fragment createSplashFragment() {
        return new SplashFragment();
    }

    @Override
    protected Fragment createMainFragment() {
        return new MusicMainFragment();
    }

    @Override
    public void onSplashFragmentClosed() {
        super.onSplashFragmentClosed();
        exitFullScreen();
    }

    private void exitFullScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
