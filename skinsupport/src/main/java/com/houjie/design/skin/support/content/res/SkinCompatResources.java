package com.houjie.design.skin.support.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnyRes;
import android.text.TextUtils;
import android.util.TypedValue;

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
        if (!SkinCompatUserThemeManager.getInstance().isColorEmpty()) {
            ColorStateList colorStateList = SkinCompatUserThemeManager
                    .getInstance().getColorStateList(resId);
            if (null != colorStateList) {
                return colorStateList.getDefaultColor();
            }
        }
        if (null != mStrategy) {
            ColorStateList colorStateList = mStrategy.getColor(context, mSkinName, resId);
            if (null != colorStateList) {
                return colorStateList.getDefaultColor();
            }
        }
        if (!isDefaultSkin) {
            int targetResId = getTargetResId(context, resId);
            if (0 != targetResId) {
                return mResources.getColor(targetResId);
            }
        }
        return context.getResources().getColor(resId);
    }

    private int getTargetResId(Context context, int resId) {
        try {
            String resName = null;
            if (null != mStrategy) {
                resName = mStrategy.getTargetResourceEntryName(context, mSkinName, resId);
            }
            if (TextUtils.isEmpty(resName)) {
                resName = context.getResources().getResourceEntryName(resId);
            }
            String type = context.getResources().getResourceTypeName(resId);
            return mResources.getIdentifier(resName, type, mSkinPkgName);
        } catch (Exception e) {
            return 0;
        }
    }

    public static ColorStateList getColorStateList(Context context, int resId) {
        return getInstance().getSkinColorStateList(context, resId);
    }

    private ColorStateList getSkinColorStateList(Context context, int resId) {
        if (!SkinCompatUserThemeManager.getInstance().isColorEmpty()) {
            ColorStateList colorStateList = SkinCompatUserThemeManager
                    .getInstance().getColorStateList(resId);
            if (null != colorStateList) {
                return colorStateList;
            }
        }
        if (null != mStrategy) {
            ColorStateList colorStateList = mStrategy.getColorStateList(context, mSkinName, resId);
            if (null != colorStateList) {
                return colorStateList;
            }
        }
        if (!isDefaultSkin) {
            int targetResId = getTargetResId(context, resId);
            if (0 != targetResId) {
                return mResources.getColorStateList(targetResId);
            }
        }
        return context.getResources().getColorStateList(resId);
    }

    public static void getValue(Context context, @AnyRes int resId, TypedValue outValue, boolean resolveRefs) {
        getInstance().getSkinValue(context, resId, outValue, resolveRefs);
    }

    private void getSkinValue(Context context, @AnyRes int resId, TypedValue outValue, boolean resolveRefs) {
        if (!isDefaultSkin) {
            int targetResId = getTargetResId(context, resId);
            if (targetResId != 0) {
                mResources.getValue(targetResId, outValue, resolveRefs);
                return;
            }
        }
        context.getResources().getValue(resId, outValue, resolveRefs);
    }

    public static XmlResourceParser getXml(Context context, int resId) {
        return getInstance().getSkinXml(context, resId);
    }

    private XmlResourceParser getSkinXml(Context context, int resId) {
        if (!isDefaultSkin) {
            int targetResId = getTargetResId(context, resId);
            if (targetResId != 0) {
                return mResources.getXml(targetResId);
            }
        }
        return context.getResources().getXml(resId);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return getInstance().getSkinDrawable(context, resId);
    }

    private Drawable getSkinDrawable(Context context, int resId) {
        if (!SkinCompatUserThemeManager.getInstance().isColorEmpty()) {
            ColorStateList colorStateList = SkinCompatUserThemeManager.getInstance().getColorStateList(resId);
            if (colorStateList != null) {
                return new ColorDrawable(colorStateList.getDefaultColor());
            }
        }
        if (!SkinCompatUserThemeManager.getInstance().isDrawableEmpty()) {
            Drawable drawable = SkinCompatUserThemeManager.getInstance().getDrawable(resId);
            if (drawable != null) {
                return drawable;
            }
        }
        if (mStrategy != null) {
            Drawable drawable = mStrategy.getDrawable(context, mSkinName, resId);
            if (drawable != null) {
                return drawable;
            }
        }
        if (!isDefaultSkin) {
            int targetResId = getTargetResId(context, resId);
            if (targetResId != 0) {
                return mResources.getDrawable(targetResId);
            }
        }
        return context.getResources().getDrawable(resId);
    }
}
