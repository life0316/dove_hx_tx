package com.haoxi.dove.modules.pigeon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.acts.PigeonActivity;
import com.haoxi.dove.adapter.MyPigeonAdapter;
import com.haoxi.dove.base.BaseRvFragment2;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.inject.DaggerMyPigeonComponent;
import com.haoxi.dove.inject.MyPigeonMoudle;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.CustomDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;


/**
 * Created by lifei on 2017/1/6.
 */

public class MyPigeonFragment extends BaseRvFragment2 implements IGetPigeonView, MyPigeonAdapter.MyItemCheckListener, MyPigeonAdapter.RecyclerViewOnItemClickListener, OnRefreshListener {

    private static final String TAG = "MyPigeonFragment";

    @BindView(R.id.fragment_mypigeon_tv_refrash)
    TextView mRefrashTv;
    @BindView(R.id.fragment_mypigeon_ll_refrash)
    LinearLayout mRefrashLl;
    @BindView(R.id.fragment_mypigeon_show_add)
    LinearLayout mShowAddLv;
//    @BindView(R.id.fragment_mypigeon_srl)
//    SwipeRefreshLayout mSrl;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.fragment_mypigeon_swiprv)
    RecyclerView mRecyclerView;
    @BindView(R.id.mypigeon_select)
    RelativeLayout mSelectRv;

    @BindView(R.id.fragment_mypigeon_no_network)
    TextView mNetworkTv;

    @BindView(R.id.mypigeon_select_cb)
    CheckBox mSelectCb;

    private int countTemp;

    @Inject
    MyPigeonPresenter mPresenter;

    @Inject
    DaoSession daoSession;

    @Inject
    OurCodePresenter ourCodePresenter;


    @Inject
    MyPigeonAdapter pigeonAdapter;

    @Inject
    Context mContext;

    private boolean isLoad = true;

    @Inject
    RxBus mRxBus;

    private Observable<Boolean> isLoadObservable;
    private Observable<Integer> mTagNumObservable;
    private Observable<Boolean> dataObservable;
    private Observable<Integer> clickObservable;
    private Observable<Integer> unbindObservable;

    private List<String> mPigeonCodes;


    private List<InnerDoveData> pigeonBeans = new ArrayList<>();
    private List<InnerDoveData> pigeonTemps = new ArrayList<>();
    private static Handler mHandler = new Handler();

    private int unbindTag = 5;
    private int clickRadio = 0;

    private boolean longClickTag;
    private CustomDialog dialog;

    private int methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!getUserVisibleHint()) {
            if (mRxBus != null) {
                mRxBus.post("tagnum", 6);
            }
        }
    }

    @Override
    protected void inject() {

        DaggerMyPigeonComponent.builder()
                .appComponent(getAppComponent())
                .myPigeonMoudle(new MyPigeonMoudle(mContext, this))
                .build()
                .inject(this);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPigeonCodes = MyApplication.getMyBaseApplication().getmPigeonCodes();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        mRecyclerView.setAdapter(pigeonAdapter);

//        mSrl.setOnRefreshListener(this);
//        mSrl.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(this);

        mSelectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Map<Integer, Boolean> map = pigeonAdapter.getMap();
                    for (int i = 0; i < map.size(); i++) {
                        map.put(i, true);
                        pigeonAdapter.notifyDataSetChanged();
                    }
                } else {

                    if (!(countTemp > 0 && countTemp < pigeonBeans.size())) {

                        Map<Integer, Boolean> m = pigeonAdapter.getMap();

                        for (int i = 0; i < m.size(); i++) {
                            m.put(i, false);
                            pigeonAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        });


        pigeonAdapter.setItemCheckListener(this);
        pigeonAdapter.setRecyclerViewOnItemClickListener(this);

        mRefrashLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatas();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        isLoadObservable = mRxBus.register("isLoad", Boolean.class);

        mTagNumObservable = mRxBus.register("tagnum", Integer.class);

        dataObservable = mRxBus.register("isLoadData", Boolean.class);

        clickObservable = mRxBus.register("clickRadio", Integer.class);
        unbindObservable = mRxBus.register("refrash", Integer.class);

        setObservable2();

        Log.e("isload-pigeon", isLoad + "-----pigeon");

        if (isLoad) {
            if (longClickTag) {
                mRxBus.post("tagnum", 6);
                if (dialog.isShowing() && dialog != null) {
                    dialog.dismiss();
                }
            }
            getDatas();
        } else {
            Log.e(TAG, unbindTag + "-------unbindTag");
            Log.e(TAG, clickRadio + "-------clickRadio");

            if (unbindTag == clickRadio) {
                if (longClickTag) {
                    mRxBus.post("tagnum", 6);
                    if (dialog.isShowing() && dialog != null) {
                        dialog.dismiss();
                    }
                }
                getDatas();
                unbindTag = 1;
            }
        }
        isLoad = true;
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
            case MethodType.METHOD_TYPE_DOVE_DELETE:
                map.put("doveid",getDeleteObjIds());
                break;
        }
        return map;
    }

    public void setObservable2() {

        isLoadObservable.subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage() + "--isLoadObservable--" + TAG);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                isLoad = aBoolean;
            }
        });

        dataObservable.subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage() + "--dataObservable--" + TAG);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                getDatas();
            }
        });

        unbindObservable.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage() + "--unbindObservable--" + TAG);
            }

            @Override
            public void onNext(Integer integer) {
                unbindTag = integer;
            }
        });

        clickObservable.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage() + "--clickObservable--" + TAG);
            }

            @Override
            public void onNext(Integer integer) {
                clickRadio = integer;

                Log.e(TAG, unbindTag + "-------unbindTag---");
                Log.e(TAG, clickRadio + "-------clickRadio---");

                if (unbindTag == clickRadio) {
                    getDatas();
                    unbindTag = 5;
                }
            }
        });

        mTagNumObservable.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage() + "--mTagNumObservable--" + TAG);
            }

            @Override
            public void onNext(Integer integer) {
                if (integer == 1) {
                    changeAdapter();
                } else if (integer == 6) {
                    changeAdapter();
                    isLoad = true;
                }
            }
        });
    }

    private void changeAdapter() {
        if (pigeonAdapter != null) {
            pigeonAdapter.setIsShow(false);
            pigeonAdapter.setLongClickTag(false);
            longClickTag = false;
            pigeonAdapter.setLongIntTag(0);
            pigeonAdapter.notifyDataSetChanged();
        }
        mSelectRv.setVisibility(View.GONE);
        mSelectCb.setChecked(false);
//        mSrl.setEnabled(true);
        refreshLayout.setEnableRefresh(true);
        mRxBus.post("exit", 30);
        mRxBus.post("cancle", 200);
    }


    public void setObservable() {

        isLoadObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aIsLoad) {
                isLoad = aIsLoad;
            }
        });

        dataObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                getDatas();
            }
        });

        unbindObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                unbindTag = integer;


            }
        });
        clickObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                clickRadio = integer;

                Log.e(TAG, unbindTag + "-------unbindTag---");
                Log.e(TAG, clickRadio + "-------clickRadio---");

                if (unbindTag == clickRadio) {
                    getDatas();
                    unbindTag = 5;
                }
            }
        });

        mTagNumObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == 1) {
                    changeAdapter();
                } else if (integer == 6) {
                    changeAdapter();
                    isLoad = true;
                }
            }
        });
    }

    public String getDeleteObjIds() {


        StringBuffer sb = new StringBuffer();

        if (pigeonTemps != null) {
            for (int i = 0; i < pigeonTemps.size(); i++) {

                if ((i == pigeonTemps.size() - 1)) {

                    sb.append(pigeonTemps.get(i).getDoveid());
                } else {
                    sb.append(pigeonTemps.get(i).getDoveid()).append(",");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void setNetTag(boolean netTag) {
        this.netTag = netTag;
    }

    @Override
    public void toDo() {

        mPresenter.deleteDatasFromData(getUserObjId());

        methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;

        //删除成功刷新界面
        mPresenter.getDataFromNets(getParaMap());

    }

    @Override
    public String getMethod() {
        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = "/app/dove/search";
                break;
            case MethodType.METHOD_TYPE_DOVE_DELETE:
                method = "/app/dove/delete";
                break;
        }

        return method;
    }


//    @Override
//    public void onRefresh() {
//
//        if (!ApiUtils.isNetworkConnected(getActivity())) {
//
//            Log.e(TAG,"------onRefresh");
//            mPresenter.getDatas();
//        } else {
//
//            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
//            mPresenter.getDatasRefrash(getParaMap());
//        }
//    }


    @Override
    public void setPigeonData(List<InnerDoveData> pigeonData) {

        setRefrash(false);
        pigeonBeans.clear();

        if (dialog != null) {

            Log.e(TAG, (dialog.isShowing()) + "----dialog---" + (dialog != null));

            if (dialog.isShowing()) {

                dialog.dismiss();
            }
        }


        if (pigeonData != null && pigeonData.size() != 0) {


            if (pigeonData.size() >= 15) {
                numMap.put("pigeon_num", true);
            } else {
                numMap.put("pigeon_num", false);
            }

            pigeonBeans.addAll(pigeonData);
            pigeonAdapter.addData(pigeonBeans);
//            mSrl.setEnabled(true);
            refreshLayout.setEnableRefresh(true);
            mShowAddLv.setVisibility(View.GONE);

            mRxBus.post("cancle", 200);
            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            pigeonAdapter.setIsShow(false);
            pigeonAdapter.setLongClickTag(false);
            pigeonAdapter.setLongIntTag(0);
            this.longClickTag = false;

        } else {
//            mSrl.setEnabled(false);
            refreshLayout.setEnableRefresh(false);
            pigeonAdapter.addData(pigeonBeans);
            mShowAddLv.setVisibility(View.VISIBLE);
            mRxBus.post("cancle", 200);
            mSelectRv.setVisibility(View.GONE);
//            daoSession.getMyPigeonBean1Dao().deleteAll();
            mPigeonCodes.clear();
            mSelectCb.setChecked(false);
            pigeonAdapter.setIsShow(false);
            pigeonAdapter.setLongClickTag(false);
            pigeonAdapter.setLongIntTag(0);
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {
//        mSrl.setRefreshing(isRefrash);
        refreshLayout.finishRefresh(isRefrash);
    }

    @Override
    public void itemChecked(View view, int count) {

        countTemp = count;

        Log.e("count", count + "-----d--" + pigeonBeans.size());

        if (count >= pigeonBeans.size()) {
            mSelectCb.setChecked(true);
        } else {
            mSelectCb.setChecked(false);
        }
    }

    @Override
    public void onItemClickListener(View view, int position, boolean longClickTag) {

        Log.e("longClickTag", longClickTag + "-----longClickTag");

        if (!longClickTag) {
            InnerDoveData pigeonBean = pigeonBeans.get(position);

            Intent intent = new Intent(getActivity(), PigeonActivity.class);
            intent.putExtra("pigeonBean", pigeonBean);
            startActivity(intent);
        } else {
            pigeonAdapter.setSelectItem(position);
        }
    }

    @Override
    public boolean onItemLongClickListener(View view, int position, boolean longClickTag) {

        this.longClickTag = false;

        if (!longClickTag) {
            //长按事件
            pigeonAdapter.setShowBox();
            //设置选中的项
            pigeonAdapter.setSelectItem(position);
            pigeonAdapter.notifyDataSetChanged();

            mSelectRv.setVisibility(View.VISIBLE);
//            mSrl.setEnabled(false);
            refreshLayout.setEnableRefresh(false);

            pigeonAdapter.setLongClickTag(true);
            mRxBus.post("exit", 20);
            mRxBus.post("cancle", 100);
            return false;

        }
        return true;
    }

    @OnClick(R.id.mypigeon_select_delete)
    void deleteOnCli(View view) {

        pigeonTemps.clear();

        //获取你选中的item
        Map<Integer, Boolean> map = pigeonAdapter.getMap();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                pigeonTemps.add(pigeonBeans.get(i));
            }
        }

        if (pigeonTemps.size() > 0) {

            dialog = new CustomDialog(getActivity(), "删除信鸽", "确定要删除所选信鸽?", "确定", "取消");

            dialog.setCancelable(true);
            dialog.show();
            dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {

                    for (int i = 0; i < pigeonTemps.size(); i++) {

                        if (!"".equals(pigeonTemps.get(i).getRingid()) && !"-1".equals(pigeonTemps.get(i).getRingid()) &&pigeonTemps.get(i).getRingid() != null) {
                            ApiUtils.showToast(getActivity(), "当前存在信鸽处于匹配状态，不可删除");
                            return;
                        }
                    }
                    methodType = MethodType.METHOD_TYPE_DOVE_DELETE;
                    ourCodePresenter.deleteDove(getParaMap());

                    dialog.dismiss();
                }

                @Override
                public void doCancel() {
                    //initData();
                    dialog.dismiss();
                }
            });
        } else {
            ApiUtils.showToast(getActivity(), "没有可删除的信鸽");
        }
    }

    @OnClick(R.id.mypigeon_select_share)
    void shareOnCli(View view) {

        pigeonTemps.clear();

        //获取你选中的item
        Map<Integer, Boolean> map = pigeonAdapter.getMap();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                pigeonTemps.add(pigeonBeans.get(i));
            }
        }

        if (pigeonTemps.size() > 0) {

//            Intent intent = new Intent(getActivity(), ShareActivity.class);
//            intent.putExtra("shareObjId", getDeleteObjIds());
//            startActivity(intent);
        } else {
            ApiUtils.showToast(getContext(), "你还没有选择信鸽进行分享");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mRxBus.unregister("clickRadio", clickObservable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRxBus.unregister("isLoad", isLoadObservable);
        mRxBus.unregister("tagnum", mTagNumObservable);
        mRxBus.unregister("isLoadData", dataObservable);
        mRxBus.unregister("refrash", unbindObservable);

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

        if (!ApiUtils.isNetworkConnected(getActivity())) {

            Log.e(TAG,"------onRefresh");
            mPresenter.getDatas();
        } else {

            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
            mPresenter.getDatasRefrash(getParaMap());
        }
    }
}
