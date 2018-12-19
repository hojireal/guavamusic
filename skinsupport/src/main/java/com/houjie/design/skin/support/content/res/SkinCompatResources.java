package com.houjie.design.skin.support.content.res;

import android.content.res.Resources;
import android.text.TextUtils;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.load.SkinStorageLoader;

public class SkinCompatResources {
    private static SkinCompatResources sInstance;

    private Resources mResources;
    private String mSkinName = "";
    private String mSkinPkgName = "";
    private SkinCompatManager.SkinLoaderStrategy mStrategy;
    private boolean isDefaultSkin = true;

    private SkinCompatResources() {}

    public synchronized static SkinCompatResources getInstance() {
        if (null == sInstance) {
            sInstance = new SkinCompatResources();
        }
        return sInstance;
    }


    public void reset() {
        reset();
    }

    public void reset(SkinCompatManager.SkinLoaderStrategy strategy) {
        mResources = SkinCompatManager.getInstance().getContext().getResources();
        mSkinName = "";
        mSkinPkgName = "";
        mStrategy = strategy;
        isDefaultSkin = true;
    }

    public void setupSkin(Resources resources, String pkgName,
                          String skinName, SkinStorageLoader strategy) {
        if (null == resources || TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(skinName)) {
            reset(strategy);
            return;
        }
        mResources = resources;
        mSkinPkgName = pkgName;
        mSkinName = skinName;
        mStrategy = strategy;
        isDefaultSkin = false;
        Ski

    }
}
