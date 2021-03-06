package com.haoxi.dove.modules.loginregist.ui;

import com.haoxi.dove.base.MvpView;

public interface IRegistView<T> extends MvpView {

    String getUserPhone();
    String getUserEmail();
    String getUserPwd();
    String getUserRepwd();
    String getUserSex();
    String getUserBirth();

    void setUserBirth(int year,int month,int day);
    void setUserSex(String userSex);
    void toMainActivity(T user);
}
