package com.yky.jhsdk.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

import java.util.List;

import com.yky.jhsdk.R;
import com.yky.jhsdk.models.enums.AdPlatform;
import com.yky.jhsdk.models.enums.AdType;
import com.yky.jhsdk.service.Reporter;
import com.yky.jhsdk.utils.EditSharedPreferences;
import com.yky.jhsdk.utils.Utility;

public class QQInfoFlowFragment extends Fragment implements NativeExpressAD.NativeExpressADListener {

    private static final String TAG = "QQInfoFlowFragment";
    private ViewGroup flow_container;
    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;

    private boolean isPreloadVideo = false;

    private String curAdId; //广告ID
    private long curAdRequestTime = 0; // 当前广告开始加载时间
    private long curAdLoadedTime = 0; // 当前广告加载完成时间
    private long curAdShowTime = 0; // 广告展示时间
    private long curAdClickTime = 0; // 广告点击时间
    private int isRepeat = 0; // 是否为重复点击

    private TextView tvState;

    private String getSlotId() {
        String slotId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_QQ_SLOTID_FLOW);
        if ("".equals(slotId)) {
            Log.i(TAG, "initAd: qq flow slot id is empty.");
            slotId = "2000629911207832";
        }
        return slotId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qq_info_flow, container, false);
        flow_container = (ViewGroup) view.findViewById(R.id.flow_container);

        tvState = view.findViewById(R.id.ad_qq_flow_state);

        refreshAd();

        return view;
    }

    private void refreshAd() {
        isRepeat = 0;
        curAdId = Reporter.getInstance().createAdId(3);
        curAdRequestTime = Utility.getTimestamp();

        String slotId = getSlotId();
        nativeExpressAD = new NativeExpressAD(this.getActivity(),
                                                    new ADSize(340, ADSize.AUTO_HEIGHT),
                                                    slotId,
                                                this);
        nativeExpressAD.loadAD(1);

    }

    @Override
    public void onADLoaded(List<NativeExpressADView> list) {
        Log.i(TAG, "onADLoaded: " + list.size());

        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

        if (flow_container.getVisibility() != View.VISIBLE) {
            flow_container.setVisibility(View.VISIBLE);
        }

        if (flow_container.getChildCount() > 0) {
            flow_container.removeAllViews();
        }

        curAdLoadedTime = Utility.getTimestamp();
        Reporter.getInstance().rAdLoaded(AdPlatform.AdPlatformGDT, AdType.AdTypeFlow, curAdId, curAdLoadedTime);

        nativeExpressADView = list.get(0);
        if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            nativeExpressADView.setMediaListener(mediaListener);
        }
        nativeExpressADView.render();

        flow_container.addView(nativeExpressADView);
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onRenderFail: ");
        tvState.setText("广告渲染失败");
    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onRenderSuccess: ");
        tvState.setText("广告渲染成功");
    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onADExposure: ");
        tvState.setText("广告展示成功");
        curAdShowTime = Utility.getTimestamp();
        Reporter.getInstance().rAdShow(AdPlatform.AdPlatformGDT, AdType.AdTypeFlow, curAdId, curAdLoadedTime);
    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onADClicked: ");
        curAdClickTime = Utility.getTimestamp();
        Reporter.getInstance().rAdClick(AdPlatform.AdPlatformGDT, AdType.AdTypeFlow, curAdId, curAdShowTime, isRepeat);
        if (isRepeat == 0) isRepeat += 1;
    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onADClosed: ");
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onADLeftApplication: ");
    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onADOpenOverlay: ");
    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onADCloseOverlay: ");
    }

    @Override
    public void onNoAD(AdError adError) {
        String msg = String.format("onNoAD, error code: %d, error msg: %s",
                                                                        adError.getErrorCode(),
                                                                        adError.getErrorMsg());
        Log.i(TAG, msg);
        tvState.setText("错误：" + adError.getErrorCode() + "|" + adError.getErrorMsg());
        Reporter.getInstance().rAdError(AdPlatform.AdPlatformGDT, AdType.AdTypeFlow, msg, curAdId);
    }

    @Override
    public void onStop() {
        super.onStop();
        Reporter.getInstance().rAdClose(
                AdPlatform.AdPlatformGDT,
                AdType.AdTypeFlow,
                curAdId,
                curAdRequestTime,
                curAdRequestTime,
                curAdLoadedTime,
                curAdLoadedTime,
                curAdShowTime,
                curAdClickTime,
                0);
    }

    private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
        @Override
        public void onVideoInit(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoInit: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
            tvState.setText("视频初始化...");
        }

        @Override
        public void onVideoLoading(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoLoading");
            tvState.setText("视频加载中...");
        }

        @Override
        public void onVideoCached(NativeExpressADView adView) {
            Log.i(TAG, "onVideoCached");
            // 视频素材加载完成，此时展示视频广告不会有进度条。
            if(isPreloadVideo && nativeExpressADView != null) {
                if(flow_container.getChildCount() > 0){
                    flow_container.removeAllViews();
                }
                // 广告可见才会产生曝光，否则将无法产生收益。
                flow_container.addView(nativeExpressADView);
                nativeExpressADView.render();
            }
            tvState.setText("视频缓存成功");
        }

        @Override
        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
            Log.i(TAG, "onVideoReady");
        }

        @Override
        public void onVideoStart(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoStart: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoPause(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPause: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoComplete(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoComplete: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
            String msg = String.format("onNoAD, error code: %d, error msg: %s",
                    adError.getErrorCode(),
                    adError.getErrorMsg());
            Log.i(TAG, "onVideoError: " + msg);
            tvState.setText("错误：" + adError.getErrorCode() + "|" + adError.getErrorMsg());
        }

        @Override
        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageOpen");
        }

        @Override
        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageClose");
        }
    };

    private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            StringBuilder videoBuilder = new StringBuilder();
            videoBuilder.append("{state:").append(videoPlayer.getVideoState()).append(",")
                    .append("duration:").append(videoPlayer.getDuration()).append(",")
                    .append("position:").append(videoPlayer.getCurrentPosition()).append("}");
            return videoBuilder.toString();
        }
        return null;
    }

}
