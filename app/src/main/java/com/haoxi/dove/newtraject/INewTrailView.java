package com.haoxi.dove.newtraject;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.StartFlyBean;

import java.util.List;

public interface INewTrailView extends MvpView {
    void toSetStartFly(StartFlyBean startFlyBean);
    void toSetEndFly();
//    void onFailed(String msg);
    void trailFromDao(List<InnerRouteBean> innerRouteBeanList , String type_from);
    void getFlyDatas(boolean isRefrash);
    void getPigeonDatas();
    String getPigeonObjId();
}
