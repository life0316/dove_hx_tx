package com.haoxi.dove.newin.trail;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.newin.bean.OurRouteBean;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RouteTitleModel extends BaseModel implements IGetModel<OurRouteBean> {
    @Override
    public void getDatasFromNets(Map<String, String> map,final RequestCallback<OurRouteBean> requestCallback) {
        ourNewService.searchFly(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurRouteBean, Boolean>() {
                    @Override
                    public Boolean call(OurRouteBean user) {
                        int codes = user.getCode();
                        return 200 == user.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }
}
