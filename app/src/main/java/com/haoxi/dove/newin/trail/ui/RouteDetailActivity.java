package com.haoxi.dove.newin.trail.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.haoxi.dove.utils.TraUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lifei on 2017/6/27.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_history)
public class RouteDetailActivity extends BaseActivity implements LocationSource,EasyPermissions.PermissionCallbacks {


    private static final int REQUEST_CODE_HISTORY = 0x0000;

    @BindView(R.id.activity_pegionfly_mapview)
    MapView mapView;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_newfly_ring)
    TextView mIdTv;

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

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void initInject() {

    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOncli(View view) {
        this.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);

        initMap();

        Intent intent = getIntent();
        if (intent != null) {

            String flyRecordId = intent.getStringExtra("recordid");

            pointBeanArrayList = intent.getParcelableArrayListExtra("innerRouteBean");

            if (flyRecordId != null) {
                mIdTv.setText("飞行记录id:"+flyRecordId);
            }
        }
    }

    @Override
    protected void init() {

        mTitleTv.setText("记录详情");
        mBackIv.setVisibility(View.VISIBLE);

        SharedPreferences preferences = getSharedPreferences(ConstantUtils.USERINFO, MODE_PRIVATE);
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

        //    mAMap.clear();

        initClient();
        initOption();


        AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                showPop(marker);

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

//        AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                Log.e("mafafaf", marker.getTitle() + "----title");
//
//                showPop(marker);
//                return true;
//            }
//        };
//        mAMap.setOnMarkerClickListener(listener);
    }

    //声明定位回调监听
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

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

        if (list == null && list.size() == 0) {
            mLocationClient.startLocation();
            return;
        }

        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PointBean pointBean = list.get(i);
//            latLngs.add(ApiUtils.transform(Double.parseDouble(pointBean.getLat()), Double.parseDouble(pointBean.getLat())));
            //经纬度反了
//            latLngs.add(ApiUtils.transform(pointBean.getLng(),pointBean.getLat()));
            latLngs.add(ApiUtils.transform(pointBean.getLat(),pointBean.getLng()));
        }
//        TraUtils.drawHistoryFromList(mAMap, latLngs, trailPic, Color.parseColor(trailColor), trailWidth);
        TraUtils.drawHistoryFromPointBean(mAMap,list, trailPic, Color.parseColor(trailColor), trailWidth);
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
        mHeightTv.setText(String.valueOf(Double.valueOf(eachHeight) - 20));
        mLatlngTv.setText("东经" + Math.rint(Double.valueOf(eachLongitude)) + " 北纬" + Math.rint(Double.valueOf(eachLatitude)));
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
}
