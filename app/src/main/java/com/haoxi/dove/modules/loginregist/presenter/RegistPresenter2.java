package com.haoxi.dove.modules.loginregist.presenter;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.loginregist.ui.IRegistView;
import com.haoxi.dove.modules.loginregist.RegistActivity;
import com.haoxi.dove.modules.loginregist.model.ILoginModel;
import com.haoxi.dove.modules.loginregist.model.RegistModel;
import com.haoxi.dove.newin.bean.OurUser;
import java.util.Map;
public class RegistPresenter2 extends BasePresenter<IRegistView,OurUser> implements ILoginPresenter {
    private ILoginModel registModel;
    public RegistPresenter2(RegistActivity mView) {
        attachView(mView);
        registModel = new RegistModel();
    }
    @Override
    public void requestSuccess(OurUser data) {
        super.requestSuccess(data);
        getMvpView().toMainActivity(data);
    }

    @Override
    public void getDataFromNets(Map<String,String> map) {
        checkViewAttached();
        getMvpView().showProgress();
        registModel.getDatasFromNets(map,this);
    }
}
