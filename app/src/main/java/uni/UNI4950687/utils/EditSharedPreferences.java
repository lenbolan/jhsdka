package uni.UNI4950687.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import uni.UNI4950687.MyApplication;

public class EditSharedPreferences {

    public static final String AD_STATE = "ad_state";

    public static final String AD_APP_ID = "app_id";

    public static final String AD_BU_APPID = "ad_bu_appid";
    public static final String AD_BU_SLOTID_SPLASH = "ad_bu_slotid_splash";
    public static final String AD_BU_SLOTID_REWAED = "ad_bu_slotid_reward";
    public static final String AD_BU_SLOTID_BANNER = "ad_bu_slotid_banner";
    public static final String AD_BU_SLOTID_FLOW = "ad_bu_slotid_flow";
    public static final String AD_BU_DATE = "ad_bu_date";
    public static final String AD_BU_STATE = "ad_bu_state";

    public static final String AD_QQ_APPID = "ad_qq_appid";
    public static final String AD_QQ_SLOTID_SPLASH = "ad_qq_slotid_splash";
    public static final String AD_QQ_SLOTID_REWAED = "ad_qq_slotid_reward";
    public static final String AD_QQ_SLOTID_BANNER = "ad_qq_slotid_banner";
    public static final String AD_QQ_SLOTID_FLOW = "ad_qq_slotid_flow";
    public static final String AD_QQ_DATE = "ad_qq_date";
    public static final String AD_QQ_STATE = "ad_qq_state";

    public static final String AD_RANDOM_TIME_MAX = "ad_random_time_max";
    public static final String AD_RANDOM_TIME_MIN = "ad_random_time_min";
    public static final String AD_REWARD_LIMIT_WATCH_TIME = "ad_reward_limit_watch_time";
    public static final String AD_REWARD_LIMIT_CLICK_TIME = "ad_reward_limit_click_time";
    public static final String AD_BANNER_LIMIT_WATCH_TIME = "ad_banner_limit_watch_time";
    public static final String AD_BANNER_LIMIT_CLICK_TIME = "ad_banner_limit_click_time";
    public static final String AD_FLOW_LIMIT_WATCH_TIME = "ad_flow_limit_watch_time";
    public static final String AD_FLOW_LIMIT_CLICK_TIME = "ad_flow_limit_click_time";
    public static final String AD_SPLASH_LIMIT_WATCH_TIME = "ad_splash_limit_watch_time";
    public static final String AD_SPLASH_LIMIT_CLICK_TIME = "ad_splash_limit_click_time";

    public static final String AD_USER_ID = "ad_user_id";
    public static final String AD_PHONE_NUM = "ad_phone_num";
    public static final String AD_TOKEN = "ad_token";

    private static SharedPreferences mySharedPreferences = null;

    public static SharedPreferences getSharedPreferencesInstance() {
        if (mySharedPreferences == null && null != MyApplication.getAppContext()) {
            mySharedPreferences = MyApplication.getAppContext().getSharedPreferences("app_info", Activity.MODE_PRIVATE);
        }

        return mySharedPreferences;
    }

    public static void writeBooleanToConfig(String key, boolean value) {
        getSharedPreferencesInstance().edit().putBoolean(key, value).apply();
    }

    public static boolean readBooleanFromConfig(String key, boolean defaultBoolean) {
        boolean defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getBoolean(key, defaultBoolean);
        } catch (Exception e) {
            defaultValue = false;
        }
        return defaultValue;
    }

    public static void writeStringToConfig(String key, String value) {
        getSharedPreferencesInstance().edit().putString(key, value).apply();
    }

    public static String readStringFromConfig(String key) {
        String defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getString(key, "");
        } catch (Exception e) {
            defaultValue = "";
        }
        return defaultValue;
    }

    public static void writeLongToConfig(String key, long value) {
        getSharedPreferencesInstance().edit().putLong(key, value).apply();
    }

    public static long readLongFromConfig(String key) {
        long defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getLong(key, 0);
        } catch (Exception e) {
            defaultValue = 0;
        }
        return defaultValue;
    }

    public static void writeIntToConfig(String key, int value) {
        getSharedPreferencesInstance().edit().putInt(key, value).apply();
    }

    public static int readIntFromConfig(String key) {
        return readIntFromConfig(key, 0);
    }

    public static int readIntFromConfig(String key, int defaultInt) {
        int defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getInt(key, defaultInt);
        } catch (Exception e) {
            defaultValue = 0;
        }
        return defaultValue;
    }

}
