package com.haoxi.dove.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.FlyStringAdapter;
import com.haoxi.dove.base.BaseRvFragment2;
import com.haoxi.dove.bean.FlyStringBean;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.inject.DaggerRouteFlyPComponent;
import com.haoxi.dove.inject.RouteFlyPMoudle;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.OurRouteBean;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.trail.presenter.RouteTitlePresenter;
import com.haoxi.dove.newin.trail.ui.IGetOurRouteView;
import com.haoxi.dove.newin.trail.ui.RouteDetail2Activity;
import com.haoxi.dove.newin.trail.ui.RouteDetailActivity;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
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
import rx.functions.Action1;

public class AllFlyRecordFragment  extends BaseRvFragment2 implements IGetOurRouteView,MyItemClickListener, FlyStringAdapter.MyItemCheckListener, FlyStringAdapter.RecyclerViewOnItemClickListener, OnRefreshListener {

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

    @BindView(R.id.fragment_mypigeon_show_add)
    LinearLayout mShowAddLv;
    @BindView(R.id.fragment_mypigeon_show_add_tv1)
    TextView mShowAddTv;
    @BindView(R.id.fragment_mypigeon_show_add_tv2)
    TextView mShowAddTv2;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    FlyStringAdapter titleAdapter;

    @Inject
    RouteTitlePresenter titlePresenter;

    @Inject
    RxBus mRxBus;

    @Inject
    Context mContext;

    private boolean longClickTag;
    private CustomDialog dialog;
    private Dialog popDialog;

    private int countTemp;

    private String flyRecordId = "";
    private List<InnerRouteBean> routeBeanList = new ArrayList<>();
    private List<FlyStringBean> flyStringBeans = new ArrayList<>();
    private List<InnerRouteBean> routeBeanTemps = new ArrayList<>();

    private Map<String,ArrayList<InnerRouteBean>> routeMap = new HashMap<>();

    private boolean isLoad = true;
    private boolean isClose = false;

    private int methodType = MethodType.METHOD_TYPE_SEARCH_BY_PLAYERID;
    private Observable<Boolean> closeObservable;

    public static AllFlyRecordFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        AllFlyRecordFragment fragment = new AllFlyRecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        closeObservable = mRxBus.register("close_route",Boolean.class);

        closeObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isClose = aBoolean;

                if (aBoolean){
                    if (titleAdapter.getLongClickTag()) {
//                        mSrl.setEnabled(true);
                        refreshLayout.setEnableRefresh(true);
                        mSelectRv.setVisibility(View.GONE);
                        mSelectCb.setChecked(false);
                        titleAdapter.setIsShow(false);
                        titleAdapter.setLongClickTag(false);
                        titleAdapter.setLongIntTag(0);
                        titleAdapter.notifyDataSetChanged();
                        mRxBus.post("route_back",true);
                    }
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        mRecyclerView.setAdapter(titleAdapter);

        mShowAddTv2.setVisibility(View.GONE);

        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(this);

        titleAdapter.setItemCheckListener(this);
        titleAdapter.setRecyclerViewOnItemClickListener(this);
    }


//    @Override
//    public void onRefresh() {
//        getDatas();
//    }

    @Override
    public void onItemClick(View view, int count) {
        countTemp = count;

        Log.e("count", count + "-----d--" + routeBeanList.size());

        if (count >= routeBeanList.size()) {
            mSelectCb.setChecked(true);
        } else {
            mSelectCb.setChecked(false);
        }
    }

    private List<String> flyRecordidList = new ArrayList<>();

    @Override
    public void setRouteData(OurRouteBean routeData) {
        setRefrash(false);

        flyStringBeans.clear();
        routeMap.clear();
        flyRecordidList.clear();
        routeBeanList.clear();

        if (routeData.getData() != null && routeData.getData().size() != 0) {

            for (int i = 0; i < routeData.getData().size(); i++) {

                InnerRouteBean innerRouteBean = routeData.getData().get(i);
                Log.e("longClickTagqqq", innerRouteBean.getDoveid() + "---1--longClickTag---"+innerRouteBean.getFly_recordid());


                if (!flyRecordidList.contains(innerRouteBean.getFly_recordid())){
                    flyRecordidList.add(innerRouteBean.getFly_recordid());
                    FlyStringBean flyStringBean = new FlyStringBean();
                    flyStringBean.setTitle(innerRouteBean.getFly_recordid());
                    flyStringBeans.add(flyStringBean);

                    routeBeanList.add(innerRouteBean);
                    ArrayList<InnerRouteBean> fsf = new ArrayList<>();
                    fsf.add(innerRouteBean);
                    routeMap.put(innerRouteBean.getFly_recordid(),fsf);

                }else {
//                    routeBeanList.add(innerRouteBean);
                    ArrayList<InnerRouteBean> fsf = (ArrayList<InnerRouteBean>) routeMap.get(innerRouteBean.getFly_recordid());
                    fsf.add(innerRouteBean);
                    routeMap.put(innerRouteBean.getFly_recordid(),fsf);

                }
//                List<PointBean> pointBeans = routeData.getData().get(i).getPoints();
//
//                for (int j = 0; j < pointBeans.size(); j++) {
//
//                    Log.e("pointBeans",pointBeans.get(j).getTime()+"-----time");
//                    Log.e("pointBeans",pointBeans.get(j).getLat()+"-----lat");
//                    Log.e("pointBeans",pointBeans.get(j).getLng()+"-----lng");
//                }
            }
//            routeBeanList.addAll(routeData.getData());
            titleAdapter.addData(flyStringBeans);
//            mSrl.setEnabled(true);
            refreshLayout.setEnableRefresh(true);
//            mShowAddLv.setVisibility(View.GONE);
            mShowAddLv.setVisibility(View.GONE);
            mShowAddTv.setVisibility(View.GONE);

            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            titleAdapter.setIsShow(false);
            titleAdapter.setLongClickTag(false);
            titleAdapter.setLongIntTag(0);
            this.longClickTag = false;

        } else {
//            refreshLayout.setEnableRefresh(false);

            mShowAddLv.setVisibility(View.VISIBLE);
            mShowAddTv.setVisibility(View.VISIBLE);
            mShowAddTv.setText("暂时还没有行程记录");

            titleAdapter.addData(flyStringBeans);
//            mShowAddLv.setVisibility(View.VISIBLE);
            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            titleAdapter.setIsShow(false);
            titleAdapter.setLongClickTag(false);
            titleAdapter.setLongIntTag(0);
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {
        refreshLayout.finishRefresh(isRefrash);
    }

    @Override
    public void toDo() {
        switch (methodType){
            case MethodType.METHOD_TYPE_FLY_DELETE:
                mRxBus.post("route_back",true);
                methodType = MethodType.METHOD_TYPE_SEARCH_BY_PLAYERID;
                titlePresenter.getRouteFormNets(getParaMap());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDatas();
    }

    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
        } else {
            methodType = MethodType.METHOD_TYPE_SEARCH_BY_PLAYERID;
            titlePresenter.getRouteFormNets(getParaMap());
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
            case MethodType.METHOD_TYPE_SEARCH_BY_PLAYERID:
//                map.put("playerid","pl494260");
                map.put("playerid",getUserObjId());

                break;
            case MethodType.METHOD_TYPE_FLY_DELETE:
                map.put("fly_recordid",flyRecordId);
        }
        Log.e("fafsewdfvd",map.toString());
        return map;
    }

    @Override
    public String getMethod() {

        String method = "";
        switch (methodType){
            case MethodType.METHOD_TYPE_FLY_SEARCH:
                method = MethodConstant.FLY_SEARCH;
                break;
            case MethodType.METHOD_TYPE_FLY_DELETE:
                method = MethodConstant.FLY_DELETE;
                break;
            case MethodType.METHOD_TYPE_SEARCH_BY_PLAYERID:
                method = MethodConstant.SEARCH_BY_PLAYERID;
                break;
        }

        return method;
    }


    public String getDeleteObjIds() {

        StringBuffer sb = new StringBuffer();

        if (routeBeanTemps != null) {
            for (int i = 0; i < routeBeanTemps.size(); i++) {

                if ((i == routeBeanTemps.size() - 1)) {

                    sb.append(routeBeanTemps.get(i).getFly_recordid());
                } else {
                    sb.append(routeBeanTemps.get(i).getFly_recordid()).append(",");
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void inject() {
        DaggerRouteFlyPComponent.builder()
                .appComponent(getAppComponent())
                .routeFlyPMoudle(new RouteFlyPMoudle(getActivity(),this))
                .build().inject(this);
    }

    @Override
    public void itemChecked(View view, int count) {
        countTemp = count;

        Log.e("count", count + "-----d--" + routeBeanList.size());

        if (count >= routeBeanList.size()) {
            mSelectCb.setChecked(true);
        } else {
            mSelectCb.setChecked(false);
        }
    }

    @Override
    public void onItemClickListener(View view, int position, boolean longClickTag) {
//        Log.e("longClickTagqqq", longClickTag + "-----longClickTag");

        if (!longClickTag) {
//            InnerRouteBean routeBean = routeBeanList.get(position);
//            showPop(routeBean);

            FlyStringBean flyStringBean = flyStringBeans.get(position);

            Log.e("longClickTagqqq", flyStringBean.getTitle() + "-----longClickTag");
            Log.e("longClickTagqqq", "-----"+ routeMap.get(flyStringBean.getTitle()).size());

            Intent intent = new Intent(getActivity(),RouteDetail2Activity.class);
//                intent.putExtra("innerRouteBean",innerRouteBean);
            intent.putExtra("recordid",flyStringBean.getTitle());
//            intent.putExtra("methodTag","search_by_fly_recordid");
//            intent.putParcelableArrayListExtra("innerRouteBean",routeMap.get(flyStringBean.getTitle()));
            startActivity(intent);

            for (int i = 0; i < routeMap.get(flyStringBean.getTitle()).size(); i++) {
                Log.e("longClickTagqqq", "Doveid-----"+ routeMap.get(flyStringBean.getTitle()).get(i).getDoveid());
            }

        } else {
            titleAdapter.setSelectItem(position);
        }
    }

    @Override
    public boolean onItemLongClickListener(View view, int position, boolean longClickTag) {
        this.longClickTag = false;

        if (!longClickTag) {
            //长按事件
            titleAdapter.setShowBox();
            //设置选中的项
            titleAdapter.setSelectItem(position);
            titleAdapter.notifyDataSetChanged();

            mSelectRv.setVisibility(View.VISIBLE);
//            mSrl.setEnabled(false);
            refreshLayout.setEnableRefresh(false);

            titleAdapter.setLongClickTag(true);

            mRxBus.post("route_back",false);
            return false;
        }
        return true;
    }

    @OnClick(R.id.mypigeon_select_delete)
    void deleteOnCli(View view) {
        routeBeanTemps.clear();
        //获取你选中的item
        Map<Integer, Boolean> map = titleAdapter.getMap();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                routeBeanTemps.add(routeBeanList.get(i));
            }
        }

        if (routeBeanTemps.size() > 0) {

            dialog = new CustomDialog(getActivity(), "删除信鸽", "确定要删除所选飞行记录?", "确定", "取消");

            dialog.setCancelable(true);
            dialog.show();
            dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {

                    for (int i = 0; i < routeBeanTemps.size(); i++) {

                        if (routeBeanTemps.get(i).getStatus()!= null && !"飞行结束".equals(routeBeanTemps.get(i).getStatus())) {
                            ApiUtils.showToast(getActivity(), "当前记录处于飞行状态，不可删除");
                            return;
                        }
                    }
                    methodType = MethodType.METHOD_TYPE_FLY_DELETE;
                    flyRecordId = getDeleteObjIds();
//                    getParaMap();
                    ourCodePresenter.deleteFly(getParaMap());

                    dialog.dismiss();
                }
                @Override
                public void doCancel() {
                    dialog.dismiss();
                }
            });
        } else {
            ApiUtils.showToast(getActivity(), "没有可删除的飞行记录");
        }
    }
    private void showPop(final InnerRouteBean innerRouteBean) {

        popDialog = new Dialog(getActivity(), R.style.DialogTheme);

        View view = View.inflate(getActivity(), R.layout.layout_show_route, null);
        popDialog.setCancelable(false);
        popDialog.setContentView(view);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.show_marker_ll);
        TextView mDoveNameTv = (TextView) view.findViewById(R.id.show_nickname_tv);
        TextView mRouteIdTv = (TextView) view.findViewById(R.id.route_id);
        TextView mFlyStatusTv = (TextView) view.findViewById(R.id.fly_status);
        TextView mStartTimeTv = (TextView) view.findViewById(R.id.start_time);
        TextView mStopTimeTv = (TextView) view.findViewById(R.id.stop_time);
        Button mRemoveBtn = (Button) view.findViewById(R.id.delete_route);
        Button mDetailBtn = (Button) view.findViewById(R.id.detail_route);

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (width * 62) / 72;
        params.height = (int) ((width * 52) / 72);

        layout.setLayoutParams(params);

        ImageView mDismissIv = (ImageView) view.findViewById(R.id.show_marker_dismiss);


        if (innerRouteBean.getDoveid() != null) {
            mDoveNameTv.setText(innerRouteBean.getDoveid());
        }
        if (innerRouteBean.getFly_recordid() != null) {
            mRouteIdTv.setText(innerRouteBean.getFly_recordid());
        }
        if (innerRouteBean.getStatus() != null) {
            mFlyStatusTv.setText(innerRouteBean.getStatus());
        }
        if (innerRouteBean.getCreate_time() != null) {
            mStartTimeTv.setText(innerRouteBean.getCreate_time());
        }
        if (innerRouteBean.getStop_time() != null) {
            mStopTimeTv.setText(innerRouteBean.getStop_time());
        }


        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flyRecordId = innerRouteBean.getFly_recordid();
                methodType = MethodType.METHOD_TYPE_FLY_DELETE;
                ourCodePresenter.deleteFly(getParaMap());

            }
        });
        mDismissIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });

        mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RouteDetailActivity.class);
//                intent.putExtra("innerRouteBean",innerRouteBean);
                intent.putExtra("recordid",innerRouteBean.getFly_recordid());
                intent.putParcelableArrayListExtra("innerRouteBean",innerRouteBean.getPoints());
                startActivity(intent);
            }
        });

        popDialog.show();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
        } else {
            methodType = MethodType.METHOD_TYPE_SEARCH_BY_PLAYERID;
            titlePresenter.getDatasRefrash(getParaMap());
        }
    }
}
