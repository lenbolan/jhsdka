package com.yky.jhsdk.models.httpModel.ad;

public class ReportActionHttpModel extends ReportBaseInfoHttpModel {

    public String plat;
    public String type;
    public String ad_id;
    public String action;
    public long action_time;
    public long close_time;
    public long preload_time;
    public long video_loaded_time;
    public long open_time;
    public long start_load_time;
    public long data_loaded_time;
    public long show_time;
    public long click_time;
    public long jump_time;
    public long interval_time;

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/index/plugin/execute/plugin_name/advertisement/plugin_controller/api/plugin_action/advertisement_watch";
    }

    @Override
    public String toString() {
        return super.toString() + "\nReportActionHttpModel{" +
                "plat='" + plat + '\'' +
                ", type='" + type + '\'' +
                ", ad_id='" + ad_id + '\'' +
                ", action='" + action + '\'' +
                ", action_time=" + action_time +
                ", close_time=" + close_time +
                ", preload_time=" + preload_time +
                ", video_loaded_time=" + video_loaded_time +
                ", open_time=" + open_time +
                ", start_load_time=" + start_load_time +
                ", data_loaded_time=" + data_loaded_time +
                ", show_time=" + show_time +
                ", click_time=" + click_time +
                ", jump_time=" + jump_time +
                ", interval_time=" + interval_time +
                '}';
    }
}
