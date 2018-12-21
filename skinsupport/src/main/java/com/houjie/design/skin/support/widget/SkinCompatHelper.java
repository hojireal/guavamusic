package com.houjie.design.skin.support.widget;

public abstract class SkinCompatHelper {
    protected static final String SYSTEM_ID_PREFIX = "1";
    public static final int INVALID_ID = 0;

    public static final int checkResourceId(int resId) {
        String hexResId = Integer.toHexString(resId);
        return hexResId.startsWith(SYSTEM_ID_PREFIX) ? INVALID_ID : resId;
    }

    public abstract void applySkin();
}
