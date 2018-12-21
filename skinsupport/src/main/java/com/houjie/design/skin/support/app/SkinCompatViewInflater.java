package com.houjie.design.skin.support.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.TintContextWrapper;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.houjie.design.skin.R;
import com.houjie.design.skin.support.SkinCompatManager;
import com.houjie.design.skin.support.utils.Slog;

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

    /**
     * Allows us to emulate the {@code android:theme} attribute for devices before L.
     */
    private static Context themifyContext(Context context, AttributeSet attrs,
                                          boolean useAndroidTheme, boolean useAppTheme) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View, 0, 0);
        int themeId = 0;
        if (useAndroidTheme) {
            themeId = a.getResourceId(R.styleable.View_android_theme, 0);
        }
        if (useAppTheme && 0 == themeId) {
            // ...if that didn't work, try reading app:theme (for legacy reasons) if enabled
            themeId = a.getResourceId(R.styleable.View_theme, 0);

            if (0 != themeId) {
                Slog.i(TAG, "app:theme is now deprecated. "
                        + "Please move to using android:theme instead.");
            }
        }
        a.recycle();

        if (0 != themeId && (!(context instanceof ContextThemeWrapper)
                || ((android.support.v7.view.ContextThemeWrapper) context).getThemeResId() != themeId)) {
            // If the context isn't a ContextThemeWrapper, or it is but does not have
            // the same theme as we need, wrap it in a new wrapper
            context = new ContextThemeWrapper(context, themeId);
        }
        return context;
    }

    private View createViewFromHackInflater(Context context, String name, AttributeSet attrs) {
        View view = null;
        for (SkinLayoutInflater inflater : SkinCompatManager.getInstance().getHookInflaters()) {
            view = inflater.createView(context, name, attrs);
            if (null != view) {
                break;
            }
        }
        return view;
    }

    private View createViewFromFV(Context context, String name, AttributeSet attrs) {
        View view = null;
        if (name.contains(".")) {
            return null;
        }
        switch (name) {
            case "View":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;
            case "":
                break;

        }
        return view;
    }
}
