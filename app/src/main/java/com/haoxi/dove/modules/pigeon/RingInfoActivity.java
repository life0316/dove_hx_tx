package com.haoxi.dove.modules.pigeon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.MatePigeonAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerRingInfoComponent;
import com.haoxi.dove.inject.RingInfoMoudle;
import com.haoxi.dove.modules.mvp.views.IRingInfoView;
import com.haoxi.dove.modules.mvp.presenters.RingInfoPresenter;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerRing;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.BottomPopView;
import com.haoxi.dove.widget.RecycleviewDaviding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_circle_info)
public class RingInfoActivity extends BaseActivity implements IRingInfoView {

    private static final int UPDATE_RING_ONOFF_STATUS = 1;
    private static final int UPDATE_RING_LFQ = 2;     //定位频率
    private static final int UPDATE_RING_RFQ = 3;  //上报频率
    private static final int UPDATE_RING_ON_TIME = 4;  //开机时间
    private static final int UPDATE_RING_OFF_TIME = 5;  //关机时间

    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;

    @BindView(R.id.activity_ring_footcode)
    TextView ringWithPigeonTv;
    @BindView(R.id.activity_ring_position)
    TextView postionModeTv;
    @BindView(R.id.activity_ring_reportedfreq)
    TextView repFreqTv;
    @BindView(R.id.activity_ring_schedule_on)
    TextView schOnTv;
    @BindView(R.id.activity_ring_schedule_off)
    TextView schOffTv;

    @BindView(R.id.activity_ring_ringcode)
    TextView ringCodeTv;
    @BindView(R.id.activity_ring_startdate)
    TextView startDateTv;
    @BindView(R.id.activity_ring_startstatus)
    TextView startStatusTv;
    @BindView(R.id.activity_ring_status)
    TextView ringStatusTv;

    @BindView(R.id.activity_ring_power)
    ImageView mRingPower;

    private Dialog dialog;
    private RecyclerView mMateRv;


    private final String Thrift_TAG1 = "POSITION_MODE";
    private final String Thrift_TAG2 = "REPORTED_FREQ";
    private String posiModeTag = "POSITION_MODE";


    private final String TIMING_SWITCH_1 = "BOOTTIME";
    private final String TIMING_SWITCH_2 = "SDTIME";
    private String timeSwitchTag = "BOOTTIME";

    private String onTime;
    private String offTime;

    @Inject
    RingInfoPresenter infoPresenter;

    @Inject
    MatePigeonAdapter matePigeonAdapter;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus mRxBus;

    @Inject
    DaoSession daoSession;

    private List<InnerDoveData> noMateLists = new ArrayList<>();
    private String mRingObjId;
    private String mRingCode;
    private InnerRing ringBean;
    private InnerRing myRingBean;
    private int mateSize;

    @Override
    protected void initInject() {
        DaggerRingInfoComponent.builder()
                .appComponent(getAppComponent())
                .ringInfoMoudle(new RingInfoMoudle(this, this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        mTitleTv.setText("鸽环信息");
        mBackIv.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null) {

            ringBean = intent.getParcelableExtra("ringBean");

            mateSize = intent.getIntExtra("matelist_size", 0);

            if (ringBean != null) {
                mRingCode = ringBean.getRing_code();
                mRingObjId = ringBean.getRingid();
                String mStartStuts = ringBean.getOn_off_status();
                String mStartTime = ringBean.getCreate_time();
                int positionMode = ringBean.getLfq();
                int reportedFreq = ringBean.getRfq();

                String ourOnTime = ringBean.getOn_time();
                String ourOffTime = ringBean.getOff_time();

                String footRing = ringBean.getDoveid();

                if (footRing == null || "".equals(footRing)|| "-1".equals(footRing)) {
                    ringWithPigeonTv.setText("未匹配");
                } else {
                    ringWithPigeonTv.setText(footRing);
                }

                startDateTv.setText(mStartTime);

                if (!"".equals(ourOffTime) && ourOffTime != null) {
                    schOffTv.setText(ourOffTime);
                }

                if (!"".equals(ourOnTime) && ourOnTime != null) {
                    schOnTv.setText(ourOnTime);
                }

                if (positionMode != 0) {
                    switch (positionMode) {
                        case 1:
                            postionModeTv.setText("1分钟/次");
                            break;
                        case 2:
                            postionModeTv.setText("2分钟/次");
                            break;
                        case 3:
                            postionModeTv.setText("3分钟/次");
                            break;
                        case 4:
                            postionModeTv.setText("4分钟/次");
                            break;
                        case 5:
                            postionModeTv.setText("5分钟/次");
                            break;
                    }

                }
                if (reportedFreq != 0) {

                    switch (reportedFreq) {
                        case 1:
                            repFreqTv.setText("1分钟/次");
                            break;
                        case 2:
                            repFreqTv.setText("2分钟/次");
                            break;
                        case 3:
                            repFreqTv.setText("3分钟/次");
                            break;
                        case 4:
                            repFreqTv.setText("4分钟/次");
                            break;
                        case 5:
                            repFreqTv.setText("5分钟/次");
                            break;
                    }
                }

                ringCodeTv.setText(mRingCode);

                if (mStartStuts != null && !"".equals(mStartStuts)) {

                    if ("0".equals(mStartStuts) || "关".equals(mStartStuts)) {
                        startStatusTv.setText("关机");
                    } else {
                        startStatusTv.setText("开机");
                    }
                }
            }
        }
    }

    private boolean isSetting;
    private boolean isMate;


    @OnClick(R.id.custom_toolbar_iv)
    void backIv() {

        if (isSetting || isMate) {
            RxBus.getInstance().post("isLoad", true);
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (isSetting || isMate) {
            RxBus.getInstance().post("isLoad", true);
        }
        super.onBackPressed();
    }

    @Override
    public void toDo() {

        switch (methodType){
            case MethodType.METHOD_TYPE_RING_MATCH:
                isMate = true;
                ringWithPigeonTv.setText(noMateLists.get(mPosition).getFoot_ring());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
            case MethodType.METHOD_TYPE_RING_UPDATE:

                isSetting = true;

                switch (updateRing) {

                    case UPDATE_RING_LFQ:
                        setPositionMode(update_lfq);
                        break;
                    case UPDATE_RING_RFQ:
                        setReportFreq(update_rfq);
                        break;
                    case UPDATE_RING_ON_TIME:
                        setPowerOntime(update_ontime);
                        break;
                    case UPDATE_RING_OFF_TIME:
                        setPowerOffTime(update_offtime);
                        break;
                }
                break;
        }
    }

    public Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                map.put(MethodParams.PARAMS_PLAYER_ID,getUserObjId());
                break;
            case MethodType.METHOD_TYPE_RING_UPDATE:
                map.put("ringid",getRingObjId());
                switch (updateRing){
                    case UPDATE_RING_ONOFF_STATUS:
                        map.put("on_off_status",update_onoff_status);
                        break;
                    case UPDATE_RING_LFQ:
                        map.put("lfq",update_lfq);
                        break;
                    case UPDATE_RING_RFQ:
                        map.put("rfq",update_rfq);
                        break;
                    case UPDATE_RING_ON_TIME:
                        map.put("on_time",update_ontime);
                        break;
                    case UPDATE_RING_OFF_TIME:
                        map.put("off_time",update_offtime);
                        break;
                }
                break;
        }
        return map;
    }


    private int methodType;
    private int updateRing;

    private String update_onoff_status;
    private String update_lfq;
    private String update_rfq;
    private String update_ontime;
    private String update_offtime;

    @Override
    public String getMethod() {
        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = MethodConstant.DOVE_SEARCH;
                break;
            case MethodType.METHOD_TYPE_RING_UPDATE:
                method = MethodConstant.RING_INFO_UPDATE;
                break;
            case MethodType.METHOD_TYPE_RING_MATCH:
                method = MethodConstant.RING_MATCH;
                break;
        }
        return method;
    }

    @OnClick(R.id.activity_ring_footcode)
    void matePigeon(View view) {

        if (mateSize <= 15) {
            if ("未匹配".equals(((TextView) view).getText().toString().trim())) {
                setMatePigeon();
            }
        } else {
            ApiUtils.showToast(this, "超过最大匹配数");
        }
    }

    @OnClick(R.id.activity_ring_position)
    void setPostModeOncli() {
        methodType = MethodType.METHOD_TYPE_RING_UPDATE;
        posiModeTag = "POSITION_MODE";
        setLocateTime2(Thrift_TAG1);
    }

    @OnClick(R.id.activity_ring_reportedfreq)
    void setRepFreqOncli() {
        methodType = MethodType.METHOD_TYPE_RING_UPDATE;
        posiModeTag = "REPORTED_FREQ";
        setLocateTime2(Thrift_TAG2);
    }

    @OnClick(R.id.activity_ring_schedule_on)
    void setScheduleOn() {
        methodType = MethodType.METHOD_TYPE_RING_UPDATE;
        posiModeTag = TIMING_SWITCH_1;
        toSetTime(TIMING_SWITCH_1);
    }

    @OnClick(R.id.activity_ring_schedule_off)
    void setScheduleOff() {
        methodType = MethodType.METHOD_TYPE_RING_UPDATE;
        posiModeTag = TIMING_SWITCH_2;
        toSetTime(TIMING_SWITCH_2);
    }

    public void setMatePigeon() {

        dialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.mate_pigeon_dialog,null);
        dialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mMateRv = (RecyclerView) view.findViewById(R.id.mate_pigeon_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMateRv.addItemDecoration(new RecycleviewDaviding(this, R.drawable.daviding));
        mMateRv.setLayoutManager(linearLayoutManager);
        mMateRv.setAdapter(matePigeonAdapter);

        getDoveData();

        ApiUtils.setDialogWindow(dialog);

    }

    public void getDoveData(){
        if (!ApiUtils.isNetworkConnected(this)) {
            infoPresenter.getDatasFromDao(getUserObjId());
        } else {
            methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
            infoPresenter.getDataFromNets(getParaMap());
        }
    }

    public void setPositionMode(String posMode) {

        if (myRingBean != null) {
            myRingBean.setLfq(Integer.parseInt(posMode));
        }

        switch (posMode) {
            case "1":
                postionModeTv.setText("1分钟/次");
                break;
            case "2":
                postionModeTv.setText("2分钟/次");
                break;
            case "3":
                postionModeTv.setText("3分钟/次");
                break;
            case "4":
                postionModeTv.setText("4分钟/次");
                break;
            case "5":
                postionModeTv.setText("5分钟/次");
                break;
        }
    }

    public void setReportFreq(String freq) {

        if (myRingBean != null) {
            myRingBean.setRfq(Integer.parseInt(freq));
        }

        switch (freq) {
            case "1":
                repFreqTv.setText("1分钟/次");
                break;
            case "2":
                repFreqTv.setText("2分钟/次");
                break;
            case "3":
                repFreqTv.setText("3分钟/次");
                break;
            case "4":
                repFreqTv.setText("4分钟/次");
                break;
            case "5":
                repFreqTv.setText("5分钟/次");
                break;
        }
    }

    public void setPowerOntime(String onTime) {

        schOnTv.setText(onTime);
    }

    public void setPowerOffTime(String offTime) {

        schOffTv.setText(offTime);
    }

    @Override
    public String getUserObjId() {
        return mUserObjId;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    public String getRingObjId() {
        return mRingObjId;
    }

    public String getPositionMode() {

        String positionMode = postionModeTv.getText().toString().trim();

        switch (positionMode) {
            case "1分钟/次":
                positionMode = "1";
                break;
            case "2分钟/次":
                positionMode = "2";
                break;
            case "3分钟/次":
                positionMode = "3";
                break;
            case "4分钟/次":
                positionMode = "4";
                break;
            case "5分钟/次":
                positionMode = "5";
                break;
        }

        return positionMode;
    }

    public String getReportedFreq() {

        String repFreq = repFreqTv.getText().toString().trim();

        switch (repFreq) {
            case "1分钟/次":
                repFreq = "1";
                break;
            case "2分钟/次":
                repFreq = "2";
                break;
            case "3分钟/次":
                repFreq = "3";
                break;
            case "4分钟/次":
                repFreq = "4";
                break;
            case "5分钟/次":
                repFreq = "5";
                break;
        }

        return repFreq;
    }

    public String getPowerOnTime() {
        return schOnTv.getText().toString().trim();
    }

    public String getPowerOffTime() {
        return schOffTv.getText().toString().trim();
    }

    @Override
    public void toSetData(List<InnerDoveData> object) {
        if (object.size() == 0) {
            ApiUtils.showToast(this, "没有可匹配的信鸽");
            return;
        }

        this.noMateLists = object;

        matePigeonAdapter.addPigeonDatas(object);

        for (int i = 0; i < noMateLists.size(); i++) {
            Log.e("matedfa",noMateLists.get(i).getDoveid()+"-----------"+noMateLists.get(i).getFoot_ring());
        }

        dialog.show();

        if (matePigeonAdapter != null) {
            matePigeonAdapter.setOnItemClickListener(new MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    if (noMateLists != null) {
                        String pigeonObjId = noMateLists.get(position).getDoveid();
                        methodType = MethodType.METHOD_TYPE_RING_MATCH;

                        Map<String,String> mateMap = new HashMap<>();

                        mateMap.put("method",getMethod());
                        mateMap.put("sign",getSign());
                        mateMap.put("time",getTime());
                        mateMap.put("version",getVersion());
                        mateMap.put("userid",getUserObjId());
                        mateMap.put("token",getToken());

                        JSONArray array = new JSONArray();
                        JSONObject object = new JSONObject();
                        object.put("doveid",pigeonObjId);
                        object.put("ringid",getRingObjId());
                        array.add(object);

                        mateMap.put("data",array.toJSONString());

                        Log.e("matedfa",array.toJSONString()+"-----data");

                        ourCodePresenter.ringMatchDove(mateMap);

                        mPosition = position;
                    }
                }
            });
        }
    }


    private int mPosition;

    @Override
    public void onMateSuccess(String msg, int position) {

    }

    private void locate(String tag,String timeTag){
        if (Thrift_TAG1.equals(tag)) {
            updateRing = UPDATE_RING_LFQ;
            update_lfq = timeTag;
            ourCodePresenter.updateRing(getParaMap());
        } else {
            updateRing = UPDATE_RING_RFQ;
            update_rfq = timeTag;
            ourCodePresenter.updateRing(getParaMap());
        }
    }

    private void setLocateTime2(final String tag) {

        dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.obrit_thickness_dialog,null);
        dialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mTitleTv = (TextView)view.findViewById(R.id.title);

        TextView mThickCancel = (TextView)view.findViewById(R.id.thickness_dialog_cancel);

        mThickCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (Thrift_TAG1.equals(tag)) {
            mTitleTv.setText("定位频率");
        } else {
            mTitleTv.setText("报点频率");
        }

        final TextView v1 = (TextView) view.findViewById(R.id.thickness_dialog_thin);
        TextView v2 = (TextView) view.findViewById(R.id.thickness_dialog_medium);
        TextView v3 = (TextView) view.findViewById(R.id.thickness_dialog_crude);
        TextView v4 = (TextView) view.findViewById(R.id.thickness_dialog_crude_2);
        TextView v5 = (TextView) view.findViewById(R.id.thickness_dialog_crude_3);

        final View line4 = view.findViewById(R.id.line4);
        View line5 = view.findViewById(R.id.line5);

        v4.setVisibility(View.VISIBLE);
        v5.setVisibility(View.VISIBLE);
        line4.setVisibility(View.VISIBLE);
        line5.setVisibility(View.VISIBLE);
        v1.setText("1分钟/次");
        v2.setText("2分钟/次");
        v3.setText("3分钟/次");
        v4.setText("4分钟/次");
        v5.setText("5分钟/次");

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(RingInfoActivity.this)) {
                    ApiUtils.showToast(RingInfoActivity.this, getString(R.string.net_conn_2));
                    return;
                }

                if (!checkModeAndFreq(tag, 1)) {

                    ApiUtils.showToast(RingInfoActivity.this, "报点频率必须大于定位频率");
                    dialog.dismiss();
                    return;
                }

                locate(tag,"1");

                dialog.dismiss();
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(RingInfoActivity.this)) {
                    ApiUtils.showToast(RingInfoActivity.this, getString(R.string.net_conn_2));
                    return;
                }

                if (!checkModeAndFreq(tag, 2)) {

                    ApiUtils.showToast(RingInfoActivity.this, "报点频率必须大于定位频率");
                    dialog.dismiss();
                    return;
                }

                locate(tag,"2");

                dialog.dismiss();
            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ApiUtils.isNetworkConnected(RingInfoActivity.this)) {
                    ApiUtils.showToast(RingInfoActivity.this, getString(R.string.net_conn_2));
                    return;
                }

                if (!checkModeAndFreq(tag, 3)) {

                    ApiUtils.showToast(RingInfoActivity.this, "报点频率必须大于定位频率");
                    dialog.dismiss();
                    return;
                }

                locate(tag,"3");

                dialog.dismiss();
            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(RingInfoActivity.this)) {
                    ApiUtils.showToast(RingInfoActivity.this, getString(R.string.net_conn_2));
                    return;
                }

                if (!checkModeAndFreq(tag, 4)) {

                    ApiUtils.showToast(RingInfoActivity.this, "报点频率必须大于定位频率");
                    dialog.dismiss();
                    return;
                }

                locate(tag,"4");
                dialog.dismiss();
            }
        });
        v5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(RingInfoActivity.this)) {
                    ApiUtils.showToast(RingInfoActivity.this, getString(R.string.net_conn_2));
                    return;
                }

                if (!checkModeAndFreq(tag, 5)) {

                    ApiUtils.showToast(RingInfoActivity.this, "报点频率必须大于定位频率");
                    dialog.dismiss();
                    return;
                }
                locate(tag,"5");
                dialog.dismiss();
            }
        });
        ApiUtils.setDialogWindow(dialog);
        dialog.show();
    }

    public boolean checkModeAndFreq(String tag, int num) {

        boolean flag = true;

        //报点频率要大于等于定位频率

        if (Thrift_TAG1.equals(tag)) {
            int repFreqInt = Integer.parseInt(getReportedFreq());
            if (repFreqInt < num)
                flag = false;
        } else {
            int posModeInt = Integer.parseInt(getPositionMode());
            if (num < posModeInt)
                flag = false;
        }

        return flag;
    }

    private int onHourNewVal;
    private int onMinNewVal;

    private void toSetTime(final String tag) {

        String mOntimeStrTv = getPowerOnTime();
        String mOfftimeStrTv = getPowerOffTime();

        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.timing_ontime_dialog, null);

        dialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.timing_dialog_np_year);
        NumberPicker minPicker = (NumberPicker) view.findViewById(R.id.timing_dialog_np_month);

//        TextView title = (TextView) view.findViewById(R.id.timing_dialog_ontime_title);
        TextView timingCancle = (TextView) view.findViewById(R.id.timing_dialog_cancle);
        TextView timingConfirm = (TextView) view.findViewById(R.id.timing_dialog_confirm);

        timingCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        timingConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempHour = String.valueOf(onHourNewVal);
                String tempMin = String.valueOf(onMinNewVal);

                if (onHourNewVal < 10) {
                    tempHour = "0" + tempHour;
                }
                if (onMinNewVal < 10) {
                    tempMin = "0" + tempMin;
                }

                if (TIMING_SWITCH_1.equals(tag)) {
                    onTime = tempHour + ":" + tempMin;
                    update_ontime = onTime;
                    updateRing = UPDATE_RING_ON_TIME;
                    ourCodePresenter.updateRing(getParaMap());
                } else {
                    offTime = tempHour + ":" + tempMin;
                    update_offtime = offTime;
                    updateRing = UPDATE_RING_OFF_TIME;
                    ourCodePresenter.updateRing(getParaMap());
                }
                dialog.dismiss();
            }
        });

        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setMinValue(0);

        if (TIMING_SWITCH_1.equals(tag)) {
            hourPicker.setValue(Integer.parseInt(mOntimeStrTv.split(":")[0]));
            minPicker.setValue(Integer.parseInt(mOntimeStrTv.split(":")[1]));
        } else {
            hourPicker.setValue(Integer.parseInt(mOfftimeStrTv.split(":")[0]));
            minPicker.setValue(Integer.parseInt(mOfftimeStrTv.split(":")[1]));
        }

        minPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String mMonthStr = String.valueOf(value);
                if (value < 10) {
                    mMonthStr = "0" + mMonthStr;
                }
                return mMonthStr;
            }
        });

        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String mMonthStr = String.valueOf(value);
                if (value < 10) {
                    mMonthStr = "0" + mMonthStr;
                }
                return mMonthStr;
            }
        });

        onHourNewVal = hourPicker.getValue();
        onMinNewVal = minPicker.getValue();

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                onHourNewVal = newVal;
            }
        });
        minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onMinNewVal = newVal;
            }
        });
        ApiUtils.setDialogWindow(dialog);
        dialog.show();
    }

}
