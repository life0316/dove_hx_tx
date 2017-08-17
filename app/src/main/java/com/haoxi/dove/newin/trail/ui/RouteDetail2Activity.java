package com.haoxi.dove.newin.trail.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.MyDoveAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.ToSetHolderListener;
import com.haoxi.dove.holder.MyRouteHolder;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerRouteDetialComponent;
import com.haoxi.dove.inject.RouteDetailMoudle;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.OurRouteBean;
import com.haoxi.dove.newin.bean.PointBean;
import com.haoxi.dove.newin.trail.presenter.RouteTitlePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.TraUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lifei on 2017/6/27.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_history)
public class RouteDetail2Activity extends BaseActivity implements IGetOurRouteView, LocationSource,EasyPermissions.PermissionCallbacks, ToSetHolderListener<InnerRouteBean>, MyItemClickListener {

    private static final int REQUEST_CODE_HISTORY = 0x0000;

    @BindView(R.id.activity_pegionfly_mapview)
    MapView mapView;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_newfly_ring)
    TextView mIdTv;

    @BindView(R.id.new_icon)
    ImageView mMapTypeIv;

    @Inject
    RouteTitlePresenter titlePresenter;

    @Inject
    RxBus mRxBus;

    @Inject
    Context mContext;

    @Inject
    MyDoveAdapter mAdapter;

    private boolean isMapNormal = false;


    private AMap mAMap;
    private UiSettings mUiset;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationClientOption = null;
    private OnLocationChangedListener mChangedListener;
    private AMapLocation mAMapLocation;


    private int trailWidth = 10;
    private String trailColor = "#00ff00";
    private int trailPic = R.mipmap.icon_img_3;


    private Map<String,String> mTrailColorMap = new HashMap<>();
    private Map<String,Integer> mTrailPicMap = new HashMap<>();



    private int methodType = MethodType.METHOD_TYPE_FLY_BY_RECORDID;
    private List<Integer> mDataPics = new ArrayList<Integer>(Arrays.asList(
            R.mipmap.icon_img_3, R.mipmap.icon_img_2
            , R.mipmap.icon_img_4, R.mipmap.icon_img_5
            , R.mipmap.icon_img_6, R.mipmap.icon_img_7
            , R.mipmap.icon_img_8, R.mipmap.icon_img_9
            , R.mipmap.icon_img_2, R.mipmap.icon_img_3
            , R.mipmap.icon_img_4, R.mipmap.icon_img_5
            , R.mipmap.icon_img_6, R.mipmap.icon_img_7
            , R.mipmap.icon_img_8
    ));

    private List<String> mTraicColorList = new ArrayList<String>();


    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };
    private String flyRecordId;

    @Override
    protected void initInject() {

        DaggerRouteDetialComponent.builder().appComponent(getAppComponent())
                .routeDetailMoudle(new RouteDetailMoudle(this,this))
                .build().inject(this);
    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOncli(View view) {
        this.finish();
    }


    @OnClick(R.id.new_icon_wei)
    void addOnclick(View v) {
//        Log.e(TAG, "点击添加信鸽、鸽环");
//        showBottomPop2(v);
        showWindow(v);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);

        initMap();

        Intent intent = getIntent();
        if (intent != null) {

            flyRecordId = intent.getStringExtra("recordid");

//            pointBeanArrayList = intent.getParcelableArrayListExtra("innerRouteBean");

            if (flyRecordId != null) {
                mIdTv.setText("飞行记录id:"+ flyRecordId);
            }
        }

        String[] trailColorArr = getResources().getStringArray(R.array.TraicColor);
        for (String color : trailColorArr) {
            mTraicColorList.add(color);
        }
    }

    @Override
    protected void init() {

        mTitleTv.setText("记录详情");
        mBackIv.setVisibility(View.VISIBLE);

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


    private void initMap() {

        if (mAMap == null) {
            mAMap = mapView.getMap();
            mUiset = mAMap.getUiSettings();//设置ui控件
        }
        mUiset.setZoomControlsEnabled(false);
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


        AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                TraUtils.showPop(RouteDetail2Activity.this,marker);

                return true;
            }
        };

        mAMap.setOnMarkerClickListener(listener);
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
        //mLocationClient.startLocation();


    }

    private void initClient() {

        //初始化定位
        mLocationClient = new AMapLocationClient((getApplicationContext()));
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

    }

    //声明定位回调监听
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                RouteDetail2Activity.this.mAMapLocation = aMapLocation;

                if (mChangedListener != null) {
                    mChangedListener.onLocationChanged(aMapLocation);

                    CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
                    mAMap.moveCamera(cu);

                }
            } else if (aMapLocation.getErrorCode() == 12) {
                ApiUtils.showToast(RouteDetail2Activity.this, "缺少定位权限,定位失败");
            } else if (aMapLocation.getErrorCode() == 4) {
                ApiUtils.showToast(RouteDetail2Activity.this, "当前网络较差,请求服务器异常,定位失败");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        requestCodeQRCodePermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }

    @AfterPermissionGranted(REQUEST_CODE_HISTORY)
    private void requestCodeQRCodePermissions(){

        if (!EasyPermissions.hasPermissions(this,needPermissions)) {
            EasyPermissions.requestPermissions(this,"定位的权限",REQUEST_CODE_HISTORY,needPermissions);
        }else {

            getDatas();
        }
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

    @Override
    public void toDo() {

    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());

        map.put("userid",getUserObjId());
        map.put("token",getToken());
        map.put("fly_recordid",flyRecordId);


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
            case MethodType.METHOD_TYPE_FLY_BY_RECORDID:
                method = MethodConstant.SEARCH_BY_FLY_RECORDID;
                break;
        }

        return method;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

//        if (pointBeanArrayList != null && pointBeanArrayList.size() != 0) {
//            toDrawTril(pointBeanArrayList);
//        }else {
//            mLocationClient.startLocation();
//        }

        getDatas();
    }

    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(this)) {
        } else {
            methodType = MethodType.METHOD_TYPE_FLY_BY_RECORDID;
            titlePresenter.getRouteFormNets(getParaMap());
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        mLocationClient.startLocation();

    }

    public void toDrawTril(List<PointBean> list, String doveid) {

        if (list == null && list.size() == 0) {
            mLocationClient.startLocation();
            return;
        }

        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PointBean pointBean = list.get(i);
//            latLngs.add(ApiUtils.transform(Double.parseDouble(pointBean.getLat()), Double.parseDouble(pointBean.getLat())));
            //经纬度反了
            latLngs.add(ApiUtils.transform(pointBean.getLng(),pointBean.getLat()));
        }
//        TraUtils.drawHistoryFromList(mAMap, latLngs, trailPic, Color.parseColor(trailColor), trailWidth);
        TraUtils.drawHistoryFromPointBean(mAMap, list, mTrailPicMap.get(doveid), Color.parseColor(mTrailColorMap.get(doveid)), trailWidth);
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
        if (routeData.getData() != null && routeData.getData().size() != 0) {

            doveIds.clear();

            for (int i = 0; i < routeData.getData().size(); i++) {

                InnerRouteBean innerRouteBean = routeData.getData().get(i);
                doveIds.add(innerRouteBean);

                Log.e("RouteDetail2Activity",  innerRouteBean.getDoveid() + "-----"+innerRouteBean.getFly_recordid()+"------"+innerRouteBean.getPoints().size());
                List<PointBean> pointBeans = routeData.getData().get(i).getPoints();

                mTrailColorMap.put(innerRouteBean.getDoveid(),mTraicColorList.get(i));
                mTrailPicMap.put(innerRouteBean.getDoveid(),mDataPics.get(i));

                toDrawTril(pointBeans,innerRouteBean.getDoveid());
//                for (int j = 0; j < pointBeans.size(); j++) {
//
//                    Log.e("RouteDetail2Activity", pointBeans.get(j).getTime() + "-----time");
//                    Log.e("RouteDetail2Activity", pointBeans.get(j).getLat() + "-----lat");
//                    Log.e("RouteDetail2Activity", pointBeans.get(j).getLng() + "-----lng");
//                }
            }
            mAdapter.addDatas(doveIds);
            initRecyclerVeiw();
        }
    }

    @Override
    public void setRefrash(boolean isRefrash) {

    }

    private RecyclerView mRecyclerView;
    private List<InnerRouteBean> doveIds = new ArrayList<>();

    private void initRecyclerVeiw(){
        mRecyclerView  = new RecyclerView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setBackgroundColor(getResources().getColor(R.color.darkgray));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setToSetHolderListener(this);
        mAdapter.setOnItemClickListener(this);

    }

    private void showWindow(View view){
        if (mRecyclerView != null && doveIds.size() != 0) {
            int height = 0;
            if (doveIds.size() > 6){
                height = (int) (getResources().getDimension(R.dimen.DIP_40_DP) * 6);
            }else {
                height = (int) (getResources().getDimension(R.dimen.DIP_40_DP) * doveIds.size());
            }

            PopupWindow mPopupWindow = new PopupWindow(mRecyclerView,(int)getResources().getDimension(R.dimen.DIP_150_DP),height);

            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

            mPopupWindow.showAsDropDown(view,10,10);

            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 0.5f;
            getWindow().setAttributes(params);

            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams params1 = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        }
    }
    private CameraUpdate cu;

    @Override
    public void onItemClick(View view, int position) {

        InnerRouteBean innerRouteBean = doveIds.get(position);

        if (innerRouteBean != null) {

            List<PointBean> pointBeanList = innerRouteBean.getPoints();

            if (pointBeanList != null && pointBeanList.size() > 0) {

                PointBean lastPointBean = pointBeanList.get(pointBeanList.size() - 1);

                //鸽子当前位置,,测试数据  经纬度反了
                LatLng lastLatLng = ApiUtils.transform(lastPointBean.getLng(),lastPointBean.getLat());

                if (lastLatLng != null) {
                    cu = CameraUpdateFactory.changeLatLng(lastLatLng);
                    //更新地图显示区域
                    mAMap.moveCamera(cu);
                }
            }
        }
    }

    @Override
    public void toSetHolder(MyRouteHolder holder, InnerRouteBean data, int position) {
        holder.mTitleTv.setText(data.getDoveid());

        Drawable likeLift = getResources().getDrawable(mTrailPicMap.get(data.getDoveid()));
        likeLift.setBounds(0, 0, (int) getResources().getDimension(R.dimen.DIP_20_DP), (int) getResources().getDimension(R.dimen.DIP_20_DP));
        holder.mTitleTv.setCompoundDrawables(likeLift, null, null, null);
    }
}
