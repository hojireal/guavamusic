package com.houjie.design.skin.support.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.TintContextWrapper;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.Map;

public class SkinCompatViewInflater {
    private static final String TAG = "SkinCompatViewInflater";
    private static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    private static final int[] sOnClickAttrs = new int[]{android.R.attr.onClick};

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();

    private final Object[] mConstructorArgs = new Object[2];

    public final View createView(View parent, final String name, @NonNull Context context,
                                 @NonNull AttributeSet attrs, boolean inheritContext,
                                 boolean readAndroidTheme, boolean readAppTheme, boolean wrapContext) {
        final Context originalContext = context;

        // We can emulate Lollipop's android:theme attribute propagating down the view hierarchy
        // by using the parent's context
        if (inheritContext && parent != null) {
            context = parent.getContext();
        }
        if (readAndroidTheme || readAppTheme) {
            // We then apply the theme on the context, if specified
            context = themifyContext(context, attrs, readAndroidTheme, readAppTheme);
        }
        if (wrapContext) {
            context = TintContextWrapper.wrap(context);
        }

        View view = createViewFromHackInflater(context, name, attrs);

        // We need to 'inject' our tint aware Views in place of the standard framework versions
        if (view == null) {
            view = createViewFromFV(context, name, attrs);
        }

        if (view == null) {
            view = createViewFromV7(context, name, attrs);
        }

        if (view == null) {
            view = createViewFromInflater(context, name, attrs);
        }

        if (view == null) {
            view = createViewFromTag(context, name, attrs);
        }

        if (view != null) {
            // If we have created a view, check it's android:onClick
            checkOnClickListener(view, attrs);
        }

        return view;
    }
}
