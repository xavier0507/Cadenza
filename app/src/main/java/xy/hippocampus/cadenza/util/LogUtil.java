package xy.hippocampus.cadenza.util;

import android.util.Log;

import xy.hippocampus.cadenza.BuildConfig;

/**
 * Created by Xavier Yin on 6/11/17.
 */

public class LogUtil {
    private static boolean enable = true;

    private String tag;

    private LogUtil(String tag) {
        this.tag = tag;
    }

    public static synchronized LogUtil getInstance(Class<?> clazz) {
        String tag = ">> " + clazz.getName();

        return new LogUtil(tag);
    }

    public String LogUtil() {
        return tag;
    }

    public void v(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(this.tag, message);
        }
    }

    public void v(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.v(this.tag, message, t);
        }
    }

    public void d(String message) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                if (message.length() > 4000) {
                    Log.d(this.tag, message.substring(0, 4000));
                    d(message.substring(4000));
                } else {
                    Log.d(this.tag, message);
                }
            }
        }
    }

    public void d(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.d(this.tag, message, t);
            }
        }
    }

    public void i(String message) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.i(this.tag, message);
            }
        }
    }

    public void i(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.i(this.tag, message, t);
            }
        }
    }

    public void w(String message) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.w(this.tag, message);
            }
        }
    }

    public void w(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.w(this.tag, message, t);
            }
        }
    }

    public void e(String message) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.e(this.tag, message);
            }
        }
    }

    public void e(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (enable) {
                Log.e(this.tag, message, t);
            }
        }
    }
}
