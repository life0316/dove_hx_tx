package com.haoxi.dove.modules.mvp.models;


import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.OurRouteBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by lifei on 2017/1/17.
 */

public class TraFragModel extends BaseModel implements IGetModel<OurRouteBean> {

    private static String TAG = "TraFragmodel";

    List<String> flyTags = new ArrayList<String>();


    public TraFragModel(){
    }

//    @Override
//    public void getFlyDatasFromNet(final String userObjId,JSONObject object, RequestCallback requestCallback) {
//        String str = object.toString().replace("\"", "%22").replace("{", "%7b").replace("}", "%7d").replace("[", "%5b").replace("]", "%5d").replace(" ","%20");
//
//
//        String BASE_URL = "http://139.224.41.67:18033/pigeonAppServer/service/getPigeonFlying?params="+str;
//
//        Log.e(TAG,"这里是主轨迹界面获取实时轨迹的接口:  "+BASE_URL);
//
////        netService.getPigeonFlying(object)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribeOn(Schedulers.io())
////                .flatMap(new Func1<List<RealFlyBean>, Observable<List<RealFlyBean>>>() {
////                    @Override
////                    public Observable<List<RealFlyBean>> call(final List<RealFlyBean> realFlyBeen) {
////                        return Observable.create(new Observable.OnSubscribe<List<RealFlyBean>>() {
////                            @Override
////                            public void call(Subscriber<? super List<RealFlyBean>> subscriber) {
////
////                                //flyTags.clear();
////
////                                List<RealFlyBean> realFlyBeen1 = new ArrayList<RealFlyBean>();
////                                for (int i = 0; i < realFlyBeen.size(); i++) {
////
////                                    if (!flyTags.contains(realFlyBeen.get(i).getGENERATE_TIME())) {
////                                        flyTags.add(realFlyBeen.get(i).getGENERATE_TIME());
////
////                                        realFlyBeen.get(i).setUSER_OBJ_ID(userObjId);
////
////                                        MyApplication.getDaoSession().getRealFlyBeanDao()
////                                                .insertOrReplace(realFlyBeen.get(i));
////
////                                        realFlyBeen1.add(realFlyBeen.get(i));
////                                    }
////                                }
////
////                                Log.e("sizekkkk",realFlyBeen1.size()+"----siza");
////
////                                subscriber.onNext(realFlyBeen1);
////                            }
////                        });
////                    }
////                })
////                .subscribe(new BaseSubscriber<List<RealFlyBean>>(requestCallback));
//    }

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
                .subscribe(new BaseSubscriber<OurRouteBean>(requestCallback));
    }
}
