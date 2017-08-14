package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.InnerRing;

import java.util.List;

/**
 * Created by lifei on 2017/4/5.
 */

public interface IGetRingView extends MvpView {

    String getUserObjId();
    String getToken();


    void setRingData(List<InnerRing> ringData);

    void setRefrash(boolean isRefrash);
}
