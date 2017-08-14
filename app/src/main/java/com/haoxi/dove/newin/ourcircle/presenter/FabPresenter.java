package com.haoxi.dove.newin.ourcircle.presenter;


import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.presenters.IGetPresenter;
import com.haoxi.dove.newin.ourcircle.ui.IFabView;
import com.haoxi.dove.newin.ourcircle.ui.IMyCircleView;
import com.haoxi.dove.newin.bean.OurFabBean;
import com.haoxi.dove.newin.ourcircle.model.OurFabModel;
import com.haoxi.dove.retrofit.DataLoadType;

import java.util.Map;

/**
 * Created by lifei on 2017/1/17.
 */

public class FabPresenter extends BasePresenter<IFabView,OurFabBean> implements IGetPresenter,IRefreshPresenter,ILoadMorePresenter {

    private static final String TAG = "FabPresenter";

    private IGetModel fabModel;
    private IMyCircleView mView;

    private boolean isLoadMore;

    private boolean isRefresh = true;

    public FabPresenter(IFabView mView) {

        attachView(mView);
        fabModel = new OurFabModel();
    }

    @Override
    public void beforeRequest() {
        if (!isRefresh) {
            super.beforeRequest();
        }
    }


    @Override
    public void requestSuccess(OurFabBean circleBean) {
        super.requestSuccess(circleBean);


        if (isRefresh) {
            getMvpView().setRefrash(false);

        }

        Log.e(TAG,(circleBean.getData() != null)+"------null");
        if (circleBean.getData() != null) {
            Log.e(TAG,circleBean.getData().size()+"------size");

            getMvpView().updateFabList(circleBean,"",isRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS:DataLoadType.TYPE_LOAD_MORE_SUCCESS);
            isRefresh = false;
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        if (isRefresh) {
            getMvpView().setRefrash(false);
            isRefresh = false;
        }
    }

    @Override
    public void getDataFromNets(Map<String,String> map) {

        fabModel.getDatasFromNets(map,this);

    }


    @Override
    public void refreshFromNets(Map<String,String> map) {

        isRefresh = true;

        getDataFromNets(map);

    }

    @Override
    public void loadMoreData(Map<String,String> map) {

        isRefresh = false;

        getDataFromNets(map);
    }
}
