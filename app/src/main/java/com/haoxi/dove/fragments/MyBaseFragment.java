package com.haoxi.dove.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.base.MyBaseAdapter;
import com.haoxi.dove.newin.ourcircle.ui.AttenFriendActivity;
import com.haoxi.dove.newin.ourcircle.ui.ReleaseCircleActivity;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.CustomViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
public abstract class MyBaseFragment extends Fragment {

    @BindView(R.id.fragment_base_add)
    ImageView mTabAddIv;
    @BindView(R.id.fragment_base_add2)
    ImageView mTabAdd2Iv;
    @BindView(R.id.fragment_base_search)
    ImageView mTabSearchIv;
    @BindView(R.id.fragment_base_title)
    TextView mTabTitleTv;
    @BindView(R.id.fragment_base_tabs)
    TabLayout mTablayout;
    @BindView(R.id.fragment_base_cvp)
    CustomViewPager mViewPager;
    @BindView(R.id.fragment_base_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_base_cancle)
    public TextView mCancleTv;

    protected MyBaseAdapter adapter;

    protected int viewpagerTagNum = 0;
    protected Observable<Boolean> cancleObservable;
    protected List<String> mateList;

    protected Bundle getBundle(String arg) {
        Bundle bundle = new Bundle();
        bundle.putString("key", arg);
        return bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybase, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mateList = MyApplication.getMyBaseApplication().getMateList();

        initToolbar(view);

        isPaging(true);
        mViewPager.setOffscreenPageLimit(2);
        adapter = new MyBaseAdapter(getChildFragmentManager(), mViewPager, mTablayout, getActivity());
        mTablayout.setupWithViewPager(mViewPager);

        mTabSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AttenFriendActivity.class);
                startActivity(intent);
            }
        });

        mCancleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpagerTagNum = mViewPager.getCurrentItem();
                RxBus.getInstance().post(ConstantUtils.OBSER_CANCLE, true);
                RxBus.getInstance().post("tagnum", viewpagerTagNum);
            }
        });

        mTabAdd2Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReleaseCircleActivity.class);
                startActivity(intent);
            }
        });

        adapter.clearFragment();
        setupAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        cancleObservable = RxBus.getInstance().register("cancle", Boolean.class);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister("cancle", cancleObservable);
    }

    protected void isPaging(boolean ispag) {
        mViewPager.setPagingEnabled(ispag);
    }

    protected abstract void initToolbar(View view);

    public abstract void setupAdapter(MyBaseAdapter adapter);

}
