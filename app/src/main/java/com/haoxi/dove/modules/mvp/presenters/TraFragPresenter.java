package com.haoxi.dove.modules.mvp.presenters;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.models.TraFragModel;
import com.haoxi.dove.modules.mvp.views.ITraFragView;
import com.haoxi.dove.modules.traject.OurTrailFragment;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.OurRouteBean;

import java.util.List;
import java.util.Map;


public class TraFragPresenter extends BasePresenter<ITraFragView,OurRouteBean> implements IGetPresenter {

    private static String TAG = "TraFragPresenter";
    private IGetModel traFragModel;
    private static final String TYPE_FROM_DAO = "FROM_DAO";
    private static final String TYPE_FROM_NETS = "FROM_NETS";
    private String type_from = TYPE_FROM_NETS;

    public TraFragPresenter(OurTrailFragment mView) {
        attachView(mView);
        traFragModel = new TraFragModel();
    }
    @Override
    public void requestSuccess(OurRouteBean ourRouteBean) {
        super.requestSuccess(ourRouteBean);

        Log.e(TAG, ourRouteBean.getData().size() + "----获取飞行轨迹数据---" + TAG);
        Log.e(TAG, type_from + "-----从哪里获取的数据-----" + TAG);

        List<InnerRouteBean> innerRouteBeanList = ourRouteBean.getData();

        if (innerRouteBeanList.size() == 0) {
            getMvpView().trailFromDao(innerRouteBeanList,type_from);
        } else {
            getMvpView().trailFromDao(innerRouteBeanList,type_from);
        }
    }

    @Override
    public void  getDataFromNets(Map<String, String> map) {
        type_from = TYPE_FROM_NETS;
        checkViewAttached();
        traFragModel.getDatasFromNets(map,this);
    }

    @Override
    public void requestError(String msg) {
        getMvpView().onFailed(msg);
    }

}
