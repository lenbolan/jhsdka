package com.yky.jhsdk.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QQAdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QQAdFragment extends Fragment implements RewardVideoADListener {

    private RewardVideoAD rewardVideoAD;
    private Boolean adLoaded = false;
    private Boolean videoCached = false;

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

    private RQqAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "QQAdFragment";
    private static final String TAG2 = "QQAdStatus";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Activity self;

    public QQAdFragment() {
        // Required empty public constructor
        self = this.getActivity();

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

        String keyDate = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_QQ_DATE);

        if (today.equals(keyDate)) {
            ad_watch_times = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_QQ_STATE);
        } else {
            EditSharedPreferences.writeStringToConfig(EditSharedPreferences.AD_QQ_DATE, today);
            EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_QQ_STATE, 0);
        }
    }

    private void updateViewData() {
        ad_watch_times = Math.min(ad_watch_times, 15);
        EditSharedPreferences.writeIntToConfig(EditSharedPreferences.AD_QQ_STATE, ad_watch_times);
        dataSet[ad_watch_times-1] = 1;
        adapter.notifyDataSetChanged();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QQAdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QQAdFragment newInstance(String param1, String param2) {
        QQAdFragment fragment = new QQAdFragment();
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
        String slotId = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.AD_QQ_SLOTID_REWAED);
        if ("".equals(slotId)) {
            Log.i(TAG, "initAd: qq reward slot id is empty.");
            slotId = "9011264358826997";
        }
        return slotId;
    }

    private void initAd() {
        if (rewardVideoAD != null) {
            rewardVideoAD = null;
        }

        isRepeat = 0;
        curAdId = Reporter.getInstance().createAdId(4);
        curAdOpenTime = Utility.getTimestamp();
        curAdStartLoadTime = curAdOpenTime;

        Reporter.getInstance().rAdStart(AdPlatform.AdPlatformGDT, AdType.AdTypeReward, curAdId, curAdStartLoadTime);

        String slotId = getSlotId();

        Log.d(TAG, "initAd: qq reward slotId = " + slotId);

        rewardVideoAD = new RewardVideoAD(this.getContext(), slotId, this);
        rewardVideoAD.loadAD();
    }

    private void showAd() {
        if (adLoaded) {
            if (!rewardVideoAD.hasShown()) {
                long delta = 1000;
                if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
                    rewardVideoAD.showAD();
                } else {
                    Toast.makeText(this.getContext(), "激励视频广告已过期，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
                    initAd();
                }
            } else {
                Toast.makeText(this.getContext(), "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
                initAd();
            }
        } else {
            Toast.makeText(this.getContext(), "成功加载广告后再进行广告展示！", Toast.LENGTH_LONG).show();
            initAd();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q_q_ad, container, false);

        gridView = view.findViewById(R.id.gridViewQQ);
        adapter = new RQqAdapter(this.getContext(), dataSet);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Log.i(TAG, "onItemClicked: " + position);
                showAd();
            }
        });
        gridView.setAdapter(adapter);

//        Date date = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        String today = formatter.format(date);
//        tvToday = view.findViewById(R.id.ad_qq_today);
//        tvToday.setText(today);

//        tvScore = view.findViewById(R.id.ad_qq_score);
//        SpannableString spannableString = new SpannableString("已赚取("+ad_watch_times+")点券");
//        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvScore.setText(spannableString);

        flCover = view.findViewById(R.id.flCover);

        tvState = view.findViewById(R.id.ad_qq_state);
        tvState.setText(adState);

        tvCountDown = view.findViewById(R.id.tvCountDown);

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.finish();
            }
        });

//        tvUserId = view.findViewById(R.id.tvUserId);
//        tvUserId.setText("(ID: " + getUserId() + ")");

//        tvUCode = view.findViewById(R.id.ad_qq_ucode);
//        tvUCode.setText(createCode());

        initAd();

        return view;
    }

    private String createCode() {
        String key = "yhkpzx";
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String today = formatter.format(date);
        int userId = getUserId();
        String str = key+today+userId;
        try {
            return Hash.md5(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getUserId() {
        int userId = EditSharedPreferences.readIntFromConfig(EditSharedPreferences.AD_USER_ID);
        return userId;
    }

    @Override
    public void onADLoad() {
        adLoaded = true;
        Log.i(TAG2, "onADLoad: 广告加载成功，可在此回调后进行广告展示");
        adState = "广告加载成功";
        curAdDataLoadedTime = Utility.getTimestamp();
        tvState.setText(adState);
    }

    @Override
    public void onVideoCached() {
        videoCached = true;
        Log.i(TAG2, "onVideoCached: 视频素材缓存成功，可在此回调后进行广告展示");
        adState = "视频缓存成功";
        curAdVideoLoadedTime = Utility.getTimestamp();
        Reporter.getInstance().rAdLoaded(AdPlatform.AdPlatformGDT, AdType.AdTypeReward, curAdId, curAdDataLoadedTime);
        tvState.setText(adState);
    }

    @Override
    public void onADShow() {
        Log.i(TAG2, "onADShow: 激励视频广告页面展示");
    }

    @Override
    public void onADExpose() {
        Log.i(TAG2, "onADExpose: 激励视频广告曝光");
        curAdShowTime = Utility.getTimestamp();
        Reporter.getInstance().rAdShow(AdPlatform.AdPlatformGDT, AdType.AdTypeReward, curAdId, curAdVideoLoadedTime);
    }

    @Override
    public void onReward(Map<String, Object> map) {
        Log.i(TAG2, "onReward: 激励视频触发激励");
        ad_watch_times += 1;
    }

    @Override
    public void onADClick() {
        Log.i(TAG2, "onADClick: 激励视频广告被点击");

        Reporter.getInstance().rAdClick(AdPlatform.AdPlatformGDT, AdType.AdTypeReward, curAdId, curAdShowTime, isRepeat);
        if (isRepeat == 0) isRepeat += 1;
    }

    @Override
    public void onVideoComplete() {
        Log.i(TAG2, "onVideoComplete: 激励视频播放完毕");
    }

    @Override
    public void onADClose() {
        Log.i(TAG2, "onADClose: 激励视频广告被关闭");

        Reporter.getInstance().rAdClose(AdPlatform.AdPlatformGDT,
                AdType.AdTypeReward,
                curAdId,
                curAdOpenTime,
                curAdStartLoadTime,
                curAdDataLoadedTime,
                curAdVideoLoadedTime,
                curAdShowTime,
                curAdClickTime,
                curAdJumpTime);

        initAd();
        updateViewData();

        showCover();
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
    public void onError(AdError adError) {
        String msg = String.format(TAG,
                "onError, error code: %d, error msg: %s",
                adError.getErrorCode(),
                adError.getErrorMsg());
        Log.e(TAG2, msg);
        adState = "错误：" + adError.getErrorCode() + "|" + adError.getErrorMsg();
        tvState.setText(adState);
        Reporter.getInstance().rAdError(AdPlatform.AdPlatformGDT, AdType.AdTypeReward, msg, curAdId);
    }

    private static class RQqAdapter extends BaseAdapter {

        private IItemClickListener itemClickListener;

        private LayoutInflater layoutInflater;
        private int[] data;

        public void setItemClickListener(IItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public RQqAdapter(Context context, int[] dataSet) {
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