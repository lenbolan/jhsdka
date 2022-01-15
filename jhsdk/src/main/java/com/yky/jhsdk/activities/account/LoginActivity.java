package com.yky.jhsdk.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.yky.jhsdk.R;
import com.yky.jhsdk.activities.AdActivity;
import com.yky.jhsdk.models.httpModel.LoginHttpModel;
import com.yky.jhsdk.service.Reporter;
import com.yky.jhsdk.utils.LLog;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

    private static final String TAG = "LoginActivity";

    protected TextView txtTitle;

    protected Button btnBack;

    @NotEmpty(message = "手机号不能为空")
    protected EditText txtPhone;

    @NotEmpty(message = "密码不能为空")
    @Password(min = 6, message = "密码最少为6位")
    protected EditText txtPwd;

    protected Button btnLogin;

    protected Button btnToRegist;

    private Validator validator;

    protected String strNavi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        strNavi = getIntent().getStringExtra("stat");

        txtTitle = (TextView) findViewById(R.id.text_title);
        btnBack = (Button) findViewById(R.id.button_backward);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtPwd = (EditText) findViewById(R.id.txtPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnToRegist = (Button) findViewById(R.id.btnToRegist);

        UtilitySecurityListener.setOnClickListener(this, btnLogin);
        UtilitySecurityListener.setOnClickListener(this, btnToRegist);

        txtTitle.setText("登录");

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick()) {
            return;
        }

        int viewId = view.getId();
        if (viewId == R.id.button_backward) {
            this.finish();
            return;
        }
        if (viewId == R.id.btnLogin) {
            validator.validate();
            return;
        }
        if (viewId == R.id.btnToRegist) {
            onTapToRegist();
            return;
        }
    }

    @Override
    public void onValidationSucceeded() {
        Log.d(TAG, "onValidationSucceeded: 验证成功");
        LoginHttpModel model = new LoginHttpModel();
        model.username = txtPhone.getText().toString();
        model.password = txtPwd.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = model.getUrl();

        JSONObject params = new JSONObject();
        try {
            params.put("username", model.username);
            params.put("password", model.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    LLog.d("Response: " + response.toString());
                    try {
                        setResponse(response.toString());
                    } catch (Exception ex) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    LLog.d("That didn't work!");
                }
            });

        queue.add(jsonObjectRequest);
    }

    private void setResponse(String s) {
        try {
            JSONObject dRoot = new JSONObject(s);
            int code = dRoot.getInt("code");
            if (code == 0) {
                JSONObject d = dRoot.getJSONObject("data");
                int uid = d.getInt("uid");
                String token = d.getString("token");
                if (uid > 0 && token != null) {
                    String phoneNum = txtPhone.getText().toString();
                    Reporter.getInstance().rAdInit(uid, phoneNum, token);
                }
                Reporter.getInstance().rAdBaseInfo();
                finish();

                Intent intent = new Intent(getBaseContext(), AdActivity.class);
                intent.putExtra("stat", strNavi);
                startActivity(intent);

                Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
            } else {
                String msg = dRoot.getString("msg");
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            return;
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onTapToRegist() {
        finish();
        Intent intent = new Intent(getBaseContext(), RegistrationActivity.class);
        intent.putExtra("stat", strNavi);
        startActivity(intent);
    }

}
