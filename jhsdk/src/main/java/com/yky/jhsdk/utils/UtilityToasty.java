package com.yky.jhsdk.utils;

import android.widget.Toast;

import com.renrui.libraries.util.UtilitySecurity;
import com.yky.jhsdk.AppManager;

import es.dmoral.toasty.Toasty;

public class UtilityToasty {

    public static void success(String content, boolean showIcon) {
        if (UtilitySecurity.isEmpty(content)) {
            return;
        }

        Toasty.success(AppManager.getContext(), content, Toast.LENGTH_SHORT, showIcon).show();
    }

    public static void success(String content) {
        success(content, true);
    }

    public static void success(int strSourceId) {
        success(AppManager.getContext().getString(strSourceId), true);
    }

    public static void warning(String content, boolean showIcon) {
        if (UtilitySecurity.isEmpty(content)) {
            return;
        }

        Toasty.warning(AppManager.getContext(), content, Toast.LENGTH_SHORT, showIcon).show();
    }

    public static void warning(String content) {
        warning(content, true);
    }

    public static void warning(int strSourceId) {
        warning(AppManager.getContext().getString(strSourceId), true);
    }

    public static void error(String content, boolean showIcon) {
        if (UtilitySecurity.isEmpty(content)) {
            return;
        }

        Toasty.error(AppManager.getContext(), content, Toast.LENGTH_SHORT, showIcon).show();
    }

    public static void error(String content) {
        error(content, true);
    }

    public static void error(int strSourceId) {
        error(AppManager.getContext().getString(strSourceId), true);
    }
}
