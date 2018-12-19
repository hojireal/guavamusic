package com.houjie.design.skin.support;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.print.PrinterId;
import android.util.SparseArray;

import com.houjie.design.skin.support.app.SkinActivityLifecycle;
import com.houjie.design.skin.support.app.SkinLayoutInflater;
import com.houjie.design.skin.support.load.SkinNoneLoader;
import com.houjie.design.skin.support.utils.SkinPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * @date on 2018/12/18
 * @author Jeffrey Hou(Hou jie)
 * @email p_jiehhou@tencent.com
 * @describe TODO
 **/
public class SkinCompatManager {
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
         * @param context {@link Context}
         * @param skinName 皮肤包名称。
         * @return 加载成功，返回皮肤包名称；失败
         */
        String loadSkinInBackground(Context context, String skinName);

        /**
         * 根据当前应用中的资源ID，获取皮肤包相应资源的资源名。
         *
         * @param context {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId 当前应用中需要换肤的资源ID。
         * @return 皮肤包中对应的资源名。
         */
        String getTargetResourceEntryName(Context context, String skinName, int resId);

        /**
         * 开发者可以在此函数中拦截应用中的颜色资源ID，返回对应的color值。
         *
         * @param context {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId 当前应用中需要换肤的资源ID。
         * @return 被拦截后的color值，添加到ColorStateList的defaultColor中。当不需要拦截时，返回空。
         */
        ColorStateList getColor(Context context, String skinName, int resId);

        /**
         * 开发者可以在此函数中拦截应用中的ColorStateList资源ID，返回对应的ColorStateList值。
         *
         * @param context {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId 当前应用中需要换肤的资源ID。
         * @return 被拦截后的ColorStateList值。当不需要拦截时，返回空。
         */
        ColorStateList getColorStateList(Context context, String skinName, int resId);

        /**
         * 开发者可以在此函数中拦截应用中的Drawable资源ID，返回对应的Drawable值。
         *
         * @param context {@link Context}
         * @param skinName 皮肤包名称。
         * @param resId 当前应用中需要换肤的资源ID。
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

    private SkinCompatManager(Context context) {
        mAppContext = context.getApplicationContext();
        initLoaderStrategy();
    }

    private void initLoaderStrategy() {
        mStrategyMap.put(SKIN_LOADER_STRATEGY_NONE, new SkinNoneLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_ASSETS, new );
    }

    public Context getContext() {
        return mAppContext;
    }
}
