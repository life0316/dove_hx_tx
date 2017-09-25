package com.haoxi.dove.modules.mvp.presenters;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.models.TraFragModel;
import com.haoxi.dove.modules.mvp.views.ITraFragView;
import com.haoxi.dove.modules.traject.OurTrailFragment;
import com.haoxi.dove.newin.bean.OurRouteBean;

import java.util.Map;

public class OurFragPresenter extends BasePresenter<ITraFragView,OurRouteBean> implements IGetPresenter {

    private static String TAG = "TraFragPresenter";

    private IGetModel trajectoryModel;

    private static final String TYPE_FROM_DAO = "FROM_DAO";
    private static final String TYPE_FROM_NETS = "FROM_NETS";

    private String type_from = TYPE_FROM_NETS;


    public OurFragPresenter(OurTrailFragment mView) {
        attachView(mView);
        trajectoryModel = new TraFragModel();
    }


    @Override
    public void requestSuccess(OurRouteBean realFlyBeanList) {
        super.requestSuccess(realFlyBeanList);
        if (realFlyBeanList.getData().size() == 0) {
            //getMvpView().onFailed("无飞行记录");
            getMvpView().trailFromDao(realFlyBeanList.getData(),type_from);

        } else {
            getMvpView().trailFromDao(realFlyBeanList.getData(), type_from);
        }
    }

    @Override
    public void  getDataFromNets(Map<String, String> map) {
        type_from = TYPE_FROM_NETS;
        checkViewAttached();
        trajectoryModel.getDatasFromNets(map,this);
    }

}
