package com.yky.jhsdk.models.enums;

import androidx.annotation.NonNull;

public enum AdType {

    AdTypeSplash,
    AdTypeReward,
    AdTypeBanner,
    AdTypeFlow;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case AdTypeSplash:
                return "开屏";
            case AdTypeReward:
                return "激励视频";
            case AdTypeBanner:
                return "banner";
            case AdTypeFlow:
                return "信息流";
            default:
                return "";
        }
    }
}
