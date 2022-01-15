package com.yky.jhsdk.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

import com.yky.jhsdk.R;
import com.yky.jhsdk.config.TTAdManagerHolder;
import com.yky.jhsdk.dialog.DislikeDialog;
import com.yky.jhsdk.models.enums.AdPlatform;
import com.yky.jhsdk.models.enums.AdType;
import com.yky.jhsdk.service.Reporter;
import com.yky.jhsdk.utils.EditSharedPreferences;
import com.yky.jhsdk.utils.Utility;

public class BUBannerFragment extends Fragment {

    private static final String TAG = "BUBannerFragment";
    private TTAdNative mTTAdNative;
    private FrameLayout mExpressContainer;

    private TTAdDislike mTTAdDislike;
    private TTNativeExpressAd mTTAd;

    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;

    private String curAdId; //广告ID
    private long curAdRequestTime = 0; // 当前广告开始加载时间
    private long curAdLoadedTime = 0; // 当前广告加载完成时间
    private long curAdShowTime = 0; // 广告展示时间
    private long curAdClickTime = 0; // 广告点击时间
    private int isRepeat = 0; // 是否为重复点击

    private TextView tvState;

    public BUBannerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTTSDKConfig();
    }

    private String getSlotId() {
        String slotId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_BU_SLOTID_BANNER);
        if ("".equals(slotId)) {
            Log.i(TAG, "initAd: bu banner slot id is empty.");
            slotId = "947460949";
        }
        return slotId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bu_banner, container, false);

        mExpressContainer = (FrameLayout) view.findViewById(R.id.express_container);

        tvState = view.findViewById(R.id.ad_bu_banner_state);

        String slotId = getSlotId();

        loadExpressAd(slotId, 600, 300);

        return view;
    }

    private void initTTSDKConfig() {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this.getContext());
        TTAdManagerHolder.get().requestPermissionIfNecessary(this.getContext());
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        mExpressContainer.removeAllViews();
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setAdCount(1)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                .build();

        isRepeat = 0;
        curAdId = Reporter.getInstance().createAdId(2);
        curAdRequestTime = Utility.getTimestamp();

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "onError: " + "load error : " + code + ", " + message);
                tvState.setText("错误：[" + code + "]" + message);
                mExpressContainer.removeAllViews();

                Reporter.getInstance().rAdError(AdPlatform.AdPlatformBU, AdType.AdTypeBanner, message, curAdId);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                Log.i(TAG, "onNativeExpressAdLoad: load success!");
                tvState.setText("广告加载成功");

                curAdLoadedTime = Utility.getTimestamp();
                Reporter.getInstance().rAdLoaded(AdPlatform.AdPlatformBU, AdType.AdTypeBanner, curAdId, curAdLoadedTime);

                mTTAd.render();
            }
        });
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                Log.i(TAG, "onAdClicked: 广告被点击");
                curAdClickTime = Utility.getTimestamp();
                Reporter.getInstance().rAdClick(AdPlatform.AdPlatformBU, AdType.AdTypeBanner, curAdId, curAdShowTime, isRepeat);
                if (isRepeat == 0) isRepeat += 1;
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.i(TAG, "onAdShow: 广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e(TAG, "render fail:" + (System.currentTimeMillis() - startTime));
                Log.i(TAG, "onRenderFail: " + msg + " code:" + code);
                tvState.setText("广告渲染失败");
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                Log.i(TAG, "onRenderSuccess: 渲染成功");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
                tvState.setText("广告渲染成功");

                curAdShowTime = Utility.getTimestamp();
                Reporter.getInstance().rAdShow(AdPlatform.AdPlatformBU, AdType.AdTypeBanner, curAdId, curAdLoadedTime);
            }
        });
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                Log.i(TAG, "onIdle: 点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Log.i(TAG, "onDownloadActive: 下载中，点击暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "onDownloadPaused: 下载暂停，点击继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "onDownloadFailed: 下载失败，点击重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Log.i(TAG, "onInstalled: 安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Log.i(TAG, "onDownloadFinished: 点击安装");
            }
        });
    }

    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(this.getContext(), dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    Log.i(TAG, "onItemClick: " + "点击 " + filterWord.getName());
                    mExpressContainer.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    Log.i(TAG, "onClick: 点击了为什么看到此广告");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }

        ad.setDislikeCallback(this.getActivity(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                Log.i(TAG, "onSelected: " + "点击 " + value);
                mExpressContainer.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    Log.i(TAG, "onSelected: 模版Banner 穿山甲sdk强制将view关闭了");
                }
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 点击取消");
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Reporter.getInstance().rAdClose(
                AdPlatform.AdPlatformBU,
                AdType.AdTypeBanner,
                curAdId,
                curAdRequestTime,
                curAdRequestTime,
                curAdLoadedTime,
                curAdLoadedTime,
                curAdShowTime,
                curAdClickTime,
                0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }

}
