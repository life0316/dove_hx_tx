package com.haoxi.dove.newin.trail.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.MyDoveAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.ToSetHolderListener;
import com.haoxi.dove.holder.MyRouteHolder;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerRouteDoveComponent;
import com.haoxi.dove.inject.RouteDoveMoudle;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;

@ActivityFragmentInject(contentViewId = R.layout.activity_base_srl_rv)
public class RouteDoveActivity extends BaseActivity implements IGetPigeonView, SwipeRefreshLayout.OnRefreshListener, ToSetHolderListener<InnerDoveData>,MyItemClickListener {

    @BindView(R.id.base_srl)
    SwipeRefreshLayout mSrl;

    @BindView(R.id.base_srl_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;

    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;

    @Inject
    MyDoveAdapter doveAdapter;

    @Inject
    RxBus mRxBus;

    @Inject
    MyPigeonPresenter mPresenter;

    private boolean isLoad = true;

    private List<InnerDoveData> doveDatas = new ArrayList<>();

    private int methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;

    @Override
    protected void initInject() {
        DaggerRouteDoveComponent.builder()
                .appComponent(getAppComponent())
                .routeDoveMoudle(new RouteDoveMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        mTitleTv.setText("信鸽");
        mBackIv.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        mRecyclerView.setAdapter(doveAdapter);

        mSrl.setOnRefreshListener(this);
        mSrl.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        doveAdapter.setToSetHolderListener(this);
        doveAdapter.setOnItemClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isLoad) {

            getDatas();
        }

        isLoad = false;
    }


    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(this)) {
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

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {

        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = "/app/dove/search";
                break;
        }
        return method;
    }

    @Override
    public void onRefresh() {
        getDatas();
    }

    @Override
    public String getUserObjId() {
        return mUserObjId;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public void setPigeonData(List<InnerDoveData> pigeonData) {

        mSrl.setRefreshing(false);

        if (pigeonData != null){
            doveDatas.clear();
            doveDatas.addAll(pigeonData);
            doveAdapter.addDatas(pigeonData);
        }
    }

//    @Override
//    public void setRefrash(boolean isRefrash) {
//            mSrl.setRefreshing(isRefrash);
//    }

    @Override
    public void toSetHolder(MyRouteHolder holder, InnerDoveData data, int position) {
        holder.mTitleTv.setText("信鸽： "+data.getFoot_ring());
    }

    @Override
    public void onItemClick(View view, int position) {
        InnerDoveData doveData = doveDatas.get(position);
        Intent intent = new Intent(RouteDoveActivity.this,RouteTitleActivity.class);
        intent.putExtra("dove",doveData);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        super.onBackPressed();
    }
}
