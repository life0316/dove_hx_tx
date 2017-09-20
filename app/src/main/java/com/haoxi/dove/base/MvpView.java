package com.haoxi.dove.base;

public interface MvpView {

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
