package com.houjie.design.skin.support.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.annotation.Skinable;
import com.houjie.design.skin.support.content.res.SkinCompatResources;
import com.houjie.design.skin.support.content.res.SkinCompatThemeUtils;
import com.houjie.design.skin.support.observe.SkinObservable;
import com.houjie.design.skin.support.observe.SkinObserver;
import com.houjie.design.skin.support.utils.Slog;
import com.houjie.design.skin.support.widget.SkinCompatSupportable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.WeakHashMap;

import static com.houjie.design.skin.support.widget.SkinCompatHelper.INVALID_ID;
import static com.houjie.design.skin.support.widget.SkinCompatHelper.checkResourceId;

public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "SkinActivityLifecycle";
    private static SkinActivityLifecycle sInstance;

    private WeakHashMap<Context, SkinCompatDelegate> mSkinDelegateMap;
    private WeakHashMap<Context, LazySkinObserver> mSkinObserverMap;

    /**
     * 用于记录当前Activity，在换肤后，立即刷新当前Activity以及非Activity创建的View。
     */
    private WeakReference<Activity> mCurActivityRef;

    public synchronized static SkinActivityLifecycle init(Application application) {
        if (null == sInstance) {
            sInstance = new SkinActivityLifecycle(application);
        }
        return sInstance;
    }

    private SkinActivityLifecycle(Application application) {
        application.registerActivityLifecycleCallbacks(this);
        installLayoutFactory(application);
        SkinCompatManager.getInstance().addObserver(getObserver(application));
    }

    private void installLayoutFactory(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LayoutInflaterCompat.setFactory(layoutInflater, getSkinDelegate(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SkinCompatDelegate getSkinDelegate(Context context) {
        if (mSkinDelegateMap == null) {
            mSkinDelegateMap = new WeakHashMap<>();
        }

        SkinCompatDelegate mSkinDelegate = mSkinDelegateMap.get(context);
        if (mSkinDelegate == null) {
            mSkinDelegate = SkinCompatDelegate.create(context);
            mSkinDelegateMap.put(context, mSkinDelegate);
        }
        return mSkinDelegate;
    }

    private LazySkinObserver getObserver(final Context context) {
        if (null == mSkinObserverMap) {
            mSkinObserverMap = new WeakHashMap<>();
        }
        LazySkinObserver observer = mSkinObserverMap.get(context);
        if (null == observer) {
            observer = new LazySkinObserver(context);
            mSkinObserverMap.put(context, observer);
        }
        return observer;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!isContextSkinEnable(activity)) {
            return;
        }
        installLayoutFactory(activity);
        updateStatusBarColor(activity);
        updateWindowBackground(activity);
        if (activity instanceof SkinCompatSupportable) {
            ((SkinCompatSupportable) activity).applySkin();
        }
    }

    private boolean isContextSkinEnable(Context context) {
        return SkinCompatManager.getInstance().isSkinAllActivityEnable()
                || context.getClass().getAnnotation(Skinable.class) != null
                || context instanceof SkinCompatSupportable;
    }

    private void updateStatusBarColor(Activity activity) {
        if (SkinCompatManager.getInstance().isSkinStatusBarColorEnable()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusBarColorResId = SkinCompatThemeUtils.getStatusBarColorResId(activity);
            int colorPrimaryDarkResId = SkinCompatThemeUtils.getColorPrimaryDarkResId(activity);
            if (checkResourceId(statusBarColorResId) != INVALID_ID) {
                activity.getWindow().setStatusBarColor(SkinCompatResources.getColor(activity, statusBarColorResId));
            } else if (checkResourceId(colorPrimaryDarkResId) != INVALID_ID) {
                activity.getWindow().setStatusBarColor(SkinCompatResources.getColor(activity, colorPrimaryDarkResId));
            }
        }
    }

    private void updateWindowBackground(Activity activity) {
        if (SkinCompatManager.getInstance().isSkinWindowBackgroundEnable()) {
            int windowBackgroundResId = SkinCompatThemeUtils.getWindowBackgroundResId(activity);
            if (checkResourceId(windowBackgroundResId) != INVALID_ID) {
                Drawable drawable = SkinCompatResources.getDrawableCompat(activity, windowBackgroundResId);
                if (drawable != null) {
                    activity.getWindow().setBackgroundDrawable(drawable);
                }
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurActivityRef = new WeakReference<>(activity);
        if (isContextSkinEnable(activity)) {
            LazySkinObserver observer = getObserver(activity);
            SkinCompatManager.getInstance().addObserver(observer);
            observer.updateSkinIfNeeded();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (isContextSkinEnable(activity)) {
            SkinCompatManager.getInstance().removeObserver(getObserver(activity));
            mSkinObserverMap.remove(activity);
            mSkinDelegateMap.remove(activity);
        }
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
