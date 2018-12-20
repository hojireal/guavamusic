package com.houjie.design.skin.support.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.text.TextUtils;

import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.utils.SkinPreference;
import com.houjie.design.skin.support.utils.Slog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;

import static com.houjie.design.skin.support.content.res.ColorState.checkColorValid;

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
                        mColorNameStateMap.put(state.colorName, state)
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

    public static SkinCompatUserThemeManager getInstance() {
        return INSTANCE;
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

    }
}
