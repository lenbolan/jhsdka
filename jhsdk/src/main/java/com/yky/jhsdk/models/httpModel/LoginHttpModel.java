package com.yky.jhsdk.models.httpModel;

public class LoginHttpModel extends InterFaceBaseHttpModel{

    public String username;
    public String password;

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/index/wxapp.login/phone_goin";
    }
}
