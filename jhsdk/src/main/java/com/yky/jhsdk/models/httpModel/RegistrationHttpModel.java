package com.yky.jhsdk.models.httpModel;

public class RegistrationHttpModel extends InterFaceBaseHttpModel{

    public String username;
    public String password;

    @Override
    public String getUrl() {
        return "https://app.niuit.cn/index.php/index/wxapp.login/phone_register";
    }

}
