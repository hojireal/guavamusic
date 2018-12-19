package com.houjie.design.skin.support.content.res;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;

import com.houjie.design.skin.support.exception.SkinCompatException;
import com.houjie.design.skin.support.utils.Slog;

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
            SkinCompatUserThemeManager.get().removeColorState(colorName);
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
            ColorState stateRef = SkinCompatUserThemeManager.get().getColorState(colorName);
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
        if (Slog.DEBUG && !colorValid) {
            Slog.i(TAG, "Invalid color -> " + name + ": " + color);
        }
        return colorValid;
    }


}
