package com.haoxi.dove.modules.mvp.presenters;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by lifei on 2017/1/14.
 */

public interface IPersonalPersenter {

    void uploadImage(Map<String, RequestBody> map);
}
