package com.houjie.design.skin.support;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.houjie.design.skin.support.app.SkinActivityLifecycle;
import com.houjie.design.skin.support.app.SkinLayoutInflater;
import com.houjie.design.skin.support.content.res.SkinCompatResources;
import com.houjie.design.skin.support.load.SkinAssetsLoader;
import com.houjie.design.skin.support.load.SkinBuildInLoader;
import com.houjie.design.skin.support.load.SkinNoneLoader;
import com.houjie.design.skin.support.load.SkinPrefixBuildInLoader;
import com.houjie.design.skin.support.observe.SkinObservable;
import com.houjie.design.skin.support.utils.SkinPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeffrey Hou(Hou jie)
 * @date on 2018/12/18
 * @email p_jiehhou@tencent.com
 * @describe TODO
 **/
public class SkinCompatManager extends SkinObservable {
    public static final int SKIN_LOADER_STRATEGY_NONE = -1;
    public static final int SKIN_LOADER_STRATEGY_ASSETS = 0;
    public static final int SKIN_LOADER_STRATEGY_BUILD_IN = 1;
    public static final int SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN = 2;

    private static SkinCompatManager sInstance;

    private final Context mAppContext;
    private final Object mLock = new Object();
    private boolean mLoading = false;

    private List<SkinLayoutInflater> mInflaters = new ArrayList<>();
    private List<SkinLayoutInflater> mHookInflaters = new ArrayList<>();
    private SparseArray<SkinLoaderStrategy> mStrategyMap = new SparseArray<>();

    private boolean mSkinAllActivityEnable = true;
    private boolean mSkinStatusBarColorEnable = false;
    private boolean mSkinWindowBackgroundColorEnable = true;

    public static SkinCompatManager getInstance() {
        return sInstance;
    }

    /**
     * 初始化换肤框架，监听Activity生命周期。
     *
     * @param application 应用Application.
     * @return
     */
    public static SkinCompatManager withoutActivity(Application application) {
        init(application);
        SkinActivityLifecycle.init(application);
        return sInstance;
    }

    private static SkinCompatManager init(Context context) {
        if (null == sInstance) {
            synchronized (SkinCompatManager.class) {
                if (null == sInstance) {
                    sInstance = new SkinCompatManager(context);
                }
            }
        }
        SkinPreference.init(context);
        return sInstance;
    }

    private SkinCompatManager(Context context) {
        mAppContext = context.getApplicationContext();
        initLoaderStrategy();
    }

    private void initLoaderStrategy() {
        mStrategyMap.put(SKIN_LOADER_STRATEGY_NONE, new SkinNoneLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_ASSETS, new SkinAssetsLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_BUILD_IN, new SkinBuildInLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN, new SkinPrefixBuildInLoader());
    }

    public Context getContext() {
        return mAppContext;
    }

    /**
     * 添加皮肤包加载策略。
     *
     * @param strategy 自定义加载策略
     * @return
     */
    public SkinCompatManager addStrategy(SkinLoaderStrategy strategy) {
        mStrategyMap.put(strategy.getType(), strategy);
        return this;
    }

    public SparseArray<SkinLoaderStrategy> getStrategies() {
        return mStrategyMap;
    }

    /**
     * 自定义View换肤时，可选择添加一个{@link SkinLayoutInflater}
     *
     * @param inflater 在{@link com.houjie.design.skin.support.app.SkinCompatViewInflater#createView(Context, String, String)}方法中调用。
     * @return
     */
    public SkinCompatManager addInflater(SkinLayoutInflater inflater) {
        mInflaters.add(inflater);
        return this;
    }

    public List<SkinLayoutInflater> getInflaters() {
        return mInflaters;
    }

    /**
     * 自定义View换肤时，可选择添加一个{@link SkinLayoutInflater}
     *
     * @param inflater 在{@link com.houjie.design.skin.support.app.SkinCompatViewInflater#createView(Context, String, String)}方法中最先调用.
     * @return
     */
    public SkinCompatManager addHookInflater(SkinLayoutInflater inflater) {
        mHookInflaters.add(inflater);
        return this;
    }

    public List<SkinLayoutInflater> getHookInflaters() {
        return mHookInflaters;
    }

    /**
     * 恢复默认主题，使用应用自带资源.
     */
    public void restoreDefaultTheme() {
        loadSkin("", SKIN_LOADER_STRATEGY_NONE);
    }

    /**
     * 设置是否所有Activity都换肤.
     *
     * @param enable true: 所有Activity都换肤; false: 添加注解Skinable或实现SkinCompatSupportable的Activity支持换肤.
     * @return
     */
    public SkinCompatManager setSkinAllActivityEnable(boolean enable) {
        mSkinAllActivityEnable = enable;
        return this;
    }

    public boolean isSkinAllActivityEnable() {
        return mSkinAllActivityEnable;
    }

    /**
     * 设置状态栏换肤，使用Theme中的{@link android.R.attr#statusBarColor}属性. 5.0以上有效.
     *
     * @param enable true: 打开; false: 关闭.
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SkinCompatManager setSkinStatusBarColorEnable(boolean enable) {
        mSkinStatusBarColorEnable = enable;
        return this;
    }

    public boolean isSkinStatusBarColorEnable() {
        return mSkinStatusBarColorEnable;
    }

    /**
     * 设置WindowBackground换肤，使用Theme中的{@link android.R.attr#windowBackground}属性.
     *
     * @param enable true: 打开; false: 关闭.
     * @return
     */
    public SkinCompatManager setSkinWindowBackgroundEnable(boolean enable) {
        mSkinWindowBackgroundColorEnable = enable;
        return this;
    }

    public boolean isSkinWindowBackgroundEnable() {
        return mSkinWindowBackgroundColorEnable;
    }

    /**
     * 加载记录的皮肤包，一般在Application中初始化换肤框架后调用.
     *
     * @return
     */
    public AsyncTask loadSkin() {
        String skin = SkinPreference.getInstance().getSkinName();
        int strategy = SkinPreference.getInstance().getSkinStrategy();
        if (TextUtils.isEmpty(skin) || strategy == SKIN_LOADER_STRATEGY_NONE) {
            return null;
        }
        return loadSkin(skin, null, strategy);
    }

    /**
     * 加载记录的皮肤包，一般在Application中初始化换肤框架后调用。
     *
     * @param listener 皮肤包加载监听。
     * @return
     */
    public AsyncTask loadSkin(SkinLoaderListener listener) {
        String skin = SkinPreference.getInstance().getSkinName();
        int strategy = SkinPreference.getInstance().getSkinStrategy();
        if (TextUtils.isEmpty(skin) || strategy == SKIN_LOADER_STRATEGY_NONE) {
            return null;
        }
        return loadSkin(skin, listener, strategy);
    }

    /**
     * 加载皮肤包。
     *
     * @param skinName 皮肤包名称。
     * @param strategy 皮肤包加载策略。
     * @return
     */
    public AsyncTask loadSkin(String skinName, int strategy) {
        return loadSkin(skinName, null, strategy);
    }

    public AsyncTask loadSkin(String skinName, SkinLoaderListener listener, int strategy) {
        SkinLoaderStrategy loaderStrategy = mStrategyMap.get(strategy);
        if (null == loaderStrategy) {
            return null;
        }
        return new SkinLoadTask(listener, loaderStrategy).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, skinName);
    }

    private class SkinLoadTask extends AsyncTask<String, Void, String> {
        private final SkinLoaderListener mListener;
        private final SkinLoaderStrategy mStrategy;

        SkinLoadTask(@Nullable SkinLoaderListener listener, @NonNull SkinLoaderStrategy strategy) {
            mListener = listener;
            mStrategy = strategy;
        }

        @Override
        protected void onPreExecute() {
            if (mListener != null) {
                mListener.onStart();
            }
        }

        @Override
        protected void onPostExecute(String skinName) {
            synchronized (mLock) {
                if (null != skinName) {
                    SkinPreference.getInstance().setSkinName(skinName)
                            .setSkinStrategy(mStrategy.getType()).commitEditor();
                    notifyUpdateSkin();
                    if (null != mListener)
                        mListener.onSuccess();
                } else {
                    SkinPreference.getInstance().setSkinName("")
                            .setSkinStrategy(SKIN_LOADER_STRATEGY_NONE).commitEditor();
                    if (null != mListener)
                        mListener.onFailed("Skin load failed");
                }
                mLoading = false;
                mLock.notifyAll();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            synchronized (mLock) {
                while (mLoading) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mLoading = true;
            }
            try {
                if (1 == params.length) {
                    String skinName = mStrategy.loadSkinInBackground(mAppContext, params[0]);
                    if (TextUtils.isEmpty(skinName)) {
                        SkinCompatResources.getInstance().reset(mStrategy);
                    }
                    return params[0];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SkinCompatResources.getInstance().reset();
            return null;
        }
    }

    /**
     * 皮肤包加载监听。
     */
    public interface SkinLoaderListener {
        /**
         * 开始加载。
         */
        void onStart();

        /**
         * 加载成功。
         */
        void onSuccess();

        /**
         * 加载失败
         *
         * @param errMsg 错误信息。
         */
        void onFailed(String errMsg);
    }

    /**
     * 皮肤包加载策略。
     */
    public interface SkinLoaderStrategy {
        /**
         * 后台加载皮肤包
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称。
         * @return 加载成功，返回皮肤包名称；失败
         */
        String loadSkinInBackground(Context context, String skinName);

        /**
         * 根据当前应用中的资源ID，获取皮肤包相应资源的资源名。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId    当前应用中需要换肤的资源ID。
         * @return 皮肤包中对应的资源名。
         */
        String getTargetResourceEntryName(Context context, String skinName, int resId);

        /**
         * 开发者可以在此函数中拦截应用中的颜色资源ID，返回对应的color值。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId    当前应用中需要换肤的资源ID。
         * @return 被拦截后的color值，添加到ColorStateList的defaultColor中。当不需要拦截时，返回空。
         */
        ColorStateList getColor(Context context, String skinName, int resId);

        /**
         * 开发者可以在此函数中拦截应用中的ColorStateList资源ID，返回对应的ColorStateList值。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId    当前应用中需要换肤的资源ID。
         * @return 被拦截后的ColorStateList值。当不需要拦截时，返回空。
         */
        ColorStateList getColorStateList(Context context, String skinName, int resId);

        /**
         * 开发者可以在此函数中拦截应用中的Drawable资源ID，返回对应的Drawable值。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId    当前应用中需要换肤的资源ID。
         * @return 被拦截后的Drawable值。当不需要拦截时，返回空。
         */
        Drawable getDrawable(Context context, String skinName, int resId);

        /**
         * {@link #SKIN_LOADER_STRATEGY_NONE}
         * {@link #SKIN_LOADER_STRATEGY_ASSETS}
         * {@link #SKIN_LOADER_STRATEGY_BUILD_IN}
         *
         * @return 皮肤包加载策略类型。
         */
        int getType();
    }
}
