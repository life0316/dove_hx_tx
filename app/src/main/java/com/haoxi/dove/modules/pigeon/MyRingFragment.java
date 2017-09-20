package com.haoxi.dove.modules.pigeon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.MyRingAdapter;
import com.haoxi.dove.base.BaseRvFragment2;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.inject.DaggerMyRingComponent;
import com.haoxi.dove.inject.MyRingMoudle;
import com.haoxi.dove.modules.mvp.presenters.MyRingPresenter;
import com.haoxi.dove.modules.mvp.views.IGetRingView;
import com.haoxi.dove.newin.bean.InnerRing;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
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

public class MyRingFragment extends BaseRvFragment2 implements IGetRingView, MyRingAdapter.MyItemCheckListener, MyRingAdapter.RecyclerViewOnItemClickListener, OnRefreshListener {
    private static final String TAG = "MyRingFragment";
    private int methodType = MethodType.METHOD_TYPE_RING_SEARCH;
    @BindView(R.id.fragment_mypigeon_tv_refrash)
    TextView mRefrashTv;
    @BindView(R.id.fragment_mypigeon_ll_refrash)
    LinearLayout mRefrashLl;
    @BindView(R.id.fragment_mypigeon_show_add)
    LinearLayout mShowAddLv;
    @BindView(R.id.fragment_mypigeon_show_add_tv1)
    TextView mShowAddTv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.fragment_mypigeon_swiprv)
    RecyclerView mRecyclerView;
    @BindView(R.id.myring_select)
    RelativeLayout mSelectRv;
    @BindView(R.id.myring_select_cb)
    CheckBox mSelectCb;
    @BindView(R.id.fragment_mypigeon_no_network)
    TextView mNetworkTv;

    private List<InnerRing> ringBeans = new ArrayList<>();
    private List<InnerRing> ringTemps = new ArrayList<>();
    private int countTemp;
    private boolean isLoad = true;

    @Inject
    MyRingPresenter mRingPresenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    MyRingAdapter ringAdapter;
    @Inject
    RxBus mRxBus;
    @Inject
    DaoSession daoSession;
    @Inject
    Context mContext;

    private Observable<Boolean> isLoadObservable;
    private Observable<Boolean> dataObservable;
    private Observable<Integer> mTagNumObservable;
    private List<String> mateList;
    private CustomDialog dialog;
    private Observable<String> exitObservable;

    private boolean longClickTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mateList = mApplication.getMateList();
        isLoadObservable = mRxBus.register("isLoad", Boolean.class);
        exitObservable = mRxBus.register(ConstantUtils.OBSER_EXIT,String.class);

        exitObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if (ConstantUtils.OBSER_RING.equals(s)){
                    mShowAddLv.setVisibility(View.GONE);
                    mRxBus.post(ConstantUtils.OBSER_CANCLE, true);
                    mSelectRv.setVisibility(View.GONE);
                    mSelectCb.setChecked(false);
                    ringAdapter.setIsShow(false);
                    ringAdapter.setLongClickTag(false);
                    ringAdapter.notifyDataSetChanged();
                    MyRingFragment.this.longClickTag = false;
                    SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,true);
                    SpUtils.putString(getActivity(),SpConstant.OTHER_EXIT,"");
                }
            }
        });
    }

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
        DaggerMyRingComponent.builder()
                .appComponent(getAppComponent())
                .myRingMoudle(new MyRingMoudle(mContext, this))
                .build()
                .inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(ringAdapter);
        mShowAddTv.setText(getString(R.string.need_add_ring));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(this);
        mSelectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Map<Integer, Boolean> map = ringAdapter.getMap();
                    for (int i = 0; i < map.size(); i++) {
                        map.put(i, true);
                        ringAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (countTemp > 0 && countTemp < ringBeans.size()) {

                    } else {
                        Map<Integer, Boolean> m = ringAdapter.getMap();
                        for (int i = 0; i < m.size(); i++) {
                            m.put(i, false);
                            ringAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        ringAdapter.setItemCheckListener(this);
        ringAdapter.setRecyclerViewOnItemClickListener(this);
        mRefrashLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatas();
            }
        });
    }

    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());
        switch (methodType){
            case MethodType.METHOD_TYPE_RING_SEARCH:
                map.put(MethodParams.PARAMS_PLAYER_ID,getUserObjId());
                break;
            case MethodType.METHOD_TYPE_RING_DELETE:
                map.put(MethodParams.PARAMS_RING_ID,getDeleteObjIds());
                break;
        }
        return map;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTagNumObservable = mRxBus.register("tagnum", Integer.class);
        dataObservable = mRxBus.register("isLoadData", Boolean.class);
        setObservable();
        if (isLoad) {
//            getDatas();
            refreshLayout.autoRefresh();
            isLoad = false;
        }
    }

    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
            mRingPresenter.getDatas();
        } else {
            methodType = MethodType.METHOD_TYPE_RING_SEARCH;
            mRingPresenter.getDataFromNets(getParaMap());
        }
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
                if (aBoolean) {
                    methodType = MethodType.METHOD_TYPE_RING_SEARCH;
                    getDatas();
                }
            }
        });

        mTagNumObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == 0) {
                    chang();
                } else if (integer == 6) {
                    chang();
                }
            }
        });
    }

    private void chang(){
        if (ringAdapter != null) {
            ringAdapter.setIsShow(false);
            ringAdapter.setLongClickTag(false);
            longClickTag = false;
            ringAdapter.notifyDataSetChanged();
        }
        mSelectRv.setVisibility(View.GONE);
        mSelectCb.setChecked(false);
        refreshLayout.setEnableRefresh(true);
        mRxBus.post(ConstantUtils.OBSER_CANCLE, true);
        SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,true);
        SpUtils.putString(getActivity(),SpConstant.OTHER_EXIT,"");
    }

    @Override
    public void setRingData(List<InnerRing> ringData) {
        ringBeans.clear();
        mateList.clear();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (ringData != null && ringData.size() != 0) {
            ringBeans.clear();
            ringBeans.addAll(ringData);
            ringAdapter.addData(ringData);
            refreshLayout.setEnableRefresh(true);
            mShowAddLv.setVisibility(View.GONE);

            mRxBus.post(ConstantUtils.OBSER_CANCLE, true);
            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            ringAdapter.setIsShow(false);
            ringAdapter.setLongClickTag(false);
            this.longClickTag = false;

        } else {
            refreshLayout.setEnableRefresh(false);
            mShowAddLv.setVisibility(View.VISIBLE);

            ringAdapter.addData(ringBeans);
            mShowAddLv.setVisibility(View.VISIBLE);
            mRxBus.post(ConstantUtils.OBSER_CANCLE, true);
            mSelectRv.setVisibility(View.GONE);
            mPigeonCodes.clear();
            mSelectCb.setChecked(false);
            ringAdapter.setIsShow(false);
            ringAdapter.setLongClickTag(false);
            this.longClickTag = false;
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {
        refreshLayout.finishRefresh(isRefrash);
    }

    public String getDeleteObjIds() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ringTemps.size(); i++) {
            if ((i == ringTemps.size() - 1)) {
                sb.append(ringTemps.get(i).getRingid());
            } else {
                sb.append(ringTemps.get(i).getRingid()).append(",");
            }
        }
        return sb.toString();
    }

    @OnClick(R.id.myring_select_delete)
    void deleteOnCli() {
        ringTemps.clear();
        //获取你选中的item
        Map<Integer, Boolean> map = ringAdapter.getMap();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                ringTemps.add(ringBeans.get(i));
            }
        }
        if (ringTemps.size() > 0) {
            dialog = new CustomDialog(getActivity(), "删除鸽环", "确定要删除所选鸽环?", "确定", "取消");
            dialog.setCancelable(true);
            dialog.show();
            dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {
                    if (!ApiUtils.isNetworkConnected(getActivity())) {
                        ApiUtils.showToast(getContext(), getString(R.string.net_conn_2));
                        return;
                    }
                    for (int i = 0; i < ringTemps.size(); i++) {
                        if (ringTemps.get(i).getDoveid() != null && !"".equals(ringTemps.get(i).getDoveid()) && !"-1".equals(ringTemps.get(i).getDoveid())) {
                            ApiUtils.showToast(getActivity(), "当前存在鸽环处于匹配状态，不可删除");
                            return;
                        }
                    }
                    methodType = MethodType.METHOD_TYPE_RING_DELETE;
                    ourCodePresenter.deleteRing(getParaMap());
                    dialog.dismiss();
                }
                @Override
                public void doCancel() {
                    dialog.dismiss();
                }
            });
        } else {
            ApiUtils.showToast(getActivity(), "没有可删除的鸽环");
        }
    }

    @Override
    public void itemChecked(View view, int count) {
        countTemp = count;
        if (count >= ringBeans.size()) {
            mSelectCb.setChecked(true);
            countTemp = count;
        } else {
            mSelectCb.setChecked(false);
        }
    }
    @Override
    public void onItemClickListener(View view, int position, boolean longClickTag) {

        if (!longClickTag) {
            InnerRing ringBean = ringBeans.get(position);
            Intent intent = new Intent(getActivity(), RingInfoActivity.class);
            intent.putExtra("ringBean", ringBean);
            intent.putExtra("matelist_size", mateList.size());
            startActivity(intent);

        } else {
            ringAdapter.setSelectItem(position);
        }
    }

    @Override
    public boolean onItemLongClickListener(View view, int position, boolean longClickTag) {
        this.longClickTag = longClickTag;
        if (!longClickTag) {
            mRxBus.post(ConstantUtils.OBSER_CANCLE, false);
            //长按事件
            ringAdapter.setShowBox();
            //设置选中的项
            ringAdapter.setSelectItem(position);
            ringAdapter.notifyDataSetChanged();
            mSelectRv.setVisibility(View.VISIBLE);
            refreshLayout.setEnableRefresh(false);
            ringAdapter.setLongClickTag(true);

            SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,false);
            SpUtils.putString(getActivity(),SpConstant.OTHER_EXIT,ConstantUtils.OBSER_RING);

        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxBus.unregister("isLoad", isLoadObservable);
        mRxBus.unregister("tagnum", mTagNumObservable);
        mRxBus.unregister("isLoadData", dataObservable);
    }

    @Override
    public void toDo() {
        //删除成功刷新界面
        mRingPresenter.deleteDatasFromData(getUserObjId());
        methodType = MethodType.METHOD_TYPE_RING_SEARCH;
        mRingPresenter.getDataFromNets(getParaMap());
    }

    @Override
    public String getMethod() {
        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_RING_SEARCH:
                method = MethodConstant.RING_SEARCH;
                break;
            case MethodType.METHOD_TYPE_RING_DELETE:
                method = MethodConstant.RING_DELETE;
                break;
        }
        return method;
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
            mRingPresenter.getDatas();
            setRefrash(false);
        } else {
            methodType = MethodType.METHOD_TYPE_RING_SEARCH;
            mRingPresenter.getDatasRefrash(getParaMap());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRxBus.unregister(ConstantUtils.OBSER_EXIT,exitObservable);
    }
}
