package com.haoxi.dove.newin.trail.ui;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.OurRouteBean;

/**
 * Created by lifei on 2017/4/5.
 */

public interface IGetOurRouteView extends MvpView {

    String getUserObjId();
    String getToken();

    void setRouteData(OurRouteBean pigeonData);

    void setRefrash(boolean isRefrash);


}
