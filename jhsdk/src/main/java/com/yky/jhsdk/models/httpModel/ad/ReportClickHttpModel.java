package com.yky.jhsdk.models.httpModel.ad;

public class ReportClickHttpModel extends ReportActionHttpModel{

    public String is_repeat = "0";

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/index/plugin/execute/plugin_name/advertisement/plugin_controller/api/plugin_action/advertisement_download";
    }
}
