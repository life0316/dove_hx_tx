package com.haoxi.dove.newin.ourcircle.presenter;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.presenters.IGetPresenter;
import com.haoxi.dove.newin.ourcircle.ui.IPlayerInfoView;
import com.haoxi.dove.newin.bean.PlayerBean;
import com.haoxi.dove.newin.ourcircle.model.PlayerModel;

import java.util.Map;


/**
 * Created by lifei on 2017/3/30.
 */

public class PlayerPresenter extends BasePresenter<IPlayerInfoView, PlayerBean> implements IGetPresenter {

    private IGetModel attentionModel;

    private boolean isRefrash = true;

    public PlayerPresenter(IPlayerInfoView mView) {
        attachView(mView);
        attentionModel = new PlayerModel();
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
    }

    @Override
    public void requestSuccess(PlayerBean data) {
        super.requestSuccess(data);

        getMvpView().updatePlayerInfo(data);

    }

    @Override
    public void getDataFromNets(Map<String, String> map) {
        attentionModel.getDatasFromNets(map,this);
    }
}
