package com.haoxi.dove.modules.mvp.models;

import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.UploadImageBean;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UploadImageModel extends BaseModel implements IUploadImage<UploadImageBean> {
    @Override
    public void uploageImage(Map<String, RequestBody> map,final RequestCallback<UploadImageBean> requestCallback) {
        ourNewService2.getUploadPic(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<UploadImageBean, Boolean>() {
                    @Override
                    public Boolean call(UploadImageBean user) {
                        int codes = user.getCode();
                        if (codes != 200){
                            requestCallback.requestError(user.getMsg());
                        }
                        return 200 == user.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }
}
