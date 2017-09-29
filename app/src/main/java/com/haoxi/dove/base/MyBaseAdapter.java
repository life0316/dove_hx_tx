package com.haoxi.dove.base;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyBaseAdapter extends FragmentPagerAdapter {

    private final ViewPager mViewPager;
    private final Context context;
    private final TabLayout mTabLayout;

    ArrayList<Class<?>> fragments=new ArrayList<Class<?>>();
    List<String> mFragmentTitleList = new ArrayList<>();
    ArrayList<Bundle> bundles = new ArrayList<Bundle>();
    public MyBaseAdapter(FragmentManager fm, ViewPager mViewPager, TabLayout mTabLayout, Context context) {
        super(fm);
        this.mTabLayout=mTabLayout;
        this.mViewPager=mViewPager;
        this.context = context;
        //设置数据适配器
        this.mViewPager.setAdapter(this);
        this.mTabLayout.setupWithViewPager(mViewPager);
    }

    public void addFragment(Class<?> fragment, String title, Bundle bundle){
        fragments.add(fragment);
        mFragmentTitleList.add(title);
        bundles.add(bundle);
        notifyDataSetChanged();
    }

    public void clearFragment(){
        fragments.clear();
        mFragmentTitleList.clear();
        bundles.clear();
    }

    @Override
    public Fragment getItem(int position) {
        Class<?> clazz=fragments.get(position);
        return Fragment.instantiate(context,clazz.getName(), bundles.get(position));
    }

    @Override
    public int getCount() {
        return fragments.size()==0?0:fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
