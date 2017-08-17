package com.haoxi.dove.modules.home;

import android.os.Handler;
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
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
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

    private static final String TAG = "MyPigeonActivity";

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
                //TODO  弹出个对话框
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
        mRxBus.post("isLoad", false);
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
            case MethodType.METHOD_TYPE_RING_DEMATCH:

                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("doveid",pigeonObjId_out);
                object.put("ringid",ringObjId_out);
                array.add(object);

                map.put("data",array.toJSONString());
                break;
        }

        return map;
    }

    private String pigeonObjId_out = "";
    private String ringObjId_out = "";

    public void unbind(final InnerDoveData doveData, final String pigeonObjId, final String ringCode, final String ringObjId) {

        Log.e("expandItemClickListener", "解绑");

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
        mRxBus.post("refrash",0);
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

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad", false);
        super.onBackPressed();
    }
}
