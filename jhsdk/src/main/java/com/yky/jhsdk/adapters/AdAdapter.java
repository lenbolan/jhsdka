package com.yky.jhsdk.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import com.yky.jhsdk.fragments.BUAdFragment;
import com.yky.jhsdk.fragments.BUBannerFragment;
import com.yky.jhsdk.fragments.BUInfoFlowFragment;
import com.yky.jhsdk.fragments.QQAdFragment;
import com.yky.jhsdk.fragments.QQBannerFragment;
import com.yky.jhsdk.fragments.QQInfoFlowFragment;

public class AdAdapter extends FragmentPagerAdapter {

    private List<String> tabs;

    public AdAdapter(FragmentManager fm) {
        super(fm);
        this.tabs = new ArrayList<>();
    }

    public void setTabs(List<String> list) {
        this.tabs.clear();
        this.tabs.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        String tabTitle = tabs.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("param1", tabTitle);

        switch (tabTitle) {
            case "签到":
            case "积分一档":
                BUAdFragment buAdFragment = new BUAdFragment();
                buAdFragment.setArguments(bundle);
                return buAdFragment;
            case "积分二档":
                BUBannerFragment buBannerFragment = new BUBannerFragment();
                buBannerFragment.setArguments(bundle);
                return buBannerFragment;
            case "积分三档":
                BUInfoFlowFragment buInfoFlowFragment = new BUInfoFlowFragment();
                buInfoFlowFragment.setArguments(bundle);
                return buInfoFlowFragment;
            case "打卡":
            case "点券一档":
                QQAdFragment qqAdFragment = new QQAdFragment();
                qqAdFragment.setArguments(bundle);
                return qqAdFragment;
            case "点券二档":
                QQBannerFragment qqBannerFragment = new QQBannerFragment();
                qqBannerFragment.setArguments(bundle);
                return qqBannerFragment;
            case "点券三档":
                QQInfoFlowFragment qqInfoFlowFragment = new QQInfoFlowFragment();
                qqInfoFlowFragment.setArguments(bundle);
                return qqInfoFlowFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String tabName = tabs.get(position);
        if (tabName == null) {
            tabName = "";
        } else if (tabName.length() > 15) {
            tabName = tabName.substring(0, 15) + "...";
        }
        return tabName;
    }
}
