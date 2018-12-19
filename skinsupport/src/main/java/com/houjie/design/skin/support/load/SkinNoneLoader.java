package com.houjie.design.skin.support.load;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.SkinCompatManager.SkinLoaderStrategy;

public class SkinNoneLoader implements SkinLoaderStrategy {
    @Override
    public String loadSkinInBackground(Context context, String skinName) {
        return null;
    }

    @Override
    public String getTargetResourceEntryName(Context context, String skinName, int resId) {
        return "";
    }

    @Override
    public ColorStateList getColor(Context context, String skinName, int resId) {
        return null;
    }

    @Override
    public ColorStateList getColorStateList(Context context, String skinName, int resId) {
        return null;
    }

    @Override
    public Drawable getDrawable(Context context, String skinName, int resId) {
        return null;
    }

    @Override
    public int getType() {
        return SkinCompatManager.SKIN_LOADER_STRATEGY_NONE;
    }
}
