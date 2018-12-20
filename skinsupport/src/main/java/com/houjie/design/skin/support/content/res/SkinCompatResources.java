package com.houjie.design.skin.support.content.res;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.SkinCompatManager.SkinLoaderStrategy;

public class SkinCompatResources {
    private static SkinCompatResources sInstance;

    private Resources mResources;
    private String mSkinName = "";
    private String mSkinPkgName = "";
    private SkinLoaderStrategy mStrategy;
    private boolean isDefaultSkin = true;

    private SkinCompatResources() {}

    public synchronized static SkinCompatResources getInstance() {
        if (null == sInstance) {
            sInstance = new SkinCompatResources();
        }
        return sInstance;
    }

    public void reset() {
        reset(SkinCompatManager.getInstance().getStrategies().get(SkinCompatManager.SKIN_LOADER_STRATEGY_NONE));
    }

    public void reset(SkinLoaderStrategy strategy) {
        mResources = SkinCompatManager.getInstance().getContext().getResources();
        mSkinName = "";
        mSkinPkgName = "";
        mStrategy = strategy;
        isDefaultSkin = true;
        SkinCompatUserThemeManager.getInstance().clearCaches();
        SkinCompatDrawableManager.getInstance().clearCaches();
    }

    public void setupSkin(Resources resources, String pkgName,
                          String skinName, SkinLoaderStrategy strategy) {
        if (null == resources || TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(skinName)) {
            reset(strategy);
            return;
        }
        mResources = resources;
        mSkinPkgName = pkgName;
        mSkinName = skinName;
        mStrategy = strategy;
        isDefaultSkin = false;
        SkinCompatUserThemeManager.getInstance().clearCaches();
        SkinCompatDrawableManager.getInstance().clearCaches();
    }

    public Resources getSkinResources() {
        return mResources;
    }

    public String getSkinPkgName() {
        return mSkinPkgName;
    }

    public boolean isDefaultSkin() {
        return isDefaultSkin;
    }

    public static int getColor(Context context, int resId) {
        return getInstance().getSkinColor(context, resId);
    }

    private int getSkinColor(Context context, int resId) {
        if (!SkinCompatUserThemeManager) {

        }
    }
}
