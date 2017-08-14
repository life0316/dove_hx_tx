package com.haoxi.dove.modules.mvp.views;

import com.alibaba.fastjson.JSONArray;
import com.haoxi.dove.base.MvpView;

/**
 * @创建者 Administrator
 * @创建时间 2017/2/16 14:05
 * @描述
 */
public interface IMateView extends MvpView{

    String getUserObjId();
    String getToken();

    JSONArray getPigeonObjId();
    JSONArray getRingObjId();

    void mateSuccess(Object obj);

}
