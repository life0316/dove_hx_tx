package com.haoxi.dove.newin.ourcircle.model;

import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

/**
 * Created by lifei on 2017/1/17.
 */

public interface ICirlcModel<T> {

    void getRouteDatasFromNet(Map<String,String> map, RequestCallback<T> requestCallback);

    void getMyCircleDatas(Map<String,String> map,RequestCallback<T> requestCallback);

    void getAllCircleDatas(Map<String,String> map,RequestCallback<T> requestCallback);
}
