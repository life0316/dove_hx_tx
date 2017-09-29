package com.haoxi.dove.modules.loginregist.presenter;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.loginregist.ui.ILoginView;
import com.haoxi.dove.modules.loginregist.model.ILoginModel;
import com.haoxi.dove.newin.bean.OurUser;
import java.util.Map;

public class LoginPresenter extends BasePresenter<ILoginView, OurUser> implements ILoginPresenter {
    private ILoginModel loginModel;
    public LoginPresenter(ILoginModel loginModel) {
        this.loginModel = loginModel;
    }
    @Override
    public void getDataFromNets(Map<String,String> map) {
        checkViewAttached();
        getMvpView().showProgress();
        loginModel.getDatasFromNets(map,this);
    }

    @Override
    public void requestSuccess(OurUser data) {
        super.requestSuccess(data);
        if (isViewAttached()) {
            getMvpView().hideProgress();
            getMvpView().toGetDetail(data);
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError("600");
        if ("".equals(msg)){
            getMvpView().showErrorMsg("网络连接异常");
        }else {
            getMvpView().loginFail(msg);
        }
    }
}
