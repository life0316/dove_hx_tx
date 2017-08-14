package com.haoxi.dove.newin.ourcircle.presenter;


import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.presenters.IGetPresenter;
import com.haoxi.dove.newin.ourcircle.model.OurCommentModel;
import com.haoxi.dove.newin.bean.OurCommentBean;
import com.haoxi.dove.newin.ourcircle.ui.IMyCommentView;
import com.haoxi.dove.retrofit.DataLoadType;

import java.util.Map;

/**
 * Created by lifei on 2017/1/17.
 */

public class InnerCommentPresenter extends BasePresenter<IMyCommentView,OurCommentBean> implements IGetPresenter,IRefreshPresenter,ILoadMorePresenter {

    private static final String TAG = "MyDynamicPresenter";

    private IGetModel comentModel;

    private boolean isLoadMore;

    private boolean isRefresh = true;

    public InnerCommentPresenter(IMyCommentView mView) {

        attachView(mView);
        comentModel = new OurCommentModel();
    }

    @Override
    public void requestSuccess(OurCommentBean circleBean) {
        super.requestSuccess(circleBean);
        Log.e(TAG,(circleBean.getData() != null)+"------null");
        if (circleBean.getData() != null) {
            Log.e(TAG,circleBean.getData().size()+"------size");


            getMvpView().updateCommentList(circleBean,"",isRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS:DataLoadType.TYPE_LOAD_MORE_SUCCESS);
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);

    }

    @Override
    public void getDataFromNets(Map<String,String> map) {

        comentModel.getDatasFromNets(map,this);
    }

    public void getDatasFromDao(String playerId, String userId, boolean isFriend, int tag) {

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
