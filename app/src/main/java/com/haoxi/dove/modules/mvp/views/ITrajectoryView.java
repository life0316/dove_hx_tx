package com.haoxi.dove.modules.mvp.views;

import com.alibaba.fastjson.JSONArray;
import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.bean.RealFlyBean;
import com.haoxi.dove.bean.SetTriBean;

import java.util.List;

/**
 * Created by lifei on 2017/1/17.
 */

public interface ITrajectoryView extends MvpView {

    void toStartFly();

    void toEndFly();

    void getFlyDatas(boolean isRefrash);

    String getToken();
    String getUserObjId();

    String getRingObjId();
    JSONArray getPigeonObjIds();

    Object getPigeonObjId();

    JSONArray getRingCode();

    String getLastTime();

    void toSetStartFly();

    void toSetEndFly();

    void toDrawTrail(List<RealFlyBean> list);

    void onFailed(String msg);


    void trailFromDao(List<RealFlyBean> list);

    void toHandler();

    void setTri(SetTriBean setTriBean, int trilWidth, String triColor, int triPic, int isFly);

}
