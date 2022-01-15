package com.yky.jhsdk.models.httpModel.ad;

import com.yky.jhsdk.models.httpModel.InterFaceBaseHttpModel;

public class ReportBaseInfoHttpModel extends InterFaceBaseHttpModel {

    public String username;
    public int uid;
    public String mobile_model;
    public String idfa;
    public String idfv = "android";
    public String operator = "未知";
    public String net_state = "";
    public String ip = "";
    public String sys_version;
    public String token;
    public String bundle_id;
    public String app_name;
    public String app_version;
    public String app_mid;

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/index/plugin/execute/plugin_name/advertisement/plugin_controller/api/plugin_action/advertisement_phone_code";
    }

    @Override
    public String toString() {
        return "ReportBaseInfoHttpModel{" +
                "username='" + username + '\'' +
                ", uid=" + uid +
                ", mobile_model='" + mobile_model + '\'' +
                ", idfa='" + idfa + '\'' +
                ", idfv='" + idfv + '\'' +
                ", operator='" + operator + '\'' +
                ", net_state='" + net_state + '\'' +
                ", ip='" + ip + '\'' +
                ", sys_version='" + sys_version + '\'' +
                ", token='" + token + '\'' +
                ", bundle_id='" + bundle_id + '\'' +
                ", app_name='" + app_name + '\'' +
                ", app_version='" + app_version + '\'' +
                ", app_mid='" + app_mid + '\'' +
                '}';
    }
}
