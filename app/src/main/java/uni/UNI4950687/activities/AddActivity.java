package uni.UNI4950687.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import uni.UNI4950687.R;
import uni.UNI4950687.db.DBHelper;
import uni.UNI4950687.db.SupplierContract.SupplierEntry;
import uni.UNI4950687.models.DeliveryEnum;
import uni.UNI4950687.models.SupplierModel;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddActivity";

    protected JDCityPicker cityPicker = new JDCityPicker();
    protected JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private boolean isEdit;
    private SupplierModel model;

    protected TextView lbName;
    protected TextView lbTel;
    protected TextView lbArea;
    protected TextView lbAddress;
    protected TextView lbDelivery;

    protected EditText txtName;
    protected EditText txtTel;
    protected EditText txtArea;
    protected EditText txtAddress;
    protected RadioGroup rgDelivery;

    protected Button btnSubmit;

    protected String strName;
    protected String strTel;
    protected String strArea;
    protected String strAddress;
    protected String strDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        model = (SupplierModel)getIntent().getSerializableExtra("model");

        Log.d(TAG, "onCreate: " + isEdit);
        Log.d(TAG, "onCreate: " + model);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.btnAdd);

        dbHelper = new DBHelper(this, DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        database = dbHelper.getWritableDatabase();

        jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
        cityPicker.init(this);
        cityPicker.setConfig(jdCityConfig);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                String res0 = province.getName() + "\n(" + province.getId() + ")\n"
                         + city.getName() + "(" + city.getId() + ")\n"
                         + district.getName() + "(" + district.getId() + ")";
                Log.d(TAG, "onSelected: " + res0);

                String res = province.getName() + city.getName() + district.getName();
                txtArea.setText(res);
            }

            @Override
            public void onCancel() {}
        });

        strName = "";
        strTel = "";
        strArea = "";
        strAddress = "";
        strDelivery = "";

        lbName = findViewById(R.id.lbName);
        lbTel = findViewById(R.id.lbTel);
        lbArea = findViewById(R.id.lbArea);
        lbAddress = findViewById(R.id.lbAddress);
        lbDelivery = findViewById(R.id.lbDelivery);

        txtName = findViewById(R.id.txtName);
        txtTel = findViewById(R.id.txtTel);
        txtArea = findViewById(R.id.txtArea);
        txtAddress = findViewById(R.id.txtAddress);
        rgDelivery = findViewById(R.id.rgDelivery);

        btnSubmit = findViewById(R.id.btnSubmit);

        txtArea.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        rgDelivery.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButton1:
                        strDelivery = DeliveryEnum.Airlift.toString();
                        break;
                    case R.id.radioButton2:
                        strDelivery = DeliveryEnum.Train.toString();
                        break;
                    case R.id.radioButton3:
                        strDelivery = DeliveryEnum.Truck.toString();
                        break;
                    case R.id.radioButton4:
                        strDelivery = DeliveryEnum.Shipping.toString();
                        break;
                }
            }
        });

        initUI();
        setEditData();
    }

    protected void setEditData() {
        if (isEdit && model != null) {
            txtName.setText(model.name);
            txtArea.setText(model.area);
            txtAddress.setText(model.address);
            txtTel.setText(model.tel);

            strDelivery = model.delivery;
            if (strDelivery.equals(DeliveryEnum.Airlift.toString())) {
                rgDelivery.check(R.id.radioButton1);
            } else if (strDelivery.equals(DeliveryEnum.Train.toString())) {
                rgDelivery.check(R.id.radioButton2);
            } else if (strDelivery.equals(DeliveryEnum.Truck.toString())) {
                rgDelivery.check(R.id.radioButton3);
            } else if (strDelivery.equals(DeliveryEnum.Shipping.toString())) {
                rgDelivery.check(R.id.radioButton4);
            }

            btnSubmit.setText("修  改");
        }
    }

    protected void initUI() {
        setRedStar(lbName);
        setRedStar(lbTel);
        setRedStar(lbArea);
        setRedStar(lbAddress);
        setRedStar(lbDelivery);
    }

    protected void setRedStar(TextView label) {
        String str = label.getText().toString();
        int len = str.length();
        SpannableString textSpanned1 = new SpannableString(str);
        textSpanned1.setSpan(new ForegroundColorSpan(Color.RED), len-1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        label.setText(textSpanned1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtArea:
                cityPicker.showCityPicker();
                break;
            case R.id.btnSubmit:
                boolean isVerified = verifyForm();
                if (isVerified) {
                    if (isEdit && model != null) {
                        ContentValues values = new ContentValues();
                        values.put(SupplierEntry.COLUMN_NAME_NAME, strName);
                        values.put(SupplierEntry.COLUMN_NAME_AREA, strArea);
                        values.put(SupplierEntry.COLUMN_NAME_ADDRESS, strAddress);
                        values.put(SupplierEntry.COLUMN_NAME_TEL, strTel);
                        values.put(SupplierEntry.COLUMN_NAME_DELIVERY, strDelivery);
                        long rowId = database.update(SupplierEntry.TABLE_NAME, values, SupplierEntry._ID + "=?", new String[]{model.id});
                        if (rowId > 0) {
                            Log.d(TAG, "onClick: database update success...");
                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(SupplierEntry.COLUMN_NAME_NAME, strName);
                        values.put(SupplierEntry.COLUMN_NAME_AREA, strArea);
                        values.put(SupplierEntry.COLUMN_NAME_ADDRESS, strAddress);
                        values.put(SupplierEntry.COLUMN_NAME_TEL, strTel);
                        values.put(SupplierEntry.COLUMN_NAME_DELIVERY, strDelivery);
                        long newRowId = database.insert(SupplierEntry.TABLE_NAME, null, values);
                        if (newRowId > 0) {
                            Log.d(TAG, "onClick: database insert success...");
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    private boolean verifyForm() {
        Log.d(TAG, "verifyForm: " + strDelivery);

        strName = txtName.getText().toString().trim();
        strTel = txtTel.getText().toString().trim();
        strArea = txtArea.getText().toString().trim();
        strAddress = txtAddress.getText().toString().trim();

        if (strName.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入供应商名称", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strTel.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入联系方式", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strArea.equals("")) {
            Toast.makeText(getApplicationContext(), "请选择区域", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strAddress.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入详细地址", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strDelivery.equals("")) {
            Toast.makeText(getApplicationContext(), "请选择运输方式", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}