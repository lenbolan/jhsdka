package uni.UNI4950687;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.renrui.libraries.interfaces.IHttpRequestCancelInterFace;
import com.renrui.libraries.util.LibrariesCons;
import com.renrui.libraries.util.Logger;
import com.renrui.libraries.util.mHttpClient;

import com.yky.jhsdk.AppManager;
import com.yky.jhsdk.activities.AdActivity;
import com.yky.jhsdk.activities.account.LoginActivity;
import com.yky.jhsdk.activities.account.RegistrationActivity;
import com.yky.jhsdk.events.EventType;
import com.yky.jhsdk.models.httpModel.ad.AdDataHttpModel;

import java.util.Observable;
import java.util.Observer;

import uni.UNI4950687.utils.AppUtils;
import uni.UNI4950687.utils.EditSharedPreferences;

public class MyApplication extends Application implements Observer {

    private static final String TAG = "MyApplication";
    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        LibrariesCons.setContext(mContext);
        AppManager.setContext(mContext);

        if (this instanceof Observer) {
            Messager.getInstance().addObserver(this);
        }

        String packageName = AppUtils.getPackageName(mContext);
        String versionName = AppUtils.getVersionName(mContext);
        int versionCode = AppUtils.getVersionCode(mContext);
        Log.i(TAG, "onCreate: " + packageName + ", " + versionName + ", " + versionCode);

        AdDataHttpModel httpModel = new AdDataHttpModel();
        httpModel.setIsPostJson(false);
        httpModel.bunddleid = packageName;
        httpModel.version = versionCode;
        mHttpClient.Request(mContext, httpModel, new IHttpRequestCancelInterFace() {
            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onStart() {
                Log.i(TAG, "onStart: ");
            }

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "onResponse: " + s);
                try {
                    AppManager.setupParams(s);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(String s) {
                Log.d(TAG, "onErrorResponse: " + s);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: ");
            }
        });
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof String) {
            String message = (String)data;
            AppManager.popup(message);
        }
    }

}
