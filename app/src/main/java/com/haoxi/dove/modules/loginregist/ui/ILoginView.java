package com.haoxi.dove.modules.loginregist.ui;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.newin.bean.OurUserInfo;


public interface ILoginView extends MvpView {

    String getUserPhone();

    String getUserPwd();

    String getUserId();

    String getToken();

    void toGetDetail(OurUser user);

    void toMainActivity(OurUserInfo userInfo);

    void loginFail(String msg);


}
