package com.yky.jhsdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.renrui.libraries.util.mHttpClient.GetGsonInstance;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.qq.e.comm.managers.GDTADManager;
import com.yky.jhsdk.activities.AdActivity;
import com.yky.jhsdk.activities.account.LoginActivity;
import com.yky.jhsdk.activities.account.RegistrationActivity;
import com.yky.jhsdk.events.EventType;
import com.yky.jhsdk.models.AdInfo;
import com.yky.jhsdk.utils.AppUtils;
import com.yky.jhsdk.utils.EditSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppManager {

    private static final String TAG = "AppManager";

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setupParams(String s) {
        try {
            JSONObject dRoot = new JSONObject(s);
            JSONArray d = dRoot.getJSONArray("data");
            for (int i = 0; i < d.length(); i++) {
                JSONObject info = (JSONObject) d.get(i);
                String str = info.toString();
                AdInfo adInfo = GetGsonInstance().fromJson(str, AdInfo.class);
                EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_APP_ID, adInfo.pid);
                if ("穿山甲".equals(adInfo.platform)) {
                    EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_BU_APPID, adInfo.app_id);
                    switch (adInfo.ad_type) {
                        case 1:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_BU_SLOTID_SPLASH, adInfo.ad_id);
                            break;
                        case 2:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_BU_SLOTID_BANNER, adInfo.ad_id);
                            break;
                        case 3:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_BU_SLOTID_FLOW, adInfo.ad_id);
                            break;
                        case 4:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_BU_SLOTID_REWAED, adInfo.ad_id);
                            break;
                        default:
                            break;
                    }
                } else if ("优量汇".equals(adInfo.platform)) {
                    EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_QQ_APPID, adInfo.app_id);
                    switch (adInfo.ad_type) {
                        case 1:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_QQ_SLOTID_SPLASH, adInfo.ad_id);
                            break;
                        case 2:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_QQ_SLOTID_BANNER, adInfo.ad_id);
                            break;
                        case 3:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_QQ_SLOTID_FLOW, adInfo.ad_id);
                            break;
                        case 4:
                            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_QQ_SLOTID_REWAED, adInfo.ad_id);
                            break;
                        default:
                            break;
                    }
                } else {
                    Log.i(TAG, "setResponse: empty...");
                }
            }
            initAdParams();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initAdParams() {
        String buAppId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_BU_APPID);
        if ("".equals(buAppId)) {
            Log.i(TAG, "onCreate: bu app id is empty.");
            buAppId = "5255903";
        } else {
            Log.i(TAG, "onCreate: bu app id = " + buAppId);
            String packageName = AppUtils.getPackageName(mContext);
            TTAdSdk.init(mContext, new TTAdConfig.Builder()
                    .appId(buAppId)
                    .useTextureView(true)
                    .appName(packageName)
                    .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                    .allowShowNotify(true)
                    .debug(false)
                    .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI)
                    .supportMultiProcess(false)
                    .asyncInit(true)
                    .build());
        }

        String qqAppId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_QQ_APPID);
        if ("".equals(qqAppId)) {
            Log.i(TAG, "onCreate: qq app id is empty.");
            qqAppId = "1101152570";
        } else {
            Log.i(TAG, "onCreate: qq app id = " + qqAppId);
            GDTADManager.getInstance().initWith(mContext, qqAppId);
        }
    }

    public static void popup(String strNavi) {
        Intent intent;
        if (strNavi == EventType.NAVI_TO_LOGIN) {
            intent = new Intent(mContext, LoginActivity.class);
        } else if (strNavi == EventType.NAVI_TO_REG) {
            intent = new Intent(mContext, RegistrationActivity.class);
        } else {
            String phoneNum = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_PHONE_NUM);
            if (phoneNum.equals("")) {
                intent = new Intent(mContext, LoginActivity.class);
            } else {
                intent = new Intent(mContext, AdActivity.class);
            }
            intent.putExtra("stat", strNavi);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public static String getBUAppID() {
        return EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_BU_APPID);
    }

    public static String getQQAppID() {
        return EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_QQ_APPID);
    }

}
