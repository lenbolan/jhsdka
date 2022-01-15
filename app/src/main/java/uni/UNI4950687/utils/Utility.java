package uni.UNI4950687.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import uni.UNI4950687.MyApplication;

public class Utility {

    public static String loadData(AssetManager am, String resFile) {

        String jsonData = "";
        try {
            InputStream inputStream = am.open(resFile);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            jsonData = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public static int dip2px(float dipValue) {
        final float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private static Rect seekRect;

    /**
     * 增加seekBar可点击区域
     *
     * @param sb
     */
    public static void addSeekBarTouchPoint(SeekBar sb) {
        if (sb == null) {
            return;
        }

        try {
            ViewGroup vp = (ViewGroup) sb.getParent();
            vp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    seekRect = new Rect();
                    sb.getHitRect(seekRect);

                    if ((event.getY() >= (seekRect.top - 50)) && (event.getY() <= (seekRect.bottom + 50))) {

                        float y = seekRect.top + seekRect.height() / 2;
                        //seekBar only accept relative x
                        float x = event.getX() - seekRect.left;
                        if (x < 0) {
                            x = 0;
                        } else if (x > seekRect.width()) {
                            x = seekRect.width();
                        }
                        MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                                event.getAction(), x, y, event.getMetaState());
                        return sb.onTouchEvent(me);

                    }
                    return false;
                }
            });
        } catch (Exception ex) {

        }
    }

    /**
     * 获取一条随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString().toUpperCase();
    }

    /***
     * 获取时间戳 秒
     * @return 时间戳 秒
     */
    public static long getTimestamp() {
//        long time1 = new Date().getTime();
//        long time2 = System.currentTimeMillis();
//        long time3 = Calendar.getInstance().getTimeInMillis();
        return (long) (System.currentTimeMillis() / 1000);
    }

    public static String getSimOperator(Context context) {
        String opeName = "";
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String opeNum = teleManager.getSimOperator();
        if ("46001".equals(opeNum) || "46006".equals(opeNum) || "46009".equals(opeNum)) {
            opeName = "中国联通";
        } else if ("46000".equals(opeNum) || "46002".equals(opeNum) || "46004".equals(opeNum) || "46007".equals(opeNum)) {
            opeName = "中国移动";
        } else if ("46003".equals(opeNum) || "46005".equals(opeNum) || "46011".equals(opeNum)) {
            opeName = "中国电信";
        }
        return opeName;
    }

    public static String getNetType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        switch (manager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";  // 2G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return "3G"; // 3G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                return "4G";   // 4G网络
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";//5G网络
            default:
                return "";
        }
    }


}