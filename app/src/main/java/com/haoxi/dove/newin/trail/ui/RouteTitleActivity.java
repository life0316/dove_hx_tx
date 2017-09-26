package com.haoxi.dove.newin.trail.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.RouteTitleAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerRouteTitleComponent;
import com.haoxi.dove.inject.RouteTitleMoudle;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.OurRouteBean;
import com.haoxi.dove.newin.bean.PointBean;
import com.haoxi.dove.newin.trail.presenter.RouteTitlePresenter;
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

@ActivityFragmentInject(contentViewId = R.layout.activity_route_title)
public class RouteTitleActivity extends BaseActivity implements IGetOurRouteView, MyItemClickListener, RouteTitleAdapter.RecyclerViewOnItemClickListener, RouteTitleAdapter.MyItemCheckListener, View.OnClickListener, OnRefreshListener {

    private int methodType = MethodType.METHOD_TYPE_FLY_SEARCH;

    @BindView(R.id.fragment_mypigeon_tv_refrash)
    TextView mRefrashTv;
    @BindView(R.id.fragment_mypigeon_ll_refrash)
    LinearLayout mRefrashLl;
    @BindView(R.id.fragment_mypigeon_show_add)
    LinearLayout mShowAddLv;
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
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_keep)
    TextView mCancleTv;

    @Inject
    RouteTitlePresenter titlePresenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    RouteTitleAdapter titleAdapter;
    @Inject
    RxBus mRxBus;
    @Inject
    Context mContext;

    private boolean longClickTag;
    private CustomDialog dialog;
    private Dialog popDialog;
    private String flyRecordId = "";
    private String doveid = "";
    private int countTemp;
    private List<InnerRouteBean> routeBeanList = new ArrayList<>();
    private List<InnerRouteBean> routeBeanTemps = new ArrayList<>();

    private boolean isLoad = true;

    @Override
    protected void initInject() {
        DaggerRouteTitleComponent.builder()
                .appComponent(getAppComponent())
                .routeTitleMoudle(new RouteTitleMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        mTitleTv.setText("行程记录");
        mCancleTv.setText("取消");
        mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            InnerDoveData innerDoveData = intent.getParcelableExtra("dove");
            doveid = innerDoveData.getDoveid();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        mRecyclerView.setAdapter(titleAdapter);

        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(this);

        mSelectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Map<Integer, Boolean> map = titleAdapter.getMap();
                    for (int i = 0; i < map.size(); i++) {
                        map.put(i, true);
                        titleAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (!(countTemp > 0 && countTemp < routeBeanList.size())) {
                        Map<Integer, Boolean> m = titleAdapter.getMap();
                        for (int i = 0; i < m.size(); i++) {
                            m.put(i, false);
                            titleAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        titleAdapter.setItemCheckListener(this);
        titleAdapter.setRecyclerViewOnItemClickListener(this);
    }

    @Override
    public void toDo() {
        switch (methodType){
            case MethodType.METHOD_TYPE_FLY_DELETE:
                if (popDialog != null && popDialog.isShowing()) {
                    popDialog.dismiss();
                }
                methodType = MethodType.METHOD_TYPE_FLY_SEARCH;
                titlePresenter.getRouteFormNets(getParaMap());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoad) {
            refreshLayout.autoRefresh();
            isLoad = false;
        }
    }

    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(this)) {
        } else {
            methodType = MethodType.METHOD_TYPE_FLY_SEARCH;
            titlePresenter.getRouteFormNets(getParaMap());
        }
    }

    @OnClick(R.id.custom_toolbar_keep)
    void cancleOnClick(){
        chang();
    }
    private void chang(){
        if (titleAdapter != null) {
            titleAdapter.setIsShow(false);
            titleAdapter.setLongClickTag(false);
            longClickTag = false;
            titleAdapter.notifyDataSetChanged();
        }
        mSelectRv.setVisibility(View.GONE);
        mSelectCb.setChecked(false);
        refreshLayout.setEnableRefresh(true);
        mCancleTv.setVisibility(View.GONE);
    }
    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map =  super.getParaMap();
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());
        switch (methodType){
            case MethodType.METHOD_TYPE_FLY_SEARCH:
                map.put(MethodParams.PARAMS_DOVE_ID,doveid);
                break;
            case MethodType.METHOD_TYPE_FLY_DELETE:
                map.put(MethodParams.PARAMS_FLY_RECORDID,flyRecordId);
        }
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
        }
        return method;
    }

    public String getDeleteObjIds() {
        StringBuilder sb = new StringBuilder();
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
    public String getUserObjId() {
        return mUserObjId;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public void setRouteData(OurRouteBean routeData) {
        setRefrash(false);
        routeBeanList.clear();
        if (routeData.getData() != null && routeData.getData().size() != 0) {
            routeBeanList.addAll(routeData.getData());
            titleAdapter.addData(routeBeanList);
            refreshLayout.setEnableRefresh(true);
            refreshLayout.setEnableLoadmore(true);
            mShowAddLv.setVisibility(View.GONE);

            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            titleAdapter.setIsShow(false);
            titleAdapter.setLongClickTag(false);
            titleAdapter.setLongIntTag(0);
            this.longClickTag = false;
        } else {
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadmore(false);
            titleAdapter.addData(routeBeanList);
            mShowAddLv.setVisibility(View.VISIBLE);
            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            titleAdapter.setIsShow(false);
            titleAdapter.setLongClickTag(false);
            titleAdapter.setLongIntTag(0);
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {
        refreshLayout.finishLoadmore();
        refreshLayout.finishRefresh();
    }

    @Override
    public void onItemClick(View view, int count) {
        countTemp = count;
        if (count >= routeBeanList.size()) {
            mSelectCb.setChecked(true);
        } else {
            mSelectCb.setChecked(false);
        }
    }

    @Override
    public void onItemClickListener(View view, int position, boolean longClickTag) {
        if (!longClickTag) {
            InnerRouteBean routeBean = routeBeanList.get(position);
            showPop(routeBean);
        } else {
            titleAdapter.setSelectItem(position);
        }
    }

    @Override
    public boolean onItemLongClickListener(View view, int position, boolean longClickTag) {
        this.longClickTag = false;
        if (!longClickTag) {
            //长按事件
            mCancleTv.setVisibility(View.VISIBLE);
            titleAdapter.setShowBox();
            //设置选中的项
            titleAdapter.setSelectItem(position);
            titleAdapter.notifyDataSetChanged();
            mSelectRv.setVisibility(View.VISIBLE);
            refreshLayout.setEnableLoadmore(false);
            refreshLayout.setEnableRefresh(false);
            titleAdapter.setLongClickTag(true);
            return false;
        }
        return true;
    }

    @Override
    public void itemChecked(View view, int count) {
        countTemp = count;
        if (count >= routeBeanList.size()) {
            mSelectCb.setChecked(true);
        } else {
            mSelectCb.setChecked(false);
        }
    }
    @OnClick(R.id.mypigeon_select_delete)
    void deleteOnCli() {
        routeBeanTemps.clear();
        //获取你选中的item
        Map<Integer, Boolean> map = titleAdapter.getMap();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                routeBeanTemps.add(routeBeanList.get(i));
            }
        }

        if (routeBeanTemps.size() > 0) {
            dialog = new CustomDialog(this, "删除信鸽", "确定要删除所选飞行记录?", "确定", "取消");
            dialog.setCancelable(true);
            dialog.show();
            dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {
                    for (int i = 0; i < routeBeanTemps.size(); i++) {
                        if (routeBeanTemps.get(i).getStatus()!= null && !"飞行结束".equals(routeBeanTemps.get(i).getStatus())) {
                            ApiUtils.showToast(RouteTitleActivity.this, "当前记录处于飞行状态，不可删除");
                            return;
                        }
                    }
                    methodType = MethodType.METHOD_TYPE_FLY_DELETE;
                    flyRecordId = getDeleteObjIds();
                    getParaMap();
                    ourCodePresenter.deleteFly(getParaMap());
                    dialog.dismiss();
                }
                @Override
                public void doCancel() {
                    dialog.dismiss();
                }
            });
        } else {
            ApiUtils.showToast(RouteTitleActivity.this, "没有可删除的飞行记录");
        }
    }
    @Override
    public void onBackPressed() {
        if (titleAdapter.getLongClickTag()) {
            mSelectRv.setVisibility(View.GONE);
            mSelectCb.setChecked(false);
            titleAdapter.setIsShow(false);
            titleAdapter.setLongClickTag(false);
            titleAdapter.setLongIntTag(0);
            titleAdapter.notifyDataSetChanged();
        }else {
            super.onBackPressed();
        }
    }


    private void showPop(final InnerRouteBean innerRouteBean) {

        popDialog = new Dialog(this, R.style.DialogTheme);

        View view = View.inflate(this, R.layout.layout_show_route, null);
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

        @SuppressWarnings("deprecation")
        int width = getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (width * 62) / 72;
        params.height = (width * 52) / 72;

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
                Intent intent = new Intent(RouteTitleActivity.this,RouteDetailActivity.class);
                intent.putExtra("recordid",innerRouteBean.getFly_recordid());
                intent.putExtra("doveid",innerRouteBean.getDoveid());
                intent.putParcelableArrayListExtra("innerRouteBean",innerRouteBean.getPoints());
                startActivity(intent);
                popDialog.dismiss();
            }
        });
        popDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.custom_toolbar_iv:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
       getDatas();
    }
}
