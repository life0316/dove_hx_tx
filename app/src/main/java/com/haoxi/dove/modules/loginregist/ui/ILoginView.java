package com.haoxi.dove.modules.loginregist.ui;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.newin.bean.OurUserInfo;


public interface ILoginView extends MvpView {
    void toGetDetail(OurUser user);
    void loginFail(String msg);
}
