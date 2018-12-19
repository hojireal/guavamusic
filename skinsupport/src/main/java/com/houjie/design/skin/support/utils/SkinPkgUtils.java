package com.houjie.design.skin.support.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.Nullable;

public class SkinPkgUtils {

    /**
     * 获取皮肤包的包名。
     *
     * @param context {@link Context}
     * @param skinPkgPath 存储器上皮肤包的路径。
     * @return 皮肤包的包名。
     */
    public static String getSkinPackageName(Context context, String skinPkgPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
        return info.packageName;
    }

    /**
     * 获取皮肤包的资源{@link Resources}
     * @param context {@link Context}
     * @param skinPkgPath 存储器中皮肤包的路径。
     * @return 皮肤包的资源。
     */
    @Nullable
    public static Resources getSkinResource(Context context, String skinPkgPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(skinPkgPath, 0);
            info.applicationInfo.sourceDir = skinPkgPath;
            info.applicationInfo.publicSourceDir = skinPkgPath;
            Resources res = pm.getResourcesForApplication(info.applicationInfo);
            Resources superRes = context.getResources();
            return new Resources(res.getAssets(), superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
