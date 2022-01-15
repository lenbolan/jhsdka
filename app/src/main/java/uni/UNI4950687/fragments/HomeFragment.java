package uni.UNI4950687.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import uni.UNI4950687.R;
import uni.UNI4950687.activities.AddActivity;
import uni.UNI4950687.adapters.ItemAdapter;
import uni.UNI4950687.db.DBHelper;
import uni.UNI4950687.db.SupplierContract;
import uni.UNI4950687.interfaces.IMyListListener;
import uni.UNI4950687.models.SupplierModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnClickListener {

    private static final String TAG = "MainActivity";
    private static Context mContext;

    protected ArrayList<SupplierModel> models;

    protected DBHelper dbHelper;
    protected SQLiteDatabase database;

    protected ItemAdapter itemAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView mRecyclerView;
    protected EditText txtSearch;
    protected Button btnSearch;
    protected Button btnAdd;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        mContext = this.getContext();
        Stetho.initializeWithDefaults(this.getContext());

        dbHelper = new DBHelper(this.getContext(), DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        database = dbHelper.getWritableDatabase();

        models = new ArrayList<SupplierModel>();

        queryData(null);

        itemAdapter = new ItemAdapter(models);
        itemAdapter.setOnClickDelete(new IMyListListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onClick: " + position);
                SupplierModel model = models.get(position);
                Log.d(TAG, "onClick: " + model.toString());
                database.delete(SupplierContract.SupplierEntry.TABLE_NAME, SupplierContract.SupplierEntry._ID + "=?", new String[]{model.id});
                if (itemAdapter != null) {
                    models.remove(position);
                    itemAdapter.notifyItemRemoved(position);
                }
            }
        });
        itemAdapter.setOnClickEdit(new IMyListListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onClick: " + position);
                Intent intent = new Intent(mContext, AddActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("model", models.get(position));
                startActivity(intent);
            }
        });

        mLayoutManager = new LinearLayoutManager(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(itemAdapter);

        txtSearch = view.findViewById(R.id.txtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnSearch.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        return view;
    }

    private void queryData(String key) {

        String[] projection = {
                BaseColumns._ID,
                SupplierContract.SupplierEntry.COLUMN_NAME_NAME,
                SupplierContract.SupplierEntry.COLUMN_NAME_AREA,
                SupplierContract.SupplierEntry.COLUMN_NAME_ADDRESS,
                SupplierContract.SupplierEntry.COLUMN_NAME_TEL,
                SupplierContract.SupplierEntry.COLUMN_NAME_DELIVERY
        };

        String selection = null;
        String[] selectionArgs = null;

        if (key != null && key.length() > 0) {
            selection = SupplierContract.SupplierEntry.COLUMN_NAME_NAME + " like ?";
            selectionArgs = new String[] { "%" + key + "%" };
        }

        String sortOrder = SupplierContract.SupplierEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = database.query(
                SupplierContract.SupplierEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        models.removeAll(models);

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry._ID));
            String strName = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_NAME_NAME));
            String strArea = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_NAME_AREA));
            String strAddress = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_NAME_ADDRESS));
            String strTel = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_NAME_TEL));
            String strDelivery = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_NAME_DELIVERY));

            SupplierModel model = new SupplierModel();
            model.id = itemId+"";
            model.name = strName;
            model.address = strAddress;
            model.area = strArea;
            model.tel = strTel;
            model.delivery = strDelivery;
            models.add(model);
        }
        cursor.close();

        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }

        if (key == null && cursor.getCount() == 0) {
            initData();
        }
    }

    private void initData() {
        ContentValues values = new ContentValues();
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_NAME, "厦门艺华餐饮有限公司");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_AREA, "翔安区");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_ADDRESS, "翔安东路56-3号");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_TEL, "05923677545");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_DELIVERY, "货车");
        long newRowId = database.insert(SupplierContract.SupplierEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_NAME, "友缘茶业");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_AREA, "翔安区");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_ADDRESS, "祥吴一路23A-13");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_TEL, "05923766525");
        values.put(SupplierContract.SupplierEntry.COLUMN_NAME_DELIVERY, "轮船");
        newRowId = database.insert(SupplierContract.SupplierEntry.TABLE_NAME, null, values);

        SupplierModel model = new SupplierModel();
        model.id = "#123456";
        model.name = "厦门艺华餐饮有限公司";
        model.address = "翔安东路56-3号";
        model.area = "翔安区";
        model.tel = "05923677545";
        model.delivery = "货车";
        models.add(model);

        model = new SupplierModel();
        model.id = "#123457";
        model.name = "友缘茶业";
        model.address = "祥吴一路23A-13";
        model.area = "翔安区";
        model.tel = "05923766525";
        model.delivery = "船运";
        models.add(model);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                Log.d(TAG, "onClick: add button");
                Intent intent = new Intent(this.getContext(), AddActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSearch:
                Log.d(TAG, "onClick: search button");
                String strSearch = txtSearch.getText().toString();
                queryData(strSearch);
                break;
        }
    }

}