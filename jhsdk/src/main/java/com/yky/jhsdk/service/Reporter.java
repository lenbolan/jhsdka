package com.yky.jhsdk.service;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.renrui.libraries.interfaces.IHttpRequestCancelInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.mHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;

import cz.msebera.android.httpclient.Header;

import com.yky.jhsdk.AppManager;
import com.yky.jhsdk.models.ReportModel;
import com.yky.jhsdk.models.enums.AdAction;
import com.yky.jhsdk.models.enums.AdPlatform;
import com.yky.jhsdk.models.enums.AdType;
import com.yky.jhsdk.models.httpModel.InterFaceBaseHttpModel;
import com.yky.jhsdk.models.httpModel.ad.ReportActionHttpModel;
import com.yky.jhsdk.models.httpModel.ad.ReportBaseInfoHttpModel;
import com.yky.jhsdk.models.httpModel.ad.ReportClickHttpModel;
import com.yky.jhsdk.models.httpModel.ad.ReportErrorHttpModel;
import com.yky.jhsdk.utils.EditSharedPreferences;
import com.yky.jhsdk.utils.LLog;
import com.yky.jhsdk.utils.Utility;

public class Reporter {

    private static final String TAG = "Reporter";
    private volatile static Reporter reporter = null;

    private int curAdIndex = 0;

    public static Reporter getInstance() {
        if (null == reporter) {
            synchronized (Reporter.class) {
                if (null == reporter) {
                    reporter = new Reporter();
                }
            }
        }
        return reporter;
    }

    private ReportModel model = null;

    private Reporter() {
        model = new ReportModel();
        model.init();
    }

    public void rAdInit(int uid, String phoneNum, String token) {
        model.uid = uid;
        model.phoneNum = phoneNum;
        model.token = token;

        EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_USER_ID, uid);
        EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_PHONE_NUM, phoneNum);
        EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_TOKEN, token);
    }

    private ReportBaseInfoHttpModel initBaseModel(ReportBaseInfoHttpModel model) {
        model.uid = this.model.uid;
        model.username = this.model.phoneNum;
        model.mobile_model = this.model.phoneType;
        model.idfa = this.model.idfa;
        model.operator = this.model.netOperator;
        model.net_state = this.model.netState;
        model.ip = this.model.ipAddr;
        model.sys_version = this.model.sysVersion;
        model.app_mid = this.model.serverAppId+"";
        model.app_name = this.model.appName;
        model.bundle_id = this.model.bundleId;
        model.app_version = this.model.appVersion;
        model.token = this.model.token;
        return model;
    }

    private int getUserId() {
        int userId = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_USER_ID);
        return userId;
    }

    /***
     * 创建广告ID
     * @param adType 开屏=1，banner=2，信息流=3，激励视频=4
     * @return
     */
    public String createAdId(int adType) {
        curAdIndex += 1;
        long createTime = Utility.getTimestamp();
        int uid = getUserId();
        String random = Utility.getRandomString(6);
        String curAdId = uid + "_" + adType + "_" + curAdIndex + "_" + createTime + "_" + random;
        return curAdId;
    }

    public void rAdBaseInfo() {
        ReportBaseInfoHttpModel model = new ReportBaseInfoHttpModel();
        initBaseModel(model);

        baseReq(model);
    }

    public void rAdOpen(AdPlatform adPlat, AdType adType, String adId) {
        ReportActionHttpModel model = new ReportActionHttpModel();
        initBaseModel(model);
        long curTime = Utility.getTimestamp();
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action = AdAction.AdActionOpen.toString();
        model.action_time = curTime;
        model.open_time = curTime;

        baseReq(model);
    }

    public void rAdStart(AdPlatform adPlat, AdType adType, String adId, long loadedTime) {
        ReportActionHttpModel model = new ReportActionHttpModel();
        initBaseModel(model);
        long curTime = Utility.getTimestamp();
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action = AdAction.AdActionLoaded.toString();
        model.action_time = curTime;
        model.data_loaded_time = curTime;

        baseReq(model);
    }

    public void rAdLoaded(AdPlatform adPlat, AdType adType, String adId, long loadedTime) {
        ReportActionHttpModel model = new ReportActionHttpModel();
        initBaseModel(model);
        long curTime = Utility.getTimestamp();
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action = AdAction.AdActionLoaded.toString();
        model.action_time = curTime;
        model.data_loaded_time = loadedTime;

        baseReq(model);
    }

    public void rAdShow(AdPlatform adPlat, AdType adType, String adId, long loadedTime) {
        ReportActionHttpModel model = new ReportActionHttpModel();
        initBaseModel(model);
        long curTime = Utility.getTimestamp();
        long intervalTime = curTime - loadedTime;
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action = AdAction.AdActionShow.toString();
        model.action_time = curTime;
        model.show_time = curTime;
        model.preload_time = loadedTime;
        model.interval_time = intervalTime;

        baseReq(model);
    }

    public void rAdJump(AdPlatform adPlat, AdType adType, String adId) {
        ReportActionHttpModel model = new ReportActionHttpModel();
        initBaseModel(model);
        long curTime = Utility.getTimestamp();
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action = AdAction.AdActionJump.toString();
        model.action_time = curTime;
        model.jump_time = curTime;

        baseReq(model);
    }

    public void rAdClose(AdPlatform adPlat,
                         AdType adType,
                         String adId,
                         long openTime,
                         long startLoadTime,
                         long dataLoadedTime,
                         long videoLoadedTime,
                         long showTime,
                         long clickTime,
                         long jumpTime) {
        ReportActionHttpModel model = new ReportActionHttpModel();
        initBaseModel(model);
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.open_time = openTime;
        model.start_load_time = startLoadTime;
        model.data_loaded_time = dataLoadedTime;
        model.preload_time = videoLoadedTime;
        model.video_loaded_time = videoLoadedTime;
        model.show_time = showTime;
        model.click_time = clickTime;
        model.jump_time = jumpTime;
        model.close_time = Utility.getTimestamp();
        model.action_time = Utility.getTimestamp();
        if (jumpTime > 0) {
            model.action = AdAction.AdActionJumpAndClose.toString();
        } else {
            model.action = AdAction.AdActionClose.toString();
        }

        baseReq(model);
    }

    public void rAdClick(AdPlatform adPlat, AdType adType, String adId, long showTime, int isRepeat) {
        ReportClickHttpModel model = new ReportClickHttpModel();
        initBaseModel(model);
        long curTime = Utility.getTimestamp();
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action = AdAction.AdActionClick.toString();
        model.action_time = curTime;
        model.click_time = curTime;
        model.show_time = showTime;
        model.is_repeat = isRepeat+"";

        baseReq(model);
    }

    public void rAdError(AdPlatform adPlat, AdType adType, String err, String adId) {
        ReportErrorHttpModel model = new ReportErrorHttpModel();
        initBaseModel(model);
        model.plat = adPlat.toString();
        model.type = adType.toString();
        model.ad_id = adId;
        model.action_time = Utility.getTimestamp();
        model.message = err;

        baseReq(model);
    }

    private void baseReq(InterFaceBaseHttpModel model) {
        testGetModelField(model);
        model.setIsPostJson(false);
        mHttpClient.Request(AppManager.getContext(), model, new IHttpRequestCancelInterFace() {
            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onStart() {
                Log.d(TAG, "onStart: ");
            }

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "onResponse: " + s);
                try {
                    setResponse(s);
                } catch (Exception ex) {

                }
            }

            @Override
            public void onErrorResponse(String s) {
                Log.d(TAG, "onErrorResponse: " + s);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: ");
            }
        });
    }

    private void setResponse(String s) {
        try {
            JSONObject dRoot = new JSONObject(s);
            JSONObject d = dRoot.getJSONObject("data");
            JSONArray adRandomTime = d.optJSONArray("ad_random_time");
            if (adRandomTime != null) {
                JSONObject adRandomTime0 = adRandomTime.getJSONObject(0);
                int max = adRandomTime0.optInt("max");
                int min = adRandomTime0.optInt("min");
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_RANDOM_TIME_MAX, max);
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_RANDOM_TIME_MIN, min);
            }
            JSONObject rewardLimit = d.optJSONObject("incentive_video_show");
            if (rewardLimit != null) {
                int watch = rewardLimit.optInt("watch");
                int click = rewardLimit.optInt("click");
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_REWARD_LIMIT_WATCH_TIME, watch);
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_REWARD_LIMIT_CLICK_TIME, click);
            }
            JSONObject splashLimit = d.optJSONObject("open_screen_show");
            if (splashLimit != null) {
                int watch = splashLimit.optInt("watch");
                int click = splashLimit.optInt("click");
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_SPLASH_LIMIT_WATCH_TIME, watch);
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_SPLASH_LIMIT_CLICK_TIME, click);
            }
            JSONObject bannerLimit = d.optJSONObject("banner_show");
            if (bannerLimit != null) {
                int watch = bannerLimit.optInt("watch");
                int click = bannerLimit.optInt("click");
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_BANNER_LIMIT_WATCH_TIME, watch);
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_BANNER_LIMIT_CLICK_TIME, click);
            }
            JSONObject flowLimit = d.optJSONObject("flow_show");
            if (flowLimit != null) {
                int watch = flowLimit.optInt("watch");
                int click = flowLimit.optInt("click");
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_FLOW_LIMIT_WATCH_TIME, watch);
                EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_FLOW_LIMIT_CLICK_TIME, click);
            }
            String token = d.optString("token");
            if (token != null && token.length() > 0) {
                rRefreshToken(token);
            }
            checkConfig();
        } catch (Exception ex) {
            return;
        }
    }

    private void checkConfig() {
        Log.d(TAG, "checkConfig: ");
        int max = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_RANDOM_TIME_MAX);
        int min = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_RANDOM_TIME_MIN);
        Log.d(TAG, "checkConfig: (max: " + max + ", min: " + min + ")");
        if (max == 0) {
            Log.d(TAG, "checkConfig: max == 0");
            EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_RANDOM_TIME_MAX, 60);
        }
        if (min == 0) {
            Log.d(TAG, "checkConfig: min == 0");
            EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_RANDOM_TIME_MIN, 10);
        }
    }

    private void rRefreshToken(String token) {
        this.model.token = token;
        EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_TOKEN, token);
    }

    private void testGetModelField(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();
        for(int i = 0 , len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o;
                try {
                    o = fields[i].get(obj);
                    System.err.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }

}
