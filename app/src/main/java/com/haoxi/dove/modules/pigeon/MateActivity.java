package com.haoxi.dove.modules.pigeon;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.MatePigeonAdapter;
import com.haoxi.dove.adapter.MatePigeonsAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
//import com.gmax.pigeon.inject.scopes.DaggerMateComponent;
import com.haoxi.dove.inject.MateMoudle;
import com.haoxi.dove.inject.scopes.DaggerMateComponent;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.modules.mvp.views.IGetRingView;
import com.haoxi.dove.modules.mvp.views.IMateView;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.presenters.MyRingPresenter;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerRing;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.RecycleviewDaviding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_mate)
public class MateActivity extends BaseActivity implements IMateView,IGetPigeonView,IGetRingView {

    @BindView(R.id.tl_custom)
    Toolbar mToolbar;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView  mTitleTv;
    @BindView(R.id.custom_toolbar_keep)
    TextView  mKeepTv;
    @BindView(R.id.act_mate_rv)
    RecyclerView mRv;
    @BindView(R.id.act_mate_show)
    RelativeLayout mShowRl;

    private AlertDialog.Builder builder;
    private AlertDialog         dialog;
    private RecyclerView mMateRv;
    private List<String>        hasMateRings = new ArrayList<>();
    private Map<String, String> keepMates    = new HashMap<>();
    private Map<String, InnerDoveData> keepIddMates    = new HashMap<>();
    private List<InnerDoveData> tempPigeons = new ArrayList<>();
    private List<InnerRing> tempRings   = new ArrayList<>();
    private List<InnerDoveData> myPigeonBeen = new ArrayList<>();
    private int methodType = MethodType.METHOD_TYPE_RING_SEARCH;
    private String tempPigeonObjId = "";
    private String tempRingObjId   = "";
    private boolean isMate = false;

    @Inject
    MatePigeonAdapter mAdapter;
    @Inject
    MatePigeonsAdapter mRingsAdapter;
    @Inject
    MyPigeonPresenter pigeonPresenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    MyRingPresenter ringPresenter;
    @Inject
    RxBus mRxBus;
    @Inject
    DaoSession daoSession;
    private int mateSize;

    @Override
    protected void initInject() {
        DaggerMateComponent.builder()
                .appComponent(getAppComponent())
                .mateMoudle(new MateMoudle(this,this))
                .build().inject(this);
    }

    private Observable<Boolean> isLoadObservable;
    private Observable<Boolean> isSceenObservable;
    private boolean isLoad = true;

    @Override
    protected void init() {
        isLoadObservable = mRxBus.register("isLoad",Boolean.class);
        isSceenObservable = mRxBus.register("isScreen",Boolean.class);

        isSceenObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isLoad = !aBoolean;
            }
        });
        isLoadObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aIsLoad) {
                isLoad = aIsLoad;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            mateSize = intent.getIntExtra("matelist_size",0);
        }
        mBackIv.setVisibility(View.VISIBLE);
        mKeepTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("批量匹配");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRv.setLayoutManager(linearLayoutManager);
        mRv.setAdapter(mRingsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoad) {
            myRingBeen.clear();
            isFirst = true;
            getRingData();
            isLoad = false;
        }
    }

    private void getRingData(){
        if (!ApiUtils.isNetworkConnected(this)) {
            ringPresenter.getDatas();
        }else {
            methodType = MethodType.METHOD_TYPE_RING_SEARCH;
            ringPresenter.getDataFromNets(getParaMap());
        }
    }
    @OnClick(R.id.custom_toolbar_keep)
    void keepOnclick() {
        ringsArray.clear();
        pigeonsArray.clear();
        for (int i = 0; i < tempRings.size(); i++) {
            Log.e("mateactivity",(keepMates.get(tempRings.get(i).getRingid()) != null)+"---s--5");
            if (keepMates.get(tempRings.get(i).getRingid()) != null && !"".equals(keepMates.get(tempRings.get(i).getRingid()))){
                String str1 = keepMates.get(tempRings.get(i).getRingid());
                for (int j =i+1; j < tempRings.size(); j++) {
                    if (keepMates.get(tempRings.get(j).getRingid()) != null){
                        String str2 = keepMates.get(tempRings.get(j).getRingid());
                        if (str1.equals(str2)){
                            ApiUtils.showToast(MateActivity.this,"鸽环绑定重复");
                            return;
                        }
                    }
                }
                ringsArray.add(tempRings.get(i).getRingid());
                Log.e("mateactivity",keepMates.get(tempRings.get(i).getRingid())+"---s--51");
                pigeonsArray.add(keepMates.get(tempRings.get(i).getRingid()));
            }
        }
        if (pigeonsArray.size() == 0){
            ApiUtils.showToast(this,"请添加匹配");
            return;
        }
        if (mateSize + ringsArray.size() > 15) {
            ApiUtils.showToast(this,"超过最大匹配数");
            return;
        }
        methodType = MethodType.METHOD_TYPE_RING_MATCH;
        ourCodePresenter.ringMatchDove(getParaMap());
    }

    private JSONArray ringsArray = new JSONArray();
    private JSONArray pigeonsArray = new JSONArray();

    @OnClick(R.id.custom_toolbar_iv)
    void backOnclick() {
        this.finish();
    }

    @Override
    public String getUserObjId() {
        return mUserObjId;
    }
    @Override
    public String getToken() {
        return mToken;
    }
    private List<InnerRing> myRingBeen = new ArrayList<>();
    private boolean isFirst = true;
    @Override
    public void setRingData(List<InnerRing> ringData) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (ringData == null){
            ringData = new ArrayList<>();
        }
        if (isFirst) {
            for (int i = 0; i < ringData.size(); i++) {
                if ("".equals(ringData.get(i).getDove_code()) || ringData.get(i).getDove_code() == null || "-1".equals(ringData.get(i).getDove_code()) ){
                    myRingBeen.add(ringData.get(i));
                }
            }
        }else {
            myRingBeen = ringData;
        }

        this.tempRings = myRingBeen;
        if (tempRings.size() == 0 && myRingBeen.size() == 0){
            mShowRl.setVisibility(View.VISIBLE);
            mKeepTv.setVisibility(View.GONE);
            return;
        }else {
            mShowRl.setVisibility(View.GONE);
            mKeepTv.setVisibility(View.VISIBLE);
        }

        mRingsAdapter.addRingDatas(myRingBeen);
        mRingsAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                tempRingObjId = tempRings.get(position).getRingid();
                setMatePigeon();
            }
        });
    }

    private List<String> doveIdMate = new ArrayList<>();

    @Override
    public void setPigeonData(List<InnerDoveData> pigeonData) {
        doveIdMate.clear();
        for (int i = 0; i < tempRings.size(); i++) {
            String doveid = keepMates.get(tempRings.get(i).getRingid());
            if (doveid != null && !"".equals(doveid)) {
                doveIdMate.add(doveid);
            }
        }

        if (pigeonData == null) {
            pigeonData = new ArrayList<>();
        }

        Log.e("pigeonData",pigeonData.size()+"------s");

        myPigeonBeen.clear();
        for (int i = 0; i < pigeonData.size(); i++) {
            if ("".equals(pigeonData.get(i).getRing_code()) || pigeonData.get(i).getRing_code() == null || "-1".equals(pigeonData.get(i).getRing_code()) ){
                InnerDoveData innerDoveData = pigeonData.get(i);
                if (doveIdMate.contains(innerDoveData.getDoveid())){
                    innerDoveData.setSetMate(true);
                }else {
                    innerDoveData.setSetMate(false);
                    myPigeonBeen.add(innerDoveData);
                }
            }
        }

        if (myPigeonBeen.size() == 0 || myPigeonBeen == null) {
            ApiUtils.showToast(this,"没有可匹配的信鸽");
            return;
        }
        mAdapter.addDatas(myPigeonBeen);
        dialog.show();
        if (mAdapter != null) {
            mAdapter.setAddItemClickListener(new MatePigeonAdapter.AddItemClickListener() {
                @Override
                public void addItemClick(String msg, int position, InnerDoveData bean) {
                    switch (msg){
                        case "取消":
                            isFirst = false;
                            tempPigeonObjId = myPigeonBeen.get(position).getDoveid();
                            hasMateRings.remove(tempPigeonObjId);
                            keepMates.put(tempRingObjId, "");
                            keepIddMates.put(tempRingObjId,null);

                            for (int i = 0; i < tempRings.size(); i++) {
                                if (tempRingObjId.equals(tempRings.get(i).getRingid())) {
                                    tempRings.get(i).setDove_code("-1");
                                }
                            }

                            Log.e("tempRings",tempRings.size()+"-----3");
                            setRingData(tempRings);
                            dialog.dismiss();
                            break;
                        case "添加":
                            isFirst = false;
                            tempPigeonObjId = myPigeonBeen.get(position).getDoveid();
                            Log.e("mateactivity", tempRingObjId + "-----mate----" + tempPigeonObjId);
                            hasMateRings.add(tempPigeonObjId);
                            keepMates.put(tempRingObjId, tempPigeonObjId);
                            keepIddMates.put(tempRingObjId,bean);
                            for (int i = 0; i < tempRings.size(); i++) {
                                if (tempRingObjId.equals(tempRings.get(i).getRingid())) {
                                    tempRings.get(i).setDove_code(myPigeonBeen.get(position).getFoot_ring());
                                }
                            }
                            Log.e("tempRings",tempRings.size()+"-----3");
                            setRingData(tempRings);
                            dialog.dismiss();
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {

    }

    @Override
    public JSONArray getPigeonObjId() {
        return pigeonsArray;
    }

    @Override
    public JSONArray getRingObjId() {
        return ringsArray;
    }

    public void setMatePigeon() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.mate_pigeon_dialog, null);
        mMateRv = (RecyclerView) view.findViewById(R.id.mate_pigeon_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMateRv.addItemDecoration(new RecycleviewDaviding(this, R.drawable.daviding));
        mMateRv.setLayoutManager(linearLayoutManager);
        mMateRv.setAdapter(mAdapter);

        InnerDoveData innerDoveData = keepIddMates.get(tempRingObjId);
        if (innerDoveData != null) {
            innerDoveData.setSetMate(true);
            List<InnerDoveData> innerDoveDataList = new ArrayList<>();
            innerDoveDataList.add(innerDoveData);
            mAdapter.addDatas(innerDoveDataList);
            dialog.show();

            if (mAdapter != null) {

                mAdapter.setAddItemClickListener(new MatePigeonAdapter.AddItemClickListener() {
                    @Override
                    public void addItemClick(String msg, int position, InnerDoveData bean) {

                        switch (msg){
                            case "取消":
                                isFirst = false;

                                tempPigeonObjId = bean.getDoveid();
                                hasMateRings.remove(tempPigeonObjId);
                                keepMates.put(tempRingObjId, "");
                                keepIddMates.put(tempRingObjId,null);

                                for (int i = 0; i < tempRings.size(); i++) {
                                    if (tempRingObjId.equals(tempRings.get(i).getRingid())) {
                                        tempRings.get(i).setDove_code("-1");
                                    }
                                }
                                Log.e("tempRings",tempRings.size()+"-----3");
                                setRingData(tempRings);
                                dialog.dismiss();
                                break;
                            case "添加":
                                isFirst = false;

                                tempPigeonObjId = bean.getDoveid();

                                hasMateRings.add(tempPigeonObjId);
                                keepMates.put(tempRingObjId, tempPigeonObjId);
                                keepIddMates.put(tempRingObjId,bean);

                                for (int i = 0; i < tempRings.size(); i++) {
                                    if (tempRingObjId.equals(tempRings.get(i).getRingid())) {
                                        tempRings.get(i).setDove_code(bean.getFoot_ring());
                                    }
                                }
                                Log.e("tempRings",tempRings.size()+"-----3");
                                setRingData(tempRings);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
            }
        }else {
            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
            pigeonPresenter.getDataFromNets(getParaMap());
        }
        builder.setView(view);
        if (dialog == null) {
            dialog = builder.create();
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

        switch (methodType){
            case MethodType.METHOD_TYPE_RING_MATCH:
                JSONArray array = new JSONArray();

                for (int i = 0; i < tempRings.size(); i++) {
                    String  ringid = tempRings.get(i).getRingid();
                    if (keepMates.get(ringid) != null && !"".equals(keepMates.get(ringid))){
                        JSONObject object = new JSONObject();
                        object.put("doveid",keepMates.get(ringid));
                        object.put("ringid",ringid);
                        array.add(object);
                    }
                }
                map.put("data",array.toJSONString());
                break;
        }

        Log.e("mateactivity",map.toString()+"-----7");
        return map;
    }


    @Override
    public void mateSuccess(Object obj) {
        ApiUtils.showToast(this,(String)obj);
        this.finish();
    }

    @Override
    public void toDo() {
        isMate = true;
        mRxBus.post(ConstantUtils.OBSER_LOAD_DATA,true);
        this.finish();
    }

    @Override
    public String getMethod() {
        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = MethodConstant.DOVE_SEARCH;
                break;
            case MethodType.METHOD_TYPE_RING_SEARCH:
                method = MethodConstant.RING_SEARCH;
                break;
            case MethodType.METHOD_TYPE_RING_MATCH:
                method = MethodConstant.RING_MATCH;
                break;
        }
        return method;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxBus.unregister("isLoad",isLoadObservable);
    }
}
