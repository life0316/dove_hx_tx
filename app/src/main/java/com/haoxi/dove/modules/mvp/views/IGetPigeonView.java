package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.List;

public interface IGetPigeonView extends MvpView {
    String getUserObjId();
    String getToken();
    void setPigeonData(List<InnerDoveData> pigeonData);
    //void setRefrash(boolean isRefrash);
}
