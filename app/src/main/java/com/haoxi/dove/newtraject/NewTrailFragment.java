package com.haoxi.dove.newtraject;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.TraAdpter;
import com.haoxi.dove.adapter.TraAdpter2;
import com.haoxi.dove.base.BaseFragment;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.callback.MyRvItemClickListener;
import com.haoxi.dove.inject.DaggerOurTrailFragComponent;
import com.haoxi.dove.inject.OurTrailFragMoudle;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.presenters.SetTriPresenter2;
import com.haoxi.dove.modules.mvp.presenters.TraFragPresenter;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.PointBean;
import com.haoxi.dove.newin.bean.StartFlyBean;
import com.haoxi.dove.newin.ourcircle.presenter.StartFlyPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.TraUtils;
import com.haoxi.dove.widget.CustomDrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.functions.Action1;


public class NewTrailFragment extends BaseFragment implements LocationSource, IGetPigeonView,INewTrailView, MyRvItemClickListener,EasyPermissions.PermissionCallbacks{

    private int methodType = MethodType.METHOD_TYPE_DOVE_SEARCH;
    private String flyType = ConstantUtils.TYPE_START_FLY;
    private static final int REQUEST_CODE_TRAIL = 0x0001;
    @BindView(R.id.tl_custom)
    Toolbar mToolbar;
    @BindView(R.id.custom_toolbar_keep)
    TextView mRefrashTv;
    @BindView(R.id.custom_toolbar_left_tv)
    TextView mLeftTv ;
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
    OurCodePresenter ourCodePresenter;
//    @Inject
//    StartFlyPresenter startFlyPresenter;
    @Inject
    MyPigeonPresenter myPigeonPresenter;
    @Inject
    DaoSession daoSession;
    @Inject
    RxBus mRxBus;

    /**
     *  地图相关属性
     */
    private AMap mAMap;
    private UiSettings mUiset;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationClientOption = null;
    private OnLocationChangedListener mChangedListener;

    TraAdpter mAdapter;
    TraAdpter2 mAdapter2;

//    private Map<String,PointBean> preMapPoint = new HashMap<>();
//    private Dialog dialog;

    /**
     *  已经匹配的信鸽
     * 1、正在飞行的 doveid  ringid
     * 2、未飞行的   doveid  ringid
     */
    private ArrayList<String> isFlyingPigeonObjs = new ArrayList<>();
    private ArrayList<String> isFlyingRingObjs = new ArrayList<>();
    private ArrayList<String> isNotFlyPigeonObjs = new ArrayList<>();
    private ArrayList<String> isNotFlyRingObjs = new ArrayList<>();

    //飞行记录 id
    private String flyRecordId = "";

    protected String[] needPermissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };

    public static NewTrailFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        NewTrailFragment fragment = new NewTrailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_trail3;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TraAdpter(getContext());
        mAdapter2 = new TraAdpter2(getContext());
    }

    @Override
    protected void initInject() {
        DaggerNewTrailComponent.builder()
                .appComponent(getAppComponent())
                .newTrailMoudle(new NewTrailMoudle(getActivity(),this))
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //int num = SpUtils.getInt(getActivity(), SpConstant.CLICK_NUM,1);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //mRxBus.unregister("clickRadio", clickObservable);
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
                        //firstLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
                        mAMap.moveCamera(cu);
                    }
                    break;
                case 4:
                    ApiUtils.showToast(getContext(), "当前网络较差,定位失败");
                    break;
                case 7:
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mLocationClient.startLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

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

    public Map<String,String> getParaMap(String doveids,String flyRecordId){
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());
        switch (methodType){
            case MethodType.METHOD_TYPE_FLY_START:
            case MethodType.METHOD_TYPE_FLY_STOP:
                map.put(MethodParams.PARAMS_FLY_RECORDID,flyRecordId);
                map.put(MethodParams.PARAMS_DOVE_IDS,doveids);
                break;
            case MethodType.METHOD_TYPE_DOVE_SEARCH:
                map.put(MethodParams.PARAMS_PLAYER_ID,getUserObjId());
                break;
            case MethodType.METHOD_TYPE_GET_CURTIME_POINTS:
                map.put(MethodParams.PARAMS_DOVE_IDS,doveids);
                //map.put("cur_time",mLastTime);
                map.put(MethodParams.PARAMS_FLY_RECORDID,flyRecordId);
                break;
        }
        return map;
    }

    @Override
    public void onItemClick(View view, int position, boolean isShowBox, InnerDoveData innerDoveData) {

    }

    @Override
    public void setPigeonData(List<InnerDoveData> pigeonData) {

    }

    @Override
    public void toSetStartFly(StartFlyBean startFlyBean) {

    }

    @Override
    public void toSetEndFly() {

    }

    @Override
    public void trailFromDao(List<InnerRouteBean> innerRouteBeanList, String type_from) {

    }

    @Override
    public void getFlyDatas(boolean isRefrash) {

    }

    @Override
    public void getPigeonDatas() {

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

    @OnClick(R.id.fg_trajectory_start)
    void allStartOrEndFly() {
        if ("结束飞行".equals(mLeftTv.getText().toString())) {
            flyType = ConstantUtils.TYPE_ALL_END_FLY;
            methodType = MethodType.METHOD_TYPE_FLY_STOP;
            flyRecordId = SpUtils.getString(getActivity(),"fly_recordid");
            ourCodePresenter.stopFly(getParaMap(getPigeonObjId(),flyRecordId));
        }
    }
}
