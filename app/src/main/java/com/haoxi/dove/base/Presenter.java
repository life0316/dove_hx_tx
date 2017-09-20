package com.haoxi.dove.base;

public interface Presenter <V extends MvpView>{
    /**
     * presenter 与 view 建立连接
     * @param mvpView
     */
    void attachView(V mvpView);

    /**
     * p 与 v 断开连接
     */
    void detachView();
}
