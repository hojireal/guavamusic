package com.houjie.design.skin.support.load;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import com.houjie.design.skin.support.SkinCompatManager.SkinLoaderStrategy;

public class SkinBuildInLoader implements SkinLoaderStrategy {
    @Override
    public String loadSkinInBackground(Context context, String skinName) {
        return ;
    }

    @Override
    public String getTargetResourceEntryName(Context context, String skinName, int resId) {
        return context.getResources().getResourceEntryName(resId) + "_" + skinName;
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
        return 0;
    }
}
