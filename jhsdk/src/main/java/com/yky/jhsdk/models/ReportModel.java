package com.yky.jhsdk.models;

import android.content.Context;
import android.os.Build;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.github.gzuliyujiang.oaid.IGetter;
import com.renrui.libraries.util.UtilityNetWork;

import com.yky.jhsdk.AppManager;
import com.yky.jhsdk.utils.AppUtils;
import com.yky.jhsdk.utils.EditSharedPreferences;
import com.yky.jhsdk.utils.Utility;

public class ReportModel {
    /**
     * 用户id
     */
    public int uid;
    /**
     * 手机号
     */
    public String phoneNum;
    /**
     * 机型
     */
    public String phoneType;
    /**
     * 设备标识符
     */
    public String idfa;
    /**
     * 运营商
     */
    public String netOperator = "未知";
    /**
     * 网络连接状态
     */
    public String netState = "";
    /**
     * ip address
     */
    public String ipAddr = "";
    /**
     * android version
     */
    public String sysVersion;

    public String appName;

    public String bundleId;

    public String appVersion;

    public String token;

    public int serverAppId;

//    生产厂商：android.os.Build.MANUFACTURER
//    品牌：android.os.Build.BRAND
//    型号：android.os.Build.MODEL
//    Android版本：android.os.Build.VERSION.RELEASE
//    Android sdk：android.os.Build.VERSION.SDK_INT
    public void init() {
        Context context = AppManager.getContext();
        idfa = DeviceIdentifier.getAndroidID(context);
        phoneType = Build.MODEL;
        sysVersion = Build.VERSION.RELEASE;
        appName = AppUtils.getAppName(context);
        bundleId = AppUtils.getPackageName(context);
        appVersion = AppUtils.getVersionName(context);
        String appId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_APP_ID);
        if (appId != null && appId.length() > 0) {
            serverAppId = Integer.parseInt(appId);
        }

        uid = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_USER_ID);
        phoneNum = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_PHONE_NUM);
        token = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_TOKEN);

        netOperator = UtilityNetWork.getNetOperatorName(); // Utility.getSimOperator(AppManager.getContext());
        netState = UtilityNetWork.GetNetworkType(); // Utility.getNetType(AppManager.getContext());
        ipAddr = UtilityNetWork.getIPAddress();

        netOperator = netOperator.equals("") ? "未获得" : netOperator;
        netState = netState.equals("") ? "未获得" : netState;
        ipAddr = ipAddr.equals("") ? "未获得" : ipAddr;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "uid=" + uid +
                ", phoneNum='" + phoneNum + '\'' +
                ", phoneType='" + phoneType + '\'' +
                ", idfa='" + idfa + '\'' +
                ", netOperator='" + netOperator + '\'' +
                ", netState='" + netState + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", sysVersion='" + sysVersion + '\'' +
                ", appName='" + appName + '\'' +
                ", bundleId='" + bundleId + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", token='" + token + '\'' +
                ", serverAppId=" + serverAppId +
                '}';
    }

    public static void info() {
        Context context = AppManager.getContext();
        // 获取IMEI，只支持Android 10之前的系统，需要READ_PHONE_STATE权限，可能为空
        DeviceIdentifier.getIMEI(context);
        // 获取安卓ID，可能为空
        DeviceIdentifier.getAndroidID(context);
        // 获取数字版权管理ID，可能为空
        DeviceIdentifier.getWidevineID();
        // 获取伪造ID，根据硬件信息生成，不会为空，有大概率会重复
        DeviceIdentifier.getPseudoID();
        // 获取GUID，随机生成，不会为空
        DeviceIdentifier.getGUID(context);
        // 是否支持OAID/AAID
        DeviceID.supportedOAID(context);
        // 获取OAID/AAID，同步调用
        DeviceIdentifier.getOAID(context);
        // 获取OAID/AAID，异步回调
        DeviceID.getOAID(context, new IGetter() {
            @Override
            public void onOAIDGetComplete(String result) {
                // 不同厂商的OAID/AAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
            }

            @Override
            public void onOAIDGetError(Exception error) {
                // 获取OAID/AAID失败
            }
        });
    }
}
