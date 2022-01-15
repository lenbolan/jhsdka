package com.yky.jhsdk.models.enums;

import androidx.annotation.NonNull;

public enum AdAction {

    AdActionOpen,
    AdActionStart,
    AdActionLoaded,
    AdActionShow,
    AdActionJump,
    AdActionClick,
    AdActionClose,
    AdActionJumpAndClose;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case AdActionOpen:
                return "打开";
            case AdActionStart:
                return "开始加载";
            case AdActionLoaded:
                return "加载完成";
            case AdActionShow:
                return "展示";
            case AdActionJump:
                return "跳过";
            case AdActionClick:
                return "点击";
            case AdActionClose:
                return "关闭";
            case AdActionJumpAndClose:
                return "跳过并关闭";
            default:
                return "";
        }
    }

}
