package com.haoxi.dove.modules.mvp.models;

import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

public interface IGetModel<T> {
    void getDatasFromNets(Map<String, String> map, RequestCallback<T> requestCallback);
}
