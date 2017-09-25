package com.haoxi.dove.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.haoxi.dove.bean.DaoMaster;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.inject.AppComponent;
import com.haoxi.dove.inject.AppMoudle;
import com.haoxi.dove.inject.DaggerAppComponent;
import com.haoxi.dove.retrofit.RetrofitManager;
import com.haoxi.dove.utils.LogToFile;
import com.haoxi.dove.utils.RxBus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static MyApplication myApplication;
    private static DaoSession daoSession;
    private RxBus mRxBus;
    AppComponent mAppComponent;
    //已添加的信鸽足环编号
    private List<String> mPigeonCodes;
    //开始飞行的鸽子
    private Set<String> mFlyingPigeonSets;
    private Map<String,ArrayList<String>> uniqueLists;

    private Map<String,ArrayList<PolylineOptions>> pOptionsMap = new HashMap<>();
    private Map<String,ArrayList<Polyline>> polylineMap = new HashMap<>();
    private Map<String,ArrayList<MarkerOptions>> markerOptionsMap = new HashMap<>();
    private Map<String,ArrayList<Marker>> markerMap = new HashMap<>();
    private Map<String,Boolean> numMap = new HashMap<>();
    private List<String> mateList = new ArrayList<>();
    public Map<String, ArrayList<MarkerOptions>> getMarkerOptionsMap() {
        return markerOptionsMap;
    }
    public Map<String, ArrayList<Marker>> getMarkerMap() {
        return markerMap;
    }

    public Map<String,ArrayList<PolylineOptions>> getpOptionsMap(){
        if (pOptionsMap == null) {
            pOptionsMap = new HashMap<>();
        }
        return pOptionsMap;
    }
    public Map<String,Boolean> getNumMap(){
        if (numMap == null) {
            numMap = new HashMap<>();
        }
        return numMap;
    }
    public List<String> getMateList(){
        if (mateList == null) {
            mateList = new ArrayList<>();
        }
        return mateList;
    }

    public Map<String,ArrayList<Polyline>> getPolylineMap(){
        if (polylineMap == null) {
            polylineMap = new HashMap<>();
        }
        return polylineMap;
    }

    public static MyApplication getMyBaseApplication() {
        if (myApplication == null) {
            myApplication = new MyApplication();
        }
        return myApplication;
    }

    public static Context getContext(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        com.ytb.logic.CMain.setAppId(this, "hmCySW5dkeYcLxlQCh", "2a488da56d814bb657c9fc683838f18b");
        startService(new Intent(this, com.android.statis.assis.Guidiance.class));
        super.onCreate();

        //CrashReport.initCrashReport(getApplicationContext(), "80824f0a59", false);
        // 182681387751

        myApplication = this;
        LogToFile.init(this);
        setupDatabase();
        mRxBus = RxBus.getInstance();
        RetrofitManager.initRetrofit();
        initInjector();
    }

    private void initInjector() {
        mAppComponent = DaggerAppComponent.builder()
                .appMoudle(new AppMoudle(myApplication,daoSession,mRxBus)).build();
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"pigeonfly.db",null);
        SQLiteDatabase database = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }

    public List<String> getmPigeonCodes() {
        if (mPigeonCodes == null){
            mPigeonCodes = new ArrayList<>();
        }
        return mPigeonCodes;
    }

    public Set<String> getmFlyingPigeonSets() {
        if (mFlyingPigeonSets == null){
            mFlyingPigeonSets = new HashSet<>();
        }
        return mFlyingPigeonSets;
    }

    public Map<String, ArrayList<String>> getUniqueLists() {
        if (uniqueLists == null) {
            uniqueLists = new HashMap<>();
        }
        return uniqueLists;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
