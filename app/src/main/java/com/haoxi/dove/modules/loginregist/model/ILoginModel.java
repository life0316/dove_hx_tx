package com.haoxi.dove.modules.loginregist.model;

import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

public interface ILoginModel<T> {
    void getDatasFromNets(Map<String,String> map, RequestCallback<T> requestCallback);
}
