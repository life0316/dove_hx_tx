package com.haoxi.dove.modules.mvp.models;


import com.alibaba.fastjson.JSONObject;
import com.haoxi.dove.callback.MyModelCallback;
import com.haoxi.dove.callback.MyProgressCallback;
import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

/**
 * Created by lifei on 2017/1/12.
 */

public interface IAboutModel<T> {


    //获取简介数据
    void getBriefData(JSONObject params, MyModelCallback modelCallback);

    //获取版本信息

    void updateVar(JSONObject params, MyModelCallback modelCallback);

    //获取新版本
    void downloadApk(String apkUrl, MyProgressCallback progressCallback);

    void updateVar(Map<String,String> map, RequestCallback<T>  requestCallback);

}
