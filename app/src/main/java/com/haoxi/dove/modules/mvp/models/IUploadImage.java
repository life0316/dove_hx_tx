package com.haoxi.dove.modules.mvp.models;

import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017\6\9 0009.
 */

public interface IUploadImage<T> {

    void uploageImage(Map<String, RequestBody> map, RequestCallback<T> requestCallback);
}
