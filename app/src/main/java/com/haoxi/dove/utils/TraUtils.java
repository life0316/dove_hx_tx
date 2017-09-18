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
import com.haoxi.dove.bean.HistoryBean;
import com.haoxi.dove.bean.RealFlyBean;
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

    public static void drawLineFromLatlng1(float mAllMileage, final AMap aMap, final RealFlyBean realFlyBean, final RealFlyBean preFlyBean, final RealFlyBean firstFlyBean, final int icon, final int color, final int width) {

        List<RealFlyBean> currentFlyBeen = MyApplication.getMyBaseApplication().getCurrentFlyBeans();

        if (firstFlyBean != null) {
            if (markers != null && markers.size() > 0) {
                markers.get(markers.size() - 1).remove();
            }
        } else {
            if (markers != null && markers.size() > 0) {
                for (int i = 0; i < markers.size(); i++) {
                    markers.get(i).remove();
                }
            }
        }

//        float endDistance = 0;
        float endDistance = mAllMileage;

        LatLng eachFirstLatLng = null;

        if (firstFlyBean != null && firstFlyBean.getLATITUDE() != null && firstFlyBean.getLONGITUDE() != null) {

            eachFirstLatLng = ApiUtils.transform(Double.parseDouble(firstFlyBean.getLATITUDE()), Double.parseDouble(firstFlyBean.getLONGITUDE()));
        }


        if (addMarker != null) {
            addMarker.remove();

        }

        if (preFlyBean != null && preFlyBean.getLATITUDE() != null && preFlyBean.getLONGITUDE() != null) {

            preLatLng = ApiUtils.transform(Double.parseDouble(preFlyBean.getLATITUDE()), Double.parseDouble(preFlyBean.getLONGITUDE()));
        }


        final LatLng realLatLng = ApiUtils.transform(Double.parseDouble(realFlyBean.getLATITUDE()), Double.parseDouble(realFlyBean.getLONGITUDE()));


        ArrayList<MarkerOptions> optionsList = new ArrayList<>();


        Log.e("PigeonFlyActivity", (preLatLng == null) + "------prelatlng");

        if (preLatLng == null) {

            //创建markeroptions对象
            MarkerOptions options1 = new MarkerOptions();
            //设置markeroptions
            options1.position(realLatLng).title(realFlyBean.getGENERATE_TIME()
                    + "#" + realFlyBean.getFLYING_SPEED()
                    + "#" + realFlyBean.getFLYING_DIRECTION()
                    + "#" + realFlyBean.getLONGITUDE()
                    + "#" + realFlyBean.getLATITUDE()
                    + "#" + realFlyBean.getFLYING_HEIGHT()
                    + "#" + 0
            );

            //设置图标
            options1.icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_location));
            optionsList.add(options1);

            currentFlyBeen.add(preFlyBean);

            isQidian = true;

        } else {

            if (isQidian) {
                isQidian = false;
            } else {

//                float currentDistance = 0;

                if (eachFirstLatLng != null) {

//                    currentDistance = AMapUtils.calculateLineDistance(eachFirstLatLng, preLatLng);
//                    currentDistance = (float) (Math.round(currentDistance / 1000 * 100)) / 100;
//
//                    endDistance += currentDistance;
                }

                //创建markeroptions对象
                MarkerOptions options1 = new MarkerOptions();
                //设置markeroptions
                options1.position(preLatLng).title(preFlyBean.getGENERATE_TIME()
                        + "#" + preFlyBean.getFLYING_SPEED()
                        + "#" + preFlyBean.getFLYING_DIRECTION()
                        + "#" + preFlyBean.getLONGITUDE()
                        + "#" + preFlyBean.getLATITUDE()
                        + "#" + preFlyBean.getFLYING_HEIGHT()
                        + "#" + endDistance
                );

                //设置图标
                options1.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2));
                optionsList.add(options1);
            }
        }

        if (eachFirstLatLng == null) {
            //return;
        } else {
            //endDistance = AMapUtils.calculateLineDistance(preLatLng, realLatLng);
            Log.e(TAG, endDistance + "---drawLineFromLatlng1--1");

            endDistance += (float) (Math.round(AMapUtils.calculateLineDistance(preLatLng, realLatLng) / 1000 * 100)) / 100;

            Log.e(TAG, endDistance + "---drawLineFromLatlng1--2");
        }

        //设置地址定位
        //创建一个设置经纬度的cameraupdate
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(realLatLng);
        //更新地图显示区域
        aMap.moveCamera(cu);

        //创建markeroptions对象
        MarkerOptions options = new MarkerOptions();
        //设置markeroptions
        options.position(realLatLng).title(realFlyBean.getGENERATE_TIME()
                + "#" + realFlyBean.getFLYING_SPEED()
                + "#" + realFlyBean.getFLYING_DIRECTION()
                + "#" + realFlyBean.getLONGITUDE()
                + "#" + realFlyBean.getLATITUDE()
                + "#" + realFlyBean.getFLYING_HEIGHT()
                + "#" + endDistance
        );
        //设置图标
        if (icon != 0) {
            options.icon(BitmapDescriptorFactory.fromResource(icon));
            optionsList.add(options);

            markers = aMap.addMarkers(optionsList, true);

        }

        //画轨迹
        PolylineOptions polylineOptions = new PolylineOptions();

        if (preLatLng != null) {

            polylineOptions.add(preLatLng).color(color).width(width).zIndex(1).isGeodesic();
        }

        polylineOptions.add(realLatLng).color(color).width(width).zIndex(1).isGeodesic();
        aMap.addPolyline(polylineOptions);

//        //TODO 5.4
        currentFlyBeen.remove(currentFlyBeen.size() - 1);
        currentFlyBeen.add(realFlyBean);

        String time = ApiUtils.formatTime(ApiUtils.secsBetween2(firstFlyBean.getGENERATE_TIME(), realFlyBean.getGENERATE_TIME()));

        eachInfoListener.eachInfo(time, endDistance, 0, 0, 0);

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

    public static void drawTraFromList1(float mAllMileage, final AMap mAMap, final RealFlyBean firstFlyBean, final List<RealFlyBean> realFlyBeen, final int icon, final int color, final int width) {

        float totalDistance = mAllMileage;

        List<RealFlyBean> currentFlyBeen = MyApplication.getMyBaseApplication().getCurrentFlyBeans();

        ArrayList<LatLng> latLngs1 = new ArrayList<>();

        if (firstFlyBean != null) {
            if (markers != null && markers.size() > 0) {
                markers.get(markers.size() - 1).remove();
            }
        } else {
            if (markers != null && markers.size() > 0) {
                for (int i = 0; i < markers.size(); i++) {
                    markers.get(i).remove();
                }
            }
        }

        if (addMarker != null) {
            addMarker.remove();
        }

        if (realFlyBeen.size() == 0) {
            return;
        }

        //TODO 5.4
        if (currentFlyBeen.size() != 0) {
            currentFlyBeen.remove(currentFlyBeen.size() - 1);
        }

        currentFlyBeen.addAll(realFlyBeen);

        ArrayList<MarkerOptions> optionsList = new ArrayList<>();

        LatLng endLatLng = ApiUtils.transform(Double.parseDouble(realFlyBeen.get(realFlyBeen.size() - 1).getLATITUDE()), Double.parseDouble(realFlyBeen.get(realFlyBeen.size() - 1).getLONGITUDE()));
        LatLng preEndLatLng = ApiUtils.transform(Double.parseDouble(realFlyBeen.get(realFlyBeen.size() - 2).getLATITUDE()), Double.parseDouble(realFlyBeen.get(realFlyBeen.size() - 2).getLONGITUDE()));

        for (int i = 0; i < realFlyBeen.size() - 1; i++) {

            RealFlyBean realFlyBean = realFlyBeen.get(i);

            LatLng pigeonLatLng = ApiUtils.transform(Double.parseDouble(realFlyBeen.get(i).getLATITUDE()), Double.parseDouble(realFlyBeen.get(i).getLONGITUDE()));
            LatLng firstLatLng = ApiUtils.transform(Double.parseDouble(firstFlyBean.getLATITUDE()), Double.parseDouble(firstFlyBean.getLONGITUDE()));

            latLngs1.add(pigeonLatLng);

            //创建markeroptions对象
            MarkerOptions options = new MarkerOptions();


            if (i == 0 && firstFlyBean != null) {
                options.position(firstLatLng).title(firstFlyBean.getGENERATE_TIME()
                        + "#" + firstFlyBean.getFLYING_SPEED()
                        + "#" + firstFlyBean.getFLYING_DIRECTION()
                        + "#" + firstFlyBean.getLONGITUDE()
                        + "#" + firstFlyBean.getLATITUDE()
                        + "#" + firstFlyBean.getFLYING_HEIGHT()
                        + "#" + ""

                );

                //设置图标
                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_location));
                optionsList.add(options);
            } else {


                RealFlyBean eachRf = realFlyBeen.get(i - 1);
                LatLng eachPreLatLng = ApiUtils.transform(Double.parseDouble(eachRf.getLATITUDE())
                        , Double.parseDouble(eachRf.getLONGITUDE()));


                float eachDistance = AMapUtils.calculateLineDistance(eachPreLatLng, pigeonLatLng);

               // eachDistance = (float) (Math.round(eachDistance / 1000 * 100)) / 100;

                totalDistance += eachDistance;

                //设置markeroptions
                options.position(pigeonLatLng).title(realFlyBean.getGENERATE_TIME()
                        + "#" + realFlyBean.getFLYING_SPEED()
                        + "#" + realFlyBean.getFLYING_DIRECTION()
                        + "#" + realFlyBean.getLONGITUDE()
                        + "#" + realFlyBean.getLATITUDE()
                        + "#" + realFlyBean.getFLYING_HEIGHT()
                        + "#" + String.valueOf((float) (Math.round(totalDistance / 1000 * 100)) / 100)
                );

                //设置图标
                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker2));
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

        RealFlyBean lastBean = realFlyBeen.get(realFlyBeen.size() - 1);

        latLngs1.add(ApiUtils.transform(Double.parseDouble(lastBean.getLATITUDE()), Double.parseDouble(lastBean.getLONGITUDE())));

//        totalDistance = AMapUtils.calculateLineDistance(ApiUtils.transform(Double.parseDouble(firstFlyBean.getLATITUDE()), Double.parseDouble(firstFlyBean.getLONGITUDE())),
//                endLatLng);
//
//        totalDistance = (float) (Math.round(totalDistance / 1000 * 100)) / 100;
        totalDistance += AMapUtils.calculateLineDistance(preEndLatLng, endLatLng);

        totalDistance = (float) (Math.round(totalDistance / 1000 * 100)) / 100;

        Log.e("total", totalDistance + "----last");
        Log.e("total", lastBean.getLONGITUDE() + "----last");
        Log.e("total", lastBean.getLATITUDE() + "----last");

        options.position(endLatLng).title(lastBean.getGENERATE_TIME()
                + "#" + lastBean.getFLYING_SPEED()
                + "#" + lastBean.getFLYING_DIRECTION()
                + "#" + lastBean.getLONGITUDE()
                + "#" + lastBean.getLATITUDE()
                + "#" + lastBean.getFLYING_HEIGHT()
                + "#" + String.valueOf(totalDistance)
        );

        //设置图标
        if (icon != 0) {
            options.icon(BitmapDescriptorFactory.fromResource(icon));
            optionsList.add(options);

            markers = mAMap.addMarkers(optionsList, true);

        }

        //画轨迹
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs1).color(color).width(width).zIndex(1).isGeodesic();
        mAMap.addPolyline(polylineOptions);

        String time = ApiUtils.formatTime(ApiUtils.secsBetween2(realFlyBeen.get(0).getGENERATE_TIME(), lastBean.getGENERATE_TIME()));

        //eachInfoListener.eachInfo(time, totalDistance, new ArrayList<Float>());
        eachInfoListener.eachInfo(time, totalDistance, 0, 0, 0);

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
        PointBean lastPointBean;

        final ArrayList<LatLng> latLngs = new ArrayList<>();

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
//            firstLatLng = ApiUtils.transform(firstPointBean.getLng(),firstPointBean.getLat());
//            lastLatLng = ApiUtils.transform(lastPointBean.getLng(),lastPointBean.getLat());
           firstLatLng = ApiUtils.transform(firstPointBean.getLat(),firstPointBean.getLng());
            lastLatLng = ApiUtils.transform(lastPointBean.getLat(),lastPointBean.getLng());

            if (pointBeen.size() > 1) {

//                LatLng  prelastLatLng = ApiUtils.transform(pointBeen.get(pointBeen.size() - 2).getLng(),pointBeen.get(pointBeen.size() - 2).getLat());
                LatLng  prelastLatLng = ApiUtils.transform(pointBeen.get(pointBeen.size() - 2).getLat(),pointBeen.get(pointBeen.size() - 2).getLng());
                endDistance = AMapUtils.calculateLineDistance(prelastLatLng, lastLatLng);
                lastDir = getDir(lastLatLng.latitude,lastLatLng.longitude,prelastLatLng.latitude,prelastLatLng.longitude);
            }
        }

        LatLng middleLatLng = null;

        ArrayList<MarkerOptions> optionsList = new ArrayList<>();

        for (int i = 0; i < pointBeen.size() - 1; i++) {

            latLngs.add(ApiUtils.transform(pointBeen.get(i).getLat(),pointBeen.get(i).getLng()));

            if (i == pointBeen.size() / 2) {
                middleLatLng = ApiUtils.transform(pointBeen.get(i).getLat(),pointBeen.get(i).getLng());
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

//                LatLng pigeonLatLng = ApiUtils.transform(pointBeen.get(i).getLng(),pointBeen.get(i).getLat());
//                LatLng preLatLng = ApiUtils.transform(pointBeen.get(i - 1).getLng(), pointBeen.get(i - 1).getLat());
                LatLng pigeonLatLng = ApiUtils.transform(pointBeen.get(i).getLat(),pointBeen.get(i).getLng());
                LatLng preLatLng = ApiUtils.transform( pointBeen.get(i - 1).getLat(),pointBeen.get(i - 1).getLng());

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

        latLngs.add(lastLatLng);


        new Thread(new Runnable() {
            @Override
            public void run() {

                //画轨迹
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(latLngs).color(color).width(width).zIndex(1).isGeodesic();
                mAMap.addPolyline(polylineOptions);

            }
        }).start();
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
}
