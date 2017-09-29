package com.haoxi.dove.modules.traject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.TraAdpter;
import com.haoxi.dove.adapter.TraAdpter2;
import com.haoxi.dove.base.BaseFragment;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.bean.SetTriBean;
import com.haoxi.dove.callback.MyRvItemClickListener;
import com.haoxi.dove.inject.DaggerOurTrailFragComponent;
import com.haoxi.dove.inject.OurTrailFragMoudle;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.presenters.SetTriPresenter2;
import com.haoxi.dove.modules.mvp.presenters.TraFragPresenter;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.modules.mvp.views.ITraFragView;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.PointBean;
import com.haoxi.dove.newin.bean.StartFlyBean;
import com.haoxi.dove.newin.ourcircle.presenter.StartFlyPresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.StringUtils;
import com.haoxi.dove.utils.TraUtils;
import com.haoxi.dove.widget.BottomPopView;
import com.haoxi.dove.widget.CustomDrawerLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.functions.Action1;

public class OurTrailFragment extends BaseFragment implements ITraFragView, LocationSource, IGetPigeonView, MyRvItemClickListener,EasyPermissions.PermissionCallbacks {

    private static final String TAG = "OurTrailFragment";
    private boolean hasFlybeans = false;
    private int methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
    @BindView(R.id.tl_custom)
    Toolbar mToolbar;
    @BindView(R.id.custom_toolbar_keep)
    TextView mRefrashTv;
    @BindView(R.id.custom_toolbar_left_tv)
    TextView mLeftTv;
    @BindView(R.id.new_tral_map)
    TextureMapView mapView;
    @BindView(R.id.new_icon)
    ImageView mMapTypeIv;
    @BindView(R.id.drawerlaout)
    CustomDrawerLayout mDrawerLayout;
    @BindView(R.id.fg_trajectory_start)
    TextView mBtnStart;
    @BindView(R.id.fg_trajectory_rv)
    RecyclerView mTraRv;
    @BindView(R.id.mainc_view)
    LinearLayout mMaicLv;
    @BindView(R.id.custoom_dl_show)
    TextView mShowTv;

    @Inject
    TraFragPresenter traFragPresenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    StartFlyPresenter startFlyPresenter;
    @Inject
    MyPigeonPresenter myPigeonPresenter;
    @Inject
    SetTriPresenter2 setTriPresenter;
    @Inject
    DaoSession daoSession;
    @Inject
    RxBus mRxBus;

    TraAdpter mAdapter;
    TraAdpter2 mAdapter2;
    //是否进行网络请求
    private Observable<Boolean> loadObservable;
    private Observable<Integer> clickObservable;
    private Observable<String> mShowObservable;
    private Observable<Boolean> mHomeObservable;
    private Observable<Boolean> mScreenObservable;

    private AMap mAMap;
    private UiSettings mUiset;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationClientOption = null;
    private OnLocationChangedListener mChangedListener;
    private Map<String,PointBean> preMapPoint = new HashMap<>();
    private Dialog dialog;

    //默认的轨迹样式
    private int mTrailWidth = 10;
    private static final int REQUEST_CODE_TRAIL = 0x0001;

    private List<Integer> mDataPics = Arrays.asList(
            R.mipmap.icon_img_3, R.mipmap.icon_img_2
            , R.mipmap.icon_img_4, R.mipmap.icon_img_5
            , R.mipmap.icon_img_6, R.mipmap.icon_img_7
            , R.mipmap.icon_img_8, R.mipmap.icon_img_9
            , R.mipmap.icon_img_2, R.mipmap.icon_img_3
            , R.mipmap.icon_img_4, R.mipmap.icon_img_5
            , R.mipmap.icon_img_6, R.mipmap.icon_img_7
            , R.mipmap.icon_img_8
    );

    private List<String> mTraicColorList = new ArrayList();

    //已经匹配的信鸽集合
    private List<InnerDoveData> myPigeonBeanList;

    //已匹配没有飞行
    private List<InnerDoveData> notFlyList;
    private boolean isLoad = true;
    private boolean isFlying = false;
    private Marker addMarker;
    private CameraUpdate cu;
    private MarkerOptions option;
    private RecyclerView mRecyclerView;
    private PopupWindow mPopupWindow;
    private WindowManager.LayoutParams bpParams;
    private BottomPopView popView;
    private LinearLayout ll;
    private String endflyObjID;

    private Map<String, SetTriBean> flySetTriMap = new HashMap<>();
    private Map<String, InnerRouteBean> flyBeanMap = new HashMap<>();
    private Map<String, InnerDoveData> flyingPbMap = new HashMap<>();
    private Map<String, Boolean> firstGetTri = new HashMap<>();
    private String mLastTime = "";
    private int mLastLocationTime = 1;
    // 隐藏开始时间
    private long pauseTime = 0;

    private static final String TYPE_START_FLY = "START_FLY";
    private static final String TYPE_ALL_START_FLY = "ALL_START_FLY";
    private static final String TYPE_ALL_END_FLY = "ALL_END_FLY";
    private static final String TYPE_END_FLY = "END_FLY";
    private static final String TYPE_SINGLE_END_FLY = "SINGLE_END_FLY";

    private String flyType = TYPE_START_FLY;
    private String flyRecordId = "";
    private String doveid = "";
    private LatLng firstLatLng = null;

    private ArrayList<String> isFlyingPigeonObjs = new ArrayList<>();
    private ArrayList<String> isFlyingRingObjs = new ArrayList<>();
    private ArrayList<String> isNotFlyPigeonObjs = new ArrayList<>();
    private ArrayList<String> isNotFlyRingObjs = new ArrayList<>();
    private Map<String, String> mTriColorMap = new HashMap<>();
    private Map<String, Integer> mTriPicMap = new HashMap<>();

    protected String[] needPermissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mHandler.removeMessages(1);
                    if ("".equals(lastTimes.get(endflyObjID))) {
                        mAMap.clear(true);
                    }
                    methodType = MethodType.METHOD_TYPE_GET_CURTIME_POINTS;
                    traFragPresenter.getDataFromNets(getParaMap(getPigeonObjId(),flyRecordId));
                    sendEmptyMessageDelayed(1, 1000 * 60 * mLastLocationTime);
                    break;
                case 2:
                    mAMap.clear(true);
                    break;
            }
        }
    };

    public static OurTrailFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        OurTrailFragment fragment = new OurTrailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_trail3;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPigeonBeanList = new ArrayList<>();
        notFlyList = new ArrayList<>();
        loadObservable = mRxBus.register("isLoad", Boolean.class);
        mHomeObservable = mRxBus.register("home_back", Boolean.class);
        mScreenObservable = mRxBus.register("screen_on_off", Boolean.class);
        mShowObservable = mRxBus.register("exit", String.class);
        mShowObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String str) {
                if ("trail".equals(str)) {
                    if (popView != null && popView.isShowing()) {
                        popView.dismiss();
                        SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,true);
                        SpUtils.putString(getActivity(), SpConstant.OTHER_EXIT,"");
                    }
                }
            }
        });
        loadObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isLoad = aBoolean;
            }
        });
        mHomeObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    if (hasFlybeans) {
                        mHandler.removeMessages(1);
                    }
                }
            }
        });
        mScreenObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    if (hasFlybeans) {
                        mHandler.removeMessages(1);
                    }
                }else{
                    if (hasFlybeans && clickPos == 1){
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }
        });
        mAdapter = new TraAdpter(getContext());
        mAdapter2 = new TraAdpter2(getContext());
        String[] trailColorArr = getResources().getStringArray(R.array.TraicColor);
        Collections.addAll(mTraicColorList, trailColorArr);
    }

    @Override
    protected void initInject() {
        DaggerOurTrailFragComponent.builder()
                .appComponent(getAppComponent())
                .ourTrailFragMoudle(new OurTrailFragMoudle(getActivity(), this))
                .build()
                .inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mapView.onCreate(savedInstanceState);
        initMap();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mTraRv.setLayoutManager(linearLayoutManager);
        mTraRv.setAdapter(mAdapter2);
        requestCodeQRCodePermissions();
    }

    @AfterPermissionGranted(REQUEST_CODE_TRAIL)
    private void requestCodeQRCodePermissions(){
        if (!EasyPermissions.hasPermissions(getContext(),needPermissions)) {
            EasyPermissions.requestPermissions(this,"定位相关的权限",REQUEST_CODE_TRAIL,needPermissions);
        }else {
            mLocationClient.startLocation();
        }
    }
    private void initView() {
        initDrawer();
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
            mUiset = mAMap.getUiSettings();//设置ui控件
        }

        mUiset.setMyLocationButtonEnabled(true);
        mUiset.setZoomControlsEnabled(false);
        mUiset.setScaleControlsEnabled(true);//设置地图默认显示比例
        mUiset.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
        mAMap.moveCamera(cu);

        //自定义定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色

        mAMap.setLocationSource(this);

        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        mAMap.clear();
        initClient();
        initOption();
    }

    private void initClient() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TraUtils.showPop2(getActivity(),marker);
                return true;
            }
        };
        mAMap.setOnMarkerClickListener(listener);
    }

    //声明定位回调监听
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.e("aMapLocation",aMapLocation.getErrorCode()+"-------");
            switch (aMapLocation.getErrorCode()) {
                case 0:
                    if (mChangedListener != null) {
                        mChangedListener.onLocationChanged(aMapLocation);
                        firstLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
                        mAMap.moveCamera(cu);
                    }
                    break;
                case 4:
                    ApiUtils.showToast(getContext(), "当前网络较差,定位失败");
                    break;
                case 7:
//                    ApiUtils.showToast(getContext(), "没有权限");
                default:
                    break;
            }
        }
    };


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

    private boolean isMapNormal = false;

    private void initDrawer() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerToggle.syncState();
        //noinspection deprecation
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mMapTypeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMapNormal) {
                    mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                    isMapNormal = false;
                    mMapTypeIv.setImageResource(R.mipmap.icon_map_2);
                } else {
                    mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                    isMapNormal = true;
                    mMapTypeIv.setImageResource(R.mipmap.icon_map_1);
                }
            }
        });
    }

    private Map<String, String> lastTimes = new HashMap<>();
    private int clickPos = 1;

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        clickObservable = mRxBus.register("clickRadio", Integer.class);
        clickObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                clickPos = integer;
                if (integer == 1) {
                    mAMap.clear(true);
                    mLastTime = "";
                    setTriPresenter.getDaoWithObjId(getUserObjId());
                } else {
                    mHandler.removeMessages(1);
                }
            }
        });
        int num = SpUtils.getInt(getActivity(), SpConstant.CLICK_NUM,1);
        if (isLoad && num == 1) {
            mAMap.clear(true);
            setTriPresenter.getDaoWithObjId(getUserObjId());
            mLastTime = "";
        } else {
//            if (hasFlybeans && clickPos == 1) {
//                mAMap.clear(true);
//                preMapFlys.clear();
//                mLastTime = "";
//
//                setTriPresenter.getDaoWithObjId(getUserObjId());
//            }
        }
        isLoad = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mHandler.removeMessages(1);
        pauseTime = System.currentTimeMillis();
        mRxBus.unregister("clickRadio", clickObservable);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxBus.unregister("isLoad", loadObservable);
        mRxBus.unregister("showTrilPop", mShowObservable);
        mRxBus.unregister("home_back", mHomeObservable);
        mRxBus.unregister("screen_on_off", mScreenObservable);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mChangedListener = null;
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
        mLocationClient = null;
    }


//    @OnClick(R.id.fg_trajectory_start)
//    void startOnCli() {
//        if ("开始飞行".equals(mBtnStart.getText().toString())) {
//            toStartFly();
//        } else {
//            toEndFly();
//        }
//        mDrawerLayout.closeDrawers();
//    }

    @Override
    public void toStartFly() {
    }
    @Override
    public void toEndFly() {
    }

    @Override
    public void getFlyDatas(boolean isRefrash) {
        setTriPresenter.getDaoWithObjId(getUserObjId());
    }

    @Override
    public void getPigeonDatas() {
        methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
        myPigeonPresenter.getDataFromNets(getParaMap("",""));
    }

    @Override
    public String getPigeonObjId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < isFlyingPigeonObjs.size(); i++) {
            String ids = isFlyingPigeonObjs.get(i);
            if (i == isFlyingPigeonObjs.size() - 1){
                sb.append(ids);
            }else {
                sb.append(ids).append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public String getLastTime() {
        return mLastTime;
    }


    @Override
    public void toSetStartFly(StartFlyBean startFlyBean) {
        isFlying = true;
//        flyRecordId = startFlyBean.getData().getFly_recordid();
        mRefrashTv.setText("刷新");
        mRefrashTv.setVisibility(View.VISIBLE);
        getFlyDatas(false);
    }

    @Override
    public void toSetEndFly() {
        isFlying = false;
        mHandler.removeMessages(1);
        mRefrashTv.setVisibility(View.GONE);
        mRxBus.post("isLoadData", true);
        mAMap.clear(true);
        if (flyType.equals(TYPE_END_FLY)) {
            if (endflyObjID != null) {
                mTriColorMap.remove(endflyObjID);
                mTriPicMap.remove(endflyObjID);
                Log.e("endflyObjID", endflyObjID + "----endflyObjID");
//                preMapLatLng.remove(endflyObjID);
//                traFragPresenter.deleteFromDaoFromId(getUserObjId(), endflyObjID);
                setTriPresenter.deleteSetTriBean(flySetTriMap.get(endflyObjID));
                setTriPresenter.getDaoWithObjId(getUserObjId());
            }
            dialog.dismiss();

        } else {
            //清除所有
            mTriColorMap.clear();
            mTriPicMap.clear();
            //preMapLatLng.clear();
            flySetTriMap.clear();
//            traFragPresenter.deleteAllFromDaoById(getUserObjId());
            setTriPresenter.deleteAllSetTriBean(getUserObjId());
            getPigeonDatas();
        }
    }

    @Override
    public void onFailed(String msg) {
        ApiUtils.showToast(getContext(), msg);
        mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * mLastLocationTime);
    }

    private List<String> pigeonObjLists = new ArrayList<>();
    private Map<String, Boolean> loadPre = new HashMap<>();

    private String fisrtStr = "";

    @Override
    public void trailFromDao(List<InnerRouteBean> innerRouteBeanList, String type_from) {
        fisrtStr = getLastTime();
        if (addMarker != null) {
            addMarker.remove();
        }
        if (innerRouteBeanList.size() == 0) {
            mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * mLastLocationTime);
            return;
        } else {
            flyBeanMap.clear();
            for (int i = 0; i < innerRouteBeanList.size(); i++) {
                InnerRouteBean innerRouteBean = innerRouteBeanList.get(i);
                if (flyBeanMap.containsKey(innerRouteBean.getDoveid())) {
                    flyBeanMap.put(innerRouteBean.getDoveid(),innerRouteBean);
                    if (!pigeonObjLists.contains(innerRouteBean.getDoveid())) {
                        pigeonObjLists.add(innerRouteBean.getDoveid());
                    }
                    firstGetTri.put(innerRouteBean.getDoveid(), false);
                } else {
                    if (!pigeonObjLists.contains(innerRouteBean.getDoveid())) {
                        pigeonObjLists.add(innerRouteBean.getDoveid());
                    }
                    firstGetTri.put(innerRouteBean.getDoveid(), true);
                    flyBeanMap.put(innerRouteBean.getDoveid(), innerRouteBean);
                }
            }
            for (int i = 0; i < pigeonObjLists.size(); i++) {
                loadPre.put(pigeonObjLists.get(i), true);
            }
            for (int p = 0; p < pigeonObjLists.size(); p++) {
                Log.e("OurTrailFragment----", pigeonObjLists.size()+ "-------size -----9");
                InnerRouteBean preBean = null;
                SetTriBean setTriBean = flySetTriMap.get(pigeonObjLists.get(p));
                InnerRouteBean innerRouteBean = flyBeanMap.get(setTriBean.getOBJ_ID());
                ArrayList<InnerRouteBean> flyBeans = new ArrayList<>();
                PointBean prePointBean = preMapPoint.get(innerRouteBean.getDoveid());
                flyBeans.clear();
                if (innerRouteBean.getPoints() != null && innerRouteBean.getPoints().size() > 0) {
                        mLastTime = innerRouteBean.getPoints().get(innerRouteBean.getPoints().size() - 1).getTime();
                        PointBean firstPb = innerRouteBean.getPoints().get(0);
                        PointBean lastPb = innerRouteBean.getPoints().get(innerRouteBean.getPoints().size() - 1);
                        Log.e("battery"+"----", firstPb.getBattery()+ "-------getBattery -----dianliang");
                    if (prePointBean != null && !prePointBean.getTime().equals(firstPb.getTime())) {
                        preMapPoint.put(innerRouteBean.getDoveid(),lastPb);
                        innerRouteBean.getPoints().add(0,prePointBean);
                    }
                    TraUtils.drawTraInnerRoute(fisrtStr, setTriBean.getOBJ_ID(),setTriBean.getIsShowMark(), firstGetTri.get(setTriBean.getOBJ_ID()),
                            mAMap, innerRouteBean, setTriBean.getTrilPic(), Color.parseColor(setTriBean.getTrilColor()), setTriBean.getTrilWidth());
                    firstGetTri.put(setTriBean.getOBJ_ID(), false);
                    }
                }
            mHandler.sendEmptyMessageDelayed(1, 1000 * 60 * mLastLocationTime);
            }
    }

    @Override
    public void setTriMap(List<SetTriBean> setTriBeen) {
        flySetTriMap.clear();
        Log.e(TAG, setTriBeen.size() + "------setTriMap----1");
        if (setTriBeen.size() != 0 ) {
            mTriPicMap.clear();
            mTriColorMap.clear();
            for (int i = 0; i < setTriBeen.size(); i++) {
                SetTriBean setTriBean = setTriBeen.get(i);
                if (setTriBean.getIsFlying() == 1) {
                    if (!flySetTriMap.containsKey(setTriBean.getOBJ_ID())) {
                        flySetTriMap.put(setTriBean.getOBJ_ID(), setTriBean);
                        mTriPicMap.put(setTriBean.getOBJ_ID(), setTriBean.getTrilPic());
                        mTriColorMap.put(setTriBean.getOBJ_ID(), setTriBean.getTrilColor());
                    }
                }
            }
        }
        //TODO  取信鸽数据,
        getPigeonDatas();
    }

    private List<String> triIds = new ArrayList<>();
    private List<SetTriBean> setTriBeanList = new ArrayList<>();

    @Override
    public void setTri(SetTriBean setTriBean) {
        if (!triIds.contains(setTriBean.getId().toString())) {
            setTriBeanList.add(setTriBean);
        }
    }

    @OnClick(R.id.custom_toolbar_keep)
    void refrashOncli() {
        methodType = MethodType.METHOD_TYPE_GET_CURTIME_POINTS;
        mLastTime = "";
        flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
        traFragPresenter.getDataFromNets(getParaMap(getPigeonObjId(),flyRecordId));
    }

    @Override
    public void toDo() {
        switch (flyType) {
            case TYPE_END_FLY:
            case TYPE_ALL_END_FLY:
                mLocationClient.startLocation();
                toSetEndFly();
                break;
            case TYPE_SINGLE_END_FLY:
                toSetEndFly();
                break;
        }
    }

    @Override
    public String getMethod() {
        String method = "";
        switch (methodType){
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                method = MethodConstant.DOVE_SEARCH;
                break;
            case MethodType.METHOD_TYPE_FLY_START:
                method = MethodConstant.FLY_START;
                break;
            case MethodType.METHOD_TYPE_FLY_STOP:
                method = MethodConstant.FLY_STOP;
                break;
            case MethodType.METHOD_TYPE_GET_CURTIME_POINTS:
                method = MethodConstant.GET_CURTIME_POINTS;
                break;
        }
        return method;
    }

    @OnClick(R.id.new_icon_wei)
    void addOnclick() {
        Log.e(TAG, "点击添加信鸽、鸽环");
        showBottomPop2();
    }
    private boolean popShow = false;
    private void showBottomPop2() {
        if (mRecyclerView != null && myPigeonBeanList.size() != 0) {
            int height = 0;
            if (myPigeonBeanList.size() >= 3) {
                height = (int) getResources().getDimension(R.dimen.DIP_60_DP) * 3;
            } else {
                height = (int) getResources().getDimension(R.dimen.DIP_65_DP) * myPigeonBeanList.size() + 2 * (int)getResources().getDimension(R.dimen.x18);
            }
            if (popView != null && popView.isShowing()) {
                SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,true);
                SpUtils.putString(getActivity(),SpConstant.OTHER_EXIT,"");
                popView.dismiss();
            } else {
                SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,false);
                SpUtils.putString(getActivity(),SpConstant.OTHER_EXIT,"trail");

                popShow = true;
                popView = new BottomPopView(ll, (int) getResources().getDimension(R.dimen.DIP_330_DP), height, getContext());
                popView.showAtLocation(mMaicLv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                bpParams = getActivity().getWindow().getAttributes();
                bpParams.verticalMargin = 30;
                //当弹出Popupwindow时，背景变半透明
                bpParams.alpha = 0.7f;
                getActivity().getWindow().setAttributes(bpParams);
                //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
            }

            popView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    bpParams = getActivity().getWindow().getAttributes();
                    bpParams.alpha = 1f;
                    getActivity().getWindow().setAttributes(bpParams);
                }
            });
        }
    }

    @Override
    public void setPigeonData(List<InnerDoveData> list) {
        if (list == null) {
            return;
        }
        isNotFlyRingObjs.clear();
        isNotFlyPigeonObjs.clear();
        isFlyingPigeonObjs.clear();
        isFlyingRingObjs.clear();
        myPigeonBeanList.clear();
        notFlyList.clear();
        for (int i = 0; i < list.size(); i++) {
            InnerDoveData innerDoveData = list.get(i);
            if (innerDoveData.getRingid() != null && !"".equals(innerDoveData.getRingid())&& !"-1".equals(innerDoveData.getRingid())) {
                myPigeonBeanList.add(list.get(i));
            }
        }
        mAdapter.addDatas(myPigeonBeanList);
        initRecyclerView();
        for (int i = 0; i < myPigeonBeanList.size(); i++) {
            InnerDoveData doveData = myPigeonBeanList.get(i);
            if (!"".equals(doveData.getFly_recordid())){
                SpUtils.putString(getActivity(),"fly_recordid",doveData.getFly_recordid());
            }
            if (flySetTriMap.containsKey(doveData.getDoveid())) {
                flyingPbMap.put(doveData.getDoveid(), myPigeonBeanList.get(i));
                if (!isFlyingPigeonObjs.contains(doveData.getDoveid())) {
                    isFlyingPigeonObjs.add(doveData.getDoveid());
                }
                if (!isFlyingRingObjs.contains(doveData.getRingid())) {
                    isFlyingRingObjs.add(doveData.getRingid());
                }
            } else {
                if (!isNotFlyPigeonObjs.contains(doveData.getDoveid())) {
                    isNotFlyPigeonObjs.add(doveData.getDoveid());
                    notFlyList.add(myPigeonBeanList.get(i));
                }
                if (!isNotFlyRingObjs.contains(doveData.getRingid())) {
                    isNotFlyRingObjs.add(doveData.getRingid());
                }
            }
        }

        if (flySetTriMap.size() == 0) {
            SpUtils.putString(getActivity(),"fly_recordid","");
            flyRecordId = "";
            if (myPigeonBeanList.size() != 0) {
                mLeftTv.setText("全部飞行");
                mLeftTv.setVisibility(View.GONE);
                mShowTv.setVisibility(View.GONE);
                mAdapter2.addDatas(myPigeonBeanList);
                mBtnStart.setEnabled(true);
                mBtnStart.setTextColor(getResources().getColor(R.color.colorBlue));
            } else {
                mLeftTv.setVisibility(View.GONE);
                mShowTv.setVisibility(View.VISIBLE);
                mBtnStart.setEnabled(false);
                mBtnStart.setTextColor(getResources().getColor(R.color.darkgray));
            }
            mBtnStart.setText("开始飞行");
            mLocationClient.startLocation();
            hasFlybeans = false;
            //TODO  方式不太好,明天试试 定位完成后handler去clear
            mHandler.sendEmptyMessageDelayed(2, 100);
            mRefrashTv.setVisibility(View.GONE);
        } else {
            mRefrashTv.setText("刷新");
            mRefrashTv.setVisibility(View.VISIBLE);
            mShowTv.setVisibility(View.GONE);
            //TODO 514  思路：点击全部飞行；点击结束飞行
            if (flySetTriMap.size() == myPigeonBeanList.size()) {
                mLeftTv.setText("结束飞行");
                mLeftTv.setVisibility(View.GONE);
                mBtnStart.setText("结束飞行");
                mAdapter2.addDatas(myPigeonBeanList);
            } else {
                mLeftTv.setText("全部飞行");
                mLeftTv.setVisibility(View.GONE);
                mBtnStart.setText("开始飞行");
                mAdapter2.addDatas(notFlyList);
            }
            mLocationClient.stopLocation();
            hasFlybeans = true;
//            traFragPresenter.getDatasFromDao2(getUserObjId());
            methodType = MethodType.METHOD_TYPE_GET_CURTIME_POINTS;
            flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
            traFragPresenter.getDataFromNets(getParaMap(getPigeonObjId(),flyRecordId));
        }
    }

    private void initRecyclerView() {
        View view = View.inflate(getContext(), R.layout.trail_epl, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trail_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

//    @Override
//    public void setRefrash(boolean isRefrash) {
//
//    }

    private void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = (int) getResources().getDimension(R.dimen.DIP_340_DP);
        window.setAttributes(params);
    }

    @Override
    public void onItemClick(View view, final int position, boolean isShowBox, final InnerDoveData myPigeonBean1) {
        if (popView != null && popView.isShowing()) {
            SpUtils.putBoolean(getActivity(), SpConstant.MAIN_EXIT,true);
            popView.dismiss();
        }
        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setCancelable(false);
        View viewDialog = getActivity().getLayoutInflater().inflate(R.layout.start_end_dialog,null);
        dialog.setContentView(viewDialog,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final TextView v1 = (TextView) viewDialog.findViewById(R.id.thickness_dialog_new4);
        final TextView v2 = (TextView) viewDialog.findViewById(R.id.thickness_dialog_medium_new4);
        final TextView v3 = (TextView) viewDialog.findViewById(R.id.thickness_dialog_crude_new4);
        final TextView v4 = (TextView) viewDialog.findViewById(R.id.thickness_dialog_crude);
        final TextView title = (TextView) viewDialog.findViewById(R.id.status_dialog_title);
        final View line1 = viewDialog.findViewById(R.id.line1);
        final View line2 = viewDialog.findViewById(R.id.line2);
        v2.setVisibility(View.GONE);

        TextView mThickCancel = (TextView)viewDialog.findViewById(R.id.thickness_dialog_cancel);

        title.setText("信鸽:"+myPigeonBean1.getFoot_ring());

        setDialogWindow(dialog);
        dialog.show();

        mThickCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (flySetTriMap.containsKey(myPigeonBean1.getDoveid())) {
            v1.setText("鸽子位置");
            v2.setText("轨迹详情");
            v3.setText("结束飞行");
            line2.setVisibility(View.VISIBLE);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (flySetTriMap.get(myPigeonBean1.getDoveid()).getIsShowMark() == 0) {
                        v4.setText("隐藏轨迹");
                    } else {
                        v4.setText("显示轨迹");
                    }
                }
            });

        } else {
            v1.setVisibility(View.GONE);
            v2.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            v3.setText("开始飞行");
            v4.setText("隐藏轨迹");
        }
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                dialog.dismiss();
            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("开始飞行".equals(v3.getText().toString().trim())) {
                    ApiUtils.showToast(getContext(), "该信鸽还没有开始飞行");
                } else {
                    switch (v4.getText().toString()) {
                        case "显示轨迹":
                            //TODO 这里进行设置轨迹的隐藏
                            mAMap.clear(true);
                            setTriPresenter.getDaoWithObjId(getUserObjId());
                            flySetTriMap.get(myPigeonBean1.getDoveid()).setIsShowMark(0);
                            setTriPresenter.updateWityPigeonId(flySetTriMap.get(myPigeonBean1.getDoveid()));
                            v4.setText("隐藏轨迹");

                            break;
                        case "隐藏轨迹":
                            mAMap.clear(true);
                            setTriPresenter.getDaoWithObjId(getUserObjId());
                            //显示轨迹
                            flySetTriMap.get(myPigeonBean1.getDoveid()).setIsShowMark(1);
                            setTriPresenter.updateWityPigeonId(flySetTriMap.get(myPigeonBean1.getDoveid()));
                            v4.setText("显示轨迹");
                            break;
                    }
                }
                dialog.dismiss();
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });

        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("结束飞行".equals(v3.getText().toString())) {
                    flyType = TYPE_END_FLY;
                    endflyObjID = myPigeonBean1.getDoveid();
                    //TODO 结束飞行
                    methodType = MethodType.METHOD_TYPE_FLY_STOP;
//                    ourCodePresenter.stopFly(getParaMap(endflyObjID,"fly5452191137792"));
                    flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
                    ourCodePresenter.stopFly(getParaMap(endflyObjID,flyRecordId));
                } else {
                    if (mTriPicMap.size() > 15) {
                        ApiUtils.showToast(getContext(), "最多只能十五只一起飞行");
                    } else {
                        flyType = TYPE_START_FLY;
                        int picSize = mTriPicMap.size();
                        int colorSize = mTriColorMap.size();
                        SetTriBean setTriBean = new SetTriBean();
                        setTriBean.setOBJ_ID(myPigeonBean1.getDoveid());
                        setTriBean.setIsFlying(1);
                        setTriBean.setTrilPic(mDataPics.get(picSize));
                        setTriBean.setTrilWidth(mTrailWidth);
                        setTriBean.setTrilColor(mTraicColorList.get(colorSize));
                        setTriBean.setUSER_OBJ_ID(getUserObjId());
                        mTriPicMap.put(myPigeonBean1.getDoveid(), mDataPics.get(picSize));
                        mTriColorMap.put(myPigeonBean1.getDoveid(), mTraicColorList.get(colorSize));
                        daoSession.getSetTriBeanDao().insertOrReplace(setTriBean);
                        methodType = MethodType.METHOD_TYPE_FLY_START;
                        flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
                        startFlyPresenter.getDataFromNets(getParaMap(myPigeonBean1.getDoveid(),flyRecordId));
                    }
                }

                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                dialog.dismiss();
            }
        });

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flyBeanMap.get(myPigeonBean1.getDoveid()) != null) {
                    InnerRouteBean innerRouteBean = flyBeanMap.get(myPigeonBean1.getDoveid());
                    if (innerRouteBean != null) {
                        if (innerRouteBean.getCurloc() == null){
                            dialog.dismiss();
                            ApiUtils.showToast(getContext(), "没有鸽子的最新位置");
                            return;
                        }
                        //鸽子当前位置
                        LatLng lastLatLng = ApiUtils.transform(innerRouteBean.getCurloc().getLat(), innerRouteBean.getCurloc().getLng());
                        //鸽子当前位置,,测试数据  经纬度反了
//                        LatLng lastLatLng = ApiUtils.transform(innerRouteBean.getCurloc().getLng(), innerRouteBean.getCurloc().getLat());
                        if (addMarker != null) {
                            addMarker.remove();
                        }
                        cu = CameraUpdateFactory.changeLatLng(lastLatLng);
                        //更新地图显示区域
                        mAMap.moveCamera(cu);
                        //创建markeroptions对象
                        option = new MarkerOptions();
                        //设置markeroptions
                        option.position(lastLatLng).title("记录时间:" + innerRouteBean.getCurloc().getTime()
                                + "\n" + "速度:" +innerRouteBean.getCurloc().getSpeed()
                                + "\n" + "方向:" + innerRouteBean.getCurloc().getDir()
                                + "\n" + "经度:" + innerRouteBean.getCurloc().getLng()
                                + "\n" + "纬度:" + innerRouteBean.getCurloc().getLat())
                                .icon(BitmapDescriptorFactory.fromResource(mTriPicMap.get(myPigeonBean1.getDoveid())));

                        addMarker = mAMap.addMarker(option);
                        addMarker.setFlat(true);
                        addMarker.showInfoWindow();
                    } else {
                        ApiUtils.showToast(getContext(), "没有鸽子的最新位置");
                    }
                } else {
                    ApiUtils.showToast(getContext(), "没有鸽子的最新位置");
                }
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.fg_trajectory_start)
    void allStartOrEndFly() {
        if ("结束飞行".equals(mLeftTv.getText().toString())) {
            flyType = TYPE_ALL_END_FLY;
            methodType = MethodType.METHOD_TYPE_FLY_STOP;
            flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
            ourCodePresenter.stopFly(getParaMap(getPigeonObjId(),flyRecordId));
        } else {
            if (mTriPicMap.size() > 15) {
                ApiUtils.showToast(getContext(), "最多只能十五只一起飞行");
            } else {
                flyType = TYPE_ALL_START_FLY;
                methodType = MethodType.METHOD_TYPE_FLY_START;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < notFlyList.size(); i++) {
                    InnerDoveData mpb = notFlyList.get(i);
                    int picSize = mTriPicMap.size();
                    int colorSize = mTriColorMap.size();
                    SetTriBean setTriBean = new SetTriBean();
                    setTriBean.setOBJ_ID(mpb.getDoveid());
                    setTriBean.setIsFlying(1);
                    setTriBean.setTrilPic(mDataPics.get(picSize));
                    setTriBean.setTrilWidth(mTrailWidth);
                    setTriBean.setTrilColor(mTraicColorList.get(colorSize));
                    setTriBean.setUSER_OBJ_ID(getUserObjId());
                    mTriPicMap.put(mpb.getDoveid(), mDataPics.get(picSize));
                    mTriColorMap.put(mpb.getDoveid(), mTraicColorList.get(colorSize));
                    daoSession.getSetTriBeanDao().insertOrReplace(setTriBean);
                    if (i == notFlyList.size() -1){
                        sb.append(mpb.getDoveid());
                    }else {
                        sb.append(mpb.getDoveid()).append(",");
                    }
                }
//                startFlyPresenter.getDataFromNets(getParaMap(sb.toString(),flyRecordId));
                flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
                startFlyPresenter.getDataFromNets(getParaMap(sb.toString(),flyRecordId));
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mLocationClient.startLocation();
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
    public Map<String,String> getParaMap(String doveids,String flyRecordId){
        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",getUserObjId());
        map.put("token",getToken());
        switch (methodType){
            case MethodType.METHOD_TYPE_FLY_START:
            case MethodType.METHOD_TYPE_FLY_STOP:
                map.put("fly_recordid",flyRecordId);
                map.put("dove_ids",doveids);
                break;
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                map.put("playerid",getUserObjId());
                break;
            case MethodType.METHOD_TYPE_GET_CURTIME_POINTS:
                map.put("dove_ids",doveids);
                map.put("cur_time",mLastTime);
                map.put("fly_recordid",flyRecordId);
                break;
        }

        Log.e("fafsewdfvd",map.toString());
        return map;
    }
}