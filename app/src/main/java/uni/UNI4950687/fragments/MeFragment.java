package uni.UNI4950687.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yky.jhsdk.AppManager;
import com.yky.jhsdk.events.EventType;

import java.util.ArrayList;
import java.util.List;

import uni.UNI4950687.Messager;
import uni.UNI4950687.R;
import uni.UNI4950687.activities.me.AboutUsActivity;
import uni.UNI4950687.activities.me.ContactUsActivity;
import uni.UNI4950687.adapters.MeItemAdapter;
import uni.UNI4950687.models.MeItem;
import uni.UNI4950687.utils.EditSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<MeItem> itemList = new ArrayList<>();

    public MeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        MeItemAdapter adapter = new MeItemAdapter(getContext(), R.layout.item_me, itemList);

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                MeItem meItem = itemList.get(i);
//                Toast.makeText(getContext(), meItem.title, Toast.LENGTH_SHORT).show();
                if (i == 0) {
                    Intent intent = new Intent(getContext(), AboutUsActivity.class);
                    startActivity(intent);
                } else if (i == 1) {
                    Intent intent = new Intent(getContext(), ContactUsActivity.class);
                    startActivity(intent);
                } else if (i == 2) {
                    Messager.getInstance().postMessage(EventType.NAVI_TO_BUAD);
                } else if (i == 3) {
                    Messager.getInstance().postMessage(EventType.NAVI_TO_QQAD);
                }
            }
        });

        itemList.removeAll(itemList);
        initData();

        return view;
    }

    private void initData() {
        MeItem meItem = new MeItem();
        meItem.title = "关于我们";
        meItem.iconId = R.drawable.chart;
        itemList.add(meItem);

        meItem = new MeItem();
        meItem.title = "联系我们";
        meItem.iconId = R.drawable.flag;
        itemList.add(meItem);

        String buAppId = AppManager.getBUAppID();
        if ("".equals(buAppId)) {

        } else {
            meItem = new MeItem();
            meItem.title = "签到";
            meItem.iconId = R.drawable.like;
            itemList.add(meItem);
        }

        String qqAppId = AppManager.getQQAppID();
        if ("".equals(qqAppId)) {

        } else {
            meItem = new MeItem();
            meItem.title = "打卡";
            meItem.iconId = R.drawable.ppt;
            itemList.add(meItem);
        }
    }

}