package com.yky.jhsdk.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;

import java.util.Locale;

import com.yky.jhsdk.R;
import com.yky.jhsdk.models.enums.AdPlatform;
import com.yky.jhsdk.models.enums.AdType;
import com.yky.jhsdk.service.Reporter;
import com.yky.jhsdk.utils.EditSharedPreferences;
import com.yky.jhsdk.utils.Utility;

public class QQBannerFragment extends Fragment implements View.OnClickListener, UnifiedBannerADListener {

    private static final String TAG = QQBannerFragment.class.getSimpleName();

    ViewGroup bannerContainer;
    UnifiedBannerView bv;
    String posId;

    private String curAdId; //广告ID
    private long curAdRequestTime = 0; // 当前广告开始加载时间
    private long curAdLoadedTime = 0; // 当前广告加载完成时间
    private long curAdShowTime = 0; // 广告展示时间
    private long curAdClickTime = 0; // 广告点击时间
    private int isRepeat = 0; // 是否为重复点击

    private TextView tvState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qq_banner, container, false);
        bannerContainer = (ViewGroup) view.findViewById(R.id.bannerContainer);

        tvState = view.findViewById(R.id.ad_qq_banner_state);

        this.getBanner().loadAD();

        return view;
    }

    private String getSlotId() {
        String slotId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_QQ_SLOTID_BANNER);
        if ("".equals(slotId)) {
            Log.i(TAG, "initAd: qq banner slot id is empty.");
            slotId = "4080052898050840";
        }
        return slotId;
    }

    protected UnifiedBannerView getBanner() {
        String posId = getSlotId();
        if( this.bv != null && this.posId.equals(posId)) {
            return this.bv;
        }
        if(this.bv != null){
            bannerContainer.removeView(bv);
            bv.destroy();
        }

        isRepeat = 0;
        curAdId = Reporter.getInstance().createAdId(2);
        curAdRequestTime = Utility.getTimestamp();

        this.posId = posId;
        this.bv = new UnifiedBannerView(this.getActivity(), posId, this);
        bannerContainer.addView(bv, getUnifiedBannerLayoutParams());
        return this.bv;
    }

    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        Point screenSize = new Point();
        this.getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x,  Math.round(screenSize.x / 6.4F));
    }

    @Override
    public void onStop() {
        super.onStop();
        Reporter.getInstance().rAdClose(
                AdPlatform.AdPlatformGDT,
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
        if (bv != null) {
            bv.destroy();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onNoAD(AdError adError) {
        String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
        Log.d(TAG, "onNoAD: " + msg);
        tvState.setText("错误：" + adError.getErrorCode() + "|" + adError.getErrorMsg());
        Reporter.getInstance().rAdError(AdPlatform.AdPlatformGDT, AdType.AdTypeBanner, msg, curAdId);
    }

    @Override
    public void onADReceive() {
        Log.i(TAG, "onADReceive: ");
        tvState.setText("广告加载成功");
        curAdLoadedTime = Utility.getTimestamp();
        Reporter.getInstance().rAdLoaded(AdPlatform.AdPlatformGDT, AdType.AdTypeBanner, curAdId, curAdLoadedTime);
    }

    @Override
    public void onADExposure() {
        Log.i(TAG, "onADExposure: ");
        tvState.setText("广告渲染成功");
        curAdShowTime = Utility.getTimestamp();
        Reporter.getInstance().rAdShow(AdPlatform.AdPlatformGDT, AdType.AdTypeBanner, curAdId, curAdLoadedTime);
    }

    @Override
    public void onADClosed() {
        Log.i(TAG, "onADClosed: ");
    }

    @Override
    public void onADClicked() {
        Log.i(TAG, "onADClicked: ");
        curAdClickTime = Utility.getTimestamp();
        Reporter.getInstance().rAdClick(AdPlatform.AdPlatformGDT, AdType.AdTypeBanner, curAdId, curAdShowTime, isRepeat);
        if (isRepeat == 0) isRepeat += 1;
    }

    @Override
    public void onADLeftApplication() {
        Log.i(TAG, "onADLeftApplication: ");
    }

    @Override
    public void onADOpenOverlay() {
        Log.i(TAG, "onADOpenOverlay: ");
    }

    @Override
    public void onADCloseOverlay() {
        Log.i(TAG, "onADCloseOverlay: ");
    }
}
