package com.yky.jhsdk.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.yky.jhsdk.R;
import com.yky.jhsdk.interfaces.IItemClickListener;
import com.yky.jhsdk.models.enums.AdPlatform;
import com.yky.jhsdk.models.enums.AdType;
import com.yky.jhsdk.service.Reporter;
import com.yky.jhsdk.utils.EditSharedPreferences;
import com.yky.jhsdk.utils.Hash;
import com.yky.jhsdk.utils.RandomUntil;
import com.yky.jhsdk.utils.Utility;
import com.yky.jhsdk.utils.UtilityToasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BUAdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BUAdFragment extends Fragment {

    private TTAdNative mTTAdNative;
    private AdSlot adSlot;
    private TTRewardVideoAd mttRewardVideoAd;
    private Boolean mIsLoad = false;

    private GridView gridView;
    private int[] dataSet;
    private int ad_watch_times = 0;
    private FrameLayout flCover;
    private TextView tvState;
    private TextView tvCountDown;
    private Button btnBack;

    private Timer timer;
    private TimerTask task;
    private int intCD = 30;

    private String adState = "";

    private String curAdId; //广告ID
    private long curAdOpenTime = 0; // 当前广告打开时间
    private long curAdStartLoadTime = 0; // 当前广告开始加载时间
    private long curAdDataLoadedTime = 0; // 当前广告数据加载完成时间
    private long curAdVideoLoadedTime = 0; // 当前广告视频加载完成时间
    private long curAdShowTime = 0; // 广告展示时间
    private long curAdClickTime = 0; // 广告点击时间
    private long curAdJumpTime = 0; // 用户点击跳过时间
    private int isRepeat = 0; // 是否为重复点击

    private RbuAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "BUAdFragment";
    private static final String TAG2 = "BUAdStatus";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button button;

    public BUAdFragment() {
        // Required empty public constructor

        checkTodayTimes();

        dataSet = new int[15];
        for (int i = 0; i < dataSet.length; i++) {
            dataSet[i] = (i + 1) <= ad_watch_times ? 1 : 0;
        }

    }

    private void checkTodayTimes() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String today = formatter.format(date);

        String keyDate = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_BU_DATE);

        if (today.equals(keyDate)) {
            ad_watch_times = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_BU_STATE);
        } else {
            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_BU_DATE, today);
            EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_BU_STATE, 0);
        }
    }

    private void updateViewData() {
        ad_watch_times += 1;
        ad_watch_times = Math.min(ad_watch_times, 15);
        EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_BU_STATE, ad_watch_times);
        dataSet[ad_watch_times-1] = 1;
        adapter.notifyDataSetChanged();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BUAdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BUAdFragment newInstance(String param1, String param2) {
        BUAdFragment fragment = new BUAdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private String getSlotId() {
        String slotId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_BU_SLOTID_REWAED);
        if ("".equals(slotId)) {
            Log.i(TAG, "initAd: bu reward slot id is empty.");
            slotId = "947460916";
        }
        Log.i(TAG, "getSlotId: " + slotId);
        return slotId;
    }

    private void initAd() {
        if (mTTAdNative != null) {
            mTTAdNative = null;
        }
        if (adSlot != null) {
            adSlot = null;
        }
        if (mttRewardVideoAd != null) {
            mttRewardVideoAd = null;
        }
        mIsLoad = false;

        isRepeat = 0;
        curAdId = Reporter.getInstance().createAdId(4);
        curAdOpenTime = Utility.getTimestamp();
        curAdStartLoadTime = curAdOpenTime;

        String slotId = getSlotId();

        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this.getContext());
        adSlot = new AdSlot.Builder()
                .setCodeId(slotId)
                .setOrientation(TTAdConstant.VERTICAL)
                .build();

        Reporter.getInstance().rAdStart(AdPlatform.AdPlatformBU, AdType.AdTypeReward, curAdId, curAdStartLoadTime);

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG2, "onError: " + i + ", " + s);
                adState = "错误：[" + i + "]" + s;
                tvState.setText(adState);
                Reporter.getInstance().rAdError(AdPlatform.AdPlatformBU, AdType.AdTypeReward, s, curAdId);
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                Log.e(TAG, "onRewardVideoAdLoad: ");
                adState = "视频加载中...";
                tvState.setText(adState);
                mttRewardVideoAd = ttRewardVideoAd;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Log.d(TAG2, "onAdShow: 广告的展示回调");
                        curAdShowTime = Utility.getTimestamp();
                        Reporter.getInstance().rAdShow(AdPlatform.AdPlatformBU, AdType.AdTypeReward, curAdId, curAdVideoLoadedTime);
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG2, "onAdVideoBarClick: 广告下载bar点击回调");
                        Reporter.getInstance().rAdClick(AdPlatform.AdPlatformBU, AdType.AdTypeReward, curAdId, curAdShowTime, isRepeat);
                        if (isRepeat == 0) isRepeat += 1;
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG2, "onAdClose: 广告关闭回调");
                        Reporter.getInstance().rAdClose(AdPlatform.AdPlatformBU,
                                                        AdType.AdTypeReward,
                                                        curAdId,
                                                        curAdOpenTime,
                                                        curAdStartLoadTime,
                                                        curAdDataLoadedTime,
                                                        curAdVideoLoadedTime,
                                                        curAdShowTime,
                                                        curAdClickTime,
                                                        curAdJumpTime);
                        updateViewData();
                        initAd();

                        showCover();
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG2, "onVideoComplete: 视频播放完成回调");
                    }

                    @Override
                    public void onVideoError() {
                        Log.d(TAG2, "onVideoError: 视频广告播放错误回调");
                    }

                    @Override
                    public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                        Log.d(TAG2, "onRewardVerify: 奖励验证回调 " + b + ", " + i + ", " + s + ", " + i1 + ", " + s1);
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG2, "onSkippedVideo: 跳过视频播放回调");
                        curAdJumpTime = Utility.getTimestamp();
                    }
                });
            }

            @Override
            public void onRewardVideoCached() {
                Log.e(TAG2, "onRewardVideoCached: 广告视频本地加载完成的回调");
                adState = "广告加载完成";
                curAdDataLoadedTime = Utility.getTimestamp();
                curAdVideoLoadedTime = curAdDataLoadedTime;
                Reporter.getInstance().rAdLoaded(AdPlatform.AdPlatformBU, AdType.AdTypeReward, curAdId, curAdVideoLoadedTime);
                tvState.setText(adState);
                mIsLoad = true;
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                Log.e(TAG2, "onRewardVideoCached: 广告视频本地加载完成的回调");
                adState = "广告加载完成";
                curAdDataLoadedTime = Utility.getTimestamp();
                curAdVideoLoadedTime = curAdDataLoadedTime;
                Reporter.getInstance().rAdLoaded(AdPlatform.AdPlatformBU, AdType.AdTypeReward, curAdId, curAdVideoLoadedTime);
                tvState.setText(adState);
                mIsLoad = true;
            }
        });
    }

    private void showCover() {
        flCover.setVisibility(View.VISIBLE);
        int timeMax = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_RANDOM_TIME_MAX);
        int timeMin = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_RANDOM_TIME_MIN);
//        Log.d(TAG, "showCover: max = " + timeMax);
//        Log.d(TAG, "showCover: min = " + timeMin);
        intCD = RandomUntil.getNum(timeMin, timeMax);
        createTimer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: =====");

        View view = inflater.inflate(R.layout.fragment_b_u_ad, container, false);

        gridView = view.findViewById(R.id.gridViewBU);
        adapter = new RbuAdapter(this.getContext(), dataSet);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Log.i(TAG, "onItemClicked: " + position);
                if (mIsLoad) {
                    mttRewardVideoAd.showRewardVideoAd(getActivity(), TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                } else {
                    UtilityToasty.error(R.string.ad_load_failed);
                }
            }
        });
        gridView.setAdapter(adapter);

        flCover = view.findViewById(R.id.flCover);

        tvState = view.findViewById(R.id.ad_bu_state);
        tvState.setText(adState);

        tvCountDown = view.findViewById(R.id.tvCountDown);

        initAd();

        return view;
    }

    private int getUserId() {
        int userId = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_USER_ID);
        return userId;
    }

    private static class RbuAdapter extends BaseAdapter {

        private IItemClickListener itemClickListener;

        private LayoutInflater layoutInflater;
        private int[] data;

        public void setItemClickListener(IItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public RbuAdapter(Context context, int[] dataSet) {
            layoutInflater = LayoutInflater.from(context);
            data = dataSet;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View _v = layoutInflater.inflate(R.layout.view_buad_item, null);
            _v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: " + i);
                    if (itemClickListener != null) {
                        itemClickListener.onItemClicked(view, i);
                    }
                }
            });
            TextView tvSymbol = _v.findViewById(R.id.txtSymbol);
            if (data[i] == 1) {
                tvSymbol.setText("✓");
                tvSymbol.setTextColor(Color.RED);
            } else {
                tvSymbol.setText("+");
                tvSymbol.setTextColor(R.color.gray_b2b2);
            }
            return _v;
        }
    }

    private void createTimer() {
        if (timer == null) {
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
//                    Date date = new Date();
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//                    String today = formatter.format(date);
                    tvCountDown.setText(intCD+"");
                    intCD--;
                    if (intCD <= 0) {
                        flCover.setVisibility(View.INVISIBLE);
                        releaseTimer();
                    }
                }
            };
            timer.schedule(task,0, 1000);
        }
    }

    private void releaseTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: =====");
        releaseTimer();
    }

}