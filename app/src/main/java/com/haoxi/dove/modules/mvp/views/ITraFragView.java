package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.bean.SetTriBean;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.StartFlyBean;

import java.util.List;

/**
 * Created by lifei on 2017/1/17.
 */

public interface ITraFragView extends MvpView {

    void toStartFly();

    void toEndFly();

    void getFlyDatas(boolean isRefrash);

    void getPigeonDatas();

    String getToken();
    String getUserObjId();

    String getPigeonObjId();

    String getLastTime();

    void toSetStartFly(StartFlyBean startFlyBean);

    void toSetEndFly();

    void onFailed(String msg);

    void trailFromDao(List<InnerRouteBean> innerRouteBeanList , String type_from);

    void setTriMap(List<SetTriBean> setTriBeen);

    //点击开始后去查询
    void setTri(SetTriBean setTriBean);

}
