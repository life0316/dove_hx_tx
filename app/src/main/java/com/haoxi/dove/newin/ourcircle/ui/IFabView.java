package com.haoxi.dove.newin.ourcircle.ui;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.retrofit.DataLoadType;

/**
 * Created by Administrator on 2017\6\28 0028.
 */

public interface IFabView<T> extends MvpView {

    String getUserObJId();

    String getToken();

    void setRefrash(boolean refrash);

    void updateFabList(T data, String errorMsg, @DataLoadType.DataLoadTypeChecker int type);
}
