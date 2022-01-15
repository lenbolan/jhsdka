package com.yky.jhsdk.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.yky.jhsdk.R;
import com.yky.jhsdk.adapters.AdAdapter;
import com.yky.jhsdk.events.EventType;

public class AdActivity extends AppCompatActivity {

    protected TabLayout tabLayout;
    protected ViewPager viewPager;

    private AdAdapter adAdapter;
    private List<String> tabs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        String stat = getIntent().getStringExtra("stat");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabs = new ArrayList<>();

        if (stat.equals(EventType.NAVI_TO_BUAD)) {
            tabs.add("签到");
        } else if (stat.equals(EventType.NAVI_TO_QQAD)) {
            tabs.add("打卡");
        } else {
            tabs.add("积分一档");
            tabs.add("积分二档");
            tabs.add("积分三档");
            tabs.add("点券一档");
            tabs.add("点券二档");
            tabs.add("点券三档");
        }

        adAdapter = new AdAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adAdapter);
        tabLayout.setupWithViewPager(viewPager);
        adAdapter.setTabs(tabs);
    }

}
