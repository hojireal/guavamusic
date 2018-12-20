package com.houjie.design.skin.support.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.text.TextUtils;

import com.houjie.design.skin.support.exception.SkinCompatException;
import com.houjie.design.skin.support.utils.Slog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class ColorState {
    private static final String TAG = "ColorState";
    boolean onlyDefaultColor;
    String colorName;
    String colorWindowFocused;
    String colorSelected;
    String colorFocused;
    String colorEnabled;
    String colorPressed;
    String colorChecked;
    String colorActivated;
    String colorAccelerated;
    String colorHovered;
    String colorDragCanAccept;
    String colorDragHovered;
    String colorDefault;
    ColorState(String colorWindowFocused, String colorSelected, String colorFocused,
               String colorEnabled, String colorPressed, String colorChecked, String colorActivated,
               String colorAccelerated, String colorHovered, String colorDragCanAccept,
               String colorDragHovered, String colorDefault) {
        this.colorWindowFocused = colorWindowFocused;
        this.colorSelected = colorSelected;
        this.colorFocused = colorFocused;
        this.colorEnabled = colorEnabled;
        this.colorPressed = colorPressed;
        this.colorChecked = colorChecked;
        this.colorActivated = colorActivated;
        this.colorAccelerated = colorAccelerated;
        this.colorHovered = colorHovered;
        this.colorDragCanAccept = colorDragCanAccept;
        this.colorDragHovered = colorDragHovered;
        this.colorDefault = colorDefault;
        this.onlyDefaultColor = TextUtils.isEmpty(colorWindowFocused)
                && TextUtils.isEmpty(colorSelected)
                && TextUtils.isEmpty(colorFocused)
                && TextUtils.isEmpty(colorEnabled)
                && TextUtils.isEmpty(colorPressed)
                && TextUtils.isEmpty(colorChecked)
                && TextUtils.isEmpty(colorActivated)
                && TextUtils.isEmpty(colorAccelerated)
                && TextUtils.isEmpty(colorHovered)
                && TextUtils.isEmpty(colorDragCanAccept)
                && TextUtils.isEmpty(colorDragHovered);
        if (onlyDefaultColor) {
            if (!colorDefault.startsWith("#")) {
                throw new SkinCompatException("Default color cannot be a reference, when only default color is available!");
            }
        }
    }

    ColorState(String colorName, String colorDefault) {
        this.colorName = colorName;
        this.colorDefault = colorDefault;
        this.onlyDefaultColor = true;
        if (!colorDefault.startsWith("#")) {
            throw new SkinCompatException("Default color cannot be a reference, when only default color is available!");
        }
    }

    public boolean isOnlyDefaultColor() {
        return onlyDefaultColor;
    }

    public String getColorName() {
        return colorName;
    }

    public String getColorWindowFocused() {
        return colorWindowFocused;
    }

    public String getColorSelected() {
        return colorSelected;
    }

    public String getColorFocused() {
        return colorFocused;
    }

    public String getColorEnabled() {
        return colorEnabled;
    }

    public String getColorPressed() {
        return colorPressed;
    }

    public String getColorChecked() {
        return colorChecked;
    }

    public String getColorActivated() {
        return colorActivated;
    }

    public String getColorAccelerated() {
        return colorAccelerated;
    }

    public String getColorHovered() {
        return colorHovered;
    }

    public String getColorDragCanAccept() {
        return colorDragCanAccept;
    }

    public String getColorDragHovered() {
        return colorDragHovered;
    }

    public String getColorDefault() {
        return colorDefault;
    }

    ColorStateList parse() {
        if (onlyDefaultColor) {
            int defaultColor = Color.parseColor(colorDefault);
            return ColorStateList.valueOf(defaultColor);
        }
        return parseAll();
    }

    private ColorStateList parseAll() {
        int stateColorCount = 0;
        List<int[]> stateSetList = new ArrayList<>();
        List<Integer> stateColorList = new ArrayList<>();
        if (parseItem(colorWindowFocused, SkinCompatThemeUtils.WINDOW_FOCUSED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorSelected, SkinCompatThemeUtils.SELECTED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorFocused, SkinCompatThemeUtils.FOCUSED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorEnabled, SkinCompatThemeUtils.ENABLED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorPressed, SkinCompatThemeUtils.PRESSED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorChecked, SkinCompatThemeUtils.CHECKED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorActivated, SkinCompatThemeUtils.ACTIVATED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorAccelerated, SkinCompatThemeUtils.ACCELERATED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorHovered, SkinCompatThemeUtils.HOVERED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorDragCanAccept, SkinCompatThemeUtils.DRAG_CAN_ACCEPT_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorDragHovered, SkinCompatThemeUtils.DRAG_HOVERED_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;
        if (parseItem(colorDefault, SkinCompatThemeUtils.EMPTY_STATE_SET,
                stateSetList, stateColorList)) stateColorCount++;

        try {
            final int[][] states = new int[stateColorCount][];
            final int[] colors = new int[stateColorCount];
            for (int index = 0; index < stateColorCount; index++) {
                states[index] = stateSetList.get(index);
                colors[index] = stateColorList.get(index);
            }
            return new ColorStateList(states, colors);
        } catch (Exception e) {
            if (Slog.DEBUG) {
                Slog.i(TAG, colorName + " parse failure.");
            }
            SkinCompatUserThemeManager.getInstance().removeColorState(colorName);
            return null;
        }
    }

    private boolean parseItem(String colorItem, int[] colorAttrSet, List<int[]> stateSetList, List<Integer> stateColorList) {
        if (!TextUtils.isEmpty(colorItem)) {
            try {
                String colorStr = getColorStr(colorItem);
                if (!TextUtils.isEmpty(colorStr)) {
                    int colorInt = Color.parseColor(colorStr);
                    stateSetList.add(colorAttrSet);
                    stateColorList.add(colorInt);
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    private String getColorStr(String colorName) {
        if (colorName.startsWith("#")) {
            return colorName;
        } else {
            ColorState stateRef = SkinCompatUserThemeManager.getInstance().getColorState(colorName);
            if (stateRef != null) {
                if (stateRef.isOnlyDefaultColor()) {
                    return stateRef.colorDefault;
                } else {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, colorName + " cannot reference " + stateRef.colorName);
                    }
                }
            }
        }
        return null;
    }

    static boolean checkColorValid(String name, String color) {
        // 不为空
        boolean colorValid = !TextUtils.isEmpty(color)
                // 不以#开始，说明是引用其他颜色值 或者以#开始，则长度必须为7或9
                && (!color.startsWith("#") || color.length() == 7 || color.length() == 9);
        if (!colorValid) {
            Slog.i(TAG, "Invalid color -> " + name + ": " + color);
        }
        return colorValid;
    }

    static JSONObject toJSONObject(ColorState state) throws JSONException {
        JSONObject object = new JSONObject();
        if (state.onlyDefaultColor) {
            object.putOpt("colorName", state.colorName)
                    .putOpt("colorDefault", state.colorDefault)
                    .putOpt("onlyDefaultColor", state.onlyDefaultColor);
        } else {
            object.putOpt("colorName", state.colorName)
                    .putOpt("colorWindowFocused", state.colorWindowFocused)
                    .putOpt("colorSelected", state.colorSelected)
                    .putOpt("colorFocused", state.colorFocused)
                    .putOpt("colorEnabled", state.colorEnabled)
                    .putOpt("colorPressed", state.colorPressed)
                    .putOpt("colorChecked", state.colorChecked)
                    .putOpt("colorActivated", state.colorActivated)
                    .putOpt("colorAccelerated", state.colorAccelerated)
                    .putOpt("colorHovered", state.colorHovered)
                    .putOpt("colorDragCanAccept", state.colorDragCanAccept)
                    .putOpt("colorDragHovered", state.colorDragHovered)
                    .putOpt("colorDefault", state.colorDefault)
                    .putOpt("onlyDefaultColor", state.onlyDefaultColor);
        }
        return object;
    }

    static ColorState fromJSONObject(JSONObject jsonObject) {
        if (jsonObject.has("colorName")
                && jsonObject.has("colorDefault")
                && jsonObject.has("onlyDefaultColor")) {
            try {
                boolean onlyDefaultColor = jsonObject.getBoolean("onlyDefaultColor");
                String colorName = jsonObject.getString("colorName");
                String colorDefault = jsonObject.getString("colorDefault");
                if (onlyDefaultColor) {
                    return new ColorState(colorName, colorDefault);
                } else {
                    ColorBuilder builder = new ColorBuilder();
                    builder.setColorDefault(colorDefault);
                    if (jsonObject.has("colorWindowFocused")) {
                        builder.setColorWindowFocused(jsonObject.getString("colorWindowFocused"));
                    }
                    if (jsonObject.has("colorSelected")) {
                        builder.setColorSelected(jsonObject.getString("colorSelected"));
                    }
                    if (jsonObject.has("colorFocused")) {
                        builder.setColorFocused(jsonObject.getString("colorFocused"));
                    }
                    if (jsonObject.has("colorEnabled")) {
                        builder.setColorEnabled(jsonObject.getString("colorEnabled"));
                    }
                    if (jsonObject.has("colorPressed")) {
                        builder.setColorPressed(jsonObject.getString("colorPressed"));
                    }
                    if (jsonObject.has("colorChecked")) {
                        builder.setColorChecked(jsonObject.getString("colorChecked"));
                    }
                    if (jsonObject.has("colorActivated")) {
                        builder.setColorActivated(jsonObject.getString("colorActivated"));
                    }
                    if (jsonObject.has("colorAccelerated")) {
                        builder.setColorAccelerated(jsonObject.getString("colorAccelerated"));
                    }
                    if (jsonObject.has("colorHovered")) {
                        builder.setColorHovered(jsonObject.getString("colorHovered"));
                    }
                    if (jsonObject.has("colorDragCanAccept")) {
                        builder.setColorDragCanAccept(jsonObject.getString("colorDragCanAccept"));
                    }
                    if (jsonObject.has("colorDragHovered")) {
                        builder.setColorDragHovered(jsonObject.getString("colorDragHovered"));
                    }
                    ColorState state = builder.build();
                    state.colorName = colorName;
                    return state;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class ColorBuilder {
        String colorWindowFocused;
        String colorSelected;
        String colorFocused;
        String colorEnabled;
        String colorPressed;
        String colorChecked;
        String colorActivated;
        String colorAccelerated;
        String colorHovered;
        String colorDragCanAccept;
        String colorDragHovered;
        String colorDefault;

        public ColorBuilder() {
        }

        public ColorBuilder(ColorState state) {
            colorWindowFocused = state.colorWindowFocused;
            colorSelected = state.colorSelected;
            colorFocused = state.colorFocused;
            colorEnabled = state.colorEnabled;
            colorPressed = state.colorPressed;
            colorChecked = state.colorChecked;
            colorActivated = state.colorActivated;
            colorAccelerated = state.colorAccelerated;
            colorHovered = state.colorHovered;
            colorDragCanAccept = state.colorDragCanAccept;
            colorDragHovered = state.colorDragHovered;
            colorDefault = state.colorDefault;
        }

        public ColorBuilder setColorWindowFocused(String colorWindowFocused) {
            if (checkColorValid("colorWindowFocused", colorWindowFocused)) {
                this.colorWindowFocused = colorWindowFocused;
            }
            return this;

        }

        public ColorBuilder setColorWindowFocused(Context context, @ColorRes int colorRes) {
            this.colorWindowFocused = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorSelected(String colorSelected) {
            if (checkColorValid("colorSelected", colorSelected)) {
                this.colorSelected = colorSelected;
            }
            return this;
        }

        public ColorBuilder setColorSelected(Context context, @ColorRes int colorRes) {
            this.colorSelected = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorFocused(String colorFocused) {
            if (checkColorValid("colorFocused", colorFocused)) {
                this.colorFocused = colorFocused;
            }
            return this;
        }

        public ColorBuilder setColorFocused(Context context, @ColorRes int colorRes) {
            this.colorFocused = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorEnabled(String colorEnabled) {
            if (checkColorValid("colorEnabled", colorEnabled)) {
                this.colorEnabled = colorEnabled;
            }
            return this;
        }

        public ColorBuilder setColorEnabled(Context context, @ColorRes int colorRes) {
            this.colorEnabled = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorChecked(String colorChecked) {
            if (checkColorValid("colorChecked", colorChecked)) {
                this.colorChecked = colorChecked;
            }
            return this;
        }

        public ColorBuilder setColorChecked(Context context, @ColorRes int colorRes) {
            this.colorChecked = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorPressed(String colorPressed) {
            if (checkColorValid("colorPressed", colorPressed)) {
                this.colorPressed = colorPressed;
            }
            return this;
        }

        public ColorBuilder setColorPressed(Context context, @ColorRes int colorRes) {
            this.colorPressed = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorActivated(String colorActivated) {
            if (checkColorValid("colorActivated", colorActivated)) {
                this.colorActivated = colorActivated;
            }
            return this;
        }

        public ColorBuilder setColorActivated(Context context, @ColorRes int colorRes) {
            this.colorActivated = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorAccelerated(String colorAccelerated) {
            if (checkColorValid("colorAccelerated", colorAccelerated)) {
                this.colorAccelerated = colorAccelerated;
            }
            return this;
        }

        public ColorBuilder setColorAccelerated(Context context, @ColorRes int colorRes) {
            this.colorAccelerated = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorHovered(String colorHovered) {
            if (checkColorValid("colorHovered", colorHovered)) {
                this.colorHovered = colorHovered;
            }
            return this;
        }

        public ColorBuilder setColorHovered(Context context, @ColorRes int colorRes) {
            this.colorHovered = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorDragCanAccept(String colorDragCanAccept) {
            if (checkColorValid("colorDragCanAccept", colorDragCanAccept)) {
                this.colorDragCanAccept = colorDragCanAccept;
            }
            return this;
        }

        public ColorBuilder setColorDragCanAccept(Context context, @ColorRes int colorRes) {
            this.colorDragCanAccept = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorDragHovered(String colorDragHovered) {
            if (checkColorValid("colorDragHovered", colorDragHovered)) {
                this.colorDragHovered = colorDragHovered;
            }
            return this;
        }

        public ColorBuilder setColorDragHovered(Context context, @ColorRes int colorRes) {
            this.colorDragHovered = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorBuilder setColorDefault(String colorDefault) {
            if (checkColorValid("colorDefault", colorDefault)) {
                this.colorDefault = colorDefault;
            }
            return this;
        }

        public ColorBuilder setColorDefault(Context context, @ColorRes int colorRes) {
            this.colorDefault = context.getResources().getResourceEntryName(colorRes);
            return this;
        }

        public ColorState build() {
            if (TextUtils.isEmpty(colorDefault)) {
                throw new SkinCompatException("Default color can not empty!");
            }
            return new ColorState(colorWindowFocused, colorSelected, colorFocused,
                    colorEnabled, colorPressed, colorChecked, colorActivated, colorAccelerated,
                    colorHovered, colorDragCanAccept, colorDragHovered, colorDefault);
        }
    }
}
