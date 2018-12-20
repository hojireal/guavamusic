package com.houjie.design.skin.support.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.observe.SkinObservable;
import com.houjie.design.skin.support.observe.SkinObserver;
import com.houjie.design.skin.support.utils.Slog;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "SkinActivityLifecycle";
    private static SkinActivityLifecycle sInstance;

    private WeakHashMap<Context, SkinCompatDelegate> mSkinDelegateMap;
    private WeakHashMap<Context, LazySkinObserver> mSkinObserverMap;

    /**
     * 用于记录当前Activity，在换肤后，立即刷新当前Activity以及非Activity创建的View。
     */
    private WeakReference<Activity> mCurActivityRef;

    private SkinActivityLifecycle() {

    }

    public synchronized static SkinActivityLifecycle init(Application application) {
        if (null == sInstance) {
            sInstance = new SkinActivityLifecycle(application);
        }
        return sInstance;
    }

    private SkinActivityLifecycle(Application application) {
        application.registerActivityLifecycleCallbacks(this);
        installLayoutFactory(application);
        SkinCompatManager.getInstance()
    }

    private boolean isContextSkinEnable(Context context) {

    }

    private class LazySkinObserver implements SkinObserver {
        private final Context mContext;
        private boolean mMarkNeedUpdate = false;

        LazySkinObserver(Context context) {
            mContext = context;
        }

        @Override
        public void updateSkin(SkinObservable observable, Object o) {
            // 当前Activity，或者非Activity，立即刷新，否则延迟到下次onResume方法中刷新。
            if (null == mCurActivityRef || mContext == mCurActivityRef.get()
                    || !(mContext instanceof Activity)) {
                updateSkinForce();
            } else {
                mMarkNeedUpdate = true;
            }
        }

        void updateSkinForce() {
            Slog.i(TAG, "Context: " + mContext + " updateSkinForce");
            if (null == mContext) {
                return;
            }

            if (mContext instanceof Activity && isContextSkinEnable(mContext)) {
                updateStatusBarColor((Activity) mContext);
                updateWindowBackground((Activity) mContext);
            }
            getSkinDelegate(mContext).applySkin();
            if (mContext instanceof SkinCompatSupportable) {
                ((SkinCompatSupportable) mContext).applySkin();
            }
            mMarkNeedUpdate = false;
        }

        void updateSkinIfNeeded() {
            if (mMarkNeedUpdate) {
                updateSkinForce();
            }
        }
    }
}
