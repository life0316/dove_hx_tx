package com.haoxi.dove.newin.trail.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
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
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.newin.bean.PointBean;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.StringUtils;
import com.haoxi.dove.utils.TraUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@ActivityFragmentInject(contentViewId = R.layout.activity_history)
public class RouteDetailActivity extends BaseActivity implements LocationSource,EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_HISTORY = 0x0000;
    @BindView(R.id.activity_pegionfly_mapview)
    MapView mapView;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.new_icon)
    ImageView mMapTypeIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_newfly_ring)
    TextView mIdTv;
    @BindView(R.id.activity_newfly_dove)
    TextView mDoveIdTv;

    private AMap mAMap;
    private UiSettings mUiset;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationClientOption = null;
    private OnLocationChangedListener mChangedListener;
    private AMapLocation mAMapLocation;
    private int trailWidth = 10;
    private String trailColor = "#00ff00";
    private int trailPic = R.mipmap.icon_img_3;
    private SharedPreferences mTrailSp;
    private ArrayList<PointBean> pointBeanArrayList = new ArrayList<>();

    private boolean isMapNormal = false;

    protected String[] needPermissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };

    @OnClick(R.id.custom_toolbar_iv)
    void backOncli() {
        this.finish();
    }

    @OnClick(R.id.new_icon)
    void weiXing(){
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        initMap();
        Intent intent = getIntent();
        if (intent != null) {
            String flyRecordId = intent.getStringExtra("recordid");
            String doveid = intent.getStringExtra("doveid");
            pointBeanArrayList = intent.getParcelableArrayListExtra("innerRouteBean");
            if (flyRecordId != null) {
                mIdTv.setText("飞行记录id:"+flyRecordId);
            }
            if (doveid != null) {
                mDoveIdTv.setVisibility(View.VISIBLE);
                mDoveIdTv.setText("信鸽:"+doveid);
            }
        }
    }

    @Override
    protected void init() {
        mTitleTv.setText("记录详情");
        mBackIv.setVisibility(View.VISIBLE);

        mTrailSp = getSharedPreferences(ConstantUtils.TRAIL, MODE_PRIVATE);

        trailWidth = mTrailSp.getInt("thickness", 10);
        trailColor = mTrailSp.getString("color", "#00ff00");
        trailPic = mTrailSp.getInt("pic", R.mipmap.icon_img_3);

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

        initClient();
        initOption();

        AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TraUtils.showPop2(RouteDetailActivity.this,marker);
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
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    RouteDetailActivity.this.mAMapLocation = aMapLocation;
                    if (mChangedListener != null) {
                        mChangedListener.onLocationChanged(aMapLocation);
                        CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
                        mAMap.moveCamera(cu);
                    }
                } else if (aMapLocation.getErrorCode() == 12) {
                    ApiUtils.showToast(RouteDetailActivity.this, "缺少定位权限,定位失败");
                } else if (aMapLocation.getErrorCode() == 4) {
                    ApiUtils.showToast(RouteDetailActivity.this, "当前网络较差,请求服务器异常,定位失败");
                }
            }else {
                ApiUtils.showToast(RouteDetailActivity.this, "定位失败");
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
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @AfterPermissionGranted(REQUEST_CODE_HISTORY)
    private void requestCodeQRCodePermissions(){

        if (!EasyPermissions.hasPermissions(this,needPermissions)) {
            EasyPermissions.requestPermissions(this,"定位的权限",REQUEST_CODE_HISTORY,needPermissions);
        }else {

            if (pointBeanArrayList != null && pointBeanArrayList.size() != 0) {
                toDrawTril(pointBeanArrayList);
            }else {
                mLocationClient.startLocation();
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

        if (mChangedListener != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mChangedListener = null;
        }
        mLocationClient = null;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (pointBeanArrayList != null && pointBeanArrayList.size() != 0) {
            toDrawTril(pointBeanArrayList);
        }else {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        mLocationClient.startLocation();
    }

    public void toDrawTril(List<PointBean> list) {
        if (list == null || list.size() == 0) {
            mLocationClient.startLocation();
            return;
        }

//        ArrayList<LatLng> latLngs = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            PointBean pointBean = list.get(i);
////            latLngs.add(ApiUtils.transform(Double.parseDouble(pointBean.getLat()), Double.parseDouble(pointBean.getLat())));
//            //经纬度反了
////            latLngs.add(ApiUtils.transform(pointBean.getLng(),pointBean.getLat()));
//            latLngs.add(ApiUtils.transform(pointBean.getLat(),pointBean.getLng()));
//        }
//        TraUtils.drawHistoryFromList(mAMap, latLngs, trailPic, Color.parseColor(trailColor), trailWidth);

        TraUtils.drawHistoryFromPointBean(mAMap,list, trailPic, Color.parseColor(trailColor), trailWidth);
    }


}
