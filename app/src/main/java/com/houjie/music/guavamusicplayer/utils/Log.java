package com.houjie.music.guavamusicplayer.utils;

public class Log {
    public static boolean DEBUG = false;
    private static final String TAG_PREFIX = "HJMP_";
    private static final int TAG_PREFIX_LENGTH = TAG_PREFIX.length();
    private static final int MAX_TAG_LENGTH = 23;

    public static String makeLogTag(String tag) {
        if (tag.length() > MAX_TAG_LENGTH - TAG_PREFIX_LENGTH) {
            return TAG_PREFIX + tag.substring(0, MAX_TAG_LENGTH - TAG_PREFIX_LENGTH - 1);
        }
        return TAG_PREFIX + tag;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void v(String tag, Object... messages) {
        log(tag, android.util.Log.VERBOSE, null, messages);
    }

    public static void d(String tag, Object... messages) {
        log(tag, android.util.Log.DEBUG, null, messages);
    }

    public static void i(String tag, Object... messages) {
        log(tag, android.util.Log.INFO, null, messages);
    }

    public static void w(String tag, Object... messages) {
        log(tag, android.util.Log.WARN, null, messages);
    }

    public static void w(String tag, Throwable t, Object... messages) {
        log(tag, android.util.Log.WARN, t, messages);
    }

    public static void e(String tag, Object... messages) {
        log(tag, android.util.Log.ERROR, null, messages);
    }

    public static void e(String tag, Throwable t, Object... messages) {
        log(tag, android.util.Log.ERROR, t, messages);
    }

    private static void log(String tag, int level, Throwable t, Object... messages) {
        if (!android.util.Log.isLoggable(tag, level) && !DEBUG) {
            return;
        }

        String message;
        if (null == t && null != messages && 1 == messages.length) {
            message = messages[0].toString();
        } else {
            StringBuilder sb = new StringBuilder();
            if (null != messages) {
                for (Object m : messages) {
                    sb.append(m.toString());
                }
            }

            if (null != t) {
                sb.append("\n").append(android.util.Log.getStackTraceString(t));
            }
            message = sb.toString();
        }
        android.util.Log.println(level, tag, message);
    }
}
