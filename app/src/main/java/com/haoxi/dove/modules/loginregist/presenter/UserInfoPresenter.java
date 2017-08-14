package com.haoxi.dove.modules.loginregist.presenter;


import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.loginregist.ui.ILoginView;
import com.haoxi.dove.modules.loginregist.model.ILoginModel;
import com.haoxi.dove.newin.bean.OurUserInfo;

import java.util.Map;

/**
 * Created by lifei on 2016/12/26.
 */

public class UserInfoPresenter extends BasePresenter<ILoginView, OurUserInfo> implements ILoginPresenter {

    private static final String TAG = "LoginPresenter";
    private ILoginModel loginModel;

    public UserInfoPresenter(ILoginModel loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void requestSuccess(OurUserInfo data) {
        super.requestSuccess(data);

        if (isViewAttached()) {
            getMvpView().hideProgress();
            getMvpView().toMainActivity(data);
        }
    }
    @Override
    public void requestError(String msg) {
        super.requestError(msg);
    }

    @Override
    public void getDataFromNets(Map<String,String> map) {
        loginModel.getDatasFromNets(map,this);
    }
}
