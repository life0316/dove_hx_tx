package com.haoxi.dove.fragments;

import android.os.Bundle;
import android.view.View;

import com.haoxi.dove.base.MyBaseAdapter;
import com.haoxi.dove.modules.circle.AllCircleFragment;
import com.haoxi.dove.modules.circle.FriendCircleFragment;
import com.haoxi.dove.modules.circle.MyCircleFragment;
import com.haoxi.dove.newin.ourcircle.ui.CircleFragment;

import static android.view.View.VISIBLE;

/**
 * Created by lifei on 2016/12/27.
 */

public class PigeonCircleFragment extends MyBaseFragment {

    @Override
    protected void initToolbar(View view) {
        mTabTitleTv.setText("鸽圈");
        mTabAddIv.setVisibility(View.GONE);
        mTabSearchIv.setVisibility(VISIBLE);
    }

    @Override
    public void setupAdapter(MyBaseAdapter adapter) {
        adapter.addFragment(AllCircleFragment.class,"鸽圈",getBundle("鸽圈"));
        adapter.addFragment(FriendCircleFragment.class,"好友圈",getBundle("好友圈"));
        adapter.addFragment(MyCircleFragment.class,"我的鸽圈",getBundle("我的鸽圈"));
    }

    public static PigeonCircleFragment newInstance(String content){
        Bundle args = new Bundle();
        args.putString("ARGS",content);
        PigeonCircleFragment fragment = new PigeonCircleFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
