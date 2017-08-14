package com.haoxi.dove.base;

/**
 * Created by lifei on 2016/12/22.
 */

public abstract interface MvpView {

    //显示进度条
    void showProgress();

    //隐藏进度条或对话框
    void hideProgress();

    //显示错误信息
    void showErrorMsg(String errorMsg);

    void setNetTag(boolean netTag);

    void toDo();

    String getMethod();
    String getTime();
    String getSign();
    String getVersion();

}
