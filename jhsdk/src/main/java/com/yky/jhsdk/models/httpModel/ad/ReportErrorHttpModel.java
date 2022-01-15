package com.yky.jhsdk.models.httpModel.ad;

public class ReportErrorHttpModel extends ReportActionHttpModel {

    public String message;

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/index/plugin/execute/plugin_name/advertisement/plugin_controller/api/plugin_action/advertisement_error_log";
    }
}
