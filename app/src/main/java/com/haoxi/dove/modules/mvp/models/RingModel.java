package com.haoxi.dove.modules.mvp.models;

import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.OurRingBean;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class RingModel extends BaseModel implements IGetModel<OurRingBean> {
    private static String TAG = "RingModel";
    @Override
    public void getDatasFromNets(Map<String, String> map,final RequestCallback<OurRingBean> requestCallback) {
        ourNewService.searchRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurRingBean, Boolean>() {
                    @Override
                    public Boolean call(OurRingBean user) {

                        int codes = user.getCode();
                        Log.e(TAG,codes+"----codes");
                        Log.e(TAG,user.getMsg()+"----msg");
                        if (user.getData() != null) {
                            for (int i = 0; i < user.getData().size(); i++) {
//                                Log.e(TAG,user.getData().get(i).getRingid()+"----ringid");
//                                Log.e(TAG,user.getData().get(i).getRing_code()+"----ringcode");
//                                Log.e(TAG,user.getData().get(i).getPlayerid()+"----ringcode");
                                Log.e(TAG,user.getData().get(i).getPlayerid()+"----ringcode");
                            }
                        }
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
