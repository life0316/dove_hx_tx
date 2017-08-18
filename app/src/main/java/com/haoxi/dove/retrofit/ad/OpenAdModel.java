package com.haoxi.dove.retrofit.ad;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017\8\18 0018.
 */

public class OpenAdModel extends BaseModel {
    public void getDatasFromNets(Map<String, Object> map, final RequestCallback<AdviewResObj> requestCallback) {

        adNewService.getOpenAd(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

}
