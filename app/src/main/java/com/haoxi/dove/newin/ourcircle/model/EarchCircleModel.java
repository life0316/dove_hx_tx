package com.haoxi.dove.newin.ourcircle.model;

import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.newin.bean.EachCircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBeanDao;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class EarchCircleModel extends BaseModel implements IGetModel<EachCircleBean> {

    private static final String TAG = "EarchCircleModel";
    private String playerid = "";

    @Override
    public void getDatasFromNets(final Map<String, String> map, final RequestCallback<EachCircleBean> requestCallback) {

        playerid = map.get("playerid");

        map.remove(playerid);
        ourNewService.getCircleDetail(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .filter(new Func1<EachCircleBean, Boolean>() {
                    @Override
                    public Boolean call(EachCircleBean attentionBean) {

                        int codes = attentionBean.getCode();
                        Log.e(TAG,codes+"----codes");
                        Log.e(TAG,attentionBean.getMsg()+"----msg");
                        return 200 == attentionBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }
}
