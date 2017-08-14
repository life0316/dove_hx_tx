package com.haoxi.dove.newin.ourcircle.ui;

import com.haoxi.dove.base.MvpView;

/**
 * Created by Administrator on 2017\7\6 0006.
 */

public interface IMyCircleView<T> extends MvpView{

    String getToken();

    String getUserObJId();

    void updateCircleList(T data, String errorMsg, int type);

    void setRefrash(boolean refrash);
}
