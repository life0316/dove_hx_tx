package com.haoxi.dove.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.haoxi.dove.R;
import com.haoxi.dove.base.MyApplication;

import com.haoxi.dove.callback.EachInfoClickListener;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.PointBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TraUtils {

    private static final String TAG = "TraUtils";

    private static Marker addMarker;
    private static ArrayList<Marker> markers;
    private static ArrayList<Marker> allMarkers = new ArrayList<>();
    private static ArrayList<MarkerOptions> allOptionsList = new ArrayList<>();

    private static LatLng preLatLng;
    private static LatLng firstLatLng;
    private static LatLng lastLatLng;

    private static EachInfoClickListener eachInfoListener;
    private static Marker newMarker;

    private static String lastDir;

    public void setEachInfoListener(EachInfoClickListener eachInfoListener) {
        TraUtils.eachInfoListener = eachInfoListener;
    }


    private static boolean isQidian = false;

    public static void drawLineFromLatlng(final AMap aMap, final LatLng latlng, final LatLng preLatLng, final LatLng firstLatLng, final int icon, final int color, final int width) {

        float endDistance = 0;

        if (markers != null && markers.size() > 0) {

            markers.get(markers.size() - 1).remove();

            for (int i = 0; i < markers.size(); i++) {
                markers.get(i).hideInfoWindow();
            }
        }

        if (addMarker != null) {
            addMarker.remove();

            Log.e("lnglat", "----------addmarker");
        }

        ArrayList<MarkerOptions> optionsList = new ArrayList<>();

        if (preLatLng == null) {
            //创建markeroptions对象
            MarkerOptions options1 = new MarkerOptions();
            //设置markeroptions
            options1.position(latlng).title("起点位置");

            //设置图标
            options1.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
            optionsList.add(options1);

            isQidian = true;

        } else {

//            if (isQidian) {
//                isQidian = false;
//            } else {
//                //创建markeroptions对象
//                MarkerOptions options1 = new MarkerOptions();
//                //设置markeroptions
//                options1.position(preLatLng).title("起点位置").visible(isShowMarker);
//
//                //设置图标
//                options1.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2));
//                optionsList.add(options1);
//            }

        }


        if (firstLatLng == null) {
            //return;
        } else {
            endDistance = AMapUtils.calculateLineDistance(firstLatLng, latlng);
        }
        //设置地址定位
        //创建一个设置经纬度的cameraupdate
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(latlng);

        //更新地图显示区域
        aMap.moveCamera(cu);

        //创建markeroptions对象
        MarkerOptions options = new MarkerOptions();
        //设置markeroptions
//        options.position(latlng).title("记录时间:");
        options.position(latlng);
        //设置图标
        if (icon != 0) {
            options.icon(BitmapDescriptorFactory.fromResource(icon));

            optionsList.add(options);

            markers = aMap.addMarkers(optionsList, true);

            markers.get(markers.size() - 1).showInfoWindow();

            allMarkers.addAll(markers);
            allOptionsList.addAll(optionsList);
        }

        //画轨迹
        PolylineOptions polylineOptions = new PolylineOptions();

        if (preLatLng != null) {

            polylineOptions.add(preLatLng).color(color).width(width).zIndex(1).isGeodesic();
        }

        polylineOptions.add(latlng).color(color).width(width).zIndex(1).isGeodesic();
        aMap.addPolyline(polylineOptions);

    }

    private static Map<String, LatLng> firstLatlngs = new HashMap<>();
    private static Map<String, Float> distances = new HashMap<>();

    public static boolean setPoptionShow(Context context, AMap mAMap, int show, String pigeonObjId) {

        Map<String, ArrayList<Polyline>> polylines = MyApplication.getMyBaseApplication().getPolylineMap();
        ArrayList<Polyline> pl = polylines.get(pigeonObjId);

        Map<String, ArrayList<Marker>> markers = MyApplication.getMyBaseApplication().getMarkerMap();
        ArrayList<Marker> eachMarker = markers.get(pigeonObjId);

        if (show != 0) {

            if (pl != null && eachMarker != null) {
                for (int i = 0; i < pl.size(); i++) {

                    pl.get(i).remove();

                }

                for (int i = 0; i < eachMarker.size(); i++) {
                    eachMarker.get(i).remove();
                }
            } else {
                ApiUtils.showToast(context, "当前信鸽轨迹还未开始显示");
                return false;
            }

        } else {

            Map<String, ArrayList<PolylineOptions>> mapOptions = MyApplication.getMyBaseApplication().getpOptionsMap();

            ArrayList<PolylineOptions> p = mapOptions.get(pigeonObjId);

            Map<String, ArrayList<MarkerOptions>> markerOptions = MyApplication.getMyBaseApplication().getMarkerOptionsMap();
            ArrayList<MarkerOptions> eachMarkerOptions = markerOptions.get(pigeonObjId);


            if (p != null && eachMarkerOptions != null) {
                for (int i = 0; i < p.size(); i++) {

                    p.get(i).visible(show == 0);

                    pl.clear();

                    Polyline newP = mAMap.addPolyline(p.get(i));
                    pl.add(newP);

                }

                for (int i = 0; i < eachMarkerOptions.size(); i++) {
                    eachMarkerOptions.get(i).visible(true);
                    eachMarker.clear();
                    newMarker = mAMap.addMarker(eachMarkerOptions.get(i));
                    eachMarker.add(newMarker);
                }
            } else {
                ApiUtils.showToast(context, "当前信鸽轨迹还未开始显示");
                return false;
            }
        }

        return true;
    }


    public static void hideLastMarker(boolean isShowMarker) {

        isShowMarker = isShowMarker;

        if (markers != null && markers.size() > 0) {
            if (isShowMarker) {
                markers.get(markers.size() - 1).showInfoWindow();
            } else {
                markers.get(markers.size() - 1).hideInfoWindow();
            }
        }
    }

    public static void removeMarker() {
        if (markers != null && markers.size() > 0) {
            markers.get(markers.size() - 1).remove();
        }
    }

    public static void addMarker(Marker marker) {
        if (markers != null) {
            markers.add(markers.size() - 1, marker);
        }
    }

    private static boolean isShowMarker = true;

    public static void showAllMarker(AMap amap, boolean isShow) {

        isShowMarker = isShow;


        if (allMarkers != null && allMarkers.size() > 1) {
            Log.e("allMarkers", allMarkers.size() + "------allMarkers");

            Log.e("allMarkers", isShowMarker + "---2---allMarkers");
            for (int i = 1; i < allMarkers.size() - 1; i++) {

                Log.e("allMarkers", allMarkers.get(i).isVisible() + "---1---allMarkers");
                if (isShowMarker) {
                    allMarkers.get(i).setVisible(true);
                    //amap.addMarker(allOptionsList.get(i).visible(true));
                } else {
                    allMarkers.get(i).hideInfoWindow();
                    allMarkers.get(i).setVisible(false);
                }
            }
        }
    }

    public static void clearAllMarker() {
        if (allMarkers != null && allMarkers.size() > 1) {
            markers.clear();
            allMarkers.clear();
        }
    }

    public static void drawTraInnerRoute(final String firstTime,final String pigeonObjId, final int isShow, final boolean isFisrtTri, final AMap mAMap, final InnerRouteBean innerRouteBean, final int icon, final int color, final int width) {

        //final List<Marker> markerTris = new ArrayList<>();

        float currentDistance = 0;
        final Map<String, ArrayList<Marker>> markerMap = MyApplication.getMyBaseApplication().getMarkerMap();
        Map<String, ArrayList<MarkerOptions>> markerOptionsMap = MyApplication.getMyBaseApplication().getMarkerOptionsMap();

//        for (int i = 0; i < innerRouteBean.getPoints().size(); i++) {
//            Log.e("OurTrailFragment----", innerRouteBean.getPoints().size()+ "-------trailFromDao -----9");
//            Log.e("OurTrailFragment----", innerRouteBean.getPoints().get(i).getTime()+ "-------trailFromDao -----9");
//            Log.e("OurTrailFragment----", innerRouteBean.getPoints().get(i).getLat()+ "-------trailFromDao -----9");
//            Log.e("OurTrailFragment----", innerRouteBean.getPoints().get(i).getLng()+ "-------trailFromDao -----9");
//        }
//
//        Log.e("OurTrailFragment----", isShow+ "-------trailFromDao -----9");
        List<LatLng> latLngs = new ArrayList<>();
        if (newMarker != null) {
            newMarker.remove();
        }
        if (innerRouteBean.getPoints().size() == 0) {
            return;
        }
        PointBean lastPointBean = innerRouteBean.getPoints().get(innerRouteBean.getPoints().size() - 1);
        if (markerMap.get(pigeonObjId) != null && markerMap.get(pigeonObjId).size() > 0 && markerMap.get(pigeonObjId).get(0) != null) {
            markerMap.get(pigeonObjId).get(0).remove();
        }
        ArrayList<MarkerOptions> optionsList = new ArrayList<>();

//        LatLng endLatLng = ApiUtils.transform(lastPointBean.getLng(),lastPointBean.getLat());
        LatLng endLatLng = ApiUtils.transform(lastPointBean.getLat(),lastPointBean.getLng());

        for (int i = 0; i < innerRouteBean.getPoints().size() - 1; i++) {
            //创建markeroptions对象
            MarkerOptions options = new MarkerOptions();
            PointBean realFlyBean =  innerRouteBean.getPoints().get(i);
//            LatLng currentLl = ApiUtils.transform(realFlyBean.getLng(),realFlyBean.getLat());
            LatLng currentLl = ApiUtils.transform(realFlyBean.getLat(),realFlyBean.getLng());

            latLngs.add(currentLl);
            if (i == 0 && "".equals(firstTime)) {
                distances.put(pigeonObjId,0.f);
                firstLatlngs.put(pigeonObjId, currentLl);
                //设置markeroptions
                options.position(currentLl).title(realFlyBean.getTime()
                        + "#" + realFlyBean.getSpeed()
                        + "#" + realFlyBean.getDir()
                        + "#" + realFlyBean.getLng()
                        + "#" + realFlyBean.getLat()
                        + "#" + realFlyBean.getHeight()
                        + "#" + 0);

                //设置图标
                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_location)).visible(isShow == 0);
                optionsList.add(options);
            } else {

                String curDir = "";
                if (i != 0){
                    PointBean eachPreFlyBean = innerRouteBean.getPoints().get(i - 1);
//                    LatLng eachPreLl = ApiUtils.transform(eachPreFlyBean.getLng(),eachPreFlyBean.getLat());
                    LatLng eachPreLl = ApiUtils.transform(eachPreFlyBean.getLat(),eachPreFlyBean.getLng());
                    currentDistance += AMapUtils.calculateLineDistance(eachPreLl, currentLl);
                    curDir = getDir(currentLl.latitude,currentLl.longitude,eachPreLl.latitude,eachPreLl.longitude);
                }else {
                    currentDistance += distances.get(pigeonObjId);
                }

                //设置图标
                options.position(currentLl)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2)).visible(isShow == 0)
                        .title(realFlyBean.getTime()
                                + "#" + realFlyBean.getSpeed()
                                + "#" + curDir
                                + "#" + realFlyBean.getLng()
                                + "#" + realFlyBean.getLat()
                                + "#" + realFlyBean.getHeight()
                                + "#" + String.valueOf(((float) (Math.round(currentDistance / 1000 * 100)) / 100)));
                optionsList.add(options);
            }
        }
        //设置地址定位
        //创建一个设置经纬度的cameraupdate
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(endLatLng);
        //更新地图显示区域
        mAMap.moveCamera(cu);

        //创建markeroptions对象
        MarkerOptions options = new MarkerOptions();
        //设置markeroptions
        options.position(endLatLng);

        latLngs.add(endLatLng);
        //设置图标
        if (icon != 0) {
            if (innerRouteBean.getPoints().size() > 1) {
                PointBean realFlyBean = innerRouteBean.getPoints().get(innerRouteBean.getPoints().size() - 2);
//                LatLng preEndLatLng = ApiUtils.transform(realFlyBean.getLng(),realFlyBean.getLat());
                LatLng preEndLatLng = ApiUtils.transform(realFlyBean.getLat(),realFlyBean.getLng());
                currentDistance += AMapUtils.calculateLineDistance(preEndLatLng, endLatLng);
                distances.put(pigeonObjId,currentDistance);
                lastLatLng =  ApiUtils.transform(realFlyBean.getLat(),realFlyBean.getLng());
            }

            Log.e("OurTrailFragment----", icon+ "-------icon -----9");

            String lastDir = "";
            if (lastLatLng != null) {
                lastDir =  getDir(endLatLng.latitude,endLatLng.longitude,lastLatLng.latitude,lastLatLng.longitude);
            }

            options.icon(BitmapDescriptorFactory.fromResource(icon))
                    .visible(isShow == 0)
                    .title(lastPointBean.getTime()
                            + "#" + lastPointBean.getSpeed()
                            + "#" + lastDir
                            + "#" + lastPointBean.getLng()
                            + "#" + lastPointBean.getLat()
                            + "#" + lastPointBean.getHeight()
                            + "#" + String.valueOf(((float) (Math.round(currentDistance / 1000 * 100)) / 100)))
            ;
            optionsList.add(options);
            markers = mAMap.addMarkers(optionsList, true);
            if (!markerOptionsMap.containsKey(pigeonObjId)) {
                ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
                markerOptionses.add(optionsList.get(optionsList.size() - 1));
                markerOptionsMap.put(pigeonObjId, markerOptionses);
            } else {
                markerOptionsMap.get(pigeonObjId).clear();
                markerOptionsMap.get(pigeonObjId).add(optionsList.get(optionsList.size() - 1));
            }
            if (!markerMap.containsKey(pigeonObjId)) {
                ArrayList<Marker> polygons = new ArrayList<>();
                polygons.add(markers.get(markers.size() - 1));
                markerMap.put(pigeonObjId, polygons);
            } else {
                markerMap.get(pigeonObjId).clear();
                markerMap.get(pigeonObjId).add(markers.get(markers.size() - 1));
            }
        }
        //画轨迹
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs).color(color).width(width).zIndex(1).visible(isShow == 0).isGeodesic();
        mAMap.addPolyline(polylineOptions);
    }


    public static void drawHistoryFromPointBean(final AMap mAMap, final List<PointBean> pointBeen, int icon, final int color, final int width) {

        float fastSpeed = 0;
        float lowSpeed = 0;

        float eachDistance = 0;

        PointBean firstPointBean;
        final PointBean lastPointBean;

        final ArrayList<LatLng> latLngs1 = new ArrayList<>();

//        float distance = 0;

        float endDistance = 0;

        if (addMarker != null) {
            addMarker.destroy();
        }

        if (pointBeen.size() == 0) {
            return;
        } else {

            firstPointBean = pointBeen.get(0);
            lastPointBean = pointBeen.get(pointBeen.size() - 1);

            //这里暂时因为服务器经纬度弄反了
            firstLatLng = ApiUtils.transform(firstPointBean.getLng(),firstPointBean.getLat());
            lastLatLng = ApiUtils.transform(lastPointBean.getLng(),lastPointBean.getLat());
//           firstLatLng = ApiUtils.transform(firstPointBean.getLat(),firstPointBean.getLng());
//            lastLatLng = ApiUtils.transform(lastPointBean.getLat(),lastPointBean.getLng());

            if (pointBeen.size() > 1) {
                //这里暂时因为服务器经纬度弄反了
                LatLng  prelastLatLng = ApiUtils.transform(pointBeen.get(pointBeen.size() - 2).getLng(),pointBeen.get(pointBeen.size() - 2).getLat());
//                LatLng  prelastLatLng = ApiUtils.transform(pointBeen.get(pointBeen.size() - 2).getLat(),pointBeen.get(pointBeen.size() - 2).getLng());
                endDistance = AMapUtils.calculateLineDistance(prelastLatLng, lastLatLng);
                lastDir = getDir(lastLatLng.latitude,lastLatLng.longitude,prelastLatLng.latitude,prelastLatLng.longitude);
            }
        }

        LatLng middleLatLng = null;

        ArrayList<MarkerOptions> optionsList = new ArrayList<>();

        for (int i = 0; i < pointBeen.size() - 1; i++) {
            //这里暂时因为服务器经纬度弄反了
            latLngs1.add(ApiUtils.transform(pointBeen.get(i).getLng(),pointBeen.get(i).getLat()));
//          latLngs.add(ApiUtils.transform(pointBeen.get(i).getLat(),pointBeen.get(i).getLng()));

            if (i == pointBeen.size() / 2) {
                //这里暂时因为服务器经纬度弄反了
                middleLatLng = ApiUtils.transform(pointBeen.get(i).getLng(),pointBeen.get(i).getLat());
//                middleLatLng = ApiUtils.transform(pointBeen.get(i).getLat(),pointBeen.get(i).getLng());
            }

            //创建markeroptions对象
            MarkerOptions options = new MarkerOptions();

            if (i == 0) {

                fastSpeed = pointBeen.get(0).getSpeed();
                lowSpeed = pointBeen.get(0).getSpeed();

                options.position(firstLatLng).title(pointBeen.get(0).getTime()
                        + "#" + pointBeen.get(0).getSpeed()
                        + "#" + pointBeen.get(0).getDir()
                        + "#" + pointBeen.get(0).getLng()
                        + "#" + pointBeen.get(0).getLat()
                        + "#" + pointBeen.get(0).getHeight()
                        + "#" + String.valueOf(0)

                );

                //设置图标
                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_location));
                optionsList.add(options);
            } else {

                LatLng pigeonLatLng = ApiUtils.transform(pointBeen.get(i).getLng(),pointBeen.get(i).getLat());
                LatLng preLatLng = ApiUtils.transform(pointBeen.get(i - 1).getLng(), pointBeen.get(i - 1).getLat());
//                LatLng pigeonLatLng = ApiUtils.transform(pointBeen.get(i).getLat(),pointBeen.get(i).getLng());
//                LatLng preLatLng = ApiUtils.transform( pointBeen.get(i - 1).getLat(),pointBeen.get(i - 1).getLng());

                //eachDistance = AMapUtils.calculateLineDistance(preLatLng, pigeonLatLng);

                Log.e("eachDistance",eachDistance+"----1");

                eachDistance += AMapUtils.calculateLineDistance(preLatLng, pigeonLatLng);

                Log.e("eachDistance",eachDistance+"--------2");

                //eachDistance = (float) (Math.round(eachDistance / 1000 * 100)) / 100;


                if (pointBeen.get(i).getSpeed() > fastSpeed) {
                    fastSpeed = pointBeen.get(i).getSpeed();
                }
                if (pointBeen.get(i).getSpeed() < lowSpeed) {
                    lowSpeed = pointBeen.get(i).getSpeed();
                }

                String dir = getDir(pigeonLatLng.latitude,pigeonLatLng.latitude,preLatLng.latitude,preLatLng.longitude);

                //设置markeroptions
                options.position(pigeonLatLng).title(pointBeen.get(i).getTime()
                        + "#" + pointBeen.get(i).getSpeed()
                        + "#" + dir
                        + "#" + pointBeen.get(i).getLng()
                        + "#" + pointBeen.get(i).getLat()
                        + "#" + pointBeen.get(i).getHeight()
                        + "#" + String.valueOf(((float) (Math.round(eachDistance / 1000 * 100)) / 100))
                );

                //设置图标
                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2));
                optionsList.add(options);
            }
        }

        //设置地址定位
        //创建一个设置经纬度的cameraupdate
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(middleLatLng == null?firstLatLng:middleLatLng);
        //更新地图显示区域
        mAMap.moveCamera(cu);

        //创建markeroptions对象
        MarkerOptions option2 = new MarkerOptions();

        endDistance += eachDistance;

        Log.e("eachDistance",eachDistance+"--------2---"+endDistance);

        endDistance = (float) (Math.round(endDistance / 1000 * 100)) / 100;

        Log.e("eachDistance","-----------"+endDistance);

        //设置markeroptions
        option2.position(lastLatLng).title(lastPointBean.getTime()
                + "#" + lastPointBean.getSpeed()
                + "#" + lastDir
                + "#" + lastPointBean.getLng()
                + "#" + lastPointBean.getLat()
                + "#" + lastPointBean.getHeight()
                + "#" + String.valueOf(endDistance)
        );
        option2.icon(BitmapDescriptorFactory.fromResource(icon));
        optionsList.add(option2);

        mAMap.addMarkers(optionsList, true);

        latLngs1.add(lastLatLng);



        new Thread(new Runnable() {
            @Override
            public void run() {

                //画轨迹
                PolylineOptions polylineOptions = new PolylineOptions();
                if (latLngs1.size()<2 ){
                    polylineOptions.addAll(latLngs1).color(color).width(width).zIndex(1).isGeodesic();
                }else {
                    polylineOptions.addAll(moreMoreLatLng(pointBeen,pointBeen.size(),10)).color(color).width(width).zIndex(1).isGeodesic();
                }
//                polylineOptions.addAll(latLngs1).color(color).width(width).zIndex(1).isGeodesic();

                mAMap.addPolyline(polylineOptions);

            }
        }).start();
    }
    public static void showPop2(Activity context, Marker marker) {
        String markerTitle = marker.getTitle();
        String[] eachTitle = markerTitle.split("#");
        String createTime = eachTitle[0];
        String eachSpeed = eachTitle[1];
        String eachDirection = eachTitle[2];
        String eachLongitude = eachTitle[3];
        String eachLatitude = eachTitle[4];
        String eachHeight = eachTitle[5];
        String eachDistance = eachTitle[6];
        final Dialog popDialog = new Dialog(context, R.style.DialogTheme2);
        View view = View.inflate(context, R.layout.layout_show_marker3, null);
        popDialog.setCancelable(false);
        popDialog.setContentView(view);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.show_marker_ll);
        @SuppressWarnings("deprecation")
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (width * 62) / 72;
        params.height =(width * 68) / 72;
//        params.height =width;
        layout.setLayoutParams(params);
        //时间
        TextView mCreateTimeTv = (TextView) view.findViewById(R.id.show_marker_time);
        TextView mSpeedTv = (TextView) view.findViewById(R.id.show_marker_speed);
        TextView mHeightTv = (TextView) view.findViewById(R.id.show_marker_height);
        //纬经度
        TextView mLatTv = (TextView) view.findViewById(R.id.show_marker_lat);
        TextView mlngTv = (TextView) view.findViewById(R.id.show_marker_lng);
        //方向
        TextView mDirectionTv = (TextView) view.findViewById(R.id.show_marker_direction);
        //总飞行
        TextView mMileageTv = (TextView) view.findViewById(R.id.show_marker_mileage);
        ImageView mDismissIv = (ImageView) view.findViewById(R.id.show_marker_dismiss);

        mCreateTimeTv.setText(createTime);
        mSpeedTv.setText(eachSpeed);
        mHeightTv.setText(String.valueOf(Double.valueOf(eachHeight) - 20));

        String resultLng = StringUtils.format3(Double.valueOf(eachLongitude));
        String resultLat =  StringUtils.format3(Double.valueOf(eachLatitude));
        mLatTv.setText(resultLat);
        mlngTv.setText(resultLng);
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
    public static void showPop(Activity context, Marker marker) {

        String markerTitle = marker.getTitle();
        String[] eachTitle = markerTitle.split("#");

        String createTime = eachTitle[0];
        String eachSpeed = eachTitle[1];
        String eachDirection = eachTitle[2];
        String eachLongitude = eachTitle[3];
        String eachLatitude = eachTitle[4];
        String eachHeight = eachTitle[5];
        String eachDistance = eachTitle[6];

        final Dialog popDialog = new Dialog(context, R.style.DialogTheme2);

        View view = View.inflate(context, R.layout.layout_show_marker2, null);
        popDialog.setCancelable(false);
        popDialog.setContentView(view);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.show_marker_ll);

        @SuppressWarnings("deprecation")
        int width = context.getWindowManager().getDefaultDisplay().getWidth();

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (width * 62) / 72;
        params.height =(width * 42) / 72;

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

    public static String getDir(double cur_lat, double cur_lng, double lat, double lng){
        double a = cur_lat-lat;
        double b = cur_lng-lng;

        if((a > 0) && (b > 0)){
            return "东北";  //东北
        }
        else if((a > 0) && (b < 0)){
            return "西北";  //西北
        }
        else if((a < 0) && (b > 0)){
            return "东南";  //东南
        }
        else{
            return "西南";  //西南
        }
    }
    private static ArrayList<LatLng> moreMoreLatLng(List<PointBean> latLngs,int innum,int multiples){

        int outNum = 0;
//        if (innum<2 || multiples <1){
//            return latLngs;
//        }
        final ArrayList<LatLng> latLngs1 = new ArrayList<>();

        for (int i = 0; i < innum - 1; i++) {
            int multiplesTemp = multiples;

            double p0x,p1x,p2x,p3x;
            double p0y,p1y,p2y,p3y;

            p1x=latLngs.get(i).getLat();
            p1y=latLngs.get(i).getLng();
            p2x=latLngs.get(i+1) .getLat();
            p2y=latLngs.get(i+1).getLng();
            if (i==0) {
                p0x=2*p1x-p2x;
                p0y=2*p1y-p2y;
            }
            else{
                p0x=latLngs.get(i-1).getLat();
                p0y=latLngs.get(i-1).getLng();
            }
            if (i+1==innum-1) {
                p3x=2*p2x-p1x;
                p3y=2*p2y-p1y;
            }
            else{
                p3x=latLngs.get(i+2).getLat();
                p3y=latLngs.get(i+2).getLng();
            }

            for (int mum=0; mum<multiplesTemp; mum++) {
                double t=mum/(double)multiplesTemp;
                double out_x=
                        p0x * (-0.5*t*t*t + t*t - 0.5*t) +
                                p1x * (1.5*t*t*t - 2.5*t*t + 1.0) +
                                p2x * (-1.5*t*t*t + 2.0*t*t + 0.5*t) +
                                p3x * (0.5*t*t*t - 0.5*t*t);
                double out_y=
                        p0y * (-0.5*t*t*t + t*t - 0.5*t) +
                                p1y * (1.5*t*t*t - 2.5*t*t + 1.0) +
                                p2y * (-1.5*t*t*t + 2.0*t*t + 0.5*t) +
                                p3y * (0.5*t*t*t - 0.5*t*t);
//
//                {
//                    double padd = 0.3;
//                    out_x= p1x+
//                            (p2x-p0x)*padd*t+
//                            ((p2x-p1x)*3.0 - (p3x-p1x)*padd - (p2x-p0x)*2.0*padd)*t*t+
//                            ((p2x-p1x)*(-2.0) +(p3x-p1x)*padd +(p2x-p0x)*padd)*t*t*t;
//                    out_y= p1y+
//                            (p2y-p0y)*padd*t+
//                            ((p2y-p1y)*3.0 - (p3y-p1y)*padd - (p2y-p0y)*2.0*padd)*t*t+
//                            ((p2y-p1y)*(-2.0) +(p3y-p1y)*padd +(p2y-p0y)*padd)*t*t*t;
//                }

//                CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake(out_x, out_y);
                LatLng latLng = ApiUtils.transform(out_y,out_x);
                latLngs1.add(outNum,latLng);
                outNum++;
            }
        }
        LatLng latLng = ApiUtils.transform(latLngs.get(innum-1).getLng(),latLngs.get(innum-1).getLat());
        latLngs1.add(outNum,latLng);
        return latLngs1;
        }
}
