package com.haoxi.dove.modules.mvp.models;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.bean.RouteBean;
import com.haoxi.dove.callback.RequestCallback;

import java.util.List;
import java.util.Map;

/**
 * Created by lifei on 2017/3/30.
 */

public class RouteModel extends BaseModel implements IGetModel<List<RouteBean>> {

    private static String TAG = "RouteModel";

//    @Override
//    public void getDatasFromNets(JSONObject object, final RequestCallback<List<RouteBean>> requestCallback) {
//
//        String str = object.toString().replace("\"", "%22").replace("{", "%7b").replace("}", "%7d").replace("[", "%5b").replace("]", "%5d").replace(" ","%20");
//
//        String BASE_URL = "http://139.224.41.67:18033/pigeonAppServer/route/getRouteWithList?params="+str;
//
//        Log.e(TAG,"这里是获取行程列表的接口:  "+BASE_URL);
//
////        netService.getRouteDatas(object)
////                .subscribeOn(Schedulers.io())
////                .unsubscribeOn(Schedulers.io())
////                .doOnSubscribe(new Action0() {
////                    @Override
////                    public void call() {requestCallback.beforeRequest();
////                    }
////                })
////                .unsubscribeOn(Schedulers.io())
////                .doOnNext(new Action1<List<RouteBean>>() {
////                    @Override
////                    public void call(List<RouteBean> routeBeen) {
////
////                        MyApplication.getDaoSession().getRouteBeanDao().deleteAll();
////
////                        for (int i = 0; i < routeBeen.size(); i++) {
////                            MyApplication.getDaoSession().getRouteBeanDao().insertOrReplace(routeBeen.get(i));
////                        }
////                    }
////                })
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new BaseSubscriber<List<RouteBean>>(requestCallback));
//    }

    @Override
    public void getDatasFromNets(Map<String, String> map, RequestCallback<List<RouteBean>> requestCallback) {

    }
}
