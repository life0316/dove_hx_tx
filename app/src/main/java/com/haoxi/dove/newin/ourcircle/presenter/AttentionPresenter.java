package com.haoxi.dove.newin.ourcircle.presenter;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.presenters.IGetPresenter;
import com.haoxi.dove.newin.ourcircle.model.AttentionModel;
import com.haoxi.dove.newin.bean.AttentionBean;
import com.haoxi.dove.newin.ourcircle.ui.IMyCircleView;
import com.haoxi.dove.retrofit.DataLoadType;

import java.util.Map;


/**
 * Created by lifei on 2017/3/30.
 */

public class AttentionPresenter extends BasePresenter<IMyCircleView, AttentionBean> implements IGetPresenter {

    private IGetModel attentionModel;

    private boolean isRefrash = true;


    public AttentionPresenter(IMyCircleView mView) {
        attachView(mView);
        attentionModel = new AttentionModel();
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
    }

    @Override
    public void requestSuccess(AttentionBean data) {
        super.requestSuccess(data);

        getMvpView().updateCircleList(data.getData(),"", isRefrash ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);

    }

    public void getAttentionFromDao(String userObjId) {

    }

    @Override
    public void getDataFromNets(Map<String, String> map) {
        attentionModel.getDatasFromNets(map,this);
    }
}
