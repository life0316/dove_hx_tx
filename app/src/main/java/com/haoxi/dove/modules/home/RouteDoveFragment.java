package com.haoxi.dove.modules.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.MyDoveAdapter;
import com.haoxi.dove.base.BaseRvFragment2;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.ToSetHolderListener;
import com.haoxi.dove.holder.MyRouteHolder;
import com.haoxi.dove.inject.DaggerRouteDFComponent;
import com.haoxi.dove.inject.RouteDFMoudle;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.ui.RouteTitleActivity;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Administrator on 2017\7\6 0006.
 */

public class RouteDoveFragment extends BaseRvFragment2 implements IGetPigeonView, ToSetHolderListener<InnerDoveData>,MyItemClickListener, OnRefreshListener {


    private int methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.fragment_mypigeon_swiprv)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_mypigeon_show_add)
    LinearLayout mShowAddLv;
    @BindView(R.id.fragment_mypigeon_show_add_tv1)
    TextView mShowAddTv;
    @BindView(R.id.fragment_mypigeon_show_add_tv2)
    TextView mShowAddTv2;

    private boolean isLoad = true;
    private List<InnerDoveData> doveDatas = new ArrayList<>();

    @Inject
    MyDoveAdapter doveAdapter;

    @Inject
    RxBus mRxBus;

    @Inject
    MyPigeonPresenter mPresenter;


    public static RouteDoveFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        RouteDoveFragment fragment = new RouteDoveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        mShowAddTv2.setVisibility(View.GONE);

        mRecyclerView.setAdapter(doveAdapter);

        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(this);

        doveAdapter.setToSetHolderListener(this);
        doveAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoad) {
            getDatas();
        }

        isLoad = false;
    }
    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
            mPresenter.getDatas();
        } else {
            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
            mPresenter.getDataFromNets(getParaMap());
        }
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());

        map.put("userid",getUserObjId());
        map.put("token",getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                map.put("playerid",getUserObjId());
                break;
        }
        return map;
    }

//    @Override
//    public void onRefresh() {
//        getDatas();
//    }

    @Override
    public void toSetHolder(MyRouteHolder holder, InnerDoveData data, int position) {
        holder.mTitleTv.setText("信鸽： "+data.getFoot_ring());
    }

    @Override
    public void onItemClick(View view, int position) {
        InnerDoveData doveData = doveDatas.get(position);

        Intent intent = new Intent(getActivity(),RouteTitleActivity.class);
        intent.putExtra("dove",doveData);
        startActivity(intent);
    }

    @Override
    public String getMethod() {

        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = MethodConstant.DOVE_SEARCH;
                break;
        }
        return method;
    }

    @Override
    public void setPigeonData(List<InnerDoveData> pigeonData) {
//        mSrl.setRefreshing(false);
        refreshLayout.finishRefresh(false);
        if (pigeonData != null && pigeonData.size() != 0){
            doveDatas.clear();
            doveDatas.addAll(pigeonData);
            doveAdapter.addDatas(pigeonData);
            mShowAddLv.setVisibility(View.GONE);
            mShowAddTv.setVisibility(View.GONE);
        }else {
            doveDatas.clear();
            mShowAddLv.setVisibility(View.VISIBLE);
            mShowAddTv.setVisibility(View.VISIBLE);
            mShowAddTv.setText("暂时还没有行程记录");
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {
//        mSrl.setRefreshing(isRefrash);
        refreshLayout.finishRefresh(isRefrash);
    }

    @Override
    protected void inject() {

        DaggerRouteDFComponent.builder()
                .appComponent(getAppComponent())
                .routeDFMoudle(new RouteDFMoudle(getContext(),this))
                .build().inject(this);
    }
    @Override
    public String getUserObjId() {
        return userObjId;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
            mPresenter.getDatas();
        } else {
            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
            mPresenter.getDatasRefrash(getParaMap());
        }
    }
}
