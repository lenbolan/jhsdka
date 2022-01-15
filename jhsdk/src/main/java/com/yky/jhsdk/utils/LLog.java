package com.yky.jhsdk.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yky.jhsdk.config.BuildSettings;


public class LLog {

    public final static void e(String tag, String msg, Throwable tr) {
        if (BuildSettings.isDebug)
            Log.e(tag, msg, tr);
    }

    public final static void e(String tag, String msg) {
        if (BuildSettings.isDebug)
            Log.e(tag, msg);
    }

    public final static void e(String msg) {
        if (BuildSettings.isDebug)
            Log.e("", msg);
    }

    public final static void e(Throwable tr) {
        if (BuildSettings.isDebug)
            Log.e("", "", tr);
    }

    public final static void d(String tag, String msg) {
        if (BuildSettings.isDebug)
            Log.d(tag, msg);
    }

    public final static void d(String msg) {
        if (BuildSettings.isDebug)
            Log.d("", msg);
    }

    public final static void d(Throwable tr) {
        if (BuildSettings.isDebug)
            Log.d("", "", tr);
    }

    public final static void makeToast(Context context, CharSequence text, int duration) {
        if (BuildSettings.isDebug)
            Toast.makeText(context, text, duration).show();
    }

}
