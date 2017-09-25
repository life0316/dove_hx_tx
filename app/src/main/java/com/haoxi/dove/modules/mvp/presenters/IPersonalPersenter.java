package com.haoxi.dove.modules.mvp.presenters;

import java.util.Map;

import okhttp3.RequestBody;
public interface IPersonalPersenter {
    void uploadImage(Map<String, RequestBody> map);
}
