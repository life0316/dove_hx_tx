package com.haoxi.dove.modules.loginregist.model;

import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.newin.bean.OurUser;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class LoginModel extends BaseModel implements ILoginModel<OurUser> {
    @Override
    public void getDatasFromNets(Map<String,String> map,final RequestCallback<OurUser> requestCallback) {
        ourNewService.getOurLogin(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurUser, Boolean>() {
                    @Override
                    public Boolean call(OurUser user) {
                        int codes = user.getCode();
                        if (codes != 200){
                            requestCallback.requestError(user.getMsg());
                        }
                        return 200 == user.getCode();
                    }
                })
                .subscribe(new BaseSubscriber<>(requestCallback));
    }
}