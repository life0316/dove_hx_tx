package com.haoxi.dove.newin.ourcircle.model;

import android.content.Context;
import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.newin.bean.OurFabBean;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/1/17.
 */

public class OurFabModel extends BaseModel implements IGetModel<OurFabBean> {

    private static final String TAG = "OurFabModel";

    private Context mContext;

    @Override
    public void getDatasFromNets(final Map<String,String> map, final RequestCallback<OurFabBean> requestCallback) {
        ourNewService.getFab(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurFabBean, Boolean>() {
                    @Override
                    public Boolean call(OurFabBean ourFabBean) {

                        Log.e(TAG,ourFabBean.getMsg()+"======="+ourFabBean.getCode());

                        return ourFabBean.getCode() == 200;
                    }
                })
                .subscribe(new BaseSubscriber<OurFabBean>(requestCallback));

    }

}
