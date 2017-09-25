package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.List;

public interface IRingInfoView extends MvpView{

    String getUserObjId();
    String getToken();
    void toSetData(List<InnerDoveData> object);
    void onMateSuccess(String msg, int pisition);
}
