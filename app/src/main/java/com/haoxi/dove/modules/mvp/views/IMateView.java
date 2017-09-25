package com.haoxi.dove.modules.mvp.views;

import com.alibaba.fastjson.JSONArray;
import com.haoxi.dove.base.MvpView;

public interface IMateView extends MvpView{
    String getUserObjId();
    String getToken();
    JSONArray getPigeonObjId();
    JSONArray getRingObjId();
    void mateSuccess(Object obj);

}
