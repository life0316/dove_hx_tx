package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;

/**
 * Created by lifei on 2017/4/6.
 */

public interface IAddDynamicView extends MvpView {

    String getUserObjIds();
    String getToken();

    String getContent();

}
