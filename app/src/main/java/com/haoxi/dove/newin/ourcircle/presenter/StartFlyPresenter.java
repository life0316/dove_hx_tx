package com.haoxi.dove.newin.ourcircle.presenter;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.presenters.IGetPresenter;
import com.haoxi.dove.modules.mvp.views.ITraFragView;
import com.haoxi.dove.newin.bean.StartFlyBean;
import com.haoxi.dove.newin.ourcircle.model.StartFlyModel;

import java.util.Map;


/**
 * Created by lifei on 2017/3/30.
 */

public class StartFlyPresenter extends BasePresenter<ITraFragView, StartFlyBean> implements IGetPresenter {

    private IGetModel attentionModel;

    private boolean isRefrash = true;

    public StartFlyPresenter(ITraFragView mView) {
        attachView(mView);
        attentionModel = new StartFlyModel();
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
    }

    @Override
    public void requestSuccess(StartFlyBean startFlyBean) {
        super.requestSuccess(startFlyBean);

//        getMvpView().updatePlayerInfo(data);



        getMvpView().toSetStartFly(startFlyBean);
    }

    @Override
    public void getDataFromNets(Map<String, String> map) {
        attentionModel.getDatasFromNets(map,this);
    }
}
