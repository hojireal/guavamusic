package com.houjie.design.skin.support.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.utils.ImageUtils;
import com.houjie.design.skin.support.utils.SkinPreference;
import com.houjie.design.skin.support.utils.Slog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;

import static com.houjie.design.skin.support.content.res.ColorState.checkColorValid;
import static com.houjie.design.skin.support.content.res.ColorState.toJSONObject;

public class SkinCompatUserThemeManager {
    private static final String TAG = "SkinCompatUserThemeManager";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TYPE_COLOR = "color";
    private static final String KEY_TYPE_DRAWABLE = "drawable";
    private static final String KEY_DRAWABLE_NAME = "drawableName";
    private static final String KEY_DRAWABLE_PATH_AND_ANGLE = "drawablePathAndAngle";

    private static SkinCompatUserThemeManager INSTANCE = new SkinCompatUserThemeManager();

    private final HashMap<String, ColorState> mColorNameStateMap = new HashMap<>();
    private final Object mColorCacheLock = new Object();
    private final WeakHashMap<Integer, WeakReference<ColorStateList>> mColorCaches = new WeakHashMap<>();
    private final HashMap<String, String> mDrawablePathAndAngleMap = new HashMap<>();
    private final Object mDrawableCacheLock = new Object();
    private final WeakHashMap<Integer, WeakReference<Drawable>> mDrawableCaches = new WeakHashMap<>();
    private boolean mColorEmpty;
    private boolean mDrawableEmpty;

    private SkinCompatUserThemeManager() {
        try {
            startLoadFromSharedPreferences();
        } catch (JSONException e) {
            mColorNameStateMap.clear();
            mDrawablePathAndAngleMap.clear();
            Slog.i(TAG, "startLoadFromSharedPreferences error: " + e);
        }
    }

    public static SkinCompatUserThemeManager getInstance() {
        return INSTANCE;
    }

    private static boolean checkPathValid(String drawablePath) {
        boolean valid = !TextUtils.isEmpty(drawablePath) && new File(drawablePath).exists();
        if (!valid) {
            Slog.i(TAG, "Invalid drawable path : " + drawablePath);
        }
        return valid;
    }

    private void startLoadFromSharedPreferences() throws JSONException {
        String colors = SkinPreference.getInstance().getUserTheme();
        if (TextUtils.isEmpty(colors)) {
            return;
        }
        JSONArray jsonArray = new JSONArray(colors);
        Slog.i(TAG, "startLoadFromSharedPreferences: " + jsonArray.toString());
        int count = jsonArray.length();
        for (int i = 0; i < count; ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has(KEY_TYPE)) {
                String type = jsonObject.getString(KEY_TYPE);
                if (KEY_TYPE_COLOR.equals(type)) {
                    ColorState state = ColorState.fromJSONObject(jsonObject);
                    if (null != state) {
                        mColorNameStateMap.put(state.colorName, state);
                    }
                } else if (KEY_TYPE_DRAWABLE.equals(type)) {
                    String drawableName = jsonObject.getString(KEY_DRAWABLE_NAME);
                    String drawablePathAndAngle = jsonObject.getString(KEY_DRAWABLE_PATH_AND_ANGLE);
                    if (!TextUtils.isEmpty(drawableName) && !TextUtils.isEmpty(drawablePathAndAngle)) {
                        mDrawablePathAndAngleMap.put(drawableName, drawablePathAndAngle);
                    }
                }
            }
        }
        mColorEmpty = mColorNameStateMap.isEmpty();
        mDrawableEmpty = mDrawablePathAndAngleMap.isEmpty();
    }

    public void apply() {
        JSONArray jsonArray = new JSONArray();
        for (String colorName : mColorNameStateMap.keySet()) {
            ColorState state = mColorNameStateMap.get(colorName);
            if (null != state) {
                try {
                    jsonArray.put(toJSONObject(state).putOpt(KEY_TYPE, KEY_TYPE_COLOR));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (String drawableName : mDrawablePathAndAngleMap.keySet()) {
            JSONObject object = new JSONObject();
            try {
                jsonArray.put(object.putOpt(KEY_TYPE, KEY_TYPE_DRAWABLE)
                        .putOpt(KEY_DRAWABLE_NAME, drawableName)
                        .putOpt(KEY_DRAWABLE_PATH_AND_ANGLE, mDrawablePathAndAngleMap.get(drawableName)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Slog.i(TAG, "Apply user theme: " + jsonArray.toString());
        SkinPreference.getInstance().setUserTheme(jsonArray.toString()).commitEditor();
        SkinCompatManager.getInstance().notifyUpdateSkin();
    }

    public void addColorState(@ColorRes int colorRes, ColorState state) {
        String entry = getEntryName(colorRes, KEY_TYPE_COLOR);
        if (!TextUtils.isEmpty(entry) && null != state) {
            state.colorName = entry;
            mColorNameStateMap.put(entry, state);
            removeColorInCache(colorRes);
            mColorEmpty = false;
        }
    }

    private String getEntryName(int resId, String entryType) {
        Context context = SkinCompatManager.getInstance().getContext();
        String type = context.getResources().getResourceTypeName(resId);
        if (entryType.equalsIgnoreCase(type)) {
            return context.getResources().getResourceEntryName(resId);
        }
        return null;
    }

    private void removeColorInCache(@ColorRes int colorRes) {
        synchronized (mColorCacheLock) {
            mColorCaches.remove(colorRes);
        }
    }

    public void addColorState(@ColorRes int colorRes, String colorDefault) {
        if (!checkColorValid("colorDefault", colorDefault)) {
            return;
        }
        String entry = getEntryName(colorRes, KEY_TYPE_COLOR);
        if (!TextUtils.isEmpty(entry)) {
            mColorNameStateMap.put(entry, new ColorState(entry, colorDefault));
            removeColorInCache(colorRes);
            mColorEmpty = false;
        }
    }

    void removeColorState(String colorName) {
        if (!TextUtils.isEmpty(colorName)) {
            mColorNameStateMap.remove(colorName);
            mColorEmpty = mColorNameStateMap.isEmpty();
        }
    }

    public void removeColorState(@ColorRes int colorRes) {
        String entry = getEntryName(colorRes, KEY_TYPE_COLOR);
        if (!TextUtils.isEmpty(entry)) {
            mColorNameStateMap.remove(entry);
            removeColorInCache(colorRes);
            mColorEmpty = mColorNameStateMap.isEmpty();
        }
    }

    public ColorState getColorState(String colorName) {
        return mColorNameStateMap.get(colorName);
    }

    public ColorState getColorState(@ColorRes int colorRes) {
        String entry = getEntryName(colorRes, KEY_TYPE_COLOR);
        if (!TextUtils.isEmpty(entry)) {
            return mColorNameStateMap.get(entry);
        }
        return null;
    }

    public ColorStateList getColorStateList(@ColorRes int colorRes) {
        ColorStateList colorStateList = getCachedColor(colorRes);
        if (null == colorStateList) {
            String entry = getEntryName(colorRes, KEY_TYPE_COLOR);
            if (!TextUtils.isEmpty(entry)) {
                ColorState state = mColorNameStateMap.get(entry);
                if (null != state) {
                    colorStateList = state.parse();
                    if (null != colorStateList) {
                        addColorToCache(colorRes, colorStateList);
                    }
                }
            }
        }
        return colorStateList;
    }

    private ColorStateList getCachedColor(@ColorRes int colorRes) {
        synchronized (mColorCacheLock) {
            WeakReference<ColorStateList> colorRef = mColorCaches.get(colorRes);
            if (null != colorRef) {
                ColorStateList colorStateList = colorRef.get();
                if (null != colorStateList) {
                    return colorStateList;
                } else {
                    mColorCaches.remove(colorRes);
                }
            }
        }
        return null;
    }

    private void addColorToCache(@ColorRes int colorRes, ColorStateList colorStateList) {
        if (null != colorStateList) {
            synchronized (mColorCacheLock) {
                mColorCaches.put(colorRes, new WeakReference<ColorStateList>(colorStateList));
            }
        }
    }

    public void addDrawablePath(@DrawableRes int drawableRes, String drawablePath) {
        if (!checkPathValid(drawablePath)) {
            return;
        }
        String entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE);
        if (!TextUtils.isEmpty(entry)) {
            int angle = ImageUtils.getImageRotateAngle(drawablePath);
            String drawablePathAndAngle = drawablePath + ":" + String.valueOf(angle);
            mDrawablePathAndAngleMap.put(entry, drawablePathAndAngle);
            removeDrawableInCache(drawableRes);
            mDrawableEmpty = false;
        }
    }

    private void removeDrawableInCache(@DrawableRes int drawableRes) {
        synchronized (mDrawableCacheLock) {
            mDrawableCaches.remove(drawableRes);
        }
    }

    public void addDrawablePath(@DrawableRes int drawableRes, String drawablePath, int angle) {
        if (!TextUtils.isEmpty(drawablePath)) {
            return;
        }
        String entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE);
        if (!TextUtils.isEmpty(entry)) {
            String drawablePathAndAngle = drawablePath + ":" + String.valueOf(angle);
            mDrawablePathAndAngleMap.put(entry, drawablePathAndAngle);
            removeDrawableInCache(drawableRes);
            mDrawableEmpty = false;
        }
    }

    public void removeDrawablePath(@DrawableRes int drawableRes) {
        String entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE);
        if (!TextUtils.isEmpty(entry)) {
            mDrawablePathAndAngleMap.remove(entry);
            removeDrawableInCache(drawableRes);
            mDrawableEmpty = mDrawablePathAndAngleMap.isEmpty();
        }
    }

    public String getDrawablePath(String drawableName) {
        String drawablePathAndAngle = mDrawablePathAndAngleMap.get(drawableName);
        if (!TextUtils.isEmpty(drawablePathAndAngle)) {
            String[] splits = drawablePathAndAngle.split(":");
            return splits[0];
        }
        return "";
    }

    public int getDrawableAngle(String drawableName) {
        String drawablePathAndAngle = mDrawablePathAndAngleMap.get(drawableName);
        if (!TextUtils.isEmpty(drawablePathAndAngle)) {
            String[] splits = drawablePathAndAngle.split(":");
            if (splits.length == 2) {
                return Integer.valueOf(splits[1]);
            }
        }
        return 0;
    }

    public Drawable getDrawable(@DrawableRes int drawableRes) {
        Drawable drawable = getCachedDrawable(drawableRes);
        if (drawable != null) {
            return drawable;
        }
        String entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE);
        if (!TextUtils.isEmpty(entry)) {
            String drawablePathAndAngle = mDrawablePathAndAngleMap.get(entry);
            if (!TextUtils.isEmpty(drawablePathAndAngle)) {
                String[] splits = drawablePathAndAngle.split(":");
                String path = splits[0];
                int angle = 0;
                if (splits.length == 2) {
                    angle = Integer.valueOf(splits[1]);
                }
                if (checkPathValid(path)) {
                    if (angle == 0) {
                        drawable = Drawable.createFromPath(path);
                    } else {
                        Matrix m = new Matrix();
                        m.postRotate(angle);
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight(), m, true);
                        drawable = new BitmapDrawable(null, bitmap);
                    }
                    if (drawable != null) {
                        addDrawableToCache(drawableRes, drawable);
                    }
                }
            }
        }

        return drawable;
    }

    private Drawable getCachedDrawable(@DrawableRes int drawableRes) {
        synchronized (mDrawableCacheLock) {
            WeakReference<Drawable> drawableRef = mDrawableCaches.get(drawableRes);
            if (drawableRef != null) {
                Drawable drawable = drawableRef.get();
                if (drawable != null) {
                    return drawable;
                } else {
                    mDrawableCaches.remove(drawableRes);
                }
            }
        }
        return null;
    }

    private void addDrawableToCache(@DrawableRes int drawableRes, Drawable drawable) {
        if (drawable != null) {
            synchronized (mDrawableCacheLock) {
                mDrawableCaches.put(drawableRes, new WeakReference<>(drawable));
            }
        }
    }

    public void clearColors() {
        mColorNameStateMap.clear();
        clearColorCaches();
        mColorEmpty = true;
        apply();
    }

    public void clearDrawables() {
        mDrawablePathAndAngleMap.clear();
        clearDrawableCaches();
        mDrawableEmpty = true;
        apply();
    }

    boolean isColorEmpty() {
        return mColorEmpty;
    }

    boolean isDrawableEmpty() {
        return mDrawableEmpty;
    }

    void clearCaches() {
        clearColorCaches();
        clearDrawableCaches();
    }

    private void clearColorCaches() {
        synchronized (mColorCacheLock) {
            mColorCaches.clear();
        }
    }

    private void clearDrawableCaches() {
        synchronized (mDrawableCacheLock) {
            mDrawableCaches.clear();
        }
    }
}
