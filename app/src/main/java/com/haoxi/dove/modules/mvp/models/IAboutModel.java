package com.haoxi.dove.modules.mvp.models;


import com.haoxi.dove.callback.MyProgressCallback;
import com.haoxi.dove.callback.RequestCallback;
import java.util.Map;

public interface IAboutModel<T> {
    //获取新版本
    void downloadApk(String apkUrl, MyProgressCallback progressCallback);
    void updateVar(Map<String,String> map, RequestCallback<T>  requestCallback);
}
