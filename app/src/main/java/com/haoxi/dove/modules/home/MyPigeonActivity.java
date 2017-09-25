package com.haoxi.dove.modules.home;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.PigeonEdAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.bean.IsMateBean;
import com.haoxi.dove.callback.ExpandItemClickListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerMyPigeon2Component;
import com.haoxi.dove.inject.MyPigeon2Moudle;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_mycircle)
public class MyPigeonActivity extends BaseActivity implements IGetPigeonView {
    private int methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
    @BindView(R.id.act_myring_pigeon)
    ExpandableListView mElistview;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    private List<IsMateBean> groupDatas;
    private List<InnerDoveData> hasBatchDatas;
    private List<InnerDoveData> noBatchDatas;
    private List<List<InnerDoveData>> childDatas;
    private String pigeonObjId_out = "";
    private String ringObjId_out = "";

    @Inject
    PigeonEdAdapter adapter;
    @Inject
    MyPigeonPresenter presenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    DaoSession daoSession;
    @Inject
    RxBus mRxBus;

    @Override
    protected void initInject() {
        DaggerMyPigeon2Component.builder()
                .appComponent(getAppComponent())
                .myPigeon2Moudle(new MyPigeon2Moudle(this, this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        groupDatas = new ArrayList<>();
        groupDatas.add(new IsMateBean("已匹配信鸽", false));
        groupDatas.add(new IsMateBean("未匹配信鸽", false));
        hasBatchDatas = new ArrayList<>();
        noBatchDatas = new ArrayList<>();
        childDatas = new ArrayList<>();
        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("我的信鸽");
        mElistview.setGroupIndicator(null);
        adapter = new PigeonEdAdapter(this);
        mElistview.setAdapter(adapter);
        adapter.setExpandItemClickListener(new ExpandItemClickListener() {
            @Override
            public void itemClick(InnerDoveData doveData,int position, String pigeonCode, String pigeonObjId, String ringObjID, String ringCode) {
                unbind(doveData,pigeonObjId, ringCode,ringObjID);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatas();
    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli() {
        finish();
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
        childDatas.clear();
        hasBatchDatas.clear();
        noBatchDatas.clear();
        if (pigeonData != null && pigeonData.size() != 0) {
            for (int i = 0; i < pigeonData.size(); i++) {
                InnerDoveData doveData = pigeonData.get(i);
                if (doveData.getRing_code()!= null && !"-1".equals(doveData.getRing_code())&&!"".equals(doveData.getRing_code())) {
                    hasBatchDatas.add(pigeonData.get(i));
                } else {
                    noBatchDatas.add((pigeonData.get(i)));
                }
            }
        }
        childDatas.add(hasBatchDatas);
        childDatas.add(noBatchDatas);
        adapter.addDatas(groupDatas, childDatas);
    }

    @Override
    public void setRefrash(boolean isRefrash) {
    }

    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(this)) {
            presenter.getDatas();
        } else {
            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
            presenter.getDataFromNets(getParaMap());
        }
    }
    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());
        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                map.put(MethodParams.PARAMS_PLAYER_ID,getUserObjId());
                break;
            case MethodType.METHOD_TYPE_RING_DEMATCH:
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                object.put(MethodParams.PARAMS_DOVE_ID,pigeonObjId_out);
                object.put(MethodParams.PARAMS_RING_ID,ringObjId_out);
                array.add(object);
                map.put(MethodParams.PARAMS_DATA,array.toJSONString());
                break;
        }
        return map;
    }

    public void unbind(final InnerDoveData doveData, final String pigeonObjId, final String ringCode, final String ringObjId) {
        final CustomDialog dialog = new CustomDialog(this, "信鸽解绑", "与 " + ringCode + " 解绑?", "确定", "取消");
        dialog.show();
        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                methodType = MethodType.METHOD_TYPE_RING_DEMATCH;
                ringObjId_out = ringObjId;
                pigeonObjId_out = pigeonObjId;
                if (!"true".equals(doveData.getFly_status()) || doveData.getFly_status()) {
                    ourCodePresenter.ringDematchDove(getParaMap());
                }else {
                    ApiUtils.showToast(MyPigeonActivity.this,"该信鸽正在飞行,不可解绑");
                }
                dialog.dismiss();
            }
            @Override
            public void doCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void toDo() {
        getDatas();
        mRxBus.post(ConstantUtils.OBSER_LOAD_DATA,0);
    }

    @Override
    public String getMethod() {
        String method = "";
        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = MethodConstant.DOVE_SEARCH;
                break;
            case MethodType.METHOD_TYPE_RING_DEMATCH:
                method = MethodConstant.RING_DEMATCH;
                break;
        }
        return method;
    }
}
