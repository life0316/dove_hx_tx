package com.haoxi.dove.modules.pigeon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.github.clans.fab.FloatingActionMenu;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.HorizontalScrollViewAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.bean.RealFlyBean;
import com.haoxi.dove.bean.SetTriBean;
import com.haoxi.dove.callback.EachInfoClickListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.modules.mvp.presenters.SetTriPresenter;
import com.haoxi.dove.modules.mvp.presenters.TraPresenter;
import com.haoxi.dove.modules.mvp.views.ITrajectoryView;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.TraUtils;
import com.haoxi.dove.widget.ColorPickerView;
import com.haoxi.dove.widget.MyHorizontalScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

@ActivityFragmentInject(contentViewId = R.layout.activity_new_fly)
public class NewFlyActivity extends BaseActivity implements ITrajectoryView, LocationSource {

    private static final String TAG = "NewFlyActivity";

    @BindView(R.id.activity_pegionfly_mapview)
    MapView mapView;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_keep)
    TextView mRefrashTv;
    @BindView(R.id.activity_pegionfly_start)
    Button mFlyBtn;
    @BindView(R.id.activity_pigeonfly_fabmenu)
    FloatingActionMenu mFabMenu;

    @BindView(R.id.activity_pigeonfly_power)
    TextView mPowerTv;
    @BindView(R.id.activity_newfly_ring)
    TextView mRingTv;
    @BindView(R.id.total_time)
    TextView mTotalTimeTv;
    @BindView(R.id.mileage)
    TextView mMileageTv;

    @Inject
    TraPresenter traPresenter;

    @Inject
    SetTriPresenter setTriPresenter;

    @Inject
    DaoSession daoSession;

    @Inject
    RxBus mRxBus;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private final String FLY_START = "START_FLYING";
    private final String FLY_END = "FLY_END";

    private String flyTag = "START_FLYING";


    private List<Integer> mDatas2 = new ArrayList<Integer>(Arrays.asList( R.mipmap.icon_img_2, R.mipmap.icon_img_3, R.mipmap.icon_img_4
            , R.mipmap.icon_img_5, R.mipmap.icon_img_6/*, R.mipmap.icon_img_7, R.mipmap.icon_img_8,R.mipmap.icon_img_9*/));
    private String[] mDatasStr = {"样式一", "样式二", "样式三", "样式四", "样式五"/*, "样式六", "样式七", "样式八",*/};

    private AMap mAMap;
    private UiSettings mUiset;

    private OnLocationChangedListener mChangedListener;
    private AMapLocation mAMapLocation;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationClientOption = null;

    private LatLng firstLatLng = null;
    private LatLng preLatLng = null;

    private Marker addMarker;
    private CameraUpdate cu;
    private MarkerOptions option;

    private String mLastTime = "";
    private String mFirstTime = "";
    private boolean isShowMarker = true;

    private int locationTime = 1;
    private int mTrailWidth = 10;
    private String mTrailColor = "#00ff00";
    private int mTrailPic = R.mipmap.icon_img_3;
    private boolean isNeedCheck = true;
    private boolean isRefrash = false;

    private int isFly = 0;

    private long pauseTime = 0;

    private boolean isFlying = false;

    private Observable<Boolean> loadObservable;
    private boolean isLoad = true;


    private List<Long> realFlyBeanIds = new ArrayList<>();

    private int mTriPicSeleted = 0;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:

                    Log.e(TAG, locationTime + "---locationTime----" + TAG);
                    mHandler.removeMessages(1);
                    getFlyDatas(false);

                    sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
                    break;
                case 0:
                    break;
            }
        }
    };
    private MyApplication application;

    private LatLng latLng;
    private SetTriBean setTriBean;

    private static final String TYPE_DATA_FROM_NETS = "DATA_FROM_NETS";
    private static final String TYPE_DATA_FROM_DAO = "DATA_FROM_DAO";

    private String dataFrom = TYPE_DATA_FROM_DAO;
    private TraUtils traUtils;
    private List<RealFlyBean> currentFlyBeen;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        traUtils = new TraUtils();

        mapView.onCreate(savedInstanceState);

        application = MyApplication.getMyBaseApplication();
        currentFlyBeen = application.getCurrentFlyBeans();
        currentFlyBeen.clear();

        Intent intent = getIntent();
        if (intent != null) {
//            pigeonBean = (MyPigeonBean1) intent.getParcelableExtra("pigeonBean");


//            mTitleTv.setText("信鸽: " + pigeonBean.getFOOT_RING_CODE());
//            mRingTv.setText("鸽环编号: " + pigeonBean.getRING_CODE());
        }

        loadObservable = mRxBus.register("isLoad", Boolean.class);

        loadObservable.subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,e.getMessage() + "--loadObservable--"+TAG);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                isLoad = aBoolean;
            }
        });

        initMap();

//        daoSession.getMyRingBeanDao().queryBuilder()
//                .rx().list()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<MyRingBean>>() {
//                    @Override
//                    public void call(List<MyRingBean> myRingBeen) {
//                        ringBean = myRingBeen.get(0);
//
//                        if (ringBean != null) {
//                            if (ringBean.getREPORTED_FREQ() != null) {
//                                switch (ringBean.getREPORTED_FREQ()) {
//                                    case "1":
//                                        locationTime = 1;
//                                        break;
//                                    case "2":
//                                        locationTime = 2;
//                                        break;
//                                    case "3":
//                                        locationTime = 3;
//                                        break;
//                                    case "4":
//                                        locationTime = 4;
//                                        break;
//                                    case "5":
//                                        locationTime = 5;
//                                        break;
//                                }
//                            }
//                        }
//                        Log.e(TAG, locationTime + "--2---locationTime");
//                        Log.e(TAG, ringBean.getRING_CODE() + "---3----ringcode---" + ringBean.getOBJ_ID());
//                    }
//                });

        traUtils.setEachInfoListener(new EachInfoClickListener() {
            @Override
            public void eachInfo(String totalTime, float mileage, float lawSpeed,float aveSpeed,float fastSpeed) {


                if (!"".equals(totalTime)) {

                    mTotalTimeTv.setText(totalTime);
                }

                if (0 != mileage) {

                    mAllMileage = mileage;

                    mMileageTv.setText(String.valueOf(mAllMileage));
                }
            }
        });
    }

    private float mAllMileage = 0;



    @Override
    protected void initInject() {

    }

    private void restartFly() {

        Log.e(TAG, isFly + "---4---isfly");

        if (isFly == 1) {

            mLocationClient.stopLocation();

            mRefrashTv.setVisibility(View.VISIBLE);
            mRefrashTv.setText("刷新");

            mFlyBtn.setText("结束");

            if (!get) {

//                mFirstTime = mPigeonStart.getString(pigeonBean.getOBJ_ID(), "");

                if (!"".equals(mFirstTime)) {
                    mLastTime = mFirstTime;
                }

                Log.e(TAG, mLastTime + "++++heheda");

                switch (dataFrom) {
                    case TYPE_DATA_FROM_DAO:
//                        traPresenter.getDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), getRingObjId());
                        break;
                    case TYPE_DATA_FROM_NETS:
                        getFlyDatas(false);
                        break;
                }

            } else {
                mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
            }
        } else {
            mFlyBtn.setText("开始");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        TraUtils.clearAllMarker();

        if (isNeedCheck) {
            ApiUtils.checkPermissions(this, ConstantUtils.PERMISSION_REQUESTCODE_1, needPermissions);
        }

        if (isLoad) {
//            setTriPresenter.getTriSetFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), pigeonBean.getRING_OBJ_ID(), isFly, mTrailPic, mTrailColor, mTrailWidth);
        } else {
            if (isFly == 1) {
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void setTri(SetTriBean setTriBean, int trilWidth, String triColor, int triPic, int isFly) {

        this.setTriBean = setTriBean;

        if (triColor != null && !mTrailColor.equals(triColor) && !"".equals(triColor)) {
            mTrailColor = triColor;
        }
        if (trilWidth != 0 && trilWidth != mTrailWidth) {
            mTrailWidth = trilWidth;
        }
        if (triPic != 0 && triPic != mTrailPic) {
            mTrailPic = triPic;
        }

        if (isFly != 0) {
            NewFlyActivity.this.isFly = isFly;
        }

        restartFly();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ConstantUtils.PERMISSION_REQUESTCODE_1) {
            //没有授权

            if (!ApiUtils.verifyPermissions(grantResults)) {

                isNeedCheck = false;
            } else {
                mLocationClient.startLocation();
                isNeedCheck = false;
            }
        }
    }

    private void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("定位权限");
        builder.setMessage("是否允许相应权限");
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mLocationClient.startLocation();
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        mHandler.removeMessages(1);
        get = true;
        pauseTime = System.currentTimeMillis();
    }

    private boolean get = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(1);
        super.onDestroy();
        mapView.onDestroy();

        mRxBus.unregister("isLoad",loadObservable);
    }

    @Override
    public void showErrorMsg(String errorMsg) {
        super.showErrorMsg(errorMsg);
        isFlying = true;
    }

    @Override
    protected void init() {

        mBackIv.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOncli(View view) {

        if (!isFlying) {

            mRxBus.post("isLoad", false);
        }else {

            mRxBus.post("isLoad",true);

        }

        finish();
    }

    @OnClick(R.id.activity_pegionfly_show)
    void showMarkers() {
        isShowMarker = !isShowMarker;
//        TraUtils.hideLastMarker(isShowMarker);
        //TraUtils.setPoptionShow(mAMap,1,pigeonBean.getOBJ_ID());

    }

    @OnClick(R.id.custom_toolbar_keep)
    void refrashOncli(View view) {
        isRefrash = true;
       // preLatLng = null;

        mHandler.removeMessages(1);

        if (pauseTime != 0 && (System.currentTimeMillis() - pauseTime) > 1000 * 60 * locationTime) {
//            mLastTime = mPigeonStart.getString(pigeonBean.getOBJ_ID(), "");

            Log.e("systemteim", mLastTime + "-----1");

            pauseTime = 0;
        }

        Log.e("systemteim", mLastTime + "-----2");


        if ("".equals(mLastTime)){

            mAMap.clear(true);
        }

//        traPresenter.getFlyDatas(isRefrash);
        isRefrash = false;
    }

    @OnClick(R.id.activity_pigeonfly_fab_location)
    void fabLocation(View view) {
        if (addMarker != null) {
            //addMarker.remove();
        }
        TraUtils.removeMarker();

        if (lastFlyBean != null) {

            LatLng lastLatLng = ApiUtils.transform(Double.parseDouble(lastFlyBean.getLATITUDE()), Double.parseDouble(lastFlyBean.getLONGITUDE()));

            cu = CameraUpdateFactory.changeLatLng(lastLatLng);
            //更新地图显示区域
            mAMap.moveCamera(cu);

            //创建markeroptions对象
            option = new MarkerOptions();
            //设置markeroptions
            option.position(lastLatLng).title("记录时间:" + lastFlyBean.getGENERATE_TIME()
                    + "\n" + "速度:" + lastFlyBean.getFLYING_SPEED()
                    + "\n" + "方向:" + lastFlyBean.getFLYING_DIRECTION()
                    + "\n" + "经度:" + lastFlyBean.getLONGITUDE()
                    + "\n" + "纬度:" + lastFlyBean.getLATITUDE())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_img_2));

            addMarker = mAMap.addMarker(option);
            addMarker.setFlat(true);
            addMarker.showInfoWindow();

            mFabMenu.close(false);
        } else {
            ApiUtils.showToast(this, "鸽子未飞行");
        }
    }

    @OnClick(R.id.activity_pigeonfly_fab_size)
    void fabTraSize(View view) {
        setTrilSize();
    }

    @OnClick(R.id.activity_pigeonfly_fab_color)
    void fabTraColor(View view) {
        setTrilColor();
    }

    @OnClick(R.id.activity_pigeonfly_fab_pic)
    void fabTraPic(View view) {
        setTrilPic();
    }

    @OnClick(R.id.activity_pegionfly_start)
    void startBtnOnCli(View view) {

        Log.e("timefa", ApiUtils.secsBetween2("2017-05-04 17:11:20", "2017-05-04 17:13:18") + "------time");
        Log.e("timefa", ApiUtils.secsBetween2("2017-05-04 17:11:20", "2017-05-04 17:20:20") + "------time2");


        Log.e("timefa", ApiUtils.formatTime(122) + "------time3");


        if ("开始".equals(((Button) view).getText())) {

            ArrayList<RealFlyBean> list = new ArrayList<>();

            RealFlyBean realFlyBean_1 = new RealFlyBean();
            realFlyBean_1.setLATITUDE("31.1180115208");
            realFlyBean_1.setLONGITUDE("120.5448813990");

            RealFlyBean realFlyBean2 = new RealFlyBean();
            realFlyBean2.setLATITUDE("31.1316472526");
            realFlyBean2.setLONGITUDE("120.5861696782");

            RealFlyBean realFlyBean3 = new RealFlyBean();
            realFlyBean3.setLATITUDE("31.0991833329");
            realFlyBean3.setLONGITUDE("120.6321909325");

            RealFlyBean realFlyBean4 = new RealFlyBean();
            realFlyBean4.setLATITUDE("31.0806523910");
            realFlyBean4.setLONGITUDE("120.4237191725");

            RealFlyBean realFlyBean5 = new RealFlyBean();
            realFlyBean5.setLATITUDE("31.7191255716");
            realFlyBean5.setLONGITUDE("120.3750035543");

            list.add(realFlyBean_1);
            list.add(realFlyBean2);
            list.add(realFlyBean3);
            list.add(realFlyBean4);
            list.add(realFlyBean5);


            toDrawTrail(list);

//            toStartFly();
        } else {
            toEndFly();
        }
    }

    private void initMap() {

        if (mAMap == null) {
            mAMap = mapView.getMap();
            mUiset = mAMap.getUiSettings();//设置ui控件
        }

        //是否允许显示缩放按钮
        mUiset.setZoomControlsEnabled(true);
        mUiset.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);

        mUiset.setMyLocationButtonEnabled(true);

        mUiset.setScaleControlsEnabled(true);//设置地图默认显示比例
        mUiset.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);


        CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
        mAMap.moveCamera(cu);

        //自定义定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //    myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_img_2));
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色

        mAMap.setLocationSource(this);

        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);

        //    mAMap.clear();

        initClient();
        initOption();

    }

    private void initOption() {

        //初始化定位参数

        mLocationClientOption = new AMapLocationClientOption();

        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

        mLocationClientOption.setNeedAddress(true);

        //设置是否只定位一次，
        mLocationClientOption.setOnceLocation(true);
        mLocationClientOption.setInterval(2000);

        if (mLocationClientOption.isOnceLocationLatest()) {
            mLocationClientOption.setOnceLocationLatest(true);
        }
        mLocationClientOption.setWifiActiveScan(true);
        mLocationClientOption.setMockEnable(false);

        mLocationClient.setLocationOption(mLocationClientOption);
        mLocationClient.startLocation();


    }

    private void initClient() {

        //初始化定位
        mLocationClient = new AMapLocationClient((getApplicationContext()));
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Log.e("mafafaf", marker.getTitle() + "----title");

                //showEachMarker(marker);

                //marker.showInfoWindow();

                showPop(marker);
                return true;
            }
        };

        mAMap.setOnMarkerClickListener(listener);
    }


    private void showPop(Marker marker) {


        String markerTitle = marker.getTitle();
        String[] eachTitle = markerTitle.split("#");

        String createTime = eachTitle[0];
        String eachSpeed = eachTitle[1];
        String eachDirection = eachTitle[2];
        String eachLongitude = eachTitle[3];
        String eachLatitude = eachTitle[4];
        String eachHeight = eachTitle[5];
        String eachDistance = eachTitle[6];


        final Dialog popDialog = new Dialog(this, R.style.DialogTheme2);

        View view = View.inflate(this, R.layout.layout_show_marker2, null);
        popDialog.setCancelable(false);
        popDialog.setContentView(view);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.show_marker_ll);

        int width = getWindowManager().getDefaultDisplay().getWidth();

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (width * 62) / 72;
        params.height = (int) ((width * 42) / 72);

        layout.setLayoutParams(params);


        //时间
        TextView mCreateTimeTv = (TextView) view.findViewById(R.id.show_marker_time);


        TextView mSpeedTv = (TextView) view.findViewById(R.id.show_marker_speed);
        TextView mHeightTv = (TextView) view.findViewById(R.id.show_marker_height);

        //纬经度
        TextView mLatlngTv = (TextView) view.findViewById(R.id.show_marker_latlng);

        //方向
        TextView mDirectionTv = (TextView) view.findViewById(R.id.show_marker_direction);

        //总飞行
        TextView mMileageTv = (TextView) view.findViewById(R.id.show_marker_mileage);
        ImageView mDismissIv = (ImageView) view.findViewById(R.id.show_marker_dismiss);


        mCreateTimeTv.setText(createTime);
        mSpeedTv.setText(eachSpeed);
        //mHeightTv.setText(eachHeight);
        mHeightTv.setText(String.valueOf(Double.valueOf(eachHeight) - 20));
        mLatlngTv.setText("东经"+Math.rint(Double.valueOf(eachLongitude))+" 北纬"+Math.rint(Double.valueOf(eachLatitude)));
        mMileageTv.setText(eachDistance);
        mDirectionTv.setText("方向：" + eachDirection);

        mDismissIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });

        popDialog.show();
    }


    private void showEachMarker(Marker marker) {

//        options.position(endLatLng).title(lastBean.getGENERATE_TIME()
//                        + "#" + lastBean.getFLYING_SPEED()
//                        + "#" + lastBean.getFLYING_DIRECTION()
//                        + "#" + lastBean.getLONGITUDE()
//                        + "#" + lastBean.getLATITUDE()
//                        + "#" + lastBean.getFLYING_HEIGHT()
//                        + "#" + lastBean.getLATITUDE()
//                        + "#" + String.valueOf(totalDistance)

        String markerTitle = marker.getTitle();
        String[] eachTitle = markerTitle.split("#");

        String createTime = eachTitle[0];
        String eachSpeed = eachTitle[1];
        String eachDirection = eachTitle[2];
        String eachLongitude = eachTitle[3];
        String eachLatitude = eachTitle[4];
        String eachHeight = eachTitle[5];
        String eachDistance = eachTitle[6];

        builder = new AlertDialog.Builder(this);


        View view = getLayoutInflater().inflate(R.layout.layout_show_marker, null);

        TextView mCreateTimeTv = (TextView) view.findViewById(R.id.show_marker_time);
        TextView mSpeedTv = (TextView) view.findViewById(R.id.show_marker_speed);
        TextView mHeightTv = (TextView) view.findViewById(R.id.show_marker_height);
        TextView mLatitudeTv = (TextView) view.findViewById(R.id.show_marker_latitude);
        TextView mLongitudeTv = (TextView) view.findViewById(R.id.show_marker_longitude);
        TextView mDirectionTv = (TextView) view.findViewById(R.id.show_marker_direction);
        TextView mAddressTv = (TextView) view.findViewById(R.id.show_marker_address);
        TextView mMileageTv = (TextView) view.findViewById(R.id.show_marker_mileage);
        ImageView mDismissIv = (ImageView) view.findViewById(R.id.show_marker_dismiss);


        mCreateTimeTv.setText(createTime);
        mSpeedTv.setText(eachSpeed);
        mHeightTv.setText(eachHeight);
        mLongitudeTv.setText(eachLongitude);
        mLatitudeTv.setText(eachLatitude);
        mMileageTv.setText(eachDistance);
        mDirectionTv.setText("方向：" + eachDirection);


        mDismissIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Log.e("marker", marker.getTitle());

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    //声明定位回调监听
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                NewFlyActivity.this.mAMapLocation = aMapLocation;

                if (mChangedListener != null) {
                    mChangedListener.onLocationChanged(aMapLocation);

                    firstLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                    CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
                    mAMap.moveCamera(cu);

                }
            } else if (aMapLocation.getErrorCode() == 12) {
                ApiUtils.showToast(NewFlyActivity.this, "缺少定位权限,定位失败");
            } else if (aMapLocation.getErrorCode() == 4) {
                ApiUtils.showToast(NewFlyActivity.this, "当前网络较差,请求服务器异常,定位失败");
            }
        }
    };

    @Override
    public void toStartFly() {

        flyTag = FLY_START;
//        codePresenter.startFlying(getToken(), getPigeonObjId(), getRingCode());
    }


    @Override
    public void toEndFly() {

        flyTag = FLY_END;
//        codePresenter.endFlying(getToken(), getPigeonObjId(), getRingCode());
    }

    @Override
    public void getFlyDatas(boolean isRefrash) {

        Log.e(TAG, mTrailWidth + "---getFlyDatas----2--mTrailWidth");

//        traPresenter.getFlyDatas(isRefrash);
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public String getUserObjId() {
        return mUserObjId;
    }

    @Override
    public String getRingObjId() {
//        return pigeonBean.getRING_OBJ_ID();
        return "";
    }

    @Override
    public JSONArray getPigeonObjIds() {

        JSONArray array = new JSONArray();

//        if (pigeonBean != null) {
//            String pigeonObjId = pigeonBean.getOBJ_ID();
//            if (!"".equals(pigeonObjId)) {
//                array.add(pigeonObjId);
//                return array;
//            } else {
//                return null;
//            }
//        }
        return null;
    }

    @Override
    public JSONArray getPigeonObjId() {

//        JSONArray array = new JSONArray();
//        if (pigeonBean != null) {
//            String pigeonObjId = pigeonBean.getOBJ_ID();
//            if (!"".equals(pigeonObjId)) {
//                array.add(pigeonObjId);
//                return array;
//            } else {
//                return null;
//            }
//        }

        return null;
    }

    @Override
    public JSONArray getRingCode() {

//        JSONArray array = new JSONArray();
//
//        if (pigeonBean != null) {
//            String ringCode = pigeonBean.getRING_CODE();
//            if (!"".equals(ringCode)) {
//                array.add(ringCode);
//                return array;
//            } else {
//                return null;
//            }
//        }
        return null;
    }

    @Override
    public String getLastTime() {
        return mLastTime;
    }

    public void toSetStartFly() {

        isFly = 1;

        this.isFlying = true;

        if (setTriBean != null) {
            setTriBean.setIsFlying(1);
            daoSession.getSetTriBeanDao().update(setTriBean);
        }

        mRefrashTv.setVisibility(View.VISIBLE);
        mRefrashTv.setText("刷新");
        mFlyBtn.setText("结束");
        getFlyDatas(false);
    }

    public void toSetEndFly() {

        TraUtils.clearAllMarker();

        isFly = 0;

        this.isFlying = true;

        if (setTriBean != null) {
//            setTriBean.setIsFlying(0);
//            daoSession.getSetTriBeanDao().update(setTriBean);
            daoSession.getSetTriBeanDao().delete(setTriBean);
        }

        mRefrashTv.setVisibility(View.GONE);
        mFlyBtn.setVisibility(View.GONE);

//        for (int i = 0; i < realFlyBeanIds.size(); i++) {
//            traPresenter.removeDatasFromDao(realFlyBeanIds.get(i));
//        }
//
//        realFlyBeanIds.clear();

//        traPresenter.removeDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID());

        mLastTime = "";
        mHandler.removeMessages(1);
    }

    @Override
    public void toDrawTrail(final List<RealFlyBean> list) {

        if (addMarker != null) {
            addMarker.remove();
        }

        if ("".equals(getLastTime())) {
            preLatLng = null;
        }

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            final RealFlyBean realFlyBean = list.get(i);

            if (!realFlyBeanIds.contains(realFlyBean.getId())) {
                realFlyBeanIds.add(realFlyBean.getId());
            }

            if (realFlyBean.getSURPLUS_POWER() != null && !"".equals(realFlyBean.getSURPLUS_POWER())) {
                mPowerTv.setVisibility(View.VISIBLE);
                mPowerTv.setText("电量： " + realFlyBean.getSURPLUS_POWER() + "%");
            } else {
                mPowerTv.setVisibility(View.GONE);
            }

            latLng = ApiUtils.transform(Double.parseDouble(realFlyBean.getLATITUDE()), Double.parseDouble(realFlyBean.getLONGITUDE()));
            latLngs.add(latLng);

            if (i != 0) {
                RealFlyBean realFlyBean1 = list.get(i - 1);
                preLatLng = ApiUtils.transform(Double.parseDouble(realFlyBean1.getLATITUDE()), Double.parseDouble(realFlyBean1.getLONGITUDE()));
            }

            if (latLngs.size() <= 2) {
                TraUtils.drawLineFromLatlng(mAMap, latLng, preLatLng, firstLatLng, mTrailPic, Color.parseColor(mTrailColor), mTrailWidth);
            }

            if (i == list.size() - 1) {
                mLastTime = realFlyBean.getGENERATE_TIME();
                preLatLng = latLng;
                //mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
            }
        }

        if (latLngs.size() > 2) {
            //TraUtils.drawTraFromList(mAMap, latLngs, mTrailPic, Color.parseColor(mTrailColor), mTrailWidth);
//            TraUtils.drawTraFromListTest(pigeonBean.getOBJ_ID(), 1, mAMap, latLngs, mTrailPic, Color.parseColor(mTrailColor), mTrailWidth);
        }
    }

    @Override
    public void onFailed(String msg) {
        ApiUtils.showToast(this, msg);

        Log.e(TAG, (preLatLng == null) + "---pigeonfly----" + mLastTime);

        if (preLatLng == null && "".equals(mLastTime)) {

            mLocationClient.startLocation();
        } else {
            if (preLatLng != null) {
                //创建一个设置经纬度的cameraupdate
                CameraUpdate cu = CameraUpdateFactory.changeLatLng(preLatLng);
                //更新地图显示区域
                mAMap.moveCamera(cu);
            }

            //TraUtils.hideLastMarker(true);
        }

        if (isFirst) {

            mHandler.sendEmptyMessage(1);
            isFirst = false;
        } else {
            mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
        }
    }

    private boolean isFirst = true;


    private RealFlyBean preFlyBean;

    private RealFlyBean lastFlyBean;

    private RealFlyBean firstFlyBean;

    @Override
    public void trailFromDao(List<RealFlyBean> list) {

        for (int i = 0; i < list.size(); i++) {
            Log.e(TAG, list.get(i).getGENERATE_TIME() + "----2--time");
            Log.e(TAG, list.get(i).getLATITUDE() + "--3----latitude");
            Log.e(TAG, list.get(i).getLONGITUDE() + "---4---longitude");
        }

        if (addMarker != null) {
            addMarker.remove();
        }

        if ("".equals(getLastTime())) {
            preFlyBean = null;
            mAllMileage = 0;
        }

        if (preFlyBean == null) {
            firstFlyBean = list.get(0);
            currentFlyBeen.clear();
        }

        if (list.size() == 0 || list == null) {

            // mLastTime = "";
            dataFrom = TYPE_DATA_FROM_NETS;
            //getFlyDatas(isRefrash);
            mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
            return;
        } else {
            dataFrom = TYPE_DATA_FROM_DAO;
            mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
        }


        if (list.size() > 2) {
            TraUtils.drawTraFromList1(mAllMileage,mAMap, firstFlyBean, list, mTrailPic, Color.parseColor(mTrailColor), mTrailWidth);
            RealFlyBean realFlyBean = list.get(list.size() - 1);

            mLastTime = realFlyBean.getGENERATE_TIME();
            preFlyBean = realFlyBean;
            lastFlyBean = realFlyBean;
            return;
        }

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            final RealFlyBean realFlyBean = list.get(i);

            latLng = ApiUtils.transform(Double.parseDouble(realFlyBean.getLATITUDE()), Double.parseDouble(realFlyBean.getLONGITUDE()));

            latLngs.add(latLng);

            if (i != 0) {

                RealFlyBean realFlyBean1 = list.get(i - 1);
                preLatLng = ApiUtils.transform(Double.parseDouble(realFlyBean1.getLATITUDE()), Double.parseDouble(realFlyBean1.getLONGITUDE()));
                preFlyBean = realFlyBean1;
            }

            if (latLngs.size() <= 2) {
                TraUtils.drawLineFromLatlng1(mAllMileage,mAMap, realFlyBean, preFlyBean, firstFlyBean, mTrailPic, Color.parseColor(mTrailColor), mTrailWidth);
            }

            if (i == list.size() - 1) {
                mLastTime = realFlyBean.getGENERATE_TIME();
                preFlyBean = realFlyBean;
                lastFlyBean = realFlyBean;
            }
        }
    }


    @Override
    public void toHandler() {
        mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * locationTime);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mChangedListener = null;
        if (mChangedListener != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private void setTrilSize() {
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.obrit_thickness_dialog, null);
        dialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final TextView mThinTv = (TextView) view.findViewById(R.id.thickness_dialog_thin);
        final TextView mMediumTv = (TextView) view.findViewById(R.id.thickness_dialog_medium);
        final TextView mCrudeTv = (TextView) view.findViewById(R.id.thickness_dialog_crude);

        if(mTrailWidth == 5) {
            mThinTv.setTextColor(Color.RED);
        }
        else if(mTrailWidth == 10) {
            mMediumTv.setTextColor(Color.RED);
        }
        else{
            mCrudeTv.setTextColor(Color.RED);
        }

        TextView mThickCancel = (TextView) view.findViewById(R.id.thickness_dialog_cancel);
        mThinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrailWidth = 5;

                isFlying = true;

                if (setTriBean != null) {
                    setTriBean.setTrilWidth(mTrailWidth);
                    daoSession.getSetTriBeanDao().update(setTriBean);
                }

                mLastTime = "";

                if (isFly == 1) {
                    mAMap.clear();
//                    traPresenter.getDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), getRingObjId());
                }

                mThinTv.setTextColor(Color.RED);
                mMediumTv.setTextColor(0xFF0090FF);
                mCrudeTv.setTextColor(0xFF0090FF);
            }
        });
        mMediumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrailWidth = 10;

                isFlying = true;

                if (setTriBean != null) {
                    setTriBean.setTrilWidth(mTrailWidth);
                    daoSession.getSetTriBeanDao().update(setTriBean);
                }

                mLastTime = "";

                if (isFly == 1) {
                    mAMap.clear();
//                    traPresenter.getDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), getRingObjId());
                }

                mThinTv.setTextColor(0xFF0090FF);
                mMediumTv.setTextColor(Color.RED);
                mCrudeTv.setTextColor(0xFF0090FF);
            }
        });
        mCrudeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrailWidth = 20;

                isFlying = true;

                if (setTriBean != null) {
                    setTriBean.setTrilWidth(mTrailWidth);
                    daoSession.getSetTriBeanDao().update(setTriBean);
                }

                mLastTime = "";

                if (isFly == 1) {
                    mAMap.clear();
//                    traPresenter.getDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), getRingObjId());
                }

                mThinTv.setTextColor(0xFF0090FF);
                mMediumTv.setTextColor(0xFF0090FF);
                mCrudeTv.setTextColor(Color.RED);
            }
        });
        mThickCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mFabMenu.close(false);
            }
        });

        ApiUtils.setDialogWindow(dialog);
        dialog.show();
    }

    private void setTrilColor() {
        LinearLayout layout = new LinearLayout(NewFlyActivity.this);
        LinearLayout layout2 = new LinearLayout(NewFlyActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundColor(Color.parseColor("#ffffff"));

        Button mConfirmBtn = new Button(NewFlyActivity.this);
        mConfirmBtn.setText("确认");
        Button mConcelBtn = new Button(NewFlyActivity.this);
        mConcelBtn.setText("取消");
        Button mBackBtn = new Button(NewFlyActivity.this);
        mBackBtn.setText("复原");

        final TextView colorText = new TextView(NewFlyActivity.this);

        final ColorPickerView colorPick = new ColorPickerView(NewFlyActivity.this, Color.parseColor(mTrailColor), 2, colorText);
        colorPick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                colorText.setTextColor(Color.parseColor(colorText.getText().toString()));
                return false;
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = 20;
        lp2.gravity = Gravity.CENTER_HORIZONTAL;

        layout2.addView(mConcelBtn, lp2);
        layout2.addView(mBackBtn, lp2);
        layout2.addView(mConfirmBtn, lp2);

        lp2.topMargin = 20;

        layout.addView(colorPick, lp);
        layout.addView(colorText, lp2);
        layout.addView(layout2, lp2);

        final Dialog mDialog = new Dialog(NewFlyActivity.this, R.style.DialogTheme);
        mDialog.setContentView(layout, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();

        colorText.setTextColor(Color.parseColor(mTrailColor));

        mConcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mFabMenu.close(false);
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPick.setCenterColor(Color.parseColor(mTrailColor));
                colorText.setTextColor(Color.parseColor(mTrailColor));
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFlying = true;
                mAMap.clear();

                mTrailColor = colorText.getText().toString();
                if (setTriBean != null) {
                    setTriBean.setTrilColor(mTrailColor);
                    daoSession.getSetTriBeanDao().update(setTriBean);
                }

                mLastTime = "";

                if (isFly == 1) {
//                    traPresenter.getDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), getRingObjId());
                }

                mFabMenu.close(false);
                mDialog.dismiss();
            }
        });
    }

    private void setTrilPic() {
        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.obrit_pic_dialog, null);
        mDialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        MyHorizontalScrollView mHorizontalScrollView = (MyHorizontalScrollView) view.findViewById(R.id.id_horizontalScrollView);
        HorizontalScrollViewAdapter mAdapter = new HorizontalScrollViewAdapter(this, mDatas2, mDatasStr);

        TextView mTextViewClose = (TextView) view.findViewById(R.id.id_horizontalScrollView_close);
        mTextViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(false);
                mDialog.dismiss();
            }
        });

        mHorizontalScrollView.setOnItemClickListener(new MyHorizontalScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                view.setBackgroundColor(Color.parseColor("#AA024DA4"));

                mTrailPic = mDatas2.get(position);
                mTriPicSeleted = position;
                System.out.println("=======111:" + mTriPicSeleted + "=======");

                isFlying = true;

                if (setTriBean != null) {
                    setTriBean.setTrilPic(mTrailPic);
                    daoSession.getSetTriBeanDao().update(setTriBean);
                }

                mLastTime = "";

                if (isFly == 1) {
//                    traPresenter.getDatasFromDao(getUserObjId(), pigeonBean.getOBJ_ID(), getRingObjId());
                }

//                mFabMenu.close(false);
//                mDialog.dismiss();
            }
        });
        //设置适配器
        mHorizontalScrollView.initDatas(mAdapter);
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
        mHorizontalScrollView.setItemSelected(mTriPicSeleted);
    }

    @Override
    public void onBackPressed() {

        Log.e(TAG, isFlying + "---isFlying--pigeon");
        if (!isFlying) {
            mRxBus.post("isLoad", false);
        }else {
            mRxBus.post("isLoad",true);
        }

        mHandler.removeMessages(1);
        super.onBackPressed();
    }

    @Override
    public void toDo() {

        switch (flyTag) {
            case FLY_START:
                toSetStartFly();
                break;
            case FLY_END:
                toSetEndFly();
                break;
        }
    }

    @Override
    public String getMethod() {
        return null;
    }
}
