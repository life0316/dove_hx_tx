package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;

public interface IAddPigeonView extends MvpView {

    String getUserObjIds();
    String getToken();

    String getRingCode();
    String getPigeonSex();
    String getPigeonOld();
    String getPigeonColor();
    String getPigeonEyes();
    String getPigeonBlood();

    void setPigeonColor(String color);
    void setPigeonBlood(String blood);
    void setPigeonSex(String sex);
    void setPigeonOld(int year,int month,int day);
    void setPigeonEyes(String eyes);


}
