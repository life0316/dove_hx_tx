package com.haoxi.dove.newin.trail.presenter;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.newin.trail.ui.IGetOurRouteView;
import com.haoxi.dove.newin.bean.OurRouteBean;
import com.haoxi.dove.newin.trail.RouteTitleModel;

import java.util.Map;

/**
 * Created by lifei on 2017/6/27.
 */
public class RouteTitlePresenter extends BasePresenter<IGetOurRouteView,OurRouteBean> {

    private static final String TAG = "RouteTitlePresenter";

    private IGetModel routeModel;

    private boolean isRefrash = true;

    public RouteTitlePresenter(IGetOurRouteView mView) {
        attachView(mView);
        routeModel = new RouteTitleModel();
    }

    public void getRouteFormNets(Map<String,String> map){
        routeModel.getDatasFromNets(map,this);
    }


    @Override
    public void beforeRequest() {
        if (!isRefrash) {
            super.beforeRequest();
        }
    }

    @Override
    public void requestSuccess(OurRouteBean data) {
        super.requestSuccess(data);

        Log.e(TAG,data.getMsg()+"------msg");
        Log.e(TAG,data.getData().size()+"------data");

        if (isRefrash) {
            getMvpView().setRefrash(false);
            isRefrash = false;
//            getMvpView().showErrorMsg("刷新成功");
        }
        getMvpView().setRouteData(data);
    }

    public void getDatasRefrash(Map<String,String> map) {

        isRefrash = true;
        getRouteFormNets(map);
    }
}
