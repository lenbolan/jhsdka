package com.yky.jhsdk.models.enums;

import androidx.annotation.NonNull;

public enum AdPlatform {

    AdPlatformBU,
    AdPlatformGDT;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case AdPlatformBU:
                return "穿山甲";
            case AdPlatformGDT:
                return "优量汇";
            default:
                return "";
        }
    }
}
