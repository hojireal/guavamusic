package com.houjie.design.skin.support.load;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.houjie.design.skin.support.SkinCompatManager.SkinLoaderStrategy;
import com.houjie.design.skin.support.utils.SkinFileUtils;
import com.houjie.design.skin.support.utils.SkinPkgUtils;

public abstract class SkinStorageLoader implements SkinLoaderStrategy {
    @Override
    public String loadSkinInBackground(Context context, String skinName) {
        if (TextUtils.isEmpty(skinName)) {
            return skinName;
        }

        String skinPkgPath = getSkinPath(context, skinName);
        if (SkinFileUtils.isFileExists(skinPkgPath)) {
            String pkgName = SkinPkgUtils.getSkinPackageName(context, skinPkgPath);
            Resources res = SkinPkgUtils.getSkinResource(context, skinPkgPath);
            if (null != res && !TextUtils.isEmpty(pkgName)) {

            }
        }
        return null;
    }

    protected abstract String getSkinPath(Context context, String skinName);

    @Override
    public String getTargetResourceEntryName(Context context, String skinName, int resId) {
        return null;
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
}
