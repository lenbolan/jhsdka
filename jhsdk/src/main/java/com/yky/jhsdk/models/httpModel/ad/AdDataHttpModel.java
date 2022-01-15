package com.yky.jhsdk.models.httpModel.ad;

import com.yky.jhsdk.models.httpModel.InterFaceBaseHttpModel;

public class AdDataHttpModel extends InterFaceBaseHttpModel {

    public String bunddleid;
    public int version;

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/cms/wxapp.index/ad";
    }

}
