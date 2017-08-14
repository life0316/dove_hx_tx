package com.haoxi.dove.modules.home;

import android.os.Handler;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.RingEdAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.bean.IsMateBean;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerMyRing2Component;
import com.haoxi.dove.inject.MyRing2Moudle;
import com.haoxi.dove.modules.mvp.views.IGetRingView;
import com.haoxi.dove.modules.mvp.presenters.MyRingPresenter;
import com.haoxi.dove.newin.bean.InnerRing;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by lifei on 2017/1/10.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_mycircle)
public class MyRingActivity extends BaseActivity implements IGetRingView {

    private static final String TAG = "MyRingActivity";

    @BindView(R.id.act_myring_pigeon)
    ExpandableListView mElistview;
    @BindView(R.id.custom_toolbar_iv)
    ImageView          mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView           mTitleTv;

    @BindView(R.id.fragment_mypigeon_ll_refrash)
    LinearLayout refrashLl;

    private List<IsMateBean>             groupDatas;
    private List<InnerRing>       hasBatchDatas;
    private List<InnerRing>       noBatchDatas;
    private List<List<InnerRing>> childDatas;

    @Inject
    MyRingPresenter presenter;

    @Inject
    RingEdAdapter adapter;

    @Inject
    RxBus mRxBus;

    private static Handler mHandler = new Handler();


    @Override
    protected void initInject() {

        DaggerMyRing2Component.builder()
                .appComponent(getAppComponent())
                .myRing2Moudle(new MyRing2Moudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        groupDatas = new ArrayList<>();

        groupDatas.add(new IsMateBean("已匹配信鸽",false));
        groupDatas.add(new IsMateBean("未匹配信鸽",false));


        hasBatchDatas = new ArrayList<>();
        noBatchDatas = new ArrayList<>();
        childDatas = new ArrayList<>();

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("我的鸽环");
        mElistview.setGroupIndicator(null);
        mElistview.setAdapter(adapter);

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxBus.post("isLoad",false);
                finish();
            }
        });

        mElistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatas(false);
    }

    public void getDatas(boolean tag) {

        if (!ApiUtils.isNetworkConnected(this)) {
            presenter.getDatas();
        }else {
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
        map.put("playerid",getUserObjId());

        return map;
    }

    void backOnCli(View v){
        mRxBus.post("isLoad",false);
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
    public void setRingData(List<InnerRing> ringData) {

        //Log.e("ringData",ringData.size()+"-----ringData");

        childDatas.clear();

        hasBatchDatas.clear();
        noBatchDatas.clear();

        if (ringData != null&& ringData.size() != 0) {


            for (int i = 0; i < ringData.size(); i++) {
                if (!"".equals(ringData.get(i).getDoveid())){
//                    if (!"".equals(ringData.get(i).getFOOT_RING_CODE())){
                    hasBatchDatas.add(ringData.get(i));
                }else {
                    noBatchDatas.add((ringData.get(i)));
                }
            }
        }

        childDatas.add(hasBatchDatas);
        childDatas.add(noBatchDatas);

        adapter.addDatas(groupDatas,childDatas);
    }

    @Override
    public void setRefrash(boolean isRefrash) {

    }

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {
        return "/app/ring/search";
    }

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        super.onBackPressed();
    }
}
